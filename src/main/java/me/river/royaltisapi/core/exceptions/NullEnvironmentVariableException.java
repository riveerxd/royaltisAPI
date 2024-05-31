package me.river.royaltisapi.core.exceptions;

/**
 * This exception is thrown when a required environment variable is null.
 */
public class NullEnvironmentVariableException extends Exception {

    /**
     * Constructs a new NullEnvironmentVariableException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public NullEnvironmentVariableException(String message) {
        super(message);
    }
}
