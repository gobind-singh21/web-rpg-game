package com.rpg_game.game.model;

public record BasicActionServiceResponse(boolean validMove, boolean inValidTarget, boolean targetAlreadyDead, boolean characterDead, boolean invalidCurrentCharacter, boolean invalidStat, String message) {}
