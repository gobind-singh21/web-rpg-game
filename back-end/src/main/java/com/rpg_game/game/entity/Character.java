package com.rpg_game.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
  private String team = "";

  @JsonProperty("team")
  public String getTeam() {
    return team;
  }

  @JsonProperty("team")
  public void setTeam(String team) {
    this.team = team;
  }

  @Transient
  private Integer shield = 0;

  @JsonProperty("shield")
  public Integer getShield() {
    return shield;
  }

  @JsonProperty("shield")
  public void setShield(Integer shield) {
    this.shield = shield;
  }

  @Transient
  private Integer currentHealth = baseHealth;

  @JsonProperty("currentHealth")
  public int getCurrentHealth() {
    return currentHealth;
  }

  @JsonProperty("currentHealth")
  public void setCurrentHealth(int currentHealth) {
    this.currentHealth = currentHealth;
  }

  @Transient
  private List<Effect> effects = new ArrayList<Effect>();

  @JsonProperty("effects")
  public List<Effect> getEffects() {
    return effects;
  }

  public void addEffect(Effect effect) {
    boolean alreadyApplied = false;
    for(Effect currEffect : this.effects) {
      if(currEffect.getName().equals(effect.getName())) {
        int refreshedTurns = effect.getTurns();
        currEffect.refreshTurn(refreshedTurns);
        alreadyApplied = true;
        break;
      }
    }
    if(!alreadyApplied) {
      Effect newEffect = new Effect();
      newEffect.setName(effect.getName());
      newEffect.setId(effect.getId());
      newEffect.setTurns(effect.getTurns());
      newEffect.setBuff(effect.isBuff());
      newEffect.setAttackPercent(effect.getAttackPercent());
      newEffect.setDefensePercent(effect.getDefensePercent());
      newEffect.setHealthPercent(effect.getHealthPercent());
      newEffect.setSpeedPercent(effect.getSpeedPercent());
      this.effects.add(newEffect);
    }
  }

  @JsonProperty("effects")
  public void setEffects(List<Effect> effects) {
    this.effects = effects;
  }

  public void processTurn() {
    this.effects = effects.stream()
                          .filter(effect -> effect.getTurns() > 0)
                          .collect(Collectors.toCollection(ArrayList::new));
    effects.forEach(effect -> effect.decreaseTurn());
    int maxHealth = this.getMaxHealth();
    if(this.currentHealth > maxHealth) {
      this.currentHealth = maxHealth;
    }
  }

  @JsonIgnore
  public int getMaxHealth() {
    int maxHealth = baseHealth;
    int totalEffects = effects.size();
    for(int i = 0; i < totalEffects; i++) {
      maxHealth += (effects.get(i).isBuff() ? +1 : -1) * (baseHealth * effects.get(i).getHealthPercent()) / 100;
    }
    if (this.currentHealth > Integer.valueOf(maxHealth)) {
      this.currentHealth = maxHealth;
    }
    return maxHealth;
  }

  @JsonIgnore
  public int getMaxAttack() {
    int maxAttack = baseAttack;
    int totalEffects = effects.size();
    for(int i = 0; i < totalEffects; i++) {
      maxAttack += (effects.get(i).isBuff() ? +1 : -1) * (baseAttack * effects.get(i).getAttackPercent()) / 100;
    }
    return maxAttack;
  }

  @JsonIgnore
  public int getMaxDefense() {
    int maxDefense = baseDefense;
    int totalEffects = effects.size();
    for(int i = 0; i < totalEffects; i++) {
      maxDefense += (effects.get(i).isBuff() ? +1 : -1) * (baseDefense * effects.get(i).getDefensePercent()) / 100;
    }
    return maxDefense;
  }

  @JsonIgnore
  public int getMaxSpeed() {
    int maxSpeed = baseSpeed;
    int totalEffects = effects.size();
    for(int i = 0; i < totalEffects; i++) {
      maxSpeed += (effects.get(i).isBuff() ? +1 : -1) * (baseSpeed * effects.get(i).getSpeedPercent()) / 100;
    }
    return maxSpeed;
  }

  public void reduceHealth(int reduction) {
    currentHealth = Math.max(1, currentHealth - reduction);
  }

  public void receiveDamage(int incomingDamage) {
    int maxDefense = this.getMaxDefense();
    double scaling = (double) incomingDamage / (incomingDamage + maxDefense * 3.0);
    int receivedDamage = (int) Math.round(incomingDamage * scaling);
    this.currentHealth += Math.min(0, this.shield - receivedDamage);
    this.shield = Math.max(0, this.shield - receivedDamage);
    this.currentHealth = Math.max(this.currentHealth, 0);
  }

  public void receivehealing(int healing) {
    int maxhealth = this.getMaxHealth();
    currentHealth = Math.min(maxhealth, currentHealth + healing);
  }

  public void receiveShield(int shield)  {
    this.shield = shield;
  }

  @JsonIgnore
  public boolean isDead() {
    return currentHealth == 0;
  }

  @Override
  public int compareTo(Character o) {
    return Integer.compare(o.getMaxSpeed(), this.getMaxSpeed());
  }

  @Override
  @JsonIgnore
  public boolean equals(Object o) {
    if(this == o)
      return true;
    if(o == null || o.getClass() != Character.class) {
      return false;
    }
    Character other = (Character) o;
    return Objects.equals(this.id, other.id);
  }

  @Override
  @JsonIgnore
  public int hashCode() {
    return Objects.hash(id);
  }
}
