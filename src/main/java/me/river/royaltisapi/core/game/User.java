package me.river.royaltisapi.core.game;

import com.corundumstudio.socketio.SocketIOClient;
import me.river.royaltisapi.core.data.Rank;
import me.river.royaltisapi.core.data.records.Coordinates;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user.
 */
public class User {
    /**
     * The username.
     */
    private String username;
    /**
     * The password.
     */
    transient private String password;
    /**
     * The email.
     */
    private String email;
    /**
     * The socket session id.
     */
    private UUID socketSessionId;
    /**
     * The rank.
     */
    private Rank rank;
    /**
     * The SocketIO client.
     */
    transient private SocketIOClient client;

    /**
     * The user coordinates
     */
    private Coordinates coordinates;

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param password the password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Instantiates a new User.
     *
     * @param client the client
     * @param rank the rank
     */
    public User(SocketIOClient client, Rank rank) {
        this.rank = rank;
        this.client = client;
    }

    /**
     * Instantiates a new User.
     *
     * @param client the client
     */
    public User(SocketIOClient client) {
        this.client = client;
    }

    /**
     * User equals.
     * @param o the object
     * @return equals or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(client.getSessionId(), user.getClient().getSessionId());
    }

    /**
     * Gets client.
     * @return the client
     */
    public SocketIOClient getClient() {
        return client;
    }

    /**
     * User Hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(socketSessionId);
    }

    /**
     * Sets client.
     *
     * @param client the client
     */
    public void setClient(SocketIOClient client) {
        this.client = client;
    }

    /**
     * Sets rank.
     *
     * @param rank the rank
     */
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    /**
     * Sets socket session id.
     * @param socketSessionId the socket session id
     */
    public void setSocketSessionId(UUID socketSessionId) {
        this.socketSessionId = socketSessionId;
    }

    /**
     * Gets socket session id.
     * @return the socket session id
     */
    public UUID getSocketSessionId() {
        return client.getSessionId();
    }

    /**
     * Gets user coordinates
     * @return the user coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Sets user coordinates
     * @param coordinates the coordinates
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Is admin boolean.
     *
     * @return if the user is an admin or not
     */
    public boolean isAdmin(){
        return rank == Rank.ADMIN;
    }

    /**
     * Gets username.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * User to string.
     * @return the string
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

