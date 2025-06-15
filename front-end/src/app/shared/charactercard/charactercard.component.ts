import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
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
  constructor() {}

  @Input() character!: Character;
  @Output() characterSelect = new EventEmitter<Character>();
  showInfo: boolean = false;
  cardWidth: number = 100;
  cardHeight: number = 120;
  private infoTimeout: any;
  @Input() disabled: boolean = false;
  @Input() isCurrentTurn: boolean = false;
  @Input() isSelectedTarget: boolean = false;
  @Input() hasShield: boolean = false;
  @Input() isDead: boolean = false;
  @Input() showBattleUI: boolean = false;
  characterService = inject(CharacterService);

  ngOnInit(): void {
    // console.log('Character Card Component Initialized', this.character);
    if (this.character?.base) {
      this.calculateCardDimensions();
    }
  }

  getHealthPercent(): number {
    if (!this.character) return 0;
    const maxHealth = this.characterService.getMaxHealth(this.character.base.baseHealth, this.character.snapshot.effects);
    if (maxHealth === 0) return 0; // Avoid division by zero
    const percentage = (this.character.snapshot.currentHealth / maxHealth) * 100;
    return Math.max(0, Math.min(100, percentage)); // Clamp between 0 and 100
  }

  getShieldPercent(): number {
    if (!this.character || this.character.snapshot.shield <= 0) return 0;
    const maxHealth = this.characterService.getMaxHealth(this.character.base.baseHealth, this.character.snapshot.effects);
    if (maxHealth === 0) return 0;
    const percentage = (this.character.snapshot.shield / maxHealth) * 100;
    return Math.max(0, Math.min(100, percentage)); // Clamp between 0 and 100
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
      // console.log('Card dimensions calculated:', {
      //   width: this.cardWidth,
      //   height: this.cardHeight,
      // });
    };
    img.onerror = () => {
      console.error('Failed to load image:', imageUrl);
    };
    img.src = imageUrl;
  }

  onCardClick(event: MouseEvent): void {
    if (this.disabled) return;
    const target = event.target as HTMLElement;
    if (!target.closest('.info-button') && !target.closest('.character-info')) {
      this.characterSelect.emit(this.character);
    }
  }

  onInfoButtonClick(event: MouseEvent): void {
    event.stopPropagation();
    this.toggleInfo();
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
    // console.log('Fetching image URL for character:', imageUrl);
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
