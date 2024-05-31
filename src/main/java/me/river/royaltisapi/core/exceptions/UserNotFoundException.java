package me.river.royaltisapi.core.exceptions;

/**
 * This exception is thrown when a requested user is not found in the database.
 */
public class UserNotFoundException extends Exception {
    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
