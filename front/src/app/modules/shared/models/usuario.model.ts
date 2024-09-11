import { TipoUsuario } from "../enums/tipo-usuario";

export class Usuario {
    id: number;
    tipo: TipoUsuario;
    email: string;
    senha: string;
    cpf: string;

    constructor(
        id: number = 0,
        tipo: TipoUsuario = TipoUsuario.CLIENTE,
        email: string = '',
        senha: string = '',
        cpf: string = ''
    ) {
        this.id = id;
        this.tipo = tipo;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
    }
}
