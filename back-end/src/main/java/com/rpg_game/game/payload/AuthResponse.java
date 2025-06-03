package com.rpg_game.game.payload;

public class AuthResponse {
    private String username;
    private String token;
    private String type = "Bearer"; 

    
    private Integer playerId;

    /**
     * All-argument constructor
     * 
     * @param username
     * @param token
     * @param playerId
     */
    public AuthResponse(String username, String token, Integer playerId) {
        this.username = username;
        this.token = token;
        this.playerId = playerId;
    }

    /**
     * Constructor if you want to set type explicitly or omit playerId
     * 
     * @param username
     * @param token
     * @param type
     * @param playerId
     */
    public AuthResponse(String username, String token, String type, Integer playerId) {
        this.username = username;
        this.token = token;
        this.type = type;
        this.playerId = playerId;
    }


    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
    

}