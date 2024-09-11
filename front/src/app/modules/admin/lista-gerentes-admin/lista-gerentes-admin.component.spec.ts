import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaGerentesAdminComponent } from './lista-gerentes-admin.component';

describe('ListaGerentesAdminComponent', () => {
  let component: ListaGerentesAdminComponent;
  let fixture: ComponentFixture<ListaGerentesAdminComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListaGerentesAdminComponent]
    });
    fixture = TestBed.createComponent(ListaGerentesAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
