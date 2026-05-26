package models;

public abstract class Issue {
    private int id;
    private String title;
    private String assignedTo;

    public Issue(int id, String title, String assignedTo) {
        this.id = id;
        this.title = title;
        this.assignedTo = assignedTo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public abstract String getType();

    public abstract void displayDetails();
}
