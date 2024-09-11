import { AnaliseConta } from "./analise-conta";

export class RespostaAnaliseConta {
    resposta: AnaliseConta;

    constructor(resposta: AnaliseConta = new AnaliseConta()) {
        this.resposta = resposta;
    }
}
