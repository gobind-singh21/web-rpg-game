import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TurnOrderService {

  private _turnOrder = signal<number[]>([]);
  private _currentCharacter = signal<number>(0);

  public readonly turnOrder = this._turnOrder.asReadonly();
  public readonly currentCharacter = this._currentCharacter.asReadonly();

  updateTurnOrder(newTurnOrder: number[]): void {
    this._turnOrder.update(() => newTurnOrder);
  }

  nextCharacter(): void {
    this._currentCharacter.update(
      (current) => current >= (this._turnOrder().length - 1) ? 0 : current + 1
    );
  }

  setCurrentCharacter(newIndex: number): void {
    this._currentCharacter.set(newIndex);
  }

  constructor() { }
}
