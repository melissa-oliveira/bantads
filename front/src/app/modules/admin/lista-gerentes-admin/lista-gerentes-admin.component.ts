import { Component, OnInit } from '@angular/core';
import { GerenteService } from '../services/gerente.service';
import { Gerente, ListaGerentes } from '../../shared/index';

@Component({
  selector: 'app-lista-gerentes-admin',
  templateUrl: './lista-gerentes-admin.component.html',
  styleUrls: ['./lista-gerentes-admin.component.css']
})
export class ListaGerentesAdminComponent implements OnInit {
  gerentes: Gerente[] = [];
  mensagemErro: string | null = null;

  constructor(private gerenteService: GerenteService) { }

  ngOnInit(): void {
    this.listarTodos();
    this.gerentes = []
    this.ordenarPorNome();
  }

  listarTodos(): void {
    this.gerenteService.listarTodos().subscribe({
      next: (data: ListaGerentes) => {
        if (data == null) {
          this.gerentes = [];
        } else {
          this.gerentes = data.gerentes;
        }
      }
    });
  }


  remover($event: any, gerente: Gerente): void {
    $event.preventDefault();
    if (this.gerentes.length <= 1) {
      this.abrirErro();
      return;
    }

    if (confirm(`Deseja realmente remover o gerente ${gerente.nome}?`)) {
      this.gerenteService.remover(gerente.cpf!).subscribe({
        complete: () => {
          this.listarTodos();
        }
      });
    }
  }

  formatarCPF(cpf: string): string {
    // Formatar CPF para 000.000.000-00
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

  formatarTelefone(telefone: string): string {
    // Formatar telefone para (00) 00000-0000
    return telefone.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
  }

  ordenarPorNome(): void {
    this.gerentes.sort((a, b) => {
      if (!a.nome || !b.nome) {
        return 0;
      }

      const nomeA = a.nome.toLowerCase();
      const nomeB = b.nome.toLowerCase();

      if (nomeA < nomeB) {
        return -1;
      }
      if (nomeA > nomeB) {
        return 1;
      }
      return 0;
    });
  }

  abrirErro() {
    this.mensagemErro = 'Infelizmente, não é possível remover o último gerente da lista!';
  }


}
