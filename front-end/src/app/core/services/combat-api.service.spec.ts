import { TestBed } from '@angular/core/testing';

import { CombatApiService } from './combat-api.service';

describe('CombatApiService', () => {
  let service: CombatApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CombatApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
