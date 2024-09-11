import { StatusConta } from "../enums/status-conta";

export class AnaliseConta {
    public cpfCliente: string;
    public resultado: StatusConta;
    public motivoRecusa: string;

    constructor() {
        this.cpfCliente = "";
        this.resultado = StatusConta.ANALISE;
        this.motivoRecusa = "";
    }
}
