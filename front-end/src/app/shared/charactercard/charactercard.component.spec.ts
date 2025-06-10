import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CharactercardComponent } from './charactercard.component';

describe('CharactercardComponent', () => {
  let component: CharactercardComponent;
  let fixture: ComponentFixture<CharactercardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CharactercardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CharactercardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
