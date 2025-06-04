package com.rpg_game.game.model;

import java.util.HashMap;

public record ActionResponse(boolean validMove, String message, HashMap<Integer, CharacterSnapshot> lineup) {}
