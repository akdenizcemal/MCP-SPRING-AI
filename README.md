# Study Tracker MCP Server

![CI](https://github.com/akdenizcemal/MCP-SPRING-AI/actions/workflows/ci.yml/badge.svg)

An MCP (Model Context Protocol) server in Java (Spring Boot, Spring AI) that lets
Claude Desktop manage study tasks in natural language.

![Demo](docs/demo.png)

## How it works

Claude Desktop launches the jar and talks to it over stdio. The server advertises
its tools, Claude picks the one that fits the request, and the Java code validates
the input, uses the database, and returns a result.

## Tools

| Tool | What it does |
|---|---|
| `addTask` | Add a task (subject defaults to "general") |
| `listTasks` | List open tasks, optionally by subject |
| `searchTasks` | Keyword search on titles |
| `updateTask` | Change a task's title or subject |
| `completeTask` | Mark a task done |
| `deleteTask` | Delete a task, only if completed |

## Design

- Least privilege: the model can only call these six methods
- All inputs validated, since model-generated arguments are untrusted
- Errors returned as messages so the model can recover
- Logging goes to a file because stdout carries the protocol

## Stack

Java 21, Spring Boot, Spring AI, Spring Data JPA, H2, JUnit 5, Mockito, GitHub Actions

## Run

1. `./mvnw clean package -DskipTests`
2. Test: `npx @modelcontextprotocol/inspector java -jar target/demo-0.0.1-SNAPSHOT.jar`
3. Add the jar to Claude Desktop's `mcpServers` config (absolute paths) and restart it