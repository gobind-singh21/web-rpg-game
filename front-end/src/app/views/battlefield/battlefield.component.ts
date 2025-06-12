import { Component, OnInit } from '@angular/core';
import { Character } from '../../shared/types/character';
import { CharactercardComponent } from "../../shared/charactercard/charactercard.component";
import { setting } from "../setting/setting.component";
import { TeamStateService } from '../../core/services/team-state.service';
import { Router } from '@angular/router';
import { TeamMaking } from '../team-making/team-making.component';

@Component({
  selector: 'app-battlefield',
  imports: [CharactercardComponent, setting],
  templateUrl: './battlefield.component.html',
  styleUrl: './battlefield.component.css'
})
export class BattlefieldComponent implements OnInit {

  characters: Character[] = [];
  currentTeam: number = 1;
  basicAttackCount: number = 1;
  skillAttackCount: number = 1;
  team1Characters: Character[] = [];
  team2Characters: Character[] = [];

  constructor(private router: Router, private TeamStateService : TeamStateService){}


  ngOnInit() {
    // Load characters and game state
    this.team1Characters = this.TeamStateService.getTeam1Characters();
    this.team2Characters = this.TeamStateService.getTeam2Characters();

    console.log(this.team1Characters);
    console.log(this.team2Characters);

    // Validate teams are present
    if (this.team1Characters.length === 0 || this.team2Characters.length === 0) {
      console.warn('Teams not properly initialized');

      this.router.navigate(['/team-making']);
      return;
    }
  }

  onLeave() {
    // Handle leave game
  }

  onBasicAttack() {
    // Handle basic attack
  }

  onSkillAttack() {
    // Handle skill attack
  }

  ngOnDestroy(){
    this.TeamStateService.clearTeams();
  }
}
