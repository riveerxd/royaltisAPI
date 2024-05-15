package me.river.royaltisapi.core.data.records;

/**
 * Represents a lobby code.
 * @param lobbyCode
 */
public record LobbyCode(String lobbyCode) {
    /**
     * @return The lobby code.
     */
    @Override
    public String toString() {
        return this.lobbyCode;
    }
}
