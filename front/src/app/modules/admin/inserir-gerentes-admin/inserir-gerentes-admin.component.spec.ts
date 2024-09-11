import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InserirGerentesAdminComponent } from './inserir-gerentes-admin.component';

describe('InserirGerentesAdminComponent', () => {
  let component: InserirGerentesAdminComponent;
  let fixture: ComponentFixture<InserirGerentesAdminComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InserirGerentesAdminComponent]
    });
    fixture = TestBed.createComponent(InserirGerentesAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
