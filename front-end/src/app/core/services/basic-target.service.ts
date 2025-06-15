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
    this._basicTarget.set(characterId);
  }

  constructor() { }
}
