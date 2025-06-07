import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TurnOrderService {

  turnOrder = signal<number[]>([]);

  updateTurnOrder(newTurnOrder: number[]): void {
    this.turnOrder.update(() => newTurnOrder);
  }

  constructor() { }
}
