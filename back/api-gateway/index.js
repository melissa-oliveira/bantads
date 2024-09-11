// Importações
require('dotenv-safe').config()
const jwt = require('jsonwebtoken')
var http = require('http')
const express = require('express')
const httpProxy = require('express-http-proxy')
const app = express()
var cookieParser = require('cookie-parser')
var bodyParser = require('body-parser')
var logger = require('morgan')
const helmet = require('helmet')
const cors = require('cors')

// Configurações do app
app.use(cors())
app.use(cors({ origin: 'http://localhost:4200' }))
app.use(bodyParser.urlencoded({ extended: false }))
app.use(logger('dev'))
app.use(helmet())
app.use(express.json())
app.use(express.urlencoded({ extended: false }))
app.use(cookieParser())

// Constantes
const PORT = 3000
const HOST_AUTENTICACAO = 'http://ms-auth:8080'
const HOST_CLIENTE = 'http://ms-cliente:8081'
const HOST_GERENTE = 'http://ms-gerente:8082'
const HOST_CONTA = 'http://ms-conta:8083'
const HOST_SAGA = 'http://ms-saga:8084'
const SECRET = 'pijGXMDV@3FhKt5&B$!pQjP^Jm5443bf^4GvJ2f4'

// Proxy de login
const loginServiceProxy = httpProxy(HOST_AUTENTICACAO, {
  proxyReqBodyDecorator: function (bodyContent, srcReq) {
    try {
      const { email, senha } = bodyContent
      const retBody = {}
      retBody.email = email
      retBody.senha = senha
      bodyContent = retBody
      console.log(bodyContent)
    } catch (e) {
      console.log('ERRO: ' + e)
    }
    return bodyContent
  },
  proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
    proxyReqOpts.headers['Content-Type'] = 'application/json'
    proxyReqOpts.method = 'POST'
    return proxyReqOpts
  },
  userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
    console.log(userReq.body)
    if (proxyRes.statusCode === 200) {
      const str = Buffer.from(proxyResData).toString('utf-8')
      const objBody = JSON.parse(str)
      const cpf = objBody.cpf
      const tipo = objBody.tipo
      const token = jwt.sign({ cpf, tipo }, SECRET, { expiresIn: 36000 })
      userRes.status(200)
      return { auth: true, token, data: objBody }
    } else {
      console.log
      userRes.status(401)
      return { message: 'Login inválido!' }
    }
  }
})

// Token
function verifyJWT(req, res, next) {
  const token = req.headers['x-access-token']
  console.log(req.headers, token)
  if (!token)
    return res
      .status(401)
      .json({ auth: false, message: 'Token não fornecido.' })

  jwt.verify(token, SECRET, (err, decoded) => {
    console.log('Token:' + token)
    if (err)
      return res
        .status(500)
        .json({ auth: false, message: 'Falha ao autenticar o token.' })
    req.userId = decoded.id
    next()
  })
}

//////////////////
// AUTENTICAÇÃO //
//////////////////

// Realizar Login (Via MS-AUTH)
app.post('/auth/login', (req, res, next) => {
  loginServiceProxy(req, res, next)
})

// Realizar Logout
app.post('/logout', verifyJWT, (req, res) => {
  res.json({ auth: false, token: null })
})

// Realizar autocadastro (Via MS-SAGA)
app.post(`/saga/autocadastro`, (req, res, next) => {
  httpProxy(HOST_SAGA, {
    userResDecorator: function (proxyRes, _proxyResData, _userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        userRes.status(200)
        return { message: 'Conta criada com sucesso.' }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao criar conta.' }
      }
    }
  })(req, res, next)
})

// Buscar usuário por cpf (Via MS-AUTH)
app.get(`/auth/usuario/cpf/:cpf`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_AUTENTICACAO, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { usuario: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao buscar usuario.' }
      }
    }
  })(req, res, next)
})

/////////////
// CLIENTE //
/////////////

// Consultar conta (Via MS-CONTA)
app.get(`/conta/conta/cliente/:cpf`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_CONTA, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { conta: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao buscar conta de cliente.' }
      }
    }
  })(req, res, next)
})

// Consultar cliente por CPF (Via MS-CLIENTE)
app.get(`/cliente/cliente/cpf/:cpf`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_CLIENTE, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { cliente: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao buscar clientes.' }
      }
    }
  })(req, res, next)
})

// Transações: Depósito, Saque e Transferência (Via MS-CONTA)
app.post(`/conta/conta/transacao`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_CONTA, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { transacao: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Saldo insuficiente para realizar transacao.' }
      }
    }
  })(req, res, next)
})

// Consultar extrato (Via MS-CONTA)
app.get(`/conta/extrato/:numeroConta`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_CONTA, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { historicos: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao consultar extrato.' }
      }
    }
  })(req, res, next)
})

// Editar perfil (Via MS-SAGA)
app.put(`/saga/cliente`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_SAGA, {
    userResDecorator: function (proxyRes, _proxyResData, _userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        userRes.status(200)
        return { message: 'Cliente editado com sucesso.' }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao editar cliente.' }
      }
    }
  })(req, res, next)
})

/////////////
// GERENTE //
/////////////

