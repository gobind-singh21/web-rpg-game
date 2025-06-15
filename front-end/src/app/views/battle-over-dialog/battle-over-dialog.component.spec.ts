import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BattleOverDialogComponent } from './battle-over-dialog.component';

describe('BattleOverDialogComponent', () => {
  let component: BattleOverDialogComponent;
  let fixture: ComponentFixture<BattleOverDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BattleOverDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BattleOverDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
