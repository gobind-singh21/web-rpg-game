import { inject, Injectable, signal } from '@angular/core';
import { TurnOrderService } from './turn-order.service';
import { CharacterSnapService } from './character-stat.service';
import { TeamService } from './team.service';
import { Team } from '../../shared/types/team';
import { CombatApiService } from './combat-api.service';
import { ActionResponse } from '../../shared/types/actionResponse';
import { CharacterSnapshot } from '../../shared/types/characterSnapshot';

@Injectable({
  providedIn: 'root'
})
export class CombatService {

  private turnOrder = inject(TurnOrderService);
  private teams = inject(TeamService);
  private combatApiService = inject(CombatApiService);
  private characterSnapService = inject(CharacterSnapService);
  private _winningTeam = signal<Team | undefined>(undefined);

  public readonly winningTeam = this._winningTeam.asReadonly();

  constructor() { }

  private isBattleOver(): void {
    this._winningTeam.update(() => this.teams.defeatedTeam());
  }

  battleStart(): void {
    // TODO : implement start battle API
  }

  basicAttack(): void {
    this.combatApiService.basicAttackApi().subscribe({
      next: (response: ActionResponse) => {
        const newTurnOrder: number[] = [];
        response.lineup.forEach((characterSnapshot: CharacterSnapshot, characterId: number) => {
          newTurnOrder.push(characterId);
          this.characterSnapService.updateCharacterStat(characterId, characterSnapshot);
        })
        this.turnOrder.nextCharacter();
        this.isBattleOver();
      },
      error: (err: any) => {
        alert("Error in basic attack");
        console.log(err);
      }
    });
  }

  skill(): void {
    this.combatApiService.skillApi().subscribe({
      next: (response: ActionResponse) => {
        const newTurnOrder: number[] = [];
        response.lineup.forEach((characterSnapshot: CharacterSnapshot, characterId: number) => {
          newTurnOrder.push(characterId);
          this.characterSnapService.updateCharacterStat(characterId, characterSnapshot);
        })
        this.turnOrder.nextCharacter();
        this.isBattleOver();
      },
      error: (err: any) => {
        alert("Error in basic attack");
        console.log(err);
      }
    });
  }
}
