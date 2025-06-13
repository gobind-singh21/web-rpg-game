package com.rpg_game.game.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rpg_game.game.entity.Character;
import com.rpg_game.game.model.BasicActionRequest;
import com.rpg_game.game.model.CharacterSnapshot;
import com.rpg_game.game.model.SkillActionRequest;
import com.rpg_game.game.repositories.CharacterRepository;
import com.rpg_game.game.model.ActionResponse;
import com.rpg_game.game.services.ActionService;

@RestController
@RequestMapping("/api/test/public/action")
public class ActionRESTController {
  
  @Autowired
  private ActionService actionService;

  @Autowired
  private CharacterRepository characterRepository;

  @PostMapping("/basic")
  public ResponseEntity<ActionResponse> basicAttack(@RequestBody BasicActionRequest basicActionRequest) {
    var currentLineup = basicActionRequest.currentLineup();

    var currentCharacterId = basicActionRequest.currentCharacterId();
    var targetId = basicActionRequest.targetId();
    var currentCharacter = characterRepository.findById(currentCharacterId);
    var target = characterRepository.findById(targetId);
    var currentCharSnap = currentLineup.get(currentCharacterId);
    var targetSnap = currentLineup.get(targetId);
    
    if(!currentCharacter.isPresent() || currentCharSnap == null || currentCharacterId != currentCharSnap.id()) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character not found", currentLineup), HttpStatus.NOT_FOUND);
    }
    
    if(!target.isPresent() || targetSnap == null || targetSnap.id() != targetId) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Target character not found", currentLineup), HttpStatus.NOT_FOUND);
    }

    var currentTeam = currentLineup.get(currentCharacterId).team();
    var targetTeam = currentLineup.get(targetId).team();

    if(currentTeam == null) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Team of current character or target is null", currentLineup), HttpStatus.BAD_REQUEST);
    }

    currentCharacter.get().setCurrentHealth(currentLineup.get(currentCharacterId).currHealth());
    currentCharacter.get().setShield(currentLineup.get(currentCharacterId).shield());
    currentCharacter.get().setEffects(currentLineup.get(currentCharacterId).effects());

    target.get().setCurrentHealth(currentLineup.get(targetId).currHealth());
    target.get().setShield(currentLineup.get(targetId).shield());
    target.get().setEffects(currentLineup.get(targetId).effects());

    var basicResponse = actionService.basicAttack(currentCharacter.get(), target.get());
    if(!basicResponse.validMove()) {
      if(basicResponse.characterDead()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character is already dead", currentLineup), HttpStatus.BAD_REQUEST);
      } else if(basicResponse.targetAlreadyDead()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Target is already dead", currentLineup), HttpStatus.BAD_REQUEST);
      }
    }

    currentLineup.put(currentCharacterId, new CharacterSnapshot(currentCharacterId, currentTeam, currentCharacter.get().getCurrentHealth(), currentCharacter.get().getShield(), currentCharacter.get().getEffects()));
    if(target.get().isDead()) {
      currentLineup.remove(targetId);
    } else {
      currentLineup.put(targetId, new CharacterSnapshot(targetId, targetTeam, target.get().getCurrentHealth(), target.get().getShield(), target.get().getEffects()));
    }

    return new ResponseEntity<ActionResponse>(new ActionResponse(true, basicResponse.message(), currentLineup), HttpStatus.OK);
  }

  @PostMapping("/skill")
  public ResponseEntity<ActionResponse> skill(@RequestBody SkillActionRequest skillActionRequest) {
    var currentLineup = skillActionRequest.currentLineup();
    var currentTeam = skillActionRequest.currentTeam();

    var currentCharacterId = skillActionRequest.currentCharacterId();
    var currentCharacter = characterRepository.findById(currentCharacterId);

    var currentCharSnap = currentLineup.get(currentCharacterId);
    if(!currentCharacter.isPresent() || currentCharSnap == null || currentCharSnap.id() != currentCharacterId) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character not found in database", currentLineup), HttpStatus.NOT_FOUND);
    }

    int position = 0;
    int currentCharacterIndex = -1;

    var lineupList = new ArrayList<Character>();

    for(var character : currentLineup.entrySet()) {
      var foundCharacter = characterRepository.findById(character.getKey());
      var characterSnap = character.getValue();
      if(!foundCharacter.isPresent() || characterSnap == null || characterSnap.id() != character.getKey()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Character with id " + character.getKey() + " not found in database", currentLineup), HttpStatus.NOT_FOUND);
      }
      if(character.getKey() == currentCharacterId)
        currentCharacterIndex = position;
      foundCharacter.get().setCurrentHealth(characterSnap.currHealth());
      foundCharacter.get().setShield(characterSnap.shield());
      foundCharacter.get().setEffects(characterSnap.effects());
      foundCharacter.get().setTeam(characterSnap.team());
      if(!foundCharacter.get().isDead())
        lineupList.add(foundCharacter.get());
      position++;
    }

    if(currentCharacterIndex == -1) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character not found in the turn order", currentLineup), HttpStatus.BAD_REQUEST);
    }

    var response = actionService.skill(currentCharacter.get(), currentCharacterIndex, currentTeam, lineupList);

    if(!response.validMove()) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, response.message(), currentLineup), HttpStatus.BAD_REQUEST);
    }

    currentLineup.clear();
    for(var character : response.lineup()) {
      var characterSnap = new CharacterSnapshot(character.getId(), character.getTeam(), character.getCurrentHealth(), character.getShield(), character.getEffects());
      currentLineup.put(character.getId(), characterSnap);
    }

    return new ResponseEntity<ActionResponse>(new ActionResponse(true, response.message(), currentLineup), HttpStatus.OK);
  }
}

//[3,1,2,4,6]

