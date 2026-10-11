package com.mcp.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepoTest {

    @Autowired
    TaskRepo repo;

    @Test
    void findBySubject_ignoresCase_andExcludesDoneTasks() {
        repo.save(new Task("Limits", "Maths"));

        Task finished = new Task("Matrices", "Maths");
        finished.markDone();
        repo.save(finished);

        repo.save(new Task("Deadlocks", "OS"));

        assertThat(repo.findBySubjectIgnoreCaseAndDoneFalse("maths"))
                .extracting(Task::getTitle)
                .containsExactly("Limits");
    }
    @Test
    void findByTitleContaining_isCaseInsensitive() {
        repo.save(new Task("Revise Deadlocks", "OS"));
        repo.save(new Task("Limits", "Maths"));

        assertThat(repo.findByTitleContainingIgnoreCase("deadl"))
                .extracting(Task::getTitle)
                .containsExactly("Revise Deadlocks");
    }
}