package me.river.royaltisapi.core.managers;

import me.river.royaltisapi.core.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserManagerTest {

    private UserManager userManager;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
    }

    @Test
    void testRemoveUserBySessionId_UserNotFound() {
        UUID sessionId = UUID.randomUUID();

        assertThrows(UserNotFoundException.class, () -> userManager.removeUserBySessionId(sessionId));
    }

    @Test
    void testGetUserBySessionId_UserNotFound() {
        UUID sessionId = UUID.randomUUID();

        assertThrows(UserNotFoundException.class, () -> userManager.getUserBySessionId(sessionId));
    }
}
