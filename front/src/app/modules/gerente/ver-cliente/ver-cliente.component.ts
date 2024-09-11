import { Component } from '@angular/core';
import { Cliente, Conta, PesquisaCliente, PesquisaConta, Usuario } from '../../shared';
import { ClienteService } from '../services/cliente.service';
import { ContaService } from '../services/conta.service';
import { AuthService } from '../../auth/services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-ver-cliente',
  templateUrl: './ver-cliente.component.html',
  styleUrls: ['./ver-cliente.component.css']
})
export class VerClienteComponent {

  usuario: Usuario = new Usuario();
  conta: Conta = new Conta();
  cliente: Cliente = new Cliente();
  cpf: string = '';

  constructor(
    private clienteService: ClienteService,
    private contaService: ContaService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.cpf = this.route.snapshot.params['cpf'];
    this.buscarContaPorCPf();
    this.buscarClientePorCPf();
  }

  buscarContaPorCPf(): void {
    this.contaService.buscarPorCpf(this.cpf).subscribe({
      next: (data: PesquisaConta) => {
        if (data == null) {

        } else {
          this.conta = data.conta;
          console.log(this.conta);
        }
      }
    });
  }

  buscarClientePorCPf(): void {
    this.clienteService.buscarPorCpf(this.cpf).subscribe({
      next: (data: PesquisaCliente) => {
        if (data == null) {

        } else {
          this.cliente = data.cliente;
          console.log(this.cliente);
        }
      }
    });
  }
}
