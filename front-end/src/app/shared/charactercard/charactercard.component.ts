import { Component, Input, OnInit } from '@angular/core';
import { Character } from '../types/character';
import { CharacterClass } from '../types/characterClass';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { CharacterService } from '../../core/services/character.service';

@Component({
  selector: 'app-charactercard',
  imports: [CommonModule, MatIconModule],
  templateUrl: './charactercard.component.html',
  styleUrl: './charactercard.component.css',
})
export class CharactercardComponent implements OnInit {
  constructor(private characterService: CharacterService) {}

  @Input() character!: Character;
  showInfo: boolean = false;
  cardWidth: number = 100;
  cardHeight: number = 120;
  private infoTimeout: any;

  ngOnInit(): void {
    console.log('Character Card Component Initialized', this.character);
    if (this.character?.base) {
      this.calculateCardDimensions();
    }
  }

  calculateCardDimensions() {
    const imageUrl = this.getImageUrl();
    if (!imageUrl) {
      console.warn('No image URL available for character:', this.character);
      return;
    }

    const img = new Image();
    img.onload = () => {
      this.cardWidth = img.width + 20;
      this.cardHeight = img.height + 20;
      console.log('Card dimensions calculated:', {
        width: this.cardWidth,
        height: this.cardHeight,
      });
    };
    img.onerror = () => {
      console.error('Failed to load image:', imageUrl);
    };
    img.src = imageUrl;
  }

  toggleInfo(): void {
    this.showInfo = !this.showInfo;

    // Clear any existing timeout
    if (this.infoTimeout) {
      clearTimeout(this.infoTimeout);
    }

    // Set new timeout if info is shown
    if (this.showInfo) {
      this.infoTimeout = setTimeout(() => {
        this.showInfo = false;
      }, 3000); // 10 seconds
    }
  }

  getImageUrl(): string {
    const imageUrl = this.character?.base?.imageUrl;
    console.log('Fetching image URL for character:', imageUrl);
    return imageUrl || 'assets/images/placeholder.png';
  }

  getBackgroundColor(): string {
    return this.character?.base?.backgroundColor || '#d3d3d3';
  }

  getCharacterName(): string {
    return this.character?.base?.name || 'Unknown Character';
  }

  getCharacterClassName(characterClass: CharacterClass): string {
    return CharacterClass[characterClass];
  }

  getStats() {
    return {
      health: this.character.base.baseHealth,
      attack: this.character.base.baseAttack,
      defense: this.character.base.baseDefense,
      speed: this.character.base.baseSpeed,
    };
  }

  ngOnDestroy(): void {
    if (this.infoTimeout) {
      clearTimeout(this.infoTimeout);
    }
  }
}
