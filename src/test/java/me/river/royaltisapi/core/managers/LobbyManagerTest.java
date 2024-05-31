package me.river.royaltisapi.core.managers;

import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.exceptions.LobbyNotFoundException;
import me.river.royaltisapi.core.game.Lobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyManagerTest {

    private LobbyManager lobbyManager;
    private GameId testGameId;
    private Lobby testLobby;
    private LobbyCode testLobbyCode;

    @BeforeEach
    void setUp() {
        lobbyManager = new LobbyManager();
        testGameId = new GameId(1); // Assuming a valid game ID
        testLobbyCode = new LobbyCode("123456"); // Assuming a valid lobby code
        testLobby = new Lobby(testLobbyCode, testGameId);
    }

    @Test
    void doesLobbyExist_ExistingLobby() throws LobbyNotFoundException {
        lobbyManager.getLobbies().add(testLobby); // Add the test lobby
        assertTrue(lobbyManager.doesLobbyExist(testLobbyCode));
    }

    @Test
    void doesLobbyExist_NonExistingLobby() {
        assertThrows(LobbyNotFoundException.class, () -> lobbyManager.doesLobbyExist(new LobbyCode("999999"))); // Non-existing code
    }

    @Test
    void getGameIdByLobbyCode_ExistingLobby() throws LobbyNotFoundException {
        lobbyManager.getLobbies().add(testLobby); // Add the test lobby
        assertEquals(testGameId, lobbyManager.getGameIdByLobbyCode(testLobbyCode));
    }

    @Test
    void getGameIdByLobbyCode_NonExistingLobby() {
        assertThrows(LobbyNotFoundException.class, () -> lobbyManager.getGameIdByLobbyCode(new LobbyCode("999999")));
    }

    @Test
    void getLobbyByLobbyCode_ExistingLobby() throws LobbyNotFoundException {
        lobbyManager.getLobbies().add(testLobby); // Add the test lobby
        assertEquals(testLobby, lobbyManager.getLobbyByLobbyCode(testLobbyCode));
    }

    @Test
    void getLobbyByLobbyCode_NonExistingLobby() {
        assertThrows(LobbyNotFoundException.class, () -> lobbyManager.getLobbyByLobbyCode(new LobbyCode("999999")));
    }

    @Test
    void removeLobby_ExistingLobby() throws LobbyNotFoundException {
        lobbyManager.getLobbies().add(testLobby); // Add the test lobby
        assertTrue(lobbyManager.removeLobby(testLobbyCode));
        assertFalse(lobbyManager.getLobbies().contains(testLobby)); // Verify the lobby was removed
    }

    @Test
    void removeLobby_NonExistingLobby() {
        assertThrows(LobbyNotFoundException.class, () -> lobbyManager.removeLobby(new LobbyCode("999999")));
    }

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

    @Test
    void createLobby_Uniqueness() {
        // This test verifies that the generated lobby codes are unique
        for (int i = 0; i < 100; i++) { // Create a bunch of lobbies and check for uniqueness
            String lobbyCode1 = lobbyManager.createLobby(testGameId);
            String lobbyCode2 = lobbyManager.createLobby(testGameId);
            assertNotEquals(lobbyCode1, lobbyCode2);
        }
    }
}