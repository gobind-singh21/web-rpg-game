import { TestBed } from '@angular/core/testing';

import { BasicTargetService } from './basic-target.service';

describe('BasicTargetService', () => {
  let service: BasicTargetService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BasicTargetService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
