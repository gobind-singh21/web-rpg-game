import { Component, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { Character } from '../../shared/types/character';
import { CharactercardComponent } from "../../shared/charactercard/charactercard.component";
import { setting } from "../setting/setting.component";
import { Router } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import { LoggedInCheckService } from '../../core/services/logged-in-check.service';
import { TeamService } from '../../core/services/team.service';
import { CombatService } from '../../core/services/combat.service';
import { TurnOrderService } from '../../core/services/turn-order.service';
import { BasicTargetService } from '../../core/services/basic-target.service';
import { CurrentTeamService } from '../../core/services/current-team.service';
import { MatIcon } from '@angular/material/icon';
import { TurnOrderIndicatorComponent } from '../turn-order-indicator/turn-order-indicator.component';
import { BattleOverDialogComponent } from '../battle-over-dialog/battle-over-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { BattleOverDialogData } from '../../shared/types/battleOverDialogData';
import { Team } from '../../shared/types/team';
import { CustomDialogComponent } from '../../shared/custom-dialog/custom-dialog.component';

@Component({
  selector: 'app-battlefield',
  imports: [CharactercardComponent, setting, CommonModule, MatIcon, TurnOrderIndicatorComponent],
  templateUrl: './battlefield.component.html',
  styleUrl: './battlefield.component.css'
})
export class BattlefieldComponent implements OnInit, OnDestroy {

  private dialog = inject(MatDialog);
  currentTeam = inject(CurrentTeamService);
  teamService = inject(TeamService);
  basicTargetService = inject(BasicTargetService);
  turnOrder = inject(TurnOrderService);

  constructor(
    private loggedInCheckService: LoggedInCheckService,
    private router: Router,
    private location: Location,
    private combatService: CombatService
  ) {
    if(!loggedInCheckService.isAlreadyLoggedIn())
      router.navigate(["/login"]);
    effect(() => {
      const winningTeam = this.combatService.winningTeam();
      if (winningTeam) {
        this.openBattleOverDialog(winningTeam);
      }
    });
  }

  openBattleOverDialog(winningTeamData: Team): void {
    const dialogData: BattleOverDialogData = {
      winningTeam: winningTeamData,
    };

    this.dialog.open(BattleOverDialogComponent, {
      data: dialogData,
      disableClose: true,
      width: '450px',
      panelClass: 'battle-over-dialog-panel'
    });
  }

  ngOnInit() {
    if (this.teamService.team1().characters.length === 0 || this.teamService.team2().characters.length === 0) {
      console.warn('Teams not properly initialized');
      this.location.back();
      return;
    }
    this.combatService.battleStart();
    this.turnOrder.setCurrentCharacter(0);
    this.basicTargetService.selectBasicTarget(-1);
    this.combatService.resetWinningTeam();
  }

  onLeave() {
    this.teamService.clearTeams();
    this.turnOrder.setCurrentCharacter(0);
    this.basicTargetService.selectBasicTarget(-1);
    this.combatService.resetWinningTeam();
    this.location.back();
  }

  targetSelect(character: Character) {
    if((this.teamService.isCharacterInTeam1(character.id == undefined ? -1 : character.id)
      == this.teamService.isCharacterInTeam1(this.turnOrder.turnOrder()[this.turnOrder.currentCharacter()]))
      || (this.teamService.isCharacterInTeam2(character.id == undefined ? -1 : character.id)
      == this.teamService.isCharacterInTeam2(this.turnOrder.turnOrder()[this.turnOrder.currentCharacter()]))
    ) {
      console.warn("Cannot select target from the same team");
      return;
    }
    if(character.snapshot.currentHealth <= 0) {
      return;
    }
    console.log(this.basicTargetService.basicTarget());
    this.basicTargetService.selectBasicTarget(character.id == undefined ? -1 : character.id);
  }

  onBasicAttack() {
    if (this.basicTargetService.basicTarget() === -1) {
      const dialogRef = this.dialog.open(CustomDialogComponent, {
        disableClose: false,
        data: {
          title: 'No Target Selected',
          message: 'Please select a target before using basic attack!',
          buttonText: 'OK'
        },
        hasBackdrop: true,
        backdropClass: 'dialog-backdrop'
      });

      dialogRef.afterClosed().subscribe(() => {});

      return;
    }

    this.combatService.basicAttack();
  }

  onSkillAttack() {
    this.combatService.skill();
  }

  ngOnDestroy() {
    this.teamService.clearTeams();
    this.turnOrder.setCurrentCharacter(0);
    this.basicTargetService.selectBasicTarget(-1);
    this.combatService.resetWinningTeam();
  }
}
