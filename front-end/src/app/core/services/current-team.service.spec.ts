import { TestBed } from '@angular/core/testing';

import { CurrentTeamService } from './current-team.service';

describe('CurrentTeamService', () => {
  let service: CurrentTeamService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CurrentTeamService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
