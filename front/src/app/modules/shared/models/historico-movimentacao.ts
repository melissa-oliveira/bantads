import { TipoTransacao } from "../enums/tipo-transacao";
import { ClienteConta } from "./cliente-conta";

export class HistoricoMovimentacao {
    tipo: TipoTransacao;
    valor: number;
    clienteOrigem: ClienteConta;
    clienteDestino: ClienteConta;
    dataHora: string;

    constructor() {
        this.tipo = TipoTransacao.DEPOSITO;
        this.valor = 0;
        this.clienteOrigem = new ClienteConta();
        this.clienteDestino = new ClienteConta();
        this.dataHora = '';
    }
}
