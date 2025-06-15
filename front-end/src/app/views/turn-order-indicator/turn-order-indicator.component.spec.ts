import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TurnOrderIndicatorComponent } from './turn-order-indicator.component';

describe('TurnOrderIndicatorComponent', () => {
  let component: TurnOrderIndicatorComponent;
  let fixture: ComponentFixture<TurnOrderIndicatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TurnOrderIndicatorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TurnOrderIndicatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
