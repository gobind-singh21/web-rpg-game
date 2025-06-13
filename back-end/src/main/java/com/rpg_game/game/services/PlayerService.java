package com.rpg_game.game.services;

import java.util.Optional;

import com.rpg_game.game.entity.Player;
import com.rpg_game.game.model.ChangePasswordRequest;
import com.rpg_game.game.model.SignupRequest;
import com.rpg_game.game.model.SignupResponse;

public interface PlayerService {
    /**
     * Registers new Player in the system.
     * This method handles the password hashing and saving the player.
     * 
     * @param signupRequest has player registration details.
     * @return The newly created Player entity.
     * @throws RuntimeException if username or email already exists.
     */
    SignupResponse signupPlayer(SignupRequest signupRequest);

    /**
     * Finds a player by their username.
     * 
     * @param username The username of the player to find.
     * @return An Optional containing the Player if found, else empty.
     */
    Optional<Player> findByUsername(String username);

    /**
     * Finds a player by their email.
     * 
     * @param email The email of the player to find.
     * @return An Optional containing the Player if found, empty otherwise.
     */
    Optional<Player> findByEmail(String email);

    /**
     * Checks if the username already exists in the system.
     * 
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if the email already exits in the system.
     * 
     * @param email The email to check.
     * @return true if the email exists, else false.
     */
    boolean existsByEmail(String email);

    /**
     * To save the changes in the player entity.
     * 
     * @param player The player that needs to be updated.
     * @return Save and return new details in the already existing player.
     */
    Player savePlayer(Player player);

    /**
     * To change the password of the current user.
     * 
     * @param username The username who wants to change his password.
     * @param request The changePasswordDTO 
     */
    void changePassword(String username, ChangePasswordRequest request);
}
