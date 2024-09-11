import { StatusConta } from "../enums/status-conta";
import { ClienteConta } from "./cliente-conta";
import { GerenteConta } from "./gerente-conta";

export class Conta {
    id: number;
    numeroConta: string;
    limite: number;
    saldo: number;
    dataCriacao: string;
    cliente: ClienteConta;
    gerente: GerenteConta;
    statusConta: StatusConta;

    constructor(
        id: number = 0,
        numeroConta: string = '',
        limite: number = 0,
        saldo: number = 0,
        dataCriacao: string = '',
        cliente: ClienteConta = new ClienteConta(),
        gerente: GerenteConta = new GerenteConta(),
        statusConta: StatusConta = StatusConta.ANALISE
    ) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.limite = limite;
        this.saldo = saldo;
        this.dataCriacao = dataCriacao;
        this.cliente = cliente;
        this.gerente = gerente;
        this.statusConta = statusConta;
    }
}


