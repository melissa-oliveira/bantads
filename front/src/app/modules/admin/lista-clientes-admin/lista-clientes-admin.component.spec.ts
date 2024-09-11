import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaClientesAdminComponent } from './lista-clientes-admin.component';

describe('ListaClientesAdminComponent', () => {
  let component: ListaClientesAdminComponent;
  let fixture: ComponentFixture<ListaClientesAdminComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListaClientesAdminComponent]
    });
    fixture = TestBed.createComponent(ListaClientesAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
