import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Import CommonModule for standalone components

@Component({
  selector: 'app-how-to-play',
  standalone: true, // Mark as standalone component
  imports: [CommonModule], // Add CommonModule for common Angular directives
  templateUrl: './how-to-play.component.html',
  styleUrl: './how-to-play.component.css'
})
export class HowToPlayComponent {
  // No specific logic needed for a static "How to Play" page
}