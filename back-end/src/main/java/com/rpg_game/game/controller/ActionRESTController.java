package com.rpg_game.game.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rpg_game.game.entity.Character;
import com.rpg_game.game.model.BasicActionRequest;
import com.rpg_game.game.model.BattleStartRequest;
import com.rpg_game.game.model.BattleStartResponse;
import com.rpg_game.game.model.CharacterSnapshot;
import com.rpg_game.game.model.SkillActionRequest;
import com.rpg_game.game.repositories.CharacterRepository;
import com.rpg_game.game.model.ActionResponse;
import com.rpg_game.game.services.ActionService;

@RestController
@RequestMapping("/api/action")
public class ActionRESTController {
  
  @Autowired
  private ActionService actionService;

  @Autowired
  private CharacterRepository characterRepository;

  private int findIndexOfId(List<CharacterSnapshot> snapshots, int id) {
    int totalSnapshots = snapshots.size();
    for(int i = 0; i < totalSnapshots; i++) {
      if(snapshots.get(i).id() == id)
        return i;
    }
    return -1;
  }

  private CharacterSnapshot buildSnapshotFromCharacter(Character character) {
    return new CharacterSnapshot(
                  character.getId(),
                  character.getTeam(),
                  character.getCurrentHealth(),
                  character.getShield(),
                  character.getEffects()
                );
  }

  private Character buildCharacterFromSnap(CharacterSnapshot snapshot) {
    if(snapshot == null)
      return null;
    var character = characterRepository.findById(snapshot.id());
    if(!character.isPresent())
      return null;
    var foundCharacter = character.get();
    foundCharacter.setCurrentHealth(snapshot.currentHealth());
    foundCharacter.setEffects(snapshot.effects());
    foundCharacter.setShield(snapshot.shield());
    foundCharacter.setTeam(snapshot.team());
    return foundCharacter;
  }

  @PostMapping("/battle-start")
  public ResponseEntity<BattleStartResponse> battleStart(@RequestBody BattleStartRequest battleStartRequest) {
    var characters = new ArrayList<Character>();
    for(var id : battleStartRequest.characterIds()) {
      var character = characterRepository.findById(id);
      if(!character.isPresent()) {
        return new ResponseEntity<BattleStartResponse>(new BattleStartResponse(new ArrayList<Integer>()), HttpStatus.NOT_FOUND);
      }
      var foundCharacter = character.get();
      characters.add(foundCharacter);
    }
    return new ResponseEntity<BattleStartResponse>(actionService.battleStart(characters), HttpStatus.OK);
  }

  @PostMapping("/basic")
  public ResponseEntity<ActionResponse> basicAttack(@RequestBody BasicActionRequest basicActionRequest) {
    var currentLineup = basicActionRequest.currentLineup();

    var currentCharacterId = basicActionRequest.currentCharacterId();
    int currentCharacterIndex = findIndexOfId(currentLineup, currentCharacterId);
    if(currentCharacterIndex == -1) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character not found in turn order", currentLineup), HttpStatus.BAD_REQUEST);
    }
    var currentCharSnap = currentLineup.get(currentCharacterIndex);
    if(currentCharSnap == null) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character snap is null", currentLineup), HttpStatus.BAD_REQUEST);
    }
    
    var targetId = basicActionRequest.targetId();
    int targetIndex = findIndexOfId(currentLineup, targetId);
    if(targetIndex == -1) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Target character not found in turn order", currentLineup), HttpStatus.BAD_REQUEST);
    }
    var targetSnap = currentLineup.get(targetIndex);
    var currentCharacter = buildCharacterFromSnap(currentCharSnap);
    var target = buildCharacterFromSnap(targetSnap);

    if(currentCharacter == null || currentCharacterId != currentCharSnap.id()) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character not found", currentLineup), HttpStatus.NOT_FOUND);
    }
    
    if(target == null || targetSnap == null || targetSnap.id() != targetId) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Target character not found", currentLineup), HttpStatus.NOT_FOUND);
    }

    var currentTeam = currentLineup.get(currentCharacterIndex).team();

    if(currentTeam == null) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Team of current character or target is null", currentLineup), HttpStatus.BAD_REQUEST);
    }

    var basicResponse = actionService.basicAttack(currentCharacter, target);
    if(!basicResponse.validMove()) {
      if(basicResponse.characterDead()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character is already dead", currentLineup), HttpStatus.BAD_REQUEST);
      } else if(basicResponse.targetAlreadyDead()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Target is already dead", currentLineup), HttpStatus.BAD_REQUEST);
      }
    }
    
    currentCharSnap = buildSnapshotFromCharacter(currentCharacter);
    targetSnap = buildSnapshotFromCharacter(target);
    currentLineup.set(targetIndex, targetSnap);

    return new ResponseEntity<ActionResponse>(new ActionResponse(true, basicResponse.message(), currentLineup), HttpStatus.OK);
  }

  @PostMapping("/skill")
  public ResponseEntity<ActionResponse> skill(@RequestBody SkillActionRequest skillActionRequest) {
    var currentLineup = skillActionRequest.currentLineup();
    var currentTeam = skillActionRequest.currentTeam();

    var currentCharacterId = skillActionRequest.currentCharacterId();
    int currentCharacterIndex = findIndexOfId(currentLineup, currentCharacterId);
    if(currentCharacterIndex == -1) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character not found in the turn order", currentLineup), HttpStatus.BAD_REQUEST);
    }
    var currentCharSnap = currentLineup.get(currentCharacterIndex);
    var currentCharacter = buildCharacterFromSnap(currentCharSnap);

    if(currentCharSnap == null) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character snap is null", currentLineup), HttpStatus.BAD_REQUEST);
    }
    if(currentCharacter == null || currentCharSnap.id() != currentCharacterId) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Current character not found in database", currentLineup), HttpStatus.NOT_FOUND);
    }

    var lineupList = new ArrayList<Character>();

    for(var character : currentLineup) {
      var foundCharacter = buildCharacterFromSnap(character);
      if(foundCharacter == null || character == null || character.id() != character.id()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Character with id " + character.id() + " not found in database", currentLineup), HttpStatus.NOT_FOUND);
      }
      if(!foundCharacter.isDead())
        lineupList.add(foundCharacter);
    }

    var response = actionService.skill(currentCharacter, currentCharacterIndex, currentTeam, lineupList);

    if(!response.validMove()) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, response.message(), currentLineup), HttpStatus.BAD_REQUEST);
    }

    var newLineup = new ArrayList<CharacterSnapshot>();
    for(var character : lineupList) {
      newLineup.add(buildSnapshotFromCharacter(character));
    }

    return new ResponseEntity<ActionResponse>(new ActionResponse(true, response.message(), newLineup), HttpStatus.OK);
  }
}
