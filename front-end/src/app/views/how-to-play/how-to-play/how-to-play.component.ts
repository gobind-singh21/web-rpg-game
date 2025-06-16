import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Needed for things like ngFor, ngIf
import { RouterModule } from '@angular/router'; // Needed for routerLink
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-how-to-play',
  standalone: true, // Mark component as standalone
  imports: [CommonModule, RouterModule, MatIcon], // Import CommonModule and RouterModule
  templateUrl: './how-to-play.component.html',
  styleUrl: './how-to-play.component.css'
})
export class HowToPlayComponent {

}