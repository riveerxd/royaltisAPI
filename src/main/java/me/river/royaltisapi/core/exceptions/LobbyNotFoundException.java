package me.river.royaltisapi.core.exceptions;

/**
 * This exception is thrown when a requested lobby is not found.
 */
public class LobbyNotFoundException extends Exception {
    /**
     * Constructs a new LobbyNotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public LobbyNotFoundException(String message) {
        super(message);
    }
}
