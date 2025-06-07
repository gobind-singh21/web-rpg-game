import { Injectable, signal } from '@angular/core';
import { CharacterSnapshot } from '../../../types/characterSnapshot';

@Injectable({
  providedIn: 'root'
})
export class CharacterSnapService {

  characterMap = signal<Map<number, CharacterSnapshot>>(new Map());

  updateCharacterStat(characterId: number, characeterSnapshot: CharacterSnapshot): void {
    this.characterMap.update((oldCharacterMap) => {
      oldCharacterMap.set(characterId, characeterSnapshot);
      return oldCharacterMap;
    });
  }

  isCharacterDead(characterId: number): boolean {
    return this.characterMap().get(characterId) != null
    && this.characterMap().get(characterId)?.currentHealth == 0;
  }

  addCharacter(characterId: number, characterSnapshot: CharacterSnapshot): void {
    this.characterMap.update((oldCharacterMap) => oldCharacterMap.set(characterId, characterSnapshot));
  }

  constructor() { }
}
