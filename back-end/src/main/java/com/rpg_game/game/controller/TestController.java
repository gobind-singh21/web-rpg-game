package com.rpg_game.game.controller;

import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.context.SecurityContextHolder; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test") 
public class TestController {

    /**
     * An endpoint accessible by any authenticated user.
     */
    @GetMapping("/user")
    public String userAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); 

        return "Hello " + username + "! You have User access.";
    }

    /**
     * An endpoint that does not require any authentication (public access).
     */
    @GetMapping("/public")
    public String publicAccess() {
        return "This is a public endpoint. Anyone can access it.";
    }
}