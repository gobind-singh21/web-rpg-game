import { inject, Injectable, signal } from '@angular/core';
import { TurnOrderService } from './turn-order.service';
import { CharacterSnapService } from './character-stat.service';

@Injectable({
  providedIn: 'root'
})
export class BasicTargetService {

  private turnOrderService = inject(TurnOrderService);
  private characterSnapService = inject(CharacterSnapService);

  private _basicTarget = signal<number>(-1);

  public readonly basicTarget = this._basicTarget.asReadonly();

  selectBasicTarget(characterId: number): void {
    const currentCharacter: number = this.turnOrderService.turnOrder()[this.turnOrderService.currentCharacter()];
    const currentCharacterTeam = this.characterSnapService.characterMap().get(currentCharacter)?.team;
    if(currentCharacterTeam != this.characterSnapService.characterMap().get(this._basicTarget())) {
      this._basicTarget.update(() => characterId);
    }
  }

  constructor() { }
}
