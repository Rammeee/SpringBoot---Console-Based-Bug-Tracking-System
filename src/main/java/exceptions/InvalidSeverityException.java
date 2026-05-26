package exceptions;

public class InvalidSeverityException extends Exception {
    public InvalidSeverityException() {
        super("Severity must be 'Low', 'Medium', or 'High'.");
    }

    public InvalidSeverityException(String message) {
        super(message);
    }
}
