import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ViewChild } from '@angular/core';
import { Gerente } from '../../shared/index';
import { Router, RouterModule, Route, ActivatedRoute } from '@angular/router';
import { GerenteService } from '../services/gerente.service';

@Component({
  selector: 'app-editar-gerente',
  templateUrl: './editar-gerente.component.html',
  styleUrls: ['./editar-gerente.component.css']
})
export class EditarGerenteComponent implements OnInit {

  @ViewChild('formGerente') formGerente!: NgForm;
  id!: string;
  loading!: boolean;
  gerente!: Gerente;

  constructor(
    private gerenteService: GerenteService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit(): void {
    this.gerente = new Gerente(0, "", "", "", "", "");
    this.id = this.route.snapshot.params['id'];

    this.gerenteService.buscarPorId(+this.id).subscribe(resultado => {
      this.gerente = resultado.gerente;

      this.gerenteService.buscarUsuarioPorId(this.gerente.email).subscribe(resultado => {
        this.gerente.senha = resultado.usuario.senha;
      });
    });
  }

  atualizar(): void {
    if (this.formGerente.form.valid) {
      this.gerenteService.atualizar(this.gerente).subscribe(gerente => {
        this.router.navigate(['/admin/gerentes']);
      });
    }
  }

}
