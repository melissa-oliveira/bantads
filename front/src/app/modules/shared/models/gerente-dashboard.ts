export class GerenteDashboard {
    id: number;
    nome: string;
    qntClientes: number;
    saldoPositivo: number;
    saldoNegativo: number;

    constructor() {
        this.id = 0;
        this.nome = '';
        this.qntClientes = 0;
        this.saldoPositivo = 0;
        this.saldoNegativo = 0;
    }
}
