package me.river.royaltisapi.core.managers;

import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.exceptions.LobbyNotFoundException;
import me.river.royaltisapi.core.game.Lobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the `LobbyManager` class.
 * Verifies core functionality related to lobby management, including:
 * - Checking lobby existence
 * - Retrieving game IDs from lobby codes
 * - Getting lobby objects by code
 * - Removing lobbies
 * - Creating new lobbies and ensuring code uniqueness
 */
class LobbyManagerTest {

    private LobbyManager lobbyManager;
    private GameId testGameId;
    private Lobby testLobby;
    private LobbyCode testLobbyCode;

    /**
     * Initializes a new `LobbyManager`, a test `GameId`, a test `LobbyCode`,
     * and a test `Lobby` before each test case.
     */
    @BeforeEach
    void setUp() {
        lobbyManager = new LobbyManager();
        testGameId = new GameId(1);
        testLobbyCode = new LobbyCode("123456");
        testLobby = new Lobby(testLobbyCode, testGameId);
    }

    /**
     * Verifies that `doesLobbyExist` correctly identifies an existing lobby.
     */
    @Test
    void doesLobbyExist_ExistingLobby() throws LobbyNotFoundException {
        lobbyManager.getLobbies().add(testLobby);
        assertTrue(lobbyManager.doesLobbyExist(testLobbyCode));
    }

    /**
     * Verifies that `doesLobbyExist` throws `LobbyNotFoundException` for a non-existent lobby.
     */
    @Test
    void doesLobbyExist_NonExistingLobby() {
        assertThrows(LobbyNotFoundException.class, () -> lobbyManager.doesLobbyExist(new LobbyCode("999999")));
    }

    /**
     * Confirms that `getGameIdByLobbyCode` retrieves the correct `GameId` for a valid lobby code.
     */
    @Test
    void getGameIdByLobbyCode_ExistingLobby() throws LobbyNotFoundException {
        lobbyManager.getLobbies().add(testLobby);
        assertEquals(testGameId, lobbyManager.getGameIdByLobbyCode(testLobbyCode));
    }

    /**
     * Verifies that `getGameIdByLobbyCode` throws `LobbyNotFoundException` for an invalid lobby code.
     */
    @Test
    void getGameIdByLobbyCode_NonExistingLobby() {
        assertThrows(LobbyNotFoundException.class, () -> lobbyManager.getGameIdByLobbyCode(new LobbyCode("999999")));
    }

    /**
     * Ensures that `getLobbyByLobbyCode` returns the correct `Lobby` instance for a valid lobby code.
     */
    @Test
    void getLobbyByLobbyCode_ExistingLobby() throws LobbyNotFoundException {
        lobbyManager.getLobbies().add(testLobby);
        assertEquals(testLobby, lobbyManager.getLobbyByLobbyCode(testLobbyCode));
    }

    /**
     * Checks that `getLobbyByLobbyCode` throws `LobbyNotFoundException` for a non-existent lobby code.
     */
    @Test
    void getLobbyByLobbyCode_NonExistingLobby() {
        assertThrows(LobbyNotFoundException.class, () -> lobbyManager.getLobbyByLobbyCode(new LobbyCode("999999")));
    }

    /**
     * Confirms that `removeLobby` successfully removes an existing lobby and returns `true`.
     */
    @Test
    void removeLobby_ExistingLobby() throws LobbyNotFoundException {
        lobbyManager.getLobbies().add(testLobby);
        assertTrue(lobbyManager.removeLobby(testLobbyCode));
        assertFalse(lobbyManager.getLobbies().contains(testLobby));
    }

    /**
     * Tests that `removeLobby` throws `LobbyNotFoundException` when attempting to remove a non-existent lobby.
     */
    @Test
    void removeLobby_NonExistingLobby() {
        assertThrows(LobbyNotFoundException.class, () -> lobbyManager.removeLobby(new LobbyCode("999999")));
    }

    /**
     * Tests if the createLobby method generates a valid lobby code of 6 digits
     * and if the lobby is correctly added to the manager's collection of lobbies.
     */
    @Test
    void createLobby() {
        String lobbyCode = lobbyManager.createLobby(testGameId);

        // Assert that the returned lobby code is valid (6 digits)
        assertNotNull(lobbyCode);
        assertEquals(6, lobbyCode.length());
        assertTrue(lobbyCode.matches("\\d{6}")); // Ensure it's all digits

        // Check if the created lobby is added to the lobbies set
        boolean lobbyExists = false;
        for (Lobby lobby : lobbyManager.getLobbies()) {
            if (lobby.getLobbyCode().toString().equals(lobbyCode)) {
                lobbyExists = true;
                break;
            }
        }
        assertTrue(lobbyExists);
    }

    /**
     * Creates multiple lobbies and verifies that each lobby code is unique.
     */
    @Test
    void createLobby_Uniqueness() {
        for (int i = 0; i < 100; i++) {
            String lobbyCode1 = lobbyManager.createLobby(testGameId);
            String lobbyCode2 = lobbyManager.createLobby(testGameId);
            assertNotEquals(lobbyCode1, lobbyCode2);
        }
    }
}
