package com.rpg_game.game.model;

public class SignupResponse {
    
    private Integer id;
    private String username;
    private String email;
    private String jwtToken;

    public SignupResponse(Integer id, String username, String email, String jwtToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.jwtToken = jwtToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    
}
