package com.rpg_game.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg_game.game.entity.Character;
import com.rpg_game.game.model.ActionResponse;
import com.rpg_game.game.model.BasicActionRequest;
import com.rpg_game.game.model.SkillActionRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActionRESTControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    private Character createMockCharacter(Integer id, String name, Integer health, Integer attack, Integer speed) {
        Character character = new Character();
        character.setId(id);
        character.setName(name);
        character.setBaseHealth(health);
        character.setBaseAttack(attack);
        character.setBaseSpeed(speed);
        character.setEffects(new ArrayList<>());
        return character;
    }

    @Test
    @DisplayName("POST /api/test/public/action/basic - Should return OK for a successful basic attack with multiple characters")
    void basicAttack_Success() throws Exception {
        Character attacker = createMockCharacter(1, "Hero", 100, 50, 10);
        Character target = createMockCharacter(2, "Goblin", 80, 30, 8);
        Character ally1 = createMockCharacter(3, "Healer", 90, 20, 12);
        Character enemy1 = createMockCharacter(4, "Orc", 120, 60, 7);

        List<Character> lineup = new ArrayList<>(Arrays.asList(attacker, ally1, target, enemy1));
        List<Character> allies = new ArrayList<>(Arrays.asList(attacker, ally1));
        List<Character> enemies = new ArrayList<>(Arrays.asList(target, enemy1));

        BasicActionRequest request = new BasicActionRequest(0, 0, allies, enemies, lineup);

        var response = given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/basic")
        .then()
            .statusCode(200)
            .body("validMove", is(true))
            .body("message", equalTo("Hero landed basic attack on Goblin"))
            .body("newLineup", is(notNullValue()))
            .body("newLineup", hasSize(4)).extract().as(ActionResponse.class);

        System.out.println(response.message());
    }

    @Test
    @DisplayName("POST /api/test/public/action/basic - Should return BAD_REQUEST if current character index is invalid with multiple characters")
    void basicAttack_InvalidCharacterIndex() throws Exception {
        Character char1 = createMockCharacter(1, "Hero", 100, 50, 10);
        Character char2 = createMockCharacter(2, "Goblin", 80, 30, 8);
        Character char3 = createMockCharacter(3, "Healer", 90, 20, 12);
        Character char4 = createMockCharacter(4, "Orc", 120, 60, 7);

        List<Character> lineup = new ArrayList<>(Arrays.asList(char1, char2, char3, char4));
        List<Character> allies = new ArrayList<>(Arrays.asList(char1, char3));
        List<Character> enemies = new ArrayList<>(Arrays.asList(char2, char4));

        BasicActionRequest request = new BasicActionRequest(-1, 0, allies, enemies, lineup);

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/basic")
        .then()
            .statusCode(400)
            .body("validMove", is(false))
            .body("message", equalTo("Invalid current character"));
    }

    @Test
    @DisplayName("POST /api/test/public/action/basic - Should return BAD_REQUEST if service indicates invalid target with multiple characters")
    void basicAttack_ServiceInvalidTarget() throws Exception {
        Character attacker = createMockCharacter(1, "Hero", 100, 50, 10);
        Character target = createMockCharacter(2, "Goblin", 80, 30, 8);
        Character ally1 = createMockCharacter(3, "Healer", 90, 20, 12);
        Character enemy1 = createMockCharacter(4, "Orc", 120, 60, 7);

        List<Character> lineup = new ArrayList<>(Arrays.asList(attacker, ally1, target, enemy1));
        List<Character> allies = new ArrayList<>(Arrays.asList(attacker, ally1));
        List<Character> enemies = new ArrayList<>(Arrays.asList(target, enemy1));

        BasicActionRequest request = new BasicActionRequest(0, 99, allies, enemies, lineup);

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
        Character attacker = createMockCharacter(1, "Hero", 100, 50, 10);
        Character deadTarget = createMockCharacter(2, "DeadGoblin", 0, 30, 8);
        Character ally1 = createMockCharacter(3, "Healer", 90, 20, 12);
        Character enemy1 = createMockCharacter(4, "Orc", 120, 60, 7);

        List<Character> lineup = new ArrayList<>(Arrays.asList(attacker, ally1, deadTarget, enemy1));
        List<Character> allies = new ArrayList<>(Arrays.asList(attacker, ally1));
        List<Character> enemies = new ArrayList<>(Arrays.asList(deadTarget, enemy1));

        BasicActionRequest request = new BasicActionRequest(0, 0, allies, enemies, lineup);

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

    @Test
    @DisplayName("POST /api/test/public/action/skill - Should return OK for a successful skill use with multiple characters")
    void skill_Success() throws Exception {
        Character caster = createMockCharacter(1, "Mage", 100, 50, 10);
        Character ally1 = createMockCharacter(2, "Warrior", 120, 60, 8);
        Character ally2 = createMockCharacter(3, "Rogue", 80, 70, 15);
        Character enemy1 = createMockCharacter(4, "Minotaur", 200, 40, 5);
        Character enemy2 = createMockCharacter(5, "Slime", 50, 10, 3);

        List<Character> lineup = new ArrayList<>(Arrays.asList(caster, ally1, ally2, enemy1, enemy2));
        List<Character> allies = new ArrayList<>(Arrays.asList(caster, ally1, ally2));
        List<Character> enemies = new ArrayList<>(Arrays.asList(enemy1, enemy2));

        SkillActionRequest request = new SkillActionRequest(0, allies, enemies, lineup);

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
            .body("newLineup", hasSize(5));
    }

    @Test
    @DisplayName("POST /api/test/public/action/skill - Should return BAD_REQUEST if current character index is invalid with multiple characters")
    void skill_InvalidCharacterIndex() throws Exception {
        Character char1 = createMockCharacter(1, "Mage", 100, 50, 10);
        Character char2 = createMockCharacter(2, "Warrior", 120, 60, 8);
        Character char3 = createMockCharacter(3, "Rogue", 80, 70, 15);
        Character char4 = createMockCharacter(4, "Minotaur", 200, 40, 5);

        List<Character> lineup = new ArrayList<>(Arrays.asList(char1, char2, char3, char4));
        List<Character> allies = new ArrayList<>(Arrays.asList(char1, char2, char3));
        List<Character> enemies = new ArrayList<>(Arrays.asList(char4));

        SkillActionRequest request = new SkillActionRequest(-1, allies, enemies, lineup);

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
        .when()
            .post("/api/test/public/action/skill")
        .then()
            .statusCode(400)
            .body("validMove", is(false))
            .body("message", equalTo("Invalid current character"));
    }
}