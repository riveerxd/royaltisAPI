package me.river.royaltisapi.core;

import java.util.Objects;
import java.util.UUID;

public class User {
    private String username;
    private String password;
    private String email;
    private UUID socketSessionId;
    private Rank rank;
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(UUID socketSessionId, Rank rank) {
        this.socketSessionId = socketSessionId;
        this.rank = rank;
    }

    public User(UUID socketSessionId) {
        this.socketSessionId = socketSessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(socketSessionId, user.socketSessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socketSessionId);
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void setSocketSessionId(UUID socketSessionId) {
        this.socketSessionId = socketSessionId;
    }

    public UUID getSocketSessionId() {
        return socketSessionId;
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

