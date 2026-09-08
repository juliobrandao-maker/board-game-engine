# board-game-engine
Trabalho da disciplina de LPOO onde tem como foco criar em Java, uma Game Engine (motor de jogo) para jogos de tabuleiro com foco em Orientação a Objetos e arquivos de configuração (como JSON)
## Diagrama de Classes

```mermaid
classDiagram
    class Game {
        -Board tabuleiro
        -Player jogador1
        -Player jogador2
        -Player jogadorAtual
        +Game()
        +iniciarjogo() void
        +verificarvitoria(char simbolo) boolean
        +verificarlinha(char simbolo) boolean
        +verificarcoluna(char simbolo) boolean
        +verificardiagonal(char simbolo) boolean
    }

    class Board {
        -int linha
        -int coluna
        -char[][] matriz
        +getLinha() int
        +getColuna() int
        +getSimbolo(int l, int c) char
        +marcarposicao(int l, int c, char s) boolean
        +exibirTabuleiro() void
    }

    class Player {
        -String nome
        -char simbolo
        +getNome() String
        +getSimbolo() char
    }

    class Main {
        +main(String[] args)$ void
    }

    Game "1" *-- "1" Board : contem
    Game "1" *-- "2..3" Player : gerencia
    Main ..> Game : instancia
```
