package me.river.royaltisapi.core.game;

import com.corundumstudio.socketio.SocketIOClient;
import me.river.royaltisapi.core.data.Rank;
import me.river.royaltisapi.core.data.records.Coordinates;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user in the game.
 */
public class User {
    /**
     * The username of the user.
     */
    private String username;

    /**
     * The password of the user (transient to avoid serialization).
     */
    transient private String password;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The unique identifier for the user's socket session.
     */
    private UUID socketSessionId;

    /**
     * The rank of the user in the game.
     */
    private Rank rank;

    /**
     * The SocketIO client associated with the user (transient to avoid serialization).
     */
    transient private SocketIOClient client;

    /**
     * The coordinates of the user in the game.
     */
    private Coordinates coordinates;

    /**
     * Constructs a new User object with the given username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Constructs a new User object with the given SocketIO client and rank.
     *
     * @param client The SocketIO client associated with the user.
     * @param rank   The rank of the user in the game.
     */
    public User(SocketIOClient client, Rank rank) {
        this.rank = rank;
        this.client = client;
    }

    /**
     * Constructs a new User object with the given SocketIO client.
     *
     * @param client The SocketIO client associated with the user.
     */
    public User(SocketIOClient client) {
        this.client = client;
    }

    /**
     * Checks if this User object is equal to another object.
     * Two users are considered equal if they have the same socket session ID.
     *
     * @param o The object to compare to.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(client.getSessionId(), user.getClient().getSessionId());
    }

    /**
     * Returns the hash code of this User object.
     * The hash code is based on the socket session ID.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(socketSessionId);
    }

    /**
     * Gets the SocketIO client associated with this user.
     *
     * @return The SocketIO client.
     */
    public SocketIOClient getClient() {
        return client;
    }

    /**
     * Sets the SocketIO client associated with this user.
     *
     * @param client The SocketIO client to set.
     */
    public void setClient(SocketIOClient client) {
        this.client = client;
    }

    /**
     * Sets the rank of this user.
     *
     * @param rank The rank to set.
     */
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    /**
     * Gets the socket session ID of this user.
     *
     * @return The socket session ID.
     */
    public UUID getSocketSessionId() {
        return client.getSessionId();
    }

    /**
     * Sets the socket session ID of this user.
     *
     * @param socketSessionId The socket session ID to set.
     */
    public void setSocketSessionId(UUID socketSessionId) {
        this.socketSessionId = socketSessionId;
    }

    /**
     * Gets the coordinates of the user.
     *
     * @return The coordinates.
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the coordinates of the user.
     *
     * @param coordinates The coordinates to set.
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Checks if the user is an admin.
     *
     * @return True if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return rank == Rank.ADMIN;
    }

    /**
     * Gets the username of the user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the user.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the email of the user.
     *
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", socketSessionId='" + socketSessionId + '\'' +
                ", rank=" + rank +
                '}';
    }
}
