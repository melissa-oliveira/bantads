export class Endereco {
    constructor(
        public id: number = 0,
        public tipo: string = '',
        public logradouro: string = '',
        public numero: string = '',
        public complemento: string = '',
        public cep: string = '',
        public bairro: string = '',
        public cidade: string = '',
        public estado: string = ''
    ) { }
}

export class Cliente {
    constructor(
        public id: number = 0,
        public nome: string = '',
        public email: string = '',
        public cpf: string = '',
        public telefone: string = '',
        public salario: number = 0,
        public endereco: Endereco = new Endereco(),
        public senha: string = ''
    ) { }
}
