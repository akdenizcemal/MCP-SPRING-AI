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
}