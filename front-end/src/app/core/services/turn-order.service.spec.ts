import { TestBed } from '@angular/core/testing';

import { TurnOrderService } from './turn-order.service';

describe('TurnOrderService', () => {
  let service: TurnOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TurnOrderService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
