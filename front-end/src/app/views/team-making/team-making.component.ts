import { Component, OnInit } from '@angular/core';
import { Tiles } from '../../shared/tiles/tiles.component';
import { Character } from '../../shared/types/character';
import { CharacterService } from '../../core/services/character.service';
import { CharactercardComponent } from '../../shared/charactercard/charactercard.component';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TeamStateService } from '../../core/services/team-state.service';
import { LoggedInCheckService } from '../../core/services/logged-in-check.service';
import { TeamService } from '../../core/services/team.service';

@Component({
  selector: 'team-making',
  imports: [CharactercardComponent, CommonModule, Tiles],
  templateUrl: './team-making.component.html',
  styleUrl: './team-making.component.css',
})
export class TeamMaking implements OnInit {

constructor(
  private loggedInCheckService: LoggedInCheckService,
  private router: Router,
  private characterService: CharacterService,
  private teamStateService : TeamStateService,
  private teamService: TeamService
) {
  if(!loggedInCheckService.isAlreadyLoggedIn())
    router.navigate(["/login"]);
}


navigateToBattle() {

    if (this.team1Characters.length === 0 || this.team2Characters.length === 0) {
      console.warn('Both teams must have at least one character before starting the battle.');
      return;
    }
    // Store teams in service
    this.teamService.setTeam1([...this.team1Characters]);
    this.teamService.setTeam2([...this.team2Characters]);
    this.team1Characters = [];
    this.team2Characters = [];
    // this.teamStateService.setTeams(this.team1Characters, this.team2Characters);

    // Navigate without query params
    this.router.navigate(['/battlefield']);

}
  readonly MAX_TEAM_SIZE: number = 3;
  team1Characters: any[] = [];
  team2Characters: any[] = [];
  showCharacterListForTeam1: boolean = false;
  showCharacterListForTeam2: boolean = false;
  isTeam1Turn: boolean = true;
  isDeleteMode: boolean = false;
  characters: Character[] = [];

  // Removed duplicate constructor

  ngOnInit(): void {
    this.loadCharacters();
  }

  loadCharacters(): void {
    this.characterService.getAllCharacters().subscribe({
      next: (characters: Character[]) => {
        this.characters = characters;
        console.log('Characters loaded:', this.characters);
      },
      error: (error) => {
        console.error('Error loading characters:', error);
      },
    });
  }

  isCharacterSelected(character: Character): boolean {
    const isInTeam1 = this.team1Characters.some(c => c.id === character.id);
    const isInTeam2 = this.team2Characters.some(c => c.id === character.id);
    return isInTeam1 || isInTeam2;
  }

  onCharacterSelected(character: Character, teamNumber: number) {

    // Prevent selection if character is already selected
    if (this.isCharacterSelected(character)) {
      console.warn('Character already selected in a team');
      return;
    }

    const team = teamNumber === 1 ? this.team1Characters : this.team2Characters;
    if (team.length >= this.MAX_TEAM_SIZE) {
      console.warn('Team is already full (maximum 3 characters)');
      return;
    }

    const alreadySelected = team.some((c) => c.id === character.id);
    if (alreadySelected) {
      console.warn('Character already selected in this team');
      return;
    }

    if (teamNumber === 1) {
      this.team1Characters.push(character);
      console.log('Team 1 characters:', this.team1Characters);
      this.showCharacterListForTeam1 = false;
    } else {
      this.team2Characters.push(character);
      console.log('Team 2 characters:', this.team2Characters);
      this.showCharacterListForTeam2 = false;
    }

    if (!this.isDeleteMode) {
      // Normal turn-based selection
      this.isTeam1Turn = !this.isTeam1Turn;
    } else {
      // Reset delete mode after filling the deleted slot
      this.isDeleteMode = false;
      const otherTeam =
        teamNumber === 1 ? this.team2Characters : this.team1Characters;
      if (otherTeam.length < this.MAX_TEAM_SIZE) {
        this.isTeam1Turn = teamNumber === 2;
      }
    }
  }

  toggleCharacterList(team: number): void {
    const teamCharacters =
      team === 1 ? this.team1Characters : this.team2Characters;
    if (teamCharacters.length >= this.MAX_TEAM_SIZE) {
      console.warn(`Team ${team} is already full`);
      return;
    }

    // Only allow selection if it's the team's turn or in delete mode
    if (
      !this.isDeleteMode &&
      ((team === 1 && !this.isTeam1Turn) || (team === 2 && this.isTeam1Turn))
    ) {
      console.warn(`It's not Team ${team}'s turn`);
      return;
    }

    if (team === 1) {
      this.showCharacterListForTeam1 = !this.showCharacterListForTeam1;
      this.showCharacterListForTeam2 = false;
    } else {
      this.showCharacterListForTeam2 = !this.showCharacterListForTeam2;
      this.showCharacterListForTeam1 = false;
    }
  }

  handleDelete(characterId: number, teamNumber: number) {
    console.log(
      'Delete character with ID:',
      characterId,
      'from team:',
      teamNumber
    );
    if (teamNumber === 1) {
      this.team1Characters = this.team1Characters.filter(
        (char) => char.id !== characterId
      );
      this.isTeam1Turn = true;
    } else {
      this.team2Characters = this.team2Characters.filter(
        (char) => char.id !== characterId
      );
      // Enable delete mode
      this.isDeleteMode = true;
    }
  }
}
