package com.rpg_game.game.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Effect {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private Integer healthPercent;

  private Integer attackPercent;

  private Integer defensePercent;

  private Integer speedPercent;

  @Transient
  private Integer turns;

  @Transient
  public Integer getTurns() {
    return turns;
  }

  @Transient
  public void setTurns(Integer turns) {
    this.turns = turns;
  }

  @Transient
  public void decreaseTurn() {
    this.turns--;
  }

  @Transient
  public void refreshTurn(int turns) {
    this.turns = turns;
  }

  private boolean isBuff;

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

  public Integer getHealthPercent() {
    return healthPercent;
  }

  public void setHealthPercent(Integer healthPercent) {
    this.healthPercent = healthPercent;
  }

  public Integer getAttackPercent() {
    return attackPercent;
  }

  public void setAttackPercent(Integer attackPercent) {
    this.attackPercent = attackPercent;
  }

  public Integer getDefensePercent() {
    return defensePercent;
  }

  public void setDefensePercent(Integer defensePercent) {
    this.defensePercent = defensePercent;
  }

  public Integer getSpeedPercent() {
    return speedPercent;
  }

  public void setSpeedPercent(Integer speedPercent) {
    this.speedPercent = speedPercent;
  }

  public boolean isBuff() {
    return isBuff;
  }

  public void setBuff(boolean isBuff) {
    this.isBuff = isBuff;
  }
}
