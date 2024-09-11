import { Cliente } from "./cliente.model";

export class PesquisaCliente {
    cliente: Cliente;

    constructor(cliente: Cliente = new Cliente()) {
        this.cliente = cliente;
    }
}
