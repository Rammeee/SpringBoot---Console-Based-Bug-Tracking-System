package main;

import dao.BugDAO;
import exceptions.EmptyTitleException;
import exceptions.InvalidSeverityException;
import models.Bug;
import models.Issue;
import models.Task;
import services.IssueService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final IssueService issueService = new IssueService();
    private static final BugDAO bugDAO = new BugDAO();
    private static final Scanner scanner = new Scanner(System.in);
    
    private static int nextInMemoryId = 1000;
    private static boolean dbConnected = false;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("       WELCOME TO THE CONSOLE BUG TRACKER        ");
        System.out.println("=================================================");

        // Try initializing database and loading existing bugs
        try {
            System.out.print("Connecting to PostgreSQL database... ");
            bugDAO.initializeTable();
            List<Bug> existingBugs = bugDAO.getAllBugs();
            for (Bug bug : existingBugs) {
                issueService.addIssue(bug);
                if (bug.getId() >= nextInMemoryId) {
                    nextInMemoryId = bug.getId() + 1;
                }
            }
            dbConnected = true;
            System.out.println("SUCCESS! Loaded " + existingBugs.size() + " bugs from database.");
        } catch (SQLException | EmptyTitleException | InvalidSeverityException e) {
            logException(e);
            System.out.println("OFFLINE MODE. Database connection failed.");
            System.out.println("Reason: " + e.getMessage());
            System.out.println("The app will run in in-memory mode. Bugs will not be persisted.");
        }

        boolean exit = false;
        while (!exit) {
            printMenu();
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();
            System.out.println();

            switch (input) {
                case "1":
                    handleAddBug();
                    break;
                case "2":
                    handleAddTask();
                    break;
                case "3":
                    handleViewAllIssues();
                    break;
                case "4":
                    handleUpdateBugStatus();
                    break;
                case "5":
                    handleDeleteBug();
                    break;
                case "6":
                    exit = true;
                    System.out.println("Exiting Bug Tracker. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 6.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Bug Tracker Menu =====");
        System.out.println("1. Add Bug");
        System.out.println("2. Add Task");
        System.out.println("3. View All Issues");
        System.out.println("4. Update Bug Status");
        System.out.println("5. Delete Bug");
        System.out.println("6. Exit");
        System.out.println("============================");
    }

    private static void handleAddBug() {
        System.out.print("Enter Bug Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Assigned To: ");
        String assignedTo = scanner.nextLine().trim();

        System.out.print("Enter Severity (Low, Medium, High): ");
        String severity = scanner.nextLine().trim();

        try {
            // Create bug with a temporary ID.
            Bug bug = new Bug(0, title, assignedTo, severity);

            // Validate using Service layer (throws custom exceptions if invalid)
            // But wait, to prevent adding duplicate bug with temp ID 0, let's validate first.
            // Since addIssue performs validation, we can call addIssue and database insert.
            if (dbConnected) {
                try {
                    int generatedId = bugDAO.insertBug(bug);
                    // Update ID of the bug with database-generated ID
                    bug.setId(generatedId);
                    issueService.addIssue(bug);
                    System.out.println("\n[Success] Bug added to database and memory with ID: " + generatedId);
                } catch (SQLException e) {
                    logException(e);
                    System.out.println("\n[Warning] Database insert failed. Storing in memory only.");
                    int tempId = nextInMemoryId++;
                    bug.setId(tempId);
                    issueService.addIssue(bug);
                    System.out.println("[Success] Bug stored in memory with Temporary ID: " + tempId);
                }
            } else {
                int tempId = nextInMemoryId++;
                bug.setId(tempId);
                issueService.addIssue(bug);
                System.out.println("\n[Success] Bug added in-memory with ID: " + tempId);
            }
        } catch (EmptyTitleException | InvalidSeverityException e) {
            logException(e);
            System.out.println("\n[Error] Failed to add Bug: " + e.getMessage());
        }
    }

    private static void handleAddTask() {
        System.out.print("Enter Task Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Assigned To: ");
        String assignedTo = scanner.nextLine().trim();

        System.out.print("Enter Estimated Hours: ");
        String hoursInput = scanner.nextLine().trim();

        int estimatedHours;
        try {
            estimatedHours = Integer.parseInt(hoursInput);
            if (estimatedHours < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("\n[Error] Estimated Hours must be a positive integer.");
            return;
        }

        try {
            int tempId = nextInMemoryId++;
            Task task = new Task(tempId, title, assignedTo, estimatedHours);

            // Validate & Add
            issueService.addIssue(task);
            System.out.println("\n[Success] Task added to memory with ID: " + tempId);
        } catch (EmptyTitleException | InvalidSeverityException e) {
            logException(e);
            System.out.println("\n[Error] Failed to add Task: " + e.getMessage());
        }
    }

    private static void handleViewAllIssues() {
        issueService.displayAllIssues();
    }

    private static void handleUpdateBugStatus() {
        System.out.print("Enter Bug ID to update: ");
        String idInput = scanner.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("\n[Error] Invalid ID format.");
            return;
        }

        // Check if bug exists in-memory
        boolean existsInMemory = false;
        for (Issue issue : issueService.getAllIssuesList()) {
            if (issue instanceof Bug && issue.getId() == id) {
                existsInMemory = true;
                break;
            }
        }

        if (!existsInMemory) {
            System.out.println("\n[Error] Bug with ID " + id + " not found in local cache.");
            return;
        }

        System.out.print("Enter New Status (e.g. New, Open, In Progress, Resolved): ");
        String newStatus = scanner.nextLine().trim();
        if (newStatus.isEmpty()) {
            System.out.println("\n[Error] Status cannot be empty.");
            return;
        }

        boolean updated = false;
        if (dbConnected) {
            try {
                updated = bugDAO.updateStatus(id, newStatus);
            } catch (SQLException e) {
                logException(e);
                System.out.println("\n[Warning] Database update failed. Updating in memory only.");
            }
        }

        // Update in memory
        boolean memUpdated = issueService.updateBugStatusInMemory(id, newStatus);
        
        if (updated || memUpdated) {
            System.out.println("\n[Success] Bug status updated successfully.");
        } else {
            System.out.println("\n[Error] Could not update bug status.");
        }
    }

    private static void handleDeleteBug() {
        System.out.print("Enter Bug ID to delete: ");
        String idInput = scanner.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("\n[Error] Invalid ID format.");
            return;
        }

        // Check if bug exists in-memory
        boolean existsInMemory = false;
        for (Issue issue : issueService.getAllIssuesList()) {
            if (issue instanceof Bug && issue.getId() == id) {
                existsInMemory = true;
                break;
            }
        }

        if (!existsInMemory) {
            System.out.println("\n[Error] Bug with ID " + id + " not found in local cache.");
            return;
        }

        boolean deleted = false;
        if (dbConnected) {
            try {
                deleted = bugDAO.deleteBug(id);
            } catch (SQLException e) {
                logException(e);
                System.out.println("\n[Warning] Database deletion failed. Deleting from memory only.");
            }
        }

        // Delete from memory
        boolean memDeleted = issueService.deleteBugFromMemory(id);

        if (deleted || memDeleted) {
            System.out.println("\n[Success] Bug deleted successfully.");
        } else {
            System.out.println("\n[Error] Could not delete bug.");
        }
    }

    private static void logException(Exception e) {
        try (FileWriter fw = new FileWriter("error.log", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("Timestamp: " + new Date());
            pw.println("Exception Type: " + e.getClass().getName());
            pw.println("Message: " + e.getMessage());
            pw.println("Stack Trace:");
            e.printStackTrace(pw);
            pw.println("--------------------------------------------------------------------------------");
        } catch (IOException ioException) {
            System.err.println("CRITICAL: Failed to write to error.log: " + ioException.getMessage());
        }
    }
}
