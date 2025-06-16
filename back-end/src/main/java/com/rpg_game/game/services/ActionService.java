package com.rpg_game.game.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rpg_game.game.entity.Character;
import com.rpg_game.game.model.BasicActionServiceResponse;
import com.rpg_game.game.model.BattleStartResponse;
import com.rpg_game.game.model.SkillActionServiceResponse;
import com.rpg_game.game.types.Stat;

@Service
public class ActionService {

  public BattleStartResponse battleStart(List<Character> characters) {
    Collections.sort(characters);
    var sortedIds = characters.stream()
              .map(character -> character.getId())
              .collect(Collectors.toCollection(ArrayList::new));
    return new BattleStartResponse(sortedIds);
  }
  
  public BasicActionServiceResponse basicAttack(Character currentCharacter, Character target) {
    if(currentCharacter.isDead()) {
      return new BasicActionServiceResponse(false, false, false, true, false, false, "Current character already dead");
    }
    if(target.isDead()) {
      return new BasicActionServiceResponse(false, false, true, false, false, false, "Target already dead");
    }
    currentCharacter.processTurn();
    target.receiveDamage(currentCharacter.getMaxAttack());
    return new BasicActionServiceResponse(true, false, false, false, false, false, currentCharacter.getName() + " landed basic attack on " + target.getName());
  }

  public SkillActionServiceResponse skill(Character currentCharacter, int currentCharacterIndex, String currentTeam, List<Character> lineup) {
    if(currentCharacter.isDead()) {
      return new SkillActionServiceResponse(false, "Current character already dead", lineup);
    }
    currentCharacter.processTurn();
    var ability = currentCharacter.getAbility();
    var effects = ability.getEffects();

    int outputDamage = ability.getScale();
    switch(ability.getStat()) {
      case Stat.ATK:
        outputDamage = (outputDamage * currentCharacter.getMaxAttack()) / 100;
        break;
      case Stat.DEF:
        outputDamage = (outputDamage * currentCharacter.getMaxDefense()) / 100;
        break;
      case Stat.MAX_HP:
        outputDamage = (outputDamage * currentCharacter.getMaxHealth()) / 100;
        break;
      case Stat.HP:
        outputDamage = (outputDamage * currentCharacter.getBaseHealth()) / 100;
        break;
      default:
        return new SkillActionServiceResponse(false, "Invalid stat for damage", lineup);
    }

    for(var character : lineup) {
      if(character.getTeam().equals(currentTeam)) {
        character.receivehealing(ability.getHeal());
        character.setShield(ability.getShield());
        for(var effect : effects) {
          if(effect.getName().equals("Cleansing Light")) {
            character.getEffects().removeIf(existingEffect -> !existingEffect.isBuff());
          }
          else if(effect.isBuff())
            character.addEffect(effect);
        }
      } else {
        character.receiveDamage(outputDamage);
        if(!character.isDead()) {
          for(var effect : effects) {
            if(!effect.isBuff())
              character.addEffect(effect);
          }
        }
      }
    }

    if(currentCharacterIndex < lineup.size() - 1) {
      Collections.sort(lineup.subList(currentCharacterIndex + 1, lineup.size()));
    } else {
      Collections.sort(lineup);
    }

    return new SkillActionServiceResponse(true, "Skill used by " + currentCharacter.getName(), lineup);
  }

}
