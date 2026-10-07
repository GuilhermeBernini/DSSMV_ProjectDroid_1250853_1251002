# Esquema das coleções (restdb.io)

Todos os pedidos levam o cabeçalho `x-apikey` (a chave está em `local.properties`, nunca no Git).
O restdb.io acrescenta automaticamente o campo `_id` a cada documento.

## minijogos
| Campo | Tipo | Descrição |
|---|---|---|
| nome | texto | Nome do mini-jogo |
| descricao | texto | Regras resumidas |
| tipo | texto | `reflexos`, `batata_quente`, `contar_vibracoes` |

## desafios
| Campo | Tipo | Descrição |
|---|---|---|
| texto | texto | Pergunta ou desafio |
| resposta | texto | Resposta (se aplicável) |
| minijogo | texto | Mini-jogo associado |
| personalizado | booleano | `true` se criado pelos jogadores |

## jogadores
| Campo | Tipo | Descrição |
|---|---|---|
| nome | texto | Nome do jogador |
| pontuacao | número | Pontuação total |

## pontuacoes (opcional)
| Campo | Tipo | Descrição |
|---|---|---|
| jogador | texto | `_id` do jogador |
| ronda | número | Número da ronda |
| pontos | número | Pontos ganhos |
| data | data | Data/hora |

## Pedidos usados

| Operação | Pedido |
|---|---|
| GET mini-jogos | `GET /rest/minijogos` |
| GET desafios de um jogo | `GET /rest/desafios?q={"minijogo":"reflexos"}` |
| POST jogador | `POST /rest/jogadores` |
| POST desafio personalizado | `POST /rest/desafios` |
| PUT pontuação | `PUT /rest/jogadores/{id}` |
| DELETE desafio personalizado | `DELETE /rest/desafios/{id}` |
