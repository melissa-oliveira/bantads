import { Usuario } from "./usuario.model";

export class PesquisaUsuario {
    usuario: Usuario;

    constructor(usuario: Usuario = new Usuario()) {
        this.usuario = usuario;
    }
}
