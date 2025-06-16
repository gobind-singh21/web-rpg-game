import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BasicTargetService {
  private _basicTarget = signal<number>(-1);

  public readonly basicTarget = this._basicTarget.asReadonly();

  selectBasicTarget(characterId: number): void {
    this._basicTarget.set(characterId);
  }

  constructor() { }
}
