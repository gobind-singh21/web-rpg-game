package com.rpg_game.game.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;

  private String username;

  private String passwordDigest;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) 
  @JoinTable(
      name = "player_characters", 
      joinColumns = @JoinColumn(name = "player_id"), 
      inverseJoinColumns = @JoinColumn(name = "character_id") 
  )
  private Set<Character> characters = new HashSet<>();

  public Player() {}

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setId(Long id) {
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

  public Set<Character> getCharacters() { 
    return characters;
  }

  public void setCharacters(Set<Character> characters) { 
    this.characters = characters;
  }

  public void addCharacter(Character character) {
    this.characters.add(character);
    character.getPlayers().add(this); 
  }

  public void removeCharacter(Character character) {
      this.characters.remove(character);
      character.getPlayers().remove(this); 
  }
}