// Listar de cpfs de clientes para aprovar por cpf gerente
app.get(
  `/conta/conta/analise/cpf/gerente/:cpf`,
  verifyJWT,
  async (req, res, next) => {
    httpProxy(HOST_CONTA, {
      proxyReqBodyDecorator: function (bodyContent, srcReq) {
        return bodyContent
      },
      proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json'
        return proxyReqOpts
      },
      userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
        if (proxyRes.statusCode == 200) {
          const str = Buffer.from(proxyResData).toString('utf-8')
          const objBody = JSON.parse(str)
          userRes.status(200)
          return { cpfs: objBody }
        } else {
          userRes.status(proxyRes.statusCode)
          return { message: 'Erro ao listar cpfs para analise.' }
        }
      }
    })(req, res, next)
  }
)

// Listar clientes por cpf de gerente
app.get(
  `/conta/conta/aprovado/gerente/:cpf`,
  verifyJWT,
  async (req, res, next) => {
    httpProxy(HOST_CONTA, {
      proxyReqBodyDecorator: function (bodyContent, srcReq) {
        return bodyContent
      },
      proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json'
        return proxyReqOpts
      },
      userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
        if (proxyRes.statusCode == 200) {
          const str = Buffer.from(proxyResData).toString('utf-8')
          const objBody = JSON.parse(str)
          userRes.status(200)
          return { contas: objBody }
        } else {
          userRes.status(proxyRes.statusCode)
          return { message: 'Erro ao listar contas de gerente.' }
        }
      }
    })(req, res, next)
  }
)

// Aprovar conta (Via MS-CONTA)
app.post(`/conta/conta/aprovar`, (req, res, next) => {
  httpProxy(HOST_CONTA, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { resposta: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao aprovar conta.' }
      }
    }
  })(req, res, next)
})

// Reprovar conta (Via MS-CONTA)
app.post(`/conta/conta/recusar`, (req, res, next) => {
  httpProxy(HOST_CONTA, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { resposta: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao reprovar conta.' }
      }
    }
  })(req, res, next)
})

// Listar 3 melhores clientes de gerente (Via MS-CONTA)
app.get(
  `/conta/conta/melhoresClientes/:cpf`,
  verifyJWT,
  async (req, res, next) => {
    httpProxy(HOST_CONTA, {
      proxyReqBodyDecorator: function (bodyContent, srcReq) {
        return bodyContent
      },
      proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json'
        return proxyReqOpts
      },
      userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
        if (proxyRes.statusCode == 200) {
          const str = Buffer.from(proxyResData).toString('utf-8')
          const objBody = JSON.parse(str)
          userRes.status(200)
          return { contas: objBody }
        } else {
          userRes.status(proxyRes.statusCode)
          return { message: 'Erro ao listar melhores contas.' }
        }
      }
    })(req, res, next)
  }
)

///////////
// ADMIN //
///////////

// Dashboard de gerentes (Via MS-CONTA)
app.get(`/conta/gerente/dashboard`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_CONTA, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { gerentesDashboard: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao listar gerentes.' }
      }
    }
  })(req, res, next)
})

// Listar todas as contas dos clientes (Via MS-CONTA)
app.get(`/conta/conta/aprovado`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_CONTA, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { contas: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao listar contas aprovadas.' }
      }
    }
  })(req, res, next)
})

// CRUD de gerente
// Buscar Gerente por ID (Via MS-GERENTE)
app.get(`/gerente/gerente/:id`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_GERENTE, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, _userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { gerente: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao buscar gerente.' }
      }
    }
  })(req, res, next)
})

// Buscar Gerente por Email (Via MS-AUTH)
app.get(`/auth/usuario/:email`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_AUTENTICACAO, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, _userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { usuario: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao buscar gerente.' }
      }
    }
  })(req, res, next)
})

// Listar Gerente (Via MS-GERENTE)
app.get(`/gerente/gerente`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_GERENTE, {
    proxyReqBodyDecorator: function (bodyContent, srcReq) {
      return bodyContent
    },
    proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
      proxyReqOpts.headers['Content-Type'] = 'application/json'
      return proxyReqOpts
    },
    userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        const str = Buffer.from(proxyResData).toString('utf-8')
        const objBody = JSON.parse(str)
        userRes.status(200)
        return { gerentes: objBody }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao listar gerentes.' }
      }
    }
  })(req, res, next)
})

// Criar Gerente (Via MS-SAGA)
app.post(`/saga/gerente`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_SAGA, {
    userResDecorator: function (proxyRes, _proxyResData, _userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        userRes.status(200)
        return { message: 'Gerente criado com sucesso.' }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao criar gerente.' }
      }
    }
  })(req, res, next)
})

// Editar Gerente (Via MS-SAGA)
app.put(`/saga/gerente`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_SAGA, {
    userResDecorator: function (proxyRes, _proxyResData, _userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        userRes.status(200)
        return { message: 'Gerente editado com sucesso.' }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao editar gerente.' }
      }
    }
  })(req, res, next)
})

// Excluir Gerente (Via MS-SAGA)
app.delete(`/saga/gerente/:cpf`, verifyJWT, async (req, res, next) => {
  httpProxy(HOST_SAGA, {
    userResDecorator: function (proxyRes, _proxyResData, _userReq, userRes) {
      if (proxyRes.statusCode == 200) {
        userRes.status(200)
        return { message: 'Gerente excluído com sucesso.' }
      } else {
        userRes.status(proxyRes.statusCode)
        return { message: 'Erro ao excluir gerente.' }
      }
    }
  })(req, res, next)
})

// Criação do servidor HTTP na porta 3000
var server = http.createServer(app)
server.listen(PORT, () => console.log(`Server running on port ${PORT}`))
