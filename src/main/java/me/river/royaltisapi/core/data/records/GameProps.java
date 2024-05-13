package me.river.royaltisapi.core.data.records;

/**
 * Represents the properties of a game.
 * @param gameId The unique identifier of the game.
 * @param count The number of border moves.
 * @param interval The interval between border move.
 * @param lobbyCode The lobby code of the game.
 */
public record GameProps(int gameId, int count, int interval, String lobbyCode) {
    /**
     * Returns LobbyCode instance of the lobby code string.
     * @return The unique identifier of the game.
     */
    public LobbyCode getLobbyCode() {
        return new LobbyCode(this.lobbyCode);
    }
}