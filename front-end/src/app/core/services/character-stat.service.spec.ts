import { TestBed } from '@angular/core/testing';

import { CharacterSnapService } from './character-stat.service';

describe('CharacterService', () => {
  let service: CharacterSnapService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CharacterSnapService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
