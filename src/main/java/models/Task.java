package models;

public class Task extends Issue {
    private int estimatedHours;

    public Task(int id, String title, String assignedTo, int estimatedHours) {
        super(id, title, assignedTo);
        this.estimatedHours = estimatedHours;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    @Override
    public String getType() {
        return "Task";
    }

    @Override
    public void displayDetails() {
        System.out.println("+-----------------------------------------------------+");
        System.out.printf("| [Task #%d] %-38s |\n", getId(), getTitle());
        System.out.println("+-----------------------------------------------------+");
        System.out.printf("| Assigned To     : %-33s |\n", getAssignedTo());
        System.out.printf("| Estimated Hours : %-33d |\n", getEstimatedHours());
        System.out.println("+-----------------------------------------------------+");
    }
}
