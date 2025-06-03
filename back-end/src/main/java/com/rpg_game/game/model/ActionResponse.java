package com.rpg_game.game.model;

import java.util.List;

import com.rpg_game.game.entity.Character;

public record ActionResponse(boolean validMove, String message, List<Character> newLineup) {}
