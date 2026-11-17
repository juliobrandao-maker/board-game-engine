# 🧩 Board Game Engine — Linguagem de Programação Orientada a Objetos (UPE/POLI 2026.2)

Uma **Game Engine** reutilizável, extensível e genérica em Java projetada para interpretar arquivos de configuração JSON e executar múltiplos jogos de tabuleiro no terminal, sem necessidade de alterar ou recompilar o código-fonte da aplicação.

---

## 🏛️ Arquitetura e Conceitos de POO Aplicados

- **Abstração e Polimorfismo (`WinCondition`)**: O motor `Game` executa o loop da partida, inicializa o tabuleiro, valida jogadas, executa movimentos e verifica vitórias por meio do contrato da interface `WinCondition`, permanecendo agnóstico em relação às regras concretas.
- **Padrão Factory Method (`WinConditionFactory`)**: Mapeia e instancia dinamicamente as condições de vitória a partir do parâmetro `tipo` informado no JSON.
- **Mapeamento Flexível (`GameConfig` & Gson)**: Utiliza anotações `@SerializedName(alternate = ...)` para aceitar arquivos JSON configurados tanto em português (`linhas`, `colunas`, `jogador`) quanto em inglês (`rows`, `columns`, `players`).
- **Encapsulamento e Integridade (`Board`)**: Protege o estado interno da matriz e valida limites de coordenadas, casas vazias, adjacências e capturas por cerco.
- **Tratamento Resiliente de Exceções**: Utiliza exceções personalizadas (`ConfiguraçãoIvalidaException` e `MovimentaçãoInvalidaException`) para tratar configurações malformadas e jogadas inválidas sem derrubar a aplicação com *stack traces*.

---

## 📐 Diagrama de Classes (Mermaid)

```mermaid
classDiagram
    class Main {
        +main(String[] args)
    }

    class GameLoader {
        +LoadConfig(String caminhodoarquivo) GameConfig
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
        +inicializarJogo(Board tabuleiro, List~Player~ jogadores)
        +eJogadaValida(Board tabuleiro, int linha, int coluna, char simbolo) boolean
        +executarJogada(Board tabuleiro, int linha, int coluna, char simbolo)
        +exibirInfoPlacar(List~Player~ jogadores)
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
        +executarJogada(Board tabuleiro, int linha, int coluna, char simbolo)
        +exibirInfoPlacar(List~Player~ jogadores)
        +verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) boolean
    }

    class ReachPositionWinCondition {
        -char simboloJogador1
        +inicializarJogo(Board tabuleiro, List~Player~ jogadores)
        +eJogadaValida(Board tabuleiro, int linha, int coluna, char simbolo) boolean
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
        +ePosicaoAdjacenteAoSimboloValida(int l, int c, char simbolo, int linhaBase) boolean
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
    Game ..> MovimentaçãoInvalidaException