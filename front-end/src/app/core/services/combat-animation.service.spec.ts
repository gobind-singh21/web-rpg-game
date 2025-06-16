import { TestBed } from '@angular/core/testing';

import { CombatAnimationService } from './combat-animation.service';

describe('CombatAnimationService', () => {
  let service: CombatAnimationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CombatAnimationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
