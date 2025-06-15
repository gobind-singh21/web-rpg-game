package com.rpg_game.game.model;

import java.util.List;

public record ActionResponse(boolean validMove, String message, List<CharacterSnapshot> lineup) {}
