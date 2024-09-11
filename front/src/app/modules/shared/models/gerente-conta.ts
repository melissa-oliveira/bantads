export class GerenteConta {
    id: number;
    nome: string;
    cpf: string;
    qntClientes: number;

    constructor(id: number = 0, nome: string = '', cpf: string = '', qntClientes: number = 0) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.qntClientes = qntClientes;
    }
}