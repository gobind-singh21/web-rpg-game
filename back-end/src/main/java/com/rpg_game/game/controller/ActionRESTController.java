package com.rpg_game.game.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rpg_game.game.model.BasicActionRequest;
import com.rpg_game.game.model.SkillActionRequest;
import com.rpg_game.game.model.ActionResponse;
import com.rpg_game.game.services.ActionService;

@RestController
@RequestMapping("/action")
public class ActionRESTController {
  
  @Autowired
  private ActionService actionService;

  @PostMapping("/basic")
  public ResponseEntity<ActionResponse> basicAttack(@RequestBody BasicActionRequest basicActionRequest) {
    var currentLineup = basicActionRequest.currentLineup();
    
    if(basicActionRequest.currentCharacterIndex() < 0 || basicActionRequest.currentCharacterIndex() >= basicActionRequest.currentLineup().size()) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Invalid current character", currentLineup), HttpStatus.BAD_REQUEST);
    }

    var currentCharacter = basicActionRequest.currentLineup().get(basicActionRequest.currentCharacterIndex());
    var basicResponse = actionService.basicAttack(currentCharacter, basicActionRequest.targetIndex(), basicActionRequest.enemies());
    if(!basicResponse.validMove()) {
      if(basicResponse.inValidTarget()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Invalid target", currentLineup), HttpStatus.BAD_REQUEST);
      } else if(basicResponse.invalidStat()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Invalid stat", currentLineup), HttpStatus.BAD_REQUEST);
      } else if(basicResponse.targetAlreadyDead()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Target is already dead", currentLineup), HttpStatus.BAD_REQUEST);
      }
    }

    if(basicActionRequest.currentCharacterIndex() < currentLineup.size() - 1)
      Collections.sort(currentLineup.subList(basicActionRequest.currentCharacterIndex() + 1, currentLineup.size() - 1));
    else
      Collections.sort(currentLineup);
    
    return new ResponseEntity<ActionResponse>(new ActionResponse(true, currentCharacter.getName() + " landed basic attack on " + basicActionRequest.enemies().get(basicActionRequest.targetIndex()).getName(), currentLineup), HttpStatus.OK);
  }

  @PostMapping("/skill")
  public ResponseEntity<ActionResponse> skill(@RequestBody SkillActionRequest skillActionRequest) {
    var currentIndex = skillActionRequest.currentCharacterIndex();
    var currentLineup = skillActionRequest.currentLineup();
    if(currentIndex < 0 || currentIndex >= skillActionRequest.currentLineup().size()) {
      return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Invalid current character", currentLineup), HttpStatus.BAD_REQUEST);
    }

    var currentCharacter = currentLineup.get(currentIndex);

    var response = actionService.skill(currentCharacter, skillActionRequest.allies(), skillActionRequest.enemies());

    if(!response.validMove()) {
      if(response.inValidTarget()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Invalid target", currentLineup), HttpStatus.BAD_REQUEST);
      } else if(response.invalidStat()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Invalid stat", currentLineup), HttpStatus.BAD_REQUEST);
      } else if(response.targetAlreadyDead()) {
        return new ResponseEntity<ActionResponse>(new ActionResponse(false, "Target is already dead", currentLineup), HttpStatus.BAD_REQUEST);
      }
    }

    if(currentIndex < currentLineup.size() - 1)
      Collections.sort(currentLineup.subList(currentIndex + 1, currentLineup.size() - 1));
    else
      Collections.sort(currentLineup);

    return new ResponseEntity<ActionResponse>(new ActionResponse(true, "Skill used", currentLineup), HttpStatus.OK);
  }
}
