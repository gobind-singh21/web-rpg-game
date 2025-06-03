package com.rpg_game.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg_game.game.entity.Character;
import com.rpg_game.game.model.BasicActionRequest;
import com.rpg_game.game.model.SkillActionRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort; // Use web.server for newer Spring Boot versions

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActionRESTControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper; // Still useful for converting objects to JSON string

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    // Helper method to create mock characters for testing
    private Character createMockCharacter(Integer id, String name, Integer health, Integer attack, Integer speed) {
        Character character = new Character();
        character.setId(id);
        character.setName(name);
        character.setBaseHealth(health); // This also sets currentHealth
        character.setBaseAttack(attack);
        character.setBaseSpeed(speed);
        character.setEffects(new ArrayList<>()); // Initialize transient effects list
        return character;
    }

    // --- Basic Attack Endpoint Tests ---

    @Test
    @DisplayName("POST /api/test/public/action/basic - Should return OK for a successful basic attack with multiple characters")
    void basicAttack_Success() throws Exception {
        // At least 4 characters used: attacker, target, and two other characters in lineup/enemies
        Character attacker = createMockCharacter(1, "Hero", 100, 50, 10);
        Character target = createMockCharacter(2, "Goblin", 80, 30, 8);
        Character ally1 = createMockCharacter(3, "Healer", 90, 20, 12);
        Character enemy1 = createMockCharacter(4, "Orc", 120, 60, 7);

        List<Character> lineup = new ArrayList<>(Arrays.asList(attacker, ally1, target, enemy1)); // 4 characters
        List<Character> enemies = new ArrayList<>(Arrays.asList(target, enemy1)); // 2 enemies

        BasicActionRequest request = new BasicActionRequest(0, 0, lineup, enemies, lineup);

        // Mock service response for a successful attack
        // ActionServiceResponse serviceResponse = new ActionServiceResponse(true, false, false, false, false, "Attack successful");
        // when(actionService.basicAttack(any(Character.class), anyInt(), anyList())).thenReturn(serviceResponse);

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/basic")
        .then()
            .statusCode(200)
            .body("validMove", is(true))
            .body("message", equalTo("Hero landed basic attack on Goblin"))
            .body("newLineup", is(notNullValue()))
            .body("newLineup", hasSize(4)); // Verify newLineup is present and has the correct size
        
        // verify(actionService, times(1)).basicAttack(attacker, 0, enemies);
    }

    @Test
    @DisplayName("POST /api/test/public/action/basic - Should return BAD_REQUEST if current character index is invalid with multiple characters")
    void basicAttack_InvalidCharacterIndex() throws Exception {
        // At least 4 characters used in the scenario
        Character char1 = createMockCharacter(1, "Hero", 100, 50, 10);
        Character char2 = createMockCharacter(2, "Goblin", 80, 30, 8);
        Character char3 = createMockCharacter(3, "Healer", 90, 20, 12);
        Character char4 = createMockCharacter(4, "Orc", 120, 60, 7);

        List<Character> lineup = new ArrayList<>(Arrays.asList(char1, char2, char3, char4)); // 4 characters
        List<Character> enemies = new ArrayList<>(Arrays.asList(char2, char4));

        BasicActionRequest request = new BasicActionRequest(-1, 0, lineup, enemies, lineup); // Invalid index

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/basic")
        .then()
            .statusCode(400)
            .body("validMove", is(false))
            .body("message", equalTo("Invalid current character"));
        
        // verifyNoInteractions(actionService); // Service should not be called
    }

    @Test
    @DisplayName("POST /api/test/public/action/basic - Should return BAD_REQUEST if service indicates invalid target with multiple characters")
    void basicAttack_ServiceInvalidTarget() throws Exception {
        // At least 4 characters used
        Character attacker = createMockCharacter(1, "Hero", 100, 50, 10);
        Character target = createMockCharacter(2, "Goblin", 80, 30, 8);
        Character ally1 = createMockCharacter(3, "Healer", 90, 20, 12);
        Character enemy1 = createMockCharacter(4, "Orc", 120, 60, 7);

        List<Character> lineup = new ArrayList<>(Arrays.asList(attacker, ally1, target, enemy1));
        List<Character> enemies = new ArrayList<>(Arrays.asList(target, enemy1));

        BasicActionRequest request = new BasicActionRequest(0, 99, lineup, enemies, lineup); // Target index could be valid in request but service invalidates

        // Mock service response for an invalid target
        // ActionServiceResponse serviceResponse = new ActionServiceResponse(false, true, false, false, false, "Target out of range");
        // when(actionService.basicAttack(any(Character.class), anyInt(), anyList())).thenReturn(serviceResponse);

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/basic")
        .then()
            .statusCode(400)
            .body("validMove", is(false))
            .body("message", equalTo("Invalid target"));
    }

    @Test
    @DisplayName("POST /api/test/public/action/basic - Should return BAD_REQUEST if service indicates target is already dead with multiple characters")
    void basicAttack_ServiceTargetAlreadyDead() throws Exception {
        // At least 4 characters used
        Character attacker = createMockCharacter(1, "Hero", 100, 50, 10);
        Character deadTarget = createMockCharacter(2, "DeadGoblin", 0, 30, 8);
        Character ally1 = createMockCharacter(3, "Healer", 90, 20, 12);
        Character enemy1 = createMockCharacter(4, "Orc", 120, 60, 7); // Another enemy

        List<Character> lineup = new ArrayList<>(Arrays.asList(attacker, ally1, deadTarget, enemy1));
        List<Character> enemies = new ArrayList<>(Arrays.asList(deadTarget, enemy1));

        BasicActionRequest request = new BasicActionRequest(0, 0, lineup, enemies, lineup);

        // ActionServiceResponse serviceResponse = new ActionServiceResponse(false, false, true, false, false, "Target is already dead");
        // when(actionService.basicAttack(any(Character.class), anyInt(), anyList())).thenReturn(serviceResponse);

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/basic")
        .then()
            .statusCode(400)
            .body("validMove", is(false))
            .body("message", equalTo("Target is already dead"));
    }

    // --- Skill Endpoint Tests ---

    @Test
    @DisplayName("POST /api/test/public/action/skill - Should return OK for a successful skill use with multiple characters")
    void skill_Success() throws Exception {
        // At least 4 characters used
        Character caster = createMockCharacter(1, "Mage", 100, 50, 10);
        Character ally1 = createMockCharacter(2, "Warrior", 120, 60, 8);
        Character ally2 = createMockCharacter(3, "Rogue", 80, 70, 15);
        Character enemy1 = createMockCharacter(4, "Minotaur", 200, 40, 5);
        Character enemy2 = createMockCharacter(5, "Slime", 50, 10, 3); // More than 4 total, ensuring variety

        List<Character> lineup = new ArrayList<>(Arrays.asList(caster, ally1, ally2, enemy1, enemy2));
        List<Character> allies = new ArrayList<>(Arrays.asList(caster, ally1, ally2)); // 3 allies
        List<Character> enemies = new ArrayList<>(Arrays.asList(enemy1, enemy2)); // 2 enemies

        SkillActionRequest request = new SkillActionRequest(0, allies, enemies, lineup);

        // ActionServiceResponse serviceResponse = new ActionServiceResponse(true, false, false, false, false, "Skill executed");
        // when(actionService.skill(any(Character.class), anyList(), anyList())).thenReturn(serviceResponse);

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/skill")
        .then()
            .statusCode(200)
            .body("validMove", is(true))
            .body("message", equalTo("Skill used"))
            .body("newLineup", is(notNullValue()))
            .body("newLineup", hasSize(5)); // Verify newLineup is present and has correct size

        // verify(actionService, times(1)).skill(caster, allies, enemies);
    }

    @Test
    @DisplayName("POST /api/test/public/action/skill - Should return BAD_REQUEST if current character index is invalid with multiple characters")
    void skill_InvalidCharacterIndex() throws Exception {
        // At least 4 characters used
        Character char1 = createMockCharacter(1, "Mage", 100, 50, 10);
        Character char2 = createMockCharacter(2, "Warrior", 120, 60, 8);
        Character char3 = createMockCharacter(3, "Rogue", 80, 70, 15);
        Character char4 = createMockCharacter(4, "Minotaur", 200, 40, 5);

        List<Character> lineup = new ArrayList<>(Arrays.asList(char1, char2, char3, char4)); // 4 characters
        List<Character> allies = new ArrayList<>(Arrays.asList(char1, char2, char3));
        List<Character> enemies = new ArrayList<>(Arrays.asList(char4));

        SkillActionRequest request = new SkillActionRequest(-1, allies, enemies, lineup); // Invalid index

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/skill")
        .then()
            .statusCode(400)
            .body("validMove", is(false))
            .body("message", equalTo("Invalid current character"));

        // verifyNoInteractions(actionService); // Service should not be called
    }

    @Test
    @DisplayName("POST /api/test/public/action/skill - Should return BAD_REQUEST if service indicates invalid stat with multiple characters")
    void skill_ServiceInvalidStat() throws Exception {
        // At least 4 characters used
        Character caster = createMockCharacter(1, "Mage", 100, 50, 10);
        Character ally1 = createMockCharacter(2, "Warrior", 120, 60, 8);
        Character ally2 = createMockCharacter(3, "Rogue", 80, 70, 15);
        Character enemy1 = createMockCharacter(4, "Minotaur", 200, 40, 5);

        List<Character> lineup = new ArrayList<>(Arrays.asList(caster, ally1, ally2, enemy1));
        List<Character> allies = new ArrayList<>(Arrays.asList(caster, ally1, ally2));
        List<Character> enemies = new ArrayList<>(Collections.singletonList(enemy1));

        SkillActionRequest request = new SkillActionRequest(0, allies, enemies, lineup);

        // Mock service response for an invalid stat
        // ActionServiceResponse serviceResponse = new ActionServiceResponse(false, false, false, false, true, "Invalid ability stat");
        // when(actionService.skill(any(Character.class), anyList(), anyList())).thenReturn(serviceResponse);

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/skill")
        .then()
            .statusCode(400)
            .body("validMove", is(false))
            .body("message", equalTo("Invalid stat"));
    }
}