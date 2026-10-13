# Board Game Engine

Engine genérica e extensível para jogos de tabuleiro desenvolvida em Java como entrega final da disciplina de Linguagem de Programação Orientada a Objetos (LPOO).

A Engine interpreta arquivos de configuração JSON e executa dinamicamente diferentes jogos de tabuleiro (como Jogo da Velha, Connect Four e variações customizadas), respeitando o princípio do Open/Closed (Aberto para extensão, fechado para modificação).

---

## 🛠️ Tecnologias Utilizadas

* **Java 17+**
* **Gson 2.10+** (Desserialização dinâmica de JSON)

---

## 🏗️ Padrões de Projeto Aplicados

1. **Strategy Pattern (`WinCondition`):** A verificação de vitória é encapsulada em uma interface. A Engine executa `verificarVitoria` de forma polimórfica, permitindo diferentes algoritmos (`AlinhamentoWinCondition`, `CantosWinCondition`) sem alterar o motor principal.
2. **Factory Method (`WinConditionFactory`):** Responsável por mapear a string de tipo vinda do JSON para a implementação concreta correspondente, isolando a instanciação e validação de regras específicas.
3. **Injeção de Dependência:** O motor `Game` recebe suas dependências (`Board`, `List<Player>`, `WinCondition`) prontas do ambiente de inicialização (`Main`).

---

## 📊 Diagrama de Classes UML

```mermaid
classDiagram
    class Main {
        +main(String[] args)
    }

    class GameLoader {
        +LoadConfig(String caminho): GameConfig
        -validarConfig(GameConfig config)
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
        -int linha
        -int coluna
        -char[][] tabuleiro
        +exibirTabuleiro()
        +posicaoVazia(int l, int c): boolean
        +marcarPosicao(int l, int c, char simbolo): boolean
        +getPosicao(int l, int c): char
        +getSimbolo(int l, int c): char
    }

    class Player {
        -String nome
        -char simbolo
        +getNome(): String
        +getSimbolo(): char
    }

    class WinCondition {
        <<interface>>
        +verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo): boolean
    }

    class AlinhamentoWinCondition {
        -int meta
        +verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo): boolean
    }

    class CantosWinCondition {
        +verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo): boolean
    }

    class WinConditionFactory {
        +criar(CondicaodeVitoriaConfig config): WinCondition
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

    WinCondition <|.. AlinhamentoWinCondition : implementa
    WinCondition <|.. CantosWinCondition : implementa
    Game --> Board : composto por
    Game --> Player : possui N
    Game --> WinCondition : utiliza
    WinConditionFactory ..> WinCondition : instancia
    Main --> GameLoader : carrega config
    Main --> WinConditionFactory : solicita regra
    Main --> Game : inicia
    GameLoader ..> ConfiguraçãoIvalidaException : lança