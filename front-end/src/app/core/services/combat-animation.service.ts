import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { CharacterSnapshot } from '../../shared/types/characterSnapshot';
import { CombatAnimationEvent } from '../../shared/types/combatAnimationEvent';

// The fixed duration for our animations in milliseconds
const ANIMATION_DURATION_MS = 1000;

@Injectable({
  providedIn: 'root'
})
export class CombatAnimationService {
  // A Subject is an Observable that we can manually trigger from within the service
  private animationEventSubject = new Subject<CombatAnimationEvent>();

  // Components will subscribe to this public Observable to receive animation events
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
    // We wrap the entire logic in a Promise
    return new Promise(resolve => {
      let animationsFired = false;

      // Compare each new snapshot to its old version
      for (const newSnap of newSnapshots) {
        const charId = newSnap.id as number;
        const oldSnap = oldSnapshots.get(charId);

        if (oldSnap) {
          const oldTotalHp = oldSnap.currentHealth + oldSnap.shield;
          const newTotalHp = newSnap.currentHealth + newSnap.shield;

          const damage = oldTotalHp - newTotalHp;
          const healing = newSnap.currentHealth - oldSnap.currentHealth;

          // If there was any change, fire an event
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

      // If any animations were triggered, wait for the duration. Otherwise, resolve immediately.
      if (animationsFired) {
        setTimeout(() => {
          resolve(); // Resolve the promise after the wait
        }, ANIMATION_DURATION_MS);
      } else {
        resolve(); // No animations, resolve immediately
      }
    });
  }
}
