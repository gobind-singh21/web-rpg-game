import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TurnOrderService {

  turnOrder = signal<number[]>([]);
  currentCharacter = signal<number>(0);

  updateTurnOrder(newTurnOrder: number[]): void {
    this.turnOrder.update(() => newTurnOrder);
  }

  nextCharacter(): void {
    this.currentCharacter.update(
      (current) => current >= this.turnOrder().length ? 0 : current + 1
    );
  }

  constructor() { }
}
