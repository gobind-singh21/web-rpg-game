package com.rpg_game.game.entity;

import java.util.List;

import com.rpg_game.game.types.CharacterClass;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

@Entity
public class Character implements Comparable<Character> {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name = "";

  private Integer baseHealth = 1000;

  private Integer baseAttack = 100;
  
  private Integer baseDefense = 100;
  
  private Integer baseSpeed = 100;
  
  private CharacterClass characterClass = CharacterClass.Ranger;
  
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
    this.currentHealth = baseHealth;
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

  public CharacterClass getCharacterClass() {
    return characterClass;
  }

  public void setCharacterClass(CharacterClass characterClass) {
    this.characterClass = characterClass;
  }

  public Ability getAbility() {
    return ability;
  }

  public void setAbility(Ability ability) {
    this.ability = ability;
  }

  @Transient
  private Integer shield = 0;

  @Transient
  private Integer currentHealth = baseHealth;

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
    this.effects = effects.stream()
                          .filter(effect -> effect.getTurns() > 0)
                          .toList();
    effects.forEach(effect -> effect.decreaseTurn());
  }

  @Transient
  public int getMaxHealth() {
    int maxHealth = 0;
    int totalEffects = effects.size();
    for(int i = 0; i < totalEffects; i++) {
      maxHealth += (effects.get(i).isBuff() ? +1 : -1) * (baseHealth * effects.get(i).getHealthPercent()) / 100;
    }
    return maxHealth;
  }

  @Transient
  public int getMaxAttack() {
    int maxAttack = 0;
    int totalEffects = effects.size();
    for(int i = 0; i < totalEffects; i++) {
      maxAttack += (effects.get(i).isBuff() ? +1 : -1) * (baseAttack * effects.get(i).getAttackPercent()) / 100;
    }
    return maxAttack;
  }

  @Transient
  public int getMaxDefense() {
    int maxDefense = 0;
    int totalEffects = effects.size();
    for(int i = 0; i < totalEffects; i++) {
      maxDefense += (effects.get(i).isBuff() ? +1 : -1) * (baseDefense * effects.get(i).getDefensePercent()) / 100;
    }
    return maxDefense;
  }

  @Transient
  public int getMaxSpeed() {
    int maxSpeed = 0;
    int totalEffects = effects.size();
    for(int i = 0; i < totalEffects; i++) {
      maxSpeed += (effects.get(i).isBuff() ? +1 : -1) * (baseSpeed * effects.get(i).getSpeedPercent()) / 100;
    }
    return maxSpeed;
  }

  @Transient
  public void reduceHealth(int reduction) {
    currentHealth = Math.max(1, currentHealth - reduction);
  }

  @Transient
  public void receiveDamage(int incomingDamage) {
    int maxDefense = this.getMaxDefense();
    double scaling = (double) incomingDamage / (incomingDamage + maxDefense * 3.0);
    int receivedDamage = (int) Math.round(incomingDamage * scaling);
    this.currentHealth += Math.min(0, this.shield - receivedDamage);
    this.shield = Math.max(0, this.shield - receivedDamage);
    this.currentHealth = Math.max(this.currentHealth, 0);
  }

  @Transient
  public void receivehealing(int healing) {
    int maxhealth = this.getMaxHealth();
    currentHealth = Math.min(maxhealth, currentHealth + healing);
  }

  @Transient
  public void receiveShield(int shield)  {
    this.shield = shield;
  }

  @Override
  public int compareTo(Character o) {
    return Integer.compare(o.getMaxSpeed(), this.getMaxSpeed());
  }
}
