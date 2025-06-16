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
import { CombatAnimationService } from './combat-animation.service';

@Injectable({
  providedIn: 'root'
})
export class CombatService {
  
  private selectedTarget: any; 
  private turnOrder = inject(TurnOrderService);
  private teams = inject(TeamService);
  private combatApiService = inject(CombatApiService);
  private characterSnapService = inject(CharacterSnapService);
  private _winningTeam = signal<Team | undefined>(undefined);
  private currentTeam = inject(CurrentTeamService);
  private basicTarget = inject(BasicTargetService);

  private animationService = inject(CombatAnimationService);

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
    const oldSnapshots = new Map(this.characterSnapService.characterMap());

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
        this.processActionResponse(oldSnapshots, response);
      },
      error: (err: any) => {
        console.log(err);
      }
    });
  }

  skill(): void {
    const oldSnapshots = new Map(this.characterSnapService.characterMap());

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
        this.processActionResponse(oldSnapshots, response);
      },
      error: (err: any) => {
        console.log(err);
      }
    });
  }

  private async processActionResponse(oldSnapshots: Map<number, CharacterSnapshot>, response: ActionResponse): Promise<void> {
    const newSnapshots: CharacterSnapshot[] = response.lineup;

    // STEP 1: Tell the animation service to play animations and WAIT for it to finish.
    await this.animationService.playActionAnimations(oldSnapshots, newSnapshots);

    // STEP 2: The animations are done. NOW, update the game's actual state.
    var newTurnOrder: number[] = newSnapshots.map(snapshot => snapshot.id as number);

    newSnapshots.forEach((snapshot) => {
      this.characterSnapService.updateCharacterStat(snapshot.id as number, snapshot);
    });

    var deadCharacterBeforeCurrent = 0;
    this.turnOrder.turnOrder().forEach((characterId, index) => {
      if(this.characterSnapService.characterMap().get(characterId)?.currentHealth as number <= 0 && index < this.turnOrder.currentCharacter())
        deadCharacterBeforeCurrent++;
    });

    this.turnOrder.setCurrentCharacter(this.turnOrder.currentCharacter() - deadCharacterBeforeCurrent);

    newTurnOrder = newTurnOrder.filter((characterId) => this.characterSnapService.characterMap().get(characterId)?.currentHealth as number > 0);

    this.teams.updateTeams();
    this.turnOrder.updateTurnOrder(newTurnOrder);
    this.basicTarget.selectBasicTarget(-1);

    // STEP 3: Advance to the next turn.
    this.turnOrder.nextCharacter();

    // STEP 4: Update other UI elements.
    this.currentTeam.currentTeam.update(() => {
      const currentCharacterId = this.turnOrder.turnOrder()[this.turnOrder.currentCharacter()];
      if (currentCharacterId === undefined) return 1;
      return this.teams.isCharacterInTeam1(currentCharacterId) ? 1 : 2;
    });

    this.isBattleOver();
  }

  hasSelectedTarget(): boolean {
    return this.selectedTarget !== null && this.selectedTarget !== undefined;
  }

  
}
