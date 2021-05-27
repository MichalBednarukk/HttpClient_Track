package pl.bednaruk.httpclient.models;

import java.util.List;

public class User {
    private int id;
    private String username;
    private String password;
    private String token;
    private List<Track> favouritesTrack;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Track> getFavouritesTrack() {
        return favouritesTrack;
    }

    public void setFavouritesTrack(List<Track> favouritesTrack) {
        this.favouritesTrack = favouritesTrack;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
