package com.rpg_game.game.model;

public record ActionServiceResponse(boolean validMove, boolean inValidTarget, boolean targetAlreadyDead, boolean characterDead, boolean invalidStat, String messsage) {}
