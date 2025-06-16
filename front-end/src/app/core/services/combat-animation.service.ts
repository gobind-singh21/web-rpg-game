import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { CharacterSnapshot } from '../../shared/types/characterSnapshot';
import { CombatAnimationEvent } from '../../shared/types/combatAnimationEvent';

const ANIMATION_DURATION_MS = 1000;

@Injectable({
  providedIn: 'root'
})
export class CombatAnimationService {
  private animationEventSubject = new Subject<CombatAnimationEvent>();

  public animationEvents$ = this.animationEventSubject.asObservable();

  constructor() { }

  /**
   * This is the main method. It compares old and new states, fires events,
   * and returns a Promise that resolves after the animation duration.
   * @param oldSnapshots The state of all characters BEFORE the action.
   * @param newSnapshots The state of all characters AFTER the action.
   * @returns A Promise that resolves when the animations are considered finished.
   */
  public playActionAnimations(oldSnapshots: Map<number, CharacterSnapshot>, newSnapshots: CharacterSnapshot[]): Promise<void> {
    return new Promise(resolve => {
      let animationsFired = false;

      for (const newSnap of newSnapshots) {
        const charId = newSnap.id as number;
        const oldSnap = oldSnapshots.get(charId);

        if (oldSnap) {
          const oldTotalHp = oldSnap.currentHealth + oldSnap.shield;
          const newTotalHp = newSnap.currentHealth + newSnap.shield;

          const damage = oldTotalHp - newTotalHp;
          const healing = newSnap.currentHealth - oldSnap.currentHealth;

          if (damage > 0 || healing > 0) {
            animationsFired = true;
            this.animationEventSubject.next({
              characterId: charId,
              damage: damage > 0 ? damage : 0,
              healing: healing > 0 ? healing : 0
            });
          }
        }
      }

      if (animationsFired) {
        setTimeout(() => {
          resolve();
        }, ANIMATION_DURATION_MS);
      } else {
        resolve();
      }
    });
  }
}
