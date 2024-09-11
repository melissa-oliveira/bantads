export class ClienteConta {
    id: number;
    nome: string;
    cpf: string;

    constructor(id: number = 0, nome: string = '', cpf: string = '') {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
    }
}