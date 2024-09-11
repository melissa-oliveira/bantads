import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Cliente, Endereco } from '../../shared';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { AutocadastroService } from '../services/autocadastro.service';
import { MailService } from '../services/mail.service';
import { Mail } from '../../shared/models/mail.model';
import { ViacepService } from '../services/viacep.service';

declare var bootstrap: any;

@Component({
  selector: 'app-autocadastro',
  templateUrl: './autocadastro.component.html',
  styleUrls: ['./autocadastro.component.css']
})
export class AutocadastroComponent implements OnInit {

  @ViewChild('formAutocadastro') formAutocadastro!: NgForm;
  @ViewChild('modalConfirmacao', { static: true }) modalConfirmacao!: ElementRef;

  cliente: Cliente = new Cliente();
  endereco: Endereco = new Endereco(0, "", "", "", "", "", "", "", "");
  isFormValid = false;
  mensagemErro: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private autocadastroService: AutocadastroService,
    private mailService: MailService,
    private viacepService: ViacepService
  ) { }
  

  ngOnInit(): void {
    this.cliente = new Cliente(0, "", "", "", "", 0, new Endereco(), "");
    this.endereco = new Endereco(0, "", "", "", "", "", "", "", "");
  }  

  converterParaFloat(valorEmString: string): number {
    console.log("Valor original:", valorEmString);
    
    let valorFormatado = valorEmString.replace("R$", "").trim();
    valorFormatado = valorFormatado.replace(/\./g, "").replace(",", ".");
    
    const valorFloat = parseFloat(valorFormatado);
    console.log("Valor final em float:", valorFloat);
    
    return valorFloat;
  }
  

  inserir(): void {
    if (this.formAutocadastro.form.valid) {
      this.cliente.senha = '';
  
      if (this.cliente.salario !== undefined) {
        let valorEmString: string = this.cliente.salario.toString();
        this.cliente.salario = this.converterParaFloat(valorEmString);
        console.log(this.cliente.salario);
      }
  
      this.cliente.endereco = this.endereco;
  
      this.autocadastroService.inserir(this.cliente).subscribe({
        next: (cliente) => {
          this.abrirModalConfirmacao();
          console.log(this.cliente);
          console.log("Cliente recebido do serviço:", JSON.stringify(cliente));
        },
        error: (error: any) => {
          if (error.status === 400) {
            this.abrirErro();
            if (!this.cliente.email) {
              console.log("E-mail não fornecido");
              return;
            }
  
            const subject = "Erro de Cadastro no Banco BANTADS!";
            const message = `Olá,
  
            Agradecemos por escolher o Banco Bantads. Infelizmente, ocorreu um erro durante o processo de cadastro de sua conta. Pedimos desculpas pelo transtorno causado e estamos trabalhando para resolver o problema o mais rápido possível.
  
            Por favor, entre em contato com nosso suporte ao cliente respondendo este e-mail para que possamos ajudá-lo a completar seu cadastro e garantir que você tenha acesso aos nossos serviços.
  
            Agradecemos pela sua paciência e compreensão.
  
            Atenciosamente,
            A equipe Bantads`;
  
            let mailStructure = new Mail(subject, message);
            this.mailService.sendEmail(this.cliente.email, mailStructure).subscribe(
              (response) => {
                console.log("E-mail enviado com sucesso!");
              }
            );
          }
        }
      });
    }
  }
  
  onCepChange(): void {
    const cep = this.endereco.cep;
    if (cep && cep.length === 8) {
      this.viacepService.consultarCEP(cep).subscribe(data => {
        if (data) {
          this.endereco.logradouro = data.logradouro;
          this.endereco.bairro = data.bairro;
          this.endereco.cidade = data.localidade;
          this.endereco.estado = data.uf;
        }
      });
    }
  }

  abrirErro() {
    this.mensagemErro = 'Dados já cadastrados! Faça login.';
  }

  abrirModalConfirmacao() {
    const modalElement = this.modalConfirmacao.nativeElement;
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
  }


}
