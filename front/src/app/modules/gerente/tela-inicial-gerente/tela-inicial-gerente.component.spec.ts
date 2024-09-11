import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TelaInicialGerenteComponent } from './tela-inicial-gerente.component';

describe('TelaInicialGerenteComponent', () => {
  let component: TelaInicialGerenteComponent;
  let fixture: ComponentFixture<TelaInicialGerenteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TelaInicialGerenteComponent]
    });
    fixture = TestBed.createComponent(TelaInicialGerenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
