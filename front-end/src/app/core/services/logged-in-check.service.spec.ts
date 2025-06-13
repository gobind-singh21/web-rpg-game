import { TestBed } from '@angular/core/testing';

import { LoggedInCheckService } from './logged-in-check.service';

describe('LoggedInCheckService', () => {
  let service: LoggedInCheckService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoggedInCheckService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
