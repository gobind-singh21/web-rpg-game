import { TestBed } from '@angular/core/testing';

import { TeamStateService } from './team-state.service';

describe('TeamStateService', () => {
  let service: TeamStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
