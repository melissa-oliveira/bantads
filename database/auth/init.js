conn = new Mongo();
db = conn.getDB("ms-auth");

db.login.createIndex({ email: 1 }, { unique: true });
db.login.createIndex({ cpf: 1 }, { unique: true });

// Criar a coleção e inserir documentos
db.login.insertMany([
    {
        _id: ObjectId(),
        "tipo": "ADMIN",
        "email": "admin@email.com",
        "cpf": null,
        "senha": "FQKg6zHmYMf+IIernR5lLPfc3W37FCyRInxaV9k12Lw=",
        "salt": "WknJuep3qDYBcg=="
    },
]);

// Criar índices únicos para os campos 'cpf' e 'email'
db.login.createIndex({ cpf: 1 }, { unique: true });
db.login.createIndex({ email: 1 }, { unique: true });