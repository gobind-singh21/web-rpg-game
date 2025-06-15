import { Component, Inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { TeamService } from '../../core/services/team.service';
import { BattleOverDialogData } from '../../shared/types/battleOverDialogData';

@Component({
  selector: 'app-battle-over-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './battle-over-dialog.component.html',
  styleUrls: ['./battle-over-dialog.component.css']
})
export class BattleOverDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<BattleOverDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BattleOverDialogData,
    private location: Location,
    private teamService: TeamService
  ) {}

  onExit(): void {
    this.teamService.clearTeams();
    this.dialogRef.close();
    this.location.back();
  }
}
