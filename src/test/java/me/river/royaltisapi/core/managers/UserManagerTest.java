package me.river.royaltisapi.core.managers;

import me.river.royaltisapi.core.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the `UserManager` class, specifically focusing on scenarios
 * where the requested user is not found in the system.
 */
class UserManagerTest {

    private UserManager userManager;

    /**
     * Sets up a fresh `UserManager` instance before each test.
     */
    @BeforeEach
    void setUp() {
        userManager = new UserManager();
    }

    /**
     * Verifies that attempting to remove a user by their session ID
     * when that user does not exist throws a `UserNotFoundException`.
     */
    @Test
    void testRemoveUserBySessionId_UserNotFound() {
        UUID sessionId = UUID.randomUUID(); // Generate a random session ID

        assertThrows(UserNotFoundException.class, () -> userManager.removeUserBySessionId(sessionId),
                "Removing a non-existent user should throw UserNotFoundException");
    }

    /**
     * Confirms that attempting to retrieve a user by their session ID
     * when that user is not present in the system results in a `UserNotFoundException`.
     */
    @Test
    void testGetUserBySessionId_UserNotFound() {
        UUID sessionId = UUID.randomUUID(); // Generate a random session ID

        assertThrows(UserNotFoundException.class, () -> userManager.getUserBySessionId(sessionId),
                "Retrieving a non-existent user should throw UserNotFoundException");
    }
}
