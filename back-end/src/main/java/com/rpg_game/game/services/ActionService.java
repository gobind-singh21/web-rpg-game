package com.rpg_game.game.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rpg_game.game.entity.Character;
import com.rpg_game.game.model.ActionServiceResponse;
import com.rpg_game.game.types.Stat;

@Service
public class ActionService {
  
  public ActionServiceResponse basicAttack(Character currentCharacter, int target, List<Character> enemies) {
    if(target < 0 || target >= enemies.size())
      return new ActionServiceResponse(false, true, false, false, false, "Invalid target");
    
    if(currentCharacter.isDead())
      return new ActionServiceResponse(false, false, false, true, false, "Current character is already dead");

    Character enemy = enemies.get(target);
    if(enemy.isDead())
      return new ActionServiceResponse(false, false, true, false, false, "Selected target is already dead");

    enemy.receiveDamage(currentCharacter.getMaxAttack());
    return new ActionServiceResponse(true, false, false, false, false, "Enemy " + (target + 1) + " received basic attack");
  }

  public ActionServiceResponse skill(Character currentCharacter, List<Character> allies, List<Character> enemies) {
    var ability = currentCharacter.getAbility();
    int outputDamage = ability.getScale();
    if(outputDamage != 0) {
      switch(ability.getStat()) {
        case Stat.MAX_HP :
          outputDamage *= currentCharacter.getMaxHealth();
          break;
        case Stat.HP :
          outputDamage *= currentCharacter.getBaseHealth();
          break;
        case Stat.ATK :
          outputDamage *= currentCharacter.getMaxAttack();
          break;
        case Stat.DEF :
          outputDamage *= currentCharacter.getMaxDefense();
          break;
        default:
          return new ActionServiceResponse(false, false, false, false, true, "Invalid stat");
      }
      for(var enemy : enemies) {
        enemy.receiveDamage(outputDamage);
      }
    }
    var effects = ability.getEffects();
    for(var effect : effects) {
      if(effect.isBuff()) {
        for(var ally : allies) {
          if(!ally.isDead())
            ally.addEffect(effect);
        }
      } else {
        for(var enemy : enemies) {
          if(!enemy.isDead())
            enemy.addEffect(effect);
        }
      }
    }

    return new ActionServiceResponse(true, false, false, false, false, "Skill used");
  }

}
