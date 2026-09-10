# Board Game Engine

Engine extensível para jogos de tabuleiro desenvolvida em Java como parte da disciplina de Linguagem de Programação Orientada a Objetos.

A Engine permite carregar e executar diferentes jogos de tabuleiro a partir de arquivos de configuração em formato JSON, sem a necessidade de modificar ou recompilar o código-fonte da aplicação.

---

## 🛠️ Tecnologias Utilizadas

* **Java 17+**
* **Gson 2.10+** (para desserialização dos arquivos JSON)

---

## 🏗️ Arquitetura da Engine

A solução utiliza **Polimorfismo**, **Interfaces** e **Injeção de Dependências** para isolar as regras específicas de cada jogo dos componentes centrais da aplicação.

```mermaid
classDiagram
    class Main {
        +main(String[] args)
    }

    class GameLoader {
        +LoadConfig(String caminho): GameConfig
    }

    class GameConfig {
        -String jogo
        -TabuleiroConfig tabuleiro
        -PlayerConfig jogador
        -RegrasConfig regras
        +getJogo(): String
        +getTabuleiro(): TabuleiroConfig
        +getJogador(): PlayerConfig
        +getRegras(): RegrasConfig
    }

    class Board {
        -char[][] matriz
        -int linha
        -int coluna
        +marcarPosicao(int l, int c, char s): boolean
        +exibirTabuleiro()
    }

    class Player {
        -String nome
        -char simbolo
        +getNome(): String
        +getSimbolo(): char
    }

    class WinCondition {
        <<interface>>
        +verificarVitoria(Board board, int linha, int coluna, char simbolo): boolean
    }

    class AlinhamentoWinCondition {
        -int quantidade
        +verificarVitoria(Board board, int linha, int coluna, char simbolo): boolean
    }

    class Game {
        -Board tabuleiro
        -List~Player~ jogadores
        -GameConfig config
        -WinCondition winCondition
        +iniciarJogo(Scanner sc)
    }

    class ConfiguraçãoinvalidaException {
    }

    WinCondition <|.. AlinhamentoWinCondition : implementa
    Game --> Board : possui
    Game --> Player : possui
    Game --> WinCondition : utiliza
    GameLoader --> GameConfig : cria
    Main --> GameLoader : invoca
    Main --> Game : instancia
    GameLoader ..> ConfiguraçãoinvalidaException : lança