import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaClientesGerenteComponent } from './lista-clientes-gerente.component';

describe('ListaClientesGerenteComponent', () => {
  let component: ListaClientesGerenteComponent;
  let fixture: ComponentFixture<ListaClientesGerenteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListaClientesGerenteComponent]
    });
    fixture = TestBed.createComponent(ListaClientesGerenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
