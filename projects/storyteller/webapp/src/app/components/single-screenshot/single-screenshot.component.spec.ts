import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleScreenshotComponent } from './single-screenshot.component';

describe('SingleScreenshotComponent', () => {
  let component: SingleScreenshotComponent;
  let fixture: ComponentFixture<SingleScreenshotComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SingleScreenshotComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleScreenshotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

