import { Component, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeamService } from '../../core/services/team.service';
import { Character } from '../../shared/types/character';

@Component({
  selector: 'app-turn-order-indicator',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './turn-order-indicator.component.html',
  styleUrls: ['./turn-order-indicator.component.css']
})
export class TurnOrderIndicatorComponent {
  @Input() turnOrder: number[] = [];
  @Input() currentCharacterIndex: number = 0;

  private teamService = inject(TeamService);

  getCharacterById(characterId: number): Character | undefined {
    const charInTeam1 = this.teamService.team1().characters.find(c => c.id === characterId);
    if (charInTeam1) {
      return charInTeam1;
    }
    return this.teamService.team2().characters.find(c => c.id === characterId);
  }
}
