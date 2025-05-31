package com.rpg_game.game.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

@Entity
public class Character {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name = "";

  private Integer baseHealth = 1000;

  private Integer baseAttack = 100;
  
  private Integer baseDefense = 100;
  
  private Integer baseSpeed = 100;
  
  private String characterClass = "";
  
  @OneToOne(cascade = CascadeType.ALL)
  private Ability ability = new Ability();

  public Character() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getBaseHealth() {
    return baseHealth;
  }

  public void setBaseHealth(Integer baseHealth) {
    this.baseHealth = baseHealth;
  }

  public Integer getBaseAttack() {
    return baseAttack;
  }

  public void setBaseAttack(Integer baseAttack) {
    this.baseAttack = baseAttack;
  }

  public Integer getBaseDefense() {
    return baseDefense;
  }

  public void setBaseDefense(Integer baseDefense) {
    this.baseDefense = baseDefense;
  }

  public Integer getBaseSpeed() {
    return baseSpeed;
  }

  public void setBaseSpeed(Integer baseSpeed) {
    this.baseSpeed = baseSpeed;
  }

  public String getCharacterClass() {
    return characterClass;
  }

  public void setCharacterClass(String characterClass) {
    this.characterClass = characterClass;
  }

  public Ability getAbility() {
    return ability;
  }

  public void setAbility(Ability ability) {
    this.ability = ability;
  }

  @Transient
  private Integer currentHealth;

  public Integer getCurrentHealth() {
    return currentHealth;
  }

  public void setCurrentHealth(Integer currentHealth) {
    this.currentHealth = currentHealth;
  }

  @Transient
  private List<Effect> effects;

  @Transient
  public List<Effect> getEffects() {
    return effects;
  }

  @Transient
  public void addEffect(Effect effect) {
    effects.add(effect);
  }

  @Transient
  public void setEffects(List<Effect> effects) {
    this.effects = effects;
  }

  @Transient
  public void processTurn() {
    List<Effect> newEffects = new ArrayList<Effect>();
    int n = effects.size();
    for(int i = 0; i < n; i++) {
      effects.get(i).decreaseTurn();
      if(effects.get(i).getTurns() >= 0)
        newEffects.add(effects.get(i));
    }
    this.effects = newEffects;
  }
}
