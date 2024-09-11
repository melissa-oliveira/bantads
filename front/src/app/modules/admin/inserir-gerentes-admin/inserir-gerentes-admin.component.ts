import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Gerente } from '../../shared/index';
import { Router, RouterModule } from '@angular/router';
import { GerenteService } from '../services/gerente.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-inserir-gerentes-admin',
  templateUrl: './inserir-gerentes-admin.component.html',
  styleUrls: ['./inserir-gerentes-admin.component.css']
})
export class InserirGerentesAdminComponent implements OnInit {

  @ViewChild('formGerente') formGerente!: NgForm;
  gerente!: Gerente;

  constructor(
    private gerenteService: GerenteService,
    private router: Router) { }

  ngOnInit(): void {
    this.gerente = new Gerente(0, "", "", "", "", "");
  }

  inserir(): void {
    if (this.formGerente.form.valid) {
      console.log("Gerente antes de enviar:", JSON.stringify(this.gerente));
      this.gerenteService.inserir(this.gerente).subscribe({
        complete: () => {
          this.router.navigate(['/admin/gerentes']);
        }
      });
    }
  }

}
