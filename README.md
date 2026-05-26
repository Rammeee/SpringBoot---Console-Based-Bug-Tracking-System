# Day 6 Assessment - Console Bug Tracker

## Project Overview

This is a simple Java console application for tracking issues, including bugs and tasks. It uses a service layer for validation and supports optional PostgreSQL persistence for bugs. If the database is unavailable, the app falls back to in-memory storage.

## Features

- Add a new bug with title, assigned user, and severity
- Add a new task with title, assigned user, and estimated hours
- View all issues currently stored in the system
- Update bug status
- Delete bugs from the database and in-memory cache
- Load bug records from PostgreSQL at startup when available
- Offline mode support if database connection fails

## Project Structure

- `src/main/java/main/Main.java` - Console application entry point
- `src/main/java/services/IssueService.java` - Business rules and validation for issues
- `src/main/java/dao/BugDAO.java` - Database access for bug persistence
- `src/main/java/util/DBUtil.java` - PostgreSQL connection helper
- `src/main/java/models/` - Domain models: `Bug`, `Task`, `Issue`
- `src/main/java/exceptions/` - Custom validation exceptions
- `src/main/java/com/day6/App.java` - Sample generated application class

## Requirements

- Java 11
- Apache Maven
- PostgreSQL (optional for persistence)

## Database Setup

By default, the app expects a PostgreSQL database with these settings:

- URL: `jdbc:postgresql://localhost:5432/bug_tracker`
- User: `postgres`
- Password: `password`

The table is created automatically on startup if it does not already exist.

### Customizing the Database Connection

Update the connection details in `src/main/java/util/DBUtil.java` if you use a different host, port, database name, or credentials.

## Build and Run

From the project root directory:

```bash
mvn clean compile exec:java
```

The `exec-maven-plugin` is configured to start the application using `main.Main`.

## Usage

When the application starts, it displays a console menu with options to:

1. Add Bug
2. Add Task
3. View All Issues
4. Update Bug Status
5. Delete Bug
6. Exit

Follow the prompts to manage issues.

## Notes

- Bugs loaded from PostgreSQL are persisted across application restarts.
- Tasks are stored only in memory and are not persisted to the database.
- Validation ensures issue titles are not blank and bug severity is one of `Low`, `Medium`, or `High`.

## Testing

A basic JUnit test class is included in `src/test/java/com/day6/AppTest.java`.

Run tests with:

```bash
mvn test
```
