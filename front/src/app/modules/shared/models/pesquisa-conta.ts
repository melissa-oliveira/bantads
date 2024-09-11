import { Conta } from "./conta.model";

export class PesquisaConta {
    conta: Conta;

    constructor(conta: Conta = new Conta()) {
        this.conta = conta;
    }
}
