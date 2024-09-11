import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../services/cliente.service';
import { Cliente, Conta, ListaContas, StatusConta } from '../../shared';

@Component({
  selector: 'app-lista-clientes-admin',
  templateUrl: './lista-clientes-admin.component.html',
  styleUrls: ['./lista-clientes-admin.component.css']
})
export class ListaClientesAdminComponent implements OnInit {
  contas: Conta[] = []
  statusContaAprovado: StatusConta = StatusConta.APROVADO;

  constructor(private clienteService: ClienteService) { }

  ngOnInit(): void {
    this.listarTodos();
    this.contas = []
  }

  listarTodos(): void {
    this.clienteService.listarTodosAprovados().subscribe({
      next: (data: ListaContas) => {
        if (data == null) {
          this.contas = [];
        } else {
          const contasOrdenadas = data.contas.sort((a, b) => {
            if (a.cliente.nome < b.cliente.nome) {
              return -1;
            }
            if (a.cliente.nome > b.cliente.nome) {
              return 1;
            }
            return 0;
          });

          this.contas = contasOrdenadas;
        }
      }
    });
  }
}
