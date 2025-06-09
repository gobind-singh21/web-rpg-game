import { inject, Injectable, signal } from '@angular/core';
import { Team } from '../../shared/types/team';
import { CharacterSnapService } from './character-stat.service';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  private characterSnaps = inject(CharacterSnapService);

  team1 = signal<Team>({
    name: "",
    skillPoints: 3,
    characters: [],
  });

  team2 = signal<Team>({
    name: "",
    skillPoints: 3,
    characters: [],
  });

  constructor() { }

  setTeam1(team1: Team): boolean {
    if(team1.characters.length != 3) {
      return false;
    }
    this.team1.update(() => team1);
    return true;
  }

  setTeam2(team2: Team): boolean {
    if(team2.characters.length != 3) {
      return false;
    }
    this.team2.update(() => team2);
    return true;
  }

  defeatedTeam(): Team | undefined {
    var teamOneDownedMembers = 0;
    var teamTwoDownedMembers = 0;
    this.team1().characters.forEach((character) => {
      if(this.characterSnaps.characterMap().get(character)?.currentHealth == 0) {
        teamOneDownedMembers++;
      }
    });
    this.team2().characters.forEach((character) => {
      if(this.characterSnaps.characterMap().get(character)?.currentHealth == 0) {
        teamTwoDownedMembers++;
      }
    });
    if(teamOneDownedMembers == this.team1().characters.length)
      return this.team1();
    if(teamTwoDownedMembers == this.team2().characters.length)
      return this.team2();
    return undefined;
  }
}
