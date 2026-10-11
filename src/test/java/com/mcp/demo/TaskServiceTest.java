package com.mcp.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepo repo;

    @InjectMocks
    TaskService service;
    @Test
    void addTask_rejectsBlankTitle() {
        String result = service.addTask("   ", "Maths");
        assertThat(result).startsWith("Error");
        verify(repo, never()).save(any());
    }
    @Test
    void addTask_defaultsSubjectToGeneral() {
        when(repo.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        service.addTask("Revise recursion", null);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Revise recursion");
        assertThat(captor.getValue().getSubject()).isEqualTo("general");
    }

    @Test
    void listTasks_withoutSubject_returnsAllOpenTasks() {
        List<Task> open = List.of(new Task("Deadlocks", "OS"));
        when(repo.findByDoneFalse()).thenReturn(open);

        assertThat(service.listTasks(null)).isEqualTo(open);
    }

    @Test
    void listTasks_withSubject_filtersAndTrims() {
        when(repo.findBySubjectIgnoreCaseAndDoneFalse("Maths")).thenReturn(List.of());

        service.listTasks("  Maths ");

        verify(repo).findBySubjectIgnoreCaseAndDoneFalse("Maths");
    }

    @Test
    void completeTask_marksTaskDoneAndSaves() {
        Task task = new Task("Deadlocks", "OS");
        when(repo.findById(1L)).thenReturn(Optional.of(task));

        String result = service.completeTask(1L);

        assertThat(task.isDone()).isTrue();
        assertThat(result).contains("marked done");
        verify(repo).save(task);
    }

    @Test
    void completeTask_unknownId_returnsMessageAndSavesNothing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        String result = service.completeTask(99L);

        assertThat(result).contains("No task with id 99");
        verify(repo, never()).save(any());
    }
    @Test
    void searchTasks_blankKeyword_returnsEmptyWithoutQuerying() {
        assertThat(service.searchTasks("  ")).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void searchTasks_trimsKeywordAndQueries() {
        List<Task> found = List.of(new Task("Deadlocks", "OS"));
        when(repo.findByTitleContainingIgnoreCase("dead")).thenReturn(found);

        assertThat(service.searchTasks(" dead ")).isEqualTo(found);
    }

    @Test
    void updateTask_changesGivenFieldsOnlyAndSaves() {
        Task task = new Task("Old", "Maths");
        when(repo.findById(1L)).thenReturn(Optional.of(task));

        String result = service.updateTask(1L, "New", null);

        assertThat(task.getTitle()).isEqualTo("New");
        assertThat(task.getSubject()).isEqualTo("Maths");
        assertThat(result).contains("updated");
        verify(repo).save(task);
    }

    @Test
    void updateTask_nothingToChange_returnsErrorWithoutTouchingRepo() {
        String result = service.updateTask(1L, " ", null);

        assertThat(result).startsWith("Error");
        verifyNoInteractions(repo);
    }

    @Test
    void updateTask_unknownId_returnsMessageAndSavesNothing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.updateTask(99L, "New", null)).contains("No task with id 99");
        verify(repo, never()).save(any());
    }

    @Test
    void deleteTask_completedTask_isDeleted() {
        Task task = new Task("Deadlocks", "OS");
        task.markDone();
        when(repo.findById(1L)).thenReturn(Optional.of(task));

        assertThat(service.deleteTask(1L)).contains("deleted");
        verify(repo).delete(task);
    }

    @Test
    void deleteTask_openTask_isRefusedAndNotDeleted() {
        Task task = new Task("Deadlocks", "OS");
        when(repo.findById(1L)).thenReturn(Optional.of(task));

        assertThat(service.deleteTask(1L)).startsWith("Refused");
        verify(repo, never()).delete(any());
    }

    @Test
    void deleteTask_unknownId_returnsMessageAndDeletesNothing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.deleteTask(99L)).contains("No task with id 99");
        verify(repo, never()).delete(any());
    }

}