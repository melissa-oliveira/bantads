import { TipoTransacao } from "../enums/tipo-transacao";

export class Transacao {
    tipo: TipoTransacao;
    valor: number;
    contaOrigem: string;
    contaDestino: string;
    dataHora: string;
    success: boolean;

    constructor(
        tipo: TipoTransacao = TipoTransacao.DEPOSITO,
        valor: number = 0,
        contaOrigem: string = '',
        contaDestino: string = '',
        dataHora: string = '',
        success: boolean = false
    ) {
        this.tipo = tipo;
        this.valor = valor;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.dataHora = dataHora;
        this.success = success
    }
}
