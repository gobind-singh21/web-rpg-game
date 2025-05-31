package com.rpg_game.game.entity;

import java.util.ArrayList;
import java.util.List;

import com.rpg_game.game.types.Stat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Ability {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name = "";

  private Integer scale = 100;

  private Stat stat = Stat.ATK;

  private Integer shield = 0;

  private Integer heal = 0;

  @ManyToMany(cascade = CascadeType.ALL)
  private List<Effect> effects = new ArrayList<Effect>();

  public Ability() {}

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

  public Integer getScale() {
    return scale;
  }

  public void setScale(Integer scale) {
    this.scale = scale;
  }

  public Stat getStat() {
    return stat;
  }

  public void setStat(Stat stat) {
    this.stat = stat;
  }

  public Integer getShield() {
    return shield;
  }

  public void setShield(Integer shield) {
    this.shield = shield;
  }

  public Integer getHeal() {
    return heal;
  }

  public void setHeal(Integer heal) {
    this.heal = heal;
  }

  public List<Effect> getEffects() {
    return effects;
  }

  public void setEffects(List<Effect> effects) {
    this.effects = effects;
  }

  public void addEffect(Effect effect) {
    this.effects.add(effect);
  }

  public void removeEffect(Effect effect) {
    this.effects.remove(effect);
  }
}
