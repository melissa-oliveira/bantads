import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ContaService } from '../services/conta.service';
import { AnaliseConta, Cliente, Conta, ListaContas, ListaCpfs, PesquisaCliente, RespostaAnaliseConta, StatusConta } from '../../shared';
import { ClienteService } from '../services/cliente.service';
import { AuthService } from '../../auth/services/auth.service';
import { MailService } from '../services/mail.service';
import { Mail } from '../../shared/models/mail.model';

@Component({
  selector: 'app-tela-inicial-gerente',
  templateUrl: './tela-inicial-gerente.component.html',
  styleUrls: ['./tela-inicial-gerente.component.css']
})
export class TelaInicialGerenteComponent {
  @ViewChild('descricaoInput', { static: false }) descricaoInput!: ElementRef;
  cpfs: string[] = []
  clientes: Cliente[] = []
  analiseConta: AnaliseConta = new AnaliseConta();
  cpfGerente: string = '';
  motivoRecusa: string = '';
  cpfSelecionado: string = '';
  cliente: Cliente = new Cliente();



  setCpfSelecionado(cpf: string) {
    this.cpfSelecionado = cpf;
  }

  constructor(
    private contaService: ContaService,
    private clienteService: ClienteService,
    private authService: AuthService,
    private mailService: MailService,
  ) { }

  ngOnInit(): void {
    this.cpfGerente = this.authService.getCpfFromToken();
    this.listarTodosCpfs();
    this.cpfs = []
  }

  listarTodosCpfs(): void {
    this.contaService.listarTodosCpfsAnalise(this.cpfGerente).subscribe({
      next: (data: ListaCpfs) => {
        if (data == null) {
          this.cpfs = [];
        } else {
          this.cpfs = data.cpfs;

          this.cpfs.forEach(cpf => {
            this.clienteService.buscarPorCpf(cpf).subscribe({
              next: (data: PesquisaCliente) => {
                if (data == null) {
                  console.log("Cliente não encontrado");
                } else {
                  this.clientes.push(data.cliente);
                }
              }
            });
          });

        }
      }
    });
  }

  aprovarConta(cpfCliente: string): void {
    this.analiseConta.resultado = StatusConta.APROVADO;
    this.analiseConta.cpfCliente = cpfCliente;
  
    this.contaService.aprovarConta(this.analiseConta).subscribe({
      next: (data: RespostaAnaliseConta) => {
        if (!data) {
          console.log("Erro ao aprovar conta");
        } else {
          this.analiseConta = new AnaliseConta();
          this.clientes = [];
          this.listarTodosCpfs();
  
          this.clienteService.buscarPorCpf(this.cpfSelecionado).subscribe({
            next: (data: PesquisaCliente) => {
              if (!data) {
                console.log("Cliente não encontrado");
              } else {
                this.cliente = data.cliente;
                console.log(this.cliente);

                this.cliente.senha = this.gerarStringAleatoria()
  
                const subject = "Bem-vindo ao banco BANTADS! Acesse sua conta agora";
                const message = `Olá ${this.cliente.nome},
  
  Agradecemos por escolher o Banco Bantads! Notamos que sua conta foi recentemente aprovada e está pronta para uso. Para acessar sua conta e começar a explorar nossos serviços, utilize a senha fornecida abaixo:
  
  ${this.cliente.senha}
  
  Acesse agora mesmo para começar a utilizar nossos serviços online.
  
  Atenciosamente,
  A equipe Bantads`;
  
                let mailStructure = new Mail(subject, message);
                this.mailService.sendEmail(this.cliente.email, mailStructure).subscribe(
                  (response) => {
                    console.log("E-mail enviado com sucesso!");
                    this.clienteService.editarPerfil(this.cliente).subscribe({
                      next: (data: PesquisaCliente) => {
                        if (data == null) {
                
                        } else {
                          this.cliente = data.cliente;
                          console.log(this.cliente);
                        }
                      }
                    });

                  },
                  (error) => {
                    console.error("Erro ao enviar e-mail", error);
                  }
                );
              }
            },
            error: (err) => {
              console.error("Erro ao buscar cliente por CPF", err);
            }
          });
        }
      },
      error: (err) => {
        console.error("Erro ao aprovar conta", err);
      }
    });
  }
  
  
  

  capturarDescricao(): void {
    if (this.descricaoInput) {
      this.motivoRecusa = this.descricaoInput.nativeElement.value;
      this.recusarConta(this.cpfSelecionado);
    } else {
      console.error('Descrição não encontrada');
    }
  }

  recusarConta(cpfCliente: string): void {
    this.analiseConta.resultado = StatusConta.RECUSADO;
    this.analiseConta.cpfCliente = cpfCliente;
    this.analiseConta.motivoRecusa = this.motivoRecusa;

    this.contaService.recusarConta(this.analiseConta).subscribe({
      next: (data: RespostaAnaliseConta) => {
        if (data == null) {
          console.log("Erro ao recusar conta");
        } else {
          this.analiseConta = new AnaliseConta();
          this.clientes = [];
          this.listarTodosCpfs();

          this.clienteService.buscarPorCpf(this.cpfSelecionado).subscribe({
            next: (data: PesquisaCliente) => {
              if (!data) {
                console.log("Cliente não encontrado");
              } else {
                this.cliente = data.cliente;
                console.log(this.cliente);

                const subject = "Conta recusada ao Banco Bantads";
                const message = `Olá ${this.cliente.nome},

Infelizmente, sua conta foi recusada devido ao seguinte motivo:

${this.motivoRecusa}

Caso ache que tenha ocorrido um erro, por favor retorne neste mesmo e-mail.

Atenciosamente,
A equipe Bantads`;

                let mailStructure = new Mail(subject, message);
                this.mailService.sendEmail(this.cliente.email, mailStructure).subscribe(
                  (response) => {
                    console.log("E-mail enviado com sucesso!");
                  },
                  (error) => {
                    console.error("Erro ao enviar e-mail", error);
                  }
                );
              }
            },
            error: (err) => {
              console.error("Erro ao buscar cliente por CPF", err);
            }
          });
        }
      }
    });
  }

  gerarNumeroAleatorio() {
    return Math.floor(Math.random() * 10);
  }

  gerarStringAleatoria() {
    let numeroAleatorio1 = this.gerarNumeroAleatorio();
    let numeroAleatorio2 = this.gerarNumeroAleatorio();
    let numeroAleatorio3 = this.gerarNumeroAleatorio();
    let numeroAleatorio4 = this.gerarNumeroAleatorio();

    let stringAleatoria = `${numeroAleatorio1}${numeroAleatorio2}${numeroAleatorio3}${numeroAleatorio4}`;

    return stringAleatoria;
  }

  gerarSequenciaBinaria(): string {
    let sequenciaBinaria = "";
    for (let i = 0; i < 12; i++) {
      sequenciaBinaria += Math.floor(Math.random() * 2).toString();
    }
    return sequenciaBinaria;
  }


}
