import { Injectable } from '@angular/core';
import { Character } from '../../shared/types/character';

@Injectable({
  providedIn: 'root'
})
export class TeamStateService {

  constructor() { }
  private team1Characters: Character[] = [];
  private team2Characters: Character[] = [];

  setTeams(team1: Character[], team2: Character[]) {
    this.team1Characters = team1;
    this.team2Characters = team2;
  }

  getTeam1Characters(): Character[] {
    return this.team1Characters;
  }

  getTeam2Characters(): Character[] {
    return this.team2Characters;
  }

  clearTeams() {
    this.team1Characters = [];
    this.team2Characters = [];
  }
  
}
