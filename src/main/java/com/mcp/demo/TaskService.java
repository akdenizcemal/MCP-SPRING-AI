package com.mcp.demo;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepo repo;

    public TaskService(TaskRepo repo) {
        this.repo = repo;
    }

    @Tool(description = "Add a study task. Use when the user wants to remember something to study.")
    public String addTask(
            @ToolParam(description = "Short title of the task") String title,
            @ToolParam(description = "Subject, e.g. Maths or Operating Systems", required = false) String subject) {

        if (title == null || title.isBlank()) {
            return "Error: title cannot be empty";
        }
        String finalSubject = (subject == null || subject.isBlank()) ? "general" : subject.trim();
        Task saved = repo.save(new Task(title.trim(), finalSubject));
        return "Added task " + saved.getId() + ": " + saved.getTitle();
    }

    @Tool(description = "List open (not completed) study tasks, optionally filtered by subject.")
    public List<Task> listTasks(
            @ToolParam(description = "Subject to filter by", required = false) String subject) {


        return (subject == null || subject.isBlank())
                ? repo.findByDoneFalse()
                : repo.findBySubjectIgnoreCaseAndDoneFalse(subject.trim());
    }

    @Tool(description = "Mark a task as completed using its id.")
    public String completeTask(@ToolParam(description = "The task id") Long taskId) {
        return repo.findById(taskId).map(task -> {
            task.markDone();
            repo.save(task);
            return "Task " + taskId + " marked done";
        }).orElse("No task with id " + taskId);
    }
    @Tool(description = "Search study tasks by a keyword in the title. Includes open and completed tasks.")
    public List<Task> searchTasks(
            @ToolParam(description = "Keyword to look for in task titles") String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return repo.findByTitleContainingIgnoreCase(keyword.trim());
    }

    @Tool(description = "Change the title and/or subject of an existing task using its id.")
    public String updateTask(
            @ToolParam(description = "The task id") Long taskId,
            @ToolParam(description = "New title", required = false) String title,
            @ToolParam(description = "New subject", required = false) String subject) {

        boolean noTitle = title == null || title.isBlank();
        boolean noSubject = subject == null || subject.isBlank();
        if (noTitle && noSubject) {
            return "Error: provide a new title or a new subject";
        }
        return repo.findById(taskId).map(task -> {
            task.update(title, subject);
            repo.save(task);
            return "Task " + taskId + " updated";
        }).orElse("No task with id " + taskId);
    }

    @Tool(description = "Permanently delete a task by id. Only completed tasks can be deleted; open tasks must be completed first.")
    public String deleteTask(@ToolParam(description = "The task id") Long taskId) {
        return repo.findById(taskId).map(task -> {
            if (!task.isDone()) {
                return "Refused: task " + taskId + " is not completed. Complete it before deleting.";
            }
            repo.delete(task);
            return "Task " + taskId + " deleted";
        }).orElse("No task with id " + taskId);
    }
}