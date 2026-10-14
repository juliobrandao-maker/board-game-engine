# 🧩 Board Game Engine — Linguagem de Programação Orientada a Objetos (UPE/POLI 2026.2)

Uma **Game Engine** reutilizável, extensível e genérica em Java projetada para interpretar arquivos de configuração JSON e executar múltiplos jogos de tabuleiro no terminal, sem necessidade de alterar ou recompilar o código-fonte da aplicação.

---

## 🏛️ Arquitetura e Conceitos de POO Aplicados

A arquitetura foi desenvolvida respeitando a separação clara de responsabilidades entre o **Motor (Engine)** e os **Dados (Configuração JSON)**:

- **Abstração e Polimorfismo (`WinCondition`)**: O motor `Game` executa o loop da partida e valida vitórias por meio do contrato genérico da interface `WinCondition`, permanecendo agnóstico em relação às regras concretas.
- **Padrão de Criação Factory (`WinConditionFactory`)**: Mapeia e instancia dinamicamente as regras de vitória com base no parâmetro `tipo` informado no JSON.
- **Mapeamento Flexível (`GameConfig` & Gson)**: Utiliza anotações `@SerializedName(alternate = ...)` para aceitar arquivos JSON configurados tanto em português (`linhas`, `colunas`, `jogador`) quanto em inglês (`rows`, `columns`, `players`).
- **Encapsulamento e Integridade (`Board`)**: Protege o estado interno da matriz e valida limites de coordenadas, casas vazias e adjacências de peças.
- **Tratamento Resiliente de Exceções**: A classe `GameLoader` valida a estrutura do JSON e lança `ConfiguraçãoIvalidaException` para exibir mensagens amigáveis ao usuário, impedindo que o programa interrompa por *stack trace*.

---

## 📐 Diagrama de Classes (Mermaid)

```mermaid
classDiagram
    class Main {
        +main(String[] args)
    }

    class GameLoader {
        +LoadConfig(String caminhoArquivo) GameConfig
        -validarConfig(GameConfig config)
    }

    class GameConfig {
        +getJogo() String
        +getTabuleiro() TabuleiroConfig
        +getJogador() PlayerConfig
        +getRegras() RegrasConfig
    }

    class WinCondition {
        <<interface>>
        +verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo)* boolean
    }

    class AlinhamentoWinCondition {
        -int meta
        +verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) boolean
    }

    class CantosWinCondition {
        +verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) boolean
    }

    class EliminationWinCondition {
        -int metaCapturas
        -Map~Character, Integer~ pontuacaoCapturas
        +registrarCaptura(char simbolo, int quantidade)
        +getCapturas(char simbolo) int
        +getMetaCapturas() int
        +verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) boolean
    }

    class ReachPositionWinCondition {
        +verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) boolean
    }

    class WinConditionFactory {
        +criar(CondicaodeVitoriaConfig config) WinCondition
    }

    class Board {
        -int linha
        -int coluna
        -char[][] tabuleiro
        +posicionarPecasIniciaisAleatorias(char simboloP1, char simboloP2)
        +exibirTabuleiro()
        +posicaoVazia(int l, int c) boolean
        +ePosicaoAdjacenteAoSimboloValida(int l, int c, char simbolo) boolean
        +marcarPosicao(int l, int c, char simbolo) boolean
        +marcarECapturar(int l, int c, char simbolo) int
        +posicaoValida(int r, int c) boolean
        +getPosicao(int l, int c) char
        +getLinha() int
        +getColuna() int
        +getSimbolo(int l, int c) char
    }

    class Player {
        -String nome
        -char simbolo
        +getNome() String
        +getSimbolo() char
    }

    class Game {
        -Board tabuleiro
        -List~Player~ jogadores
        -GameConfig config
        -WinCondition winCondition
        -Player jogadoratual
        +iniciarJogo(Scanner sc)
    }

    class ConfiguraçãoIvalidaException {
        +ConfiguraçãoIvalidaException(String mensagem)
    }

    class MovimentaçãoInvalidaException {
        +MovimentaçãoInvalidaException(String mensagem)
    }

    Main ..> GameLoader
    Main ..> WinConditionFactory
    Main ..> Game
    GameLoader ..> GameConfig
    GameLoader ..> ConfiguraçãoIvalidaException
    WinConditionFactory ..> WinCondition
    WinConditionFactory ..> ConfiguraçãoIvalidaException
    WinCondition <|.. AlinhamentoWinCondition
    WinCondition <|.. CantosWinCondition
    WinCondition <|.. EliminationWinCondition
    WinCondition <|.. ReachPositionWinCondition
    Game --> Board
    Game --> Player
    Game --> GameConfig
    Game --> WinCondition