package com.rpg_game.game.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  private String username;

  private String passwordDigest;

  @ManyToMany
  private List<Character> characters = new ArrayList<Character>();

  public Player() {}

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

  public String getPasswordDigest() {
    return passwordDigest;
  }

  public void setPasswordDigest(String passwordDigest) {
    this.passwordDigest = passwordDigest;
  }

  public List<Character> getCharacters() {
    return characters;
  }

  public void setCharacters(List<Character> characters) {
    this.characters = characters;
  }
}
