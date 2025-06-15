import { Team } from './../../shared/types/team';
import { inject, Injectable, signal } from '@angular/core';
import { CharacterSnapService } from './character-stat.service';
import { Character } from '../../shared/types/character';
import { CharacterSnapshot } from '../../shared/types/characterSnapshot';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  private characterSnaps = inject(CharacterSnapService);

  team1 = signal<Team>({
    name: "Team 1",
    skillPoints: 3,
    characters: [],
  });

  team2 = signal<Team>({
    name: "Team 2",
    skillPoints: 3,
    characters: [],
  });

  constructor() { }

  setTeam1(team: Character[]): boolean {
    if(team.length <= 0) {
      return false;
    }
    this.team1.update((oldTeam) => {
      team.forEach((character) => character.snapshot.team = "Team 1");
      const newTeam: Team = {
        ...oldTeam,
        characters: team,
      };
      return newTeam;
    });
    this.team1().characters.forEach((character) => {
      this.characterSnaps.updateCharacterStat(character.id as number, character.snapshot);
    });
    return true;
  }

  setTeam2(team: Character[]): boolean {
    if(team.length <= 0) {
      return false;
    }
    this.team2.update((oldTeam) => {
      team.forEach((character) => character.snapshot.team = "Team 2");
      const newTeam: Team = {
        ...oldTeam,
        characters: team
      };
      return newTeam;
    });
    this.team2().characters.forEach((character) => {
      this.characterSnaps.updateCharacterStat(character.id as number, character.snapshot);
    });
    console.log(this.characterSnaps.characterMap());
    return true;
  }

  defeatedTeam(): Team | undefined {
    const isTeam1Defeated = this.team1().characters.length > 0 && this.team1().characters.every(
      char => this.characterSnaps.isCharacterDead(char.id as number)
    );

    const isTeam2Defeated = this.team2().characters.length > 0 && this.team2().characters.every(
      char => this.characterSnaps.isCharacterDead(char.id as number)
    );

    if (isTeam1Defeated) return this.team1();
    if (isTeam2Defeated) return this.team2();
    return undefined;
  }

  gainTeam1Skill(): void {
    this.team1.update((oldTeam) => {
      var skillPoint = oldTeam.skillPoints;
      skillPoint++;
      const newTeam: Team = {
        name: "Team 1",
        characters: [...oldTeam.characters],
        skillPoints: skillPoint,
      };
      return newTeam;
    });
  }

  gainTeam2Skill(): void {
    this.team2.update((oldTeam) => {
      var skillPoint = oldTeam.skillPoints;
      skillPoint++;
      const newTeam: Team = {
        name: "Team 2",
        characters: [...oldTeam.characters],
        skillPoints: skillPoint,
      };
      return newTeam;
    });
  }

  useTeam1Skill(): void {
    this.team1.update((oldTeam) => {
      var skillPoint = oldTeam.skillPoints;
      skillPoint--;
      const newTeam: Team = {
        name: "Team 1",
        characters: [...oldTeam.characters],
        skillPoints: skillPoint,
      };
      return newTeam;
    });
  }

  useTeam2Skill(): void {
    this.team2.update((oldTeam) => {
      var skillPoint = oldTeam.skillPoints;
      skillPoint--;
      const newTeam: Team = {
        name: "Team 2",
        characters: [...oldTeam.characters],
        skillPoints: skillPoint,
      };
      return newTeam;
    });
  }

  isCharacterInTeam1(characterId: number): boolean {
    return this.team1().characters.filter(character => character.id == characterId).length > 0;
  }

  isCharacterInTeam2(characterId: number): boolean {
    return this.team2().characters.filter(character => character.id == characterId).length > 0;
  }

  clearTeams(): void {
    this.team1.update((oldTeam) => ({
      ...oldTeam, characters: []
    }));
    this.team2.update((oldTeam) => ({
      ...oldTeam, characters: []
    }));
  }

  updateTeams(): void {
    this.team1.update((oldTeam) => {
      const newCharacters = oldTeam.characters.map((char) => ({
        ...char,
        snapshot: this.characterSnaps.characterMap().get(char.id as number) as CharacterSnapshot
      }));
      return {...oldTeam, characters: newCharacters};
    });
    this.team2.update((oldTeam) => {
      const newCharacters = oldTeam.characters.map((char) => ({
        ...char,
        snapshot: this.characterSnaps.characterMap().get(char.id as number) as CharacterSnapshot
      }));
      return {...oldTeam, characters: newCharacters};
    });
  }
}
