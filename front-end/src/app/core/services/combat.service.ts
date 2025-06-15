import { inject, Injectable, signal } from '@angular/core';
import { TurnOrderService } from './turn-order.service';
import { CharacterSnapService } from './character-stat.service';
import { TeamService } from './team.service';
import { Team } from '../../shared/types/team';
import { CombatApiService } from './combat-api.service';
import { ActionResponse } from '../../shared/types/actionResponse';
import { CharacterSnapshot } from '../../shared/types/characterSnapshot';
import { BattleStartResponse } from '../../shared/types/battleStartResponse';
import { CurrentTeamService } from './current-team.service';
import { BasicTargetService } from './basic-target.service';

@Injectable({
  providedIn: 'root'
})
export class CombatService {

  private turnOrder = inject(TurnOrderService);
  private teams = inject(TeamService);
  private combatApiService = inject(CombatApiService);
  private characterSnapService = inject(CharacterSnapService);
  private _winningTeam = signal<Team | undefined>(undefined);
  private currentTeam = inject(CurrentTeamService);
  private basicTarget = inject(BasicTargetService);

  public readonly winningTeam = this._winningTeam.asReadonly();

  constructor() { }

  private isBattleOver(): void {
    this._winningTeam.update(() => this.teams.defeatedTeam() == this.teams.team1() ? this.teams.team2() : this.teams.defeatedTeam() == this.teams.team2() ? this.teams.team1() : undefined);
  }

  battleStart(): void {
    this.combatApiService.battleStartApi().subscribe({
      next: (response: BattleStartResponse) => {
        this.turnOrder.updateTurnOrder(response.lineup);
        this.currentTeam.currentTeam.update(() => {
          return this.teams.isCharacterInTeam1(this.turnOrder.turnOrder()[this.turnOrder.currentCharacter()]) ? 1 : 2;
        });
      },
      error: (err: any) => {
        console.log(err);
      }
    });
  }

  basicAttack(): void {
    this.combatApiService.basicAttackApi().subscribe({
      next: (response: ActionResponse) => {
        if(!response.validMove) {
          // TODO : give indication of invalid move in UI
          console.warn("Invalid move");
          return;
        }
        var currentCharacterId = this.turnOrder.turnOrder()[this.turnOrder.currentCharacter()];
        if(this.teams.isCharacterInTeam1(currentCharacterId) && this.currentTeam.currentTeam() == 1) {
          if(this.teams.team1().skillPoints < 5) {
            this.teams.gainTeam1Skill();
          }
        }
        if(this.teams.isCharacterInTeam2(currentCharacterId) && this.currentTeam.currentTeam() == 2) {
          if(this.teams.team2().skillPoints < 5) {
            this.teams.gainTeam2Skill();
          }
        }
        this.processActionResponse(response);
      },
      error: (err: any) => {
        console.log(err);
      }
    });
  }

  skill(): void {
    var currentCharacterId = this.turnOrder.turnOrder()[this.turnOrder.currentCharacter()];
    if(this.teams.isCharacterInTeam1(currentCharacterId) && this.currentTeam.currentTeam() == 1) {
      if(this.teams.team1().skillPoints <= 0) {
        console.warn("Skill points for team 1 : 0");
        return;
      }
    }
    if(this.teams.isCharacterInTeam2(currentCharacterId) && this.currentTeam.currentTeam() == 2) {
      if(this.teams.team2().skillPoints <= 0) {
        console.warn("Skill points for team 2 : 0");
        return;
      }
    }
    this.combatApiService.skillApi().subscribe({
      next: (response: ActionResponse) => {
        if(!response.validMove) {
          // TODO : give indication of invalid move in UI
          console.warn("Invalid move");
          return;
        }
        var currentCharacterId = this.turnOrder.turnOrder()[this.turnOrder.currentCharacter()];
        if(this.teams.isCharacterInTeam1(currentCharacterId) && this.currentTeam.currentTeam() == 1) {
          this.teams.useTeam1Skill();
        }
        if(this.teams.isCharacterInTeam2(currentCharacterId) && this.currentTeam.currentTeam() == 2) {
          this.teams.useTeam2Skill();
        }
        this.processActionResponse(response);
      },
      error: (err: any) => {
        console.log(err);
      }
    });
  }

  private processActionResponse(response: ActionResponse): void {
    const characterSnapshots: CharacterSnapshot[] = response.lineup;
    characterSnapshots.forEach((snapshot) => {
      this.characterSnapService.updateCharacterStat(snapshot.id as number, snapshot);
    });
    this.teams.updateTeams();
    const filteredSnapshots = characterSnapshots.filter((snapshot) => snapshot.currentHealth > 0);
    const newTurnOrder: number[] = filteredSnapshots.map(snapshot => snapshot.id as number);
    this.turnOrder.updateTurnOrder(newTurnOrder);
    this.basicTarget.selectBasicTarget(-1);
    this.turnOrder.nextCharacter();
    this.currentTeam.currentTeam.update(() => {
      const currentCharacterId = this.turnOrder.turnOrder()[this.turnOrder.currentCharacter()];
      if (currentCharacterId === undefined) return 1;
      return this.teams.isCharacterInTeam1(currentCharacterId) ? 1 : 2;
    });
    this.isBattleOver();
  }
}
