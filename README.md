# SpinParty

Party game Android (Java) baseado nos sensores do telemóvel.

**Unidade curricular:** Desenvolvimento de Software e Sistemas Móveis (DSSMV) 2026/2027
**Instituição:** ISEP, LETI, turma 2DD, equipa 10

## Equipa

| Nome | Número |
|---|---|
| Dinis Rodrigues | 1250853 |
| Guilherme Bernini | 1251002 |

## Descrição

O jogador coloca o telemóvel a girar. Enquanto roda, passam números no ecrã; quando o telemóvel para, é sorteado um mini-jogo. A aplicação obtém da API os mini-jogos e os respetivos desafios. Os jogadores acumulam pontos ao longo das rondas.

Mini-jogos previstos:
- **Reflexos:** ganha quem pegar primeiro no telemóvel quando o ecrã fica verde.
- **Batata quente:** o telemóvel é passado entre jogadores até "explodir".
- **Contar vibrações:** adivinhar o número de vibrações.

## Hardware do dispositivo

| Sensor | Utilização |
|---|---|
| Giroscópio | Detetar a rotação do telemóvel para sortear o mini-jogo |
| Acelerómetro | Detetar movimento nos mini-jogos (quem pega primeiro, passagem entre jogadores) |
| Vibração | Feedback ao jogador e mecânica de jogo (contar vibrações, aviso de fim de tempo) |

## Comunicação com o mundo (API REST: restdb.io)

| Método | Utilização |
|---|---|
| GET | Obter mini-jogos e desafios/perguntas de cada jogo |
| POST | Criar jogadores e desafios personalizados |
| PUT | Atualizar as pontuações dos jogadores no fim de cada ronda |
| DELETE | Remover desafios personalizados |

O esquema das coleções está em [docs/restdb_schema.md](docs/restdb_schema.md).

## Tecnologias

- Java, Android (Android Studio)
- Retrofit + Gson (cliente HTTP)
- restdb.io (backend REST)

## Como compilar e correr

1. Clonar o repositório e abrir a pasta no Android Studio.
2. Criar um ficheiro `local.properties` na raiz do projeto (não vai para o Git) e acrescentar a chave do restdb.io:
   ```
   RESTDB_API_KEY=a_vossa_chave_aqui
   ```
3. Ligar um telemóvel Android real (os sensores não funcionam bem no emulador).
4. Carregar em **Run**.

## Estrutura do repositório

```
app/                  código-fonte da aplicação Android
docs/                 relatório, diagramas e esquema da base de dados
docs/screenshots/     capturas de ecrã da aplicação
README.md
.gitignore
```

## Estado do projeto

- [ ] Projeto Android criado e a executar
- [ ] Ecrã de rotação com giroscópio
- [ ] Ligação ao restdb.io (GET)
- [ ] Mini-jogo: reflexos
- [ ] Mini-jogo: batata quente
- [ ] Mini-jogo: contar vibrações
- [ ] POST / PUT / DELETE
- [ ] Pontuações e ecrã final
- [ ] Relatório

## Docentes

Acesso de administração concedido a pbs@isep.ipp.pt e caf@isep.ipp.pt.
