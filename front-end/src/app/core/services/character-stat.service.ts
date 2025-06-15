import { Injectable, signal } from '@angular/core';
import { CharacterSnapshot } from '../../shared/types/characterSnapshot';

@Injectable({
  providedIn: 'root'
})
export class CharacterSnapService {

  private _characterMap = signal<Map<number, CharacterSnapshot>>(new Map());

  public readonly characterMap = this._characterMap.asReadonly();

  updateCharacterStat(characterId: number, characeterSnapshot: CharacterSnapshot): void {
    this._characterMap.update((oldCharacterMap) => {
      const newMap = new Map(oldCharacterMap);
      newMap.set(characterId, characeterSnapshot);
      return newMap;
    });
  }

  isCharacterDead(characterId: number): boolean {
    return this._characterMap().get(characterId) != null
    && this._characterMap().get(characterId)?.currentHealth == 0;
  }

  constructor() { }
}
