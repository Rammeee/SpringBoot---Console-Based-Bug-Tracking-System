package services;

import exceptions.EmptyTitleException;
import exceptions.InvalidSeverityException;
import models.Bug;
import models.Issue;
import models.Task;

import java.util.ArrayList;
import java.util.List;

public class IssueService {
    private final List<Issue> issues = new ArrayList<>();

    public void addIssue(Issue issue) throws EmptyTitleException, InvalidSeverityException {
        // Validate title
        if (issue.getTitle() == null || issue.getTitle().trim().isEmpty()) {
            throw new EmptyTitleException("Issue title cannot be blank or null.");
        }

        // Validate bug severity
        if (issue instanceof Bug) {
            Bug bug = (Bug) issue;
            String severity = bug.getSeverity();
            if (severity == null || 
                (!severity.equals("Low") && !severity.equals("Medium") && !severity.equals("High"))) {
                throw new InvalidSeverityException("Invalid severity: '" + severity + "'. Must be 'Low', 'Medium', or 'High'.");
            }
        }

        issues.add(issue);
    }

    public void displayAllIssues() {
        if (issues.isEmpty()) {
            System.out.println("No issues found in the system.");
            return;
        }

        System.out.println("\n================= SYSTEM ISSUES =================\n");
        for (Issue issue : issues) {
            issue.displayDetails();
            System.out.println();
        }
        System.out.println("=================================================");
    }

    public List<Issue> getIssuesByType(String type) {
        List<Issue> filtered = new ArrayList<>();
        if (type == null) {
            return filtered;
        }
        for (Issue issue : issues) {
            if (issue.getType().equalsIgnoreCase(type)) {
                filtered.add(issue);
            }
        }
        return filtered;
    }

    // Auxiliary method to sync status updates in-memory
    public boolean updateBugStatusInMemory(int id, String newStatus) {
        for (Issue issue : issues) {
            if (issue instanceof Bug && issue.getId() == id) {
                ((Bug) issue).setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    // Auxiliary method to sync deletions in-memory
    public boolean deleteBugFromMemory(int id) {
        for (int i = 0; i < issues.size(); i++) {
            Issue issue = issues.get(i);
            if (issue instanceof Bug && issue.getId() == id) {
                issues.remove(i);
                return true;
            }
        }
        return false;
    }

    // Getter for direct access to raw issues
    public List<Issue> getAllIssuesList() {
        return issues;
    }
}
