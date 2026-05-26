package models;

public class Bug extends Issue {
    private String severity;
    private String status;

    public Bug(int id, String title, String assignedTo, String severity) {
        super(id, title, assignedTo);
        this.severity = severity;
        this.status = "New";
    }

    public Bug(int id, String title, String assignedTo, String severity, String status) {
        super(id, title, assignedTo);
        this.severity = severity;
        this.status = status;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getType() {
        return "Bug";
    }

    @Override
    public void displayDetails() {
        System.out.println("+-----------------------------------------------------+");
        System.out.printf("| [Bug #%d] %-39s |\n", getId(), getTitle());
        System.out.println("+-----------------------------------------------------+");
        System.out.printf("| Assigned To : %-37s |\n", getAssignedTo());
        System.out.printf("| Severity    : %-37s |\n", getSeverity());
        System.out.printf("| Status      : %-37s |\n", getStatus());
        System.out.println("+-----------------------------------------------------+");
    }
}
