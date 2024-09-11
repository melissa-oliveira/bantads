import { Gerente } from "./gerente.model";

export class PesquisaGerente {
    gerente: Gerente;

    constructor(gerente: Gerente = new Gerente()) {
        this.gerente = gerente;
    }
}
