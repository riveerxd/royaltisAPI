package me.river.royaltisapi.core.game;

import com.corundumstudio.socketio.SocketIOClient;
import me.river.royaltisapi.core.data.Rank;

import java.util.Objects;
import java.util.UUID;

public class User {
    private String username;
    transient private String password;
    private String email;
    private UUID socketSessionId;
    private Rank rank;
    transient private SocketIOClient client;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(SocketIOClient client, Rank rank) {
        this.rank = rank;
        this.client = client;
    }

    public User(SocketIOClient client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(client.getSessionId(), user.getClient().getSessionId());
    }

    public SocketIOClient getClient() {
        return client;
    }

    @Override
    public int hashCode() {
        return Objects.hash(socketSessionId);
    }

    public void setClient(SocketIOClient client) {
        this.client = client;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void setSocketSessionId(UUID socketSessionId) {
        this.socketSessionId = socketSessionId;
    }

    public UUID getSocketSessionId() {
        return client.getSessionId();
    }

    public boolean isAdmin(){
        return rank == Rank.ADMIN;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

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

