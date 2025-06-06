import { inject, Injectable, signal } from '@angular/core';
import { TurnOrderService } from './turn-order.service';
import { CharacterSnapService } from './character-stat.service';
import { TeamService } from './team.service';
import { Team } from '../../../types/team';

@Injectable({
  providedIn: 'root'
})
export class CombatService {

  turnOrder = inject(TurnOrderService);
  characterSnaps = inject(CharacterSnapService);
  teams = inject(TeamService);
  winningTeam = signal<Team | undefined>(undefined);

  constructor() { }

  private isBattleOver(): void {
    this.winningTeam.update(() => this.teams.defeatedTeam());
  }

  battleStart(): void {
    // TODO : implement start battle API
  }

  basicAttack(): void {
    // TODO : implement basic attack API
    this.turnOrder.nextCharacter();
    this.isBattleOver();
  }

  skill(): void {
    // TODO : implement skill API
    this.turnOrder.nextCharacter();
    this.isBattleOver();
  }
}
