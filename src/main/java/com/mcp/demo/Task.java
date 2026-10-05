package com.mcp.demo;

import jakarta.persistence.*;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subject;
    private boolean done;

    protected Task() {}

    public Task(String title, String subject) {
        this.title = title;
        this.subject = subject;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public boolean isDone() { return done; }

    public void markDone() { this.done = true; }
}