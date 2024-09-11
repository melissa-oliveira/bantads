import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TelaInicialClienteComponent } from './tela-inicial-cliente.component';

describe('TelaInicialClienteComponent', () => {
  let component: TelaInicialClienteComponent;
  let fixture: ComponentFixture<TelaInicialClienteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TelaInicialClienteComponent]
    });
    fixture = TestBed.createComponent(TelaInicialClienteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
