package exceptions;

public class EmptyTitleException extends Exception {
    public EmptyTitleException() {
        super("Issue title cannot be blank or null.");
    }

    public EmptyTitleException(String message) {
        super(message);
    }
}
