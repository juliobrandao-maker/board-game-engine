import java.util.Random;

public class Board {
    private int linha;
    private int coluna;
    private char[][] tabuleiro;

    public Board(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        this.tabuleiro = new char[linha][coluna];

        for (int i = 0; i < linha; i++) {
            for (int j = 0; j < coluna; j++) {
                this.tabuleiro[i][j] = ' ';
            }
        }
    }

    /**
     * Posiciona uma peça inicial para cada jogador em colunas aleatórias:
     * - O primeiro jogador ('X') é posicionado na Linha 1 (índice 0) em uma coluna aleatória.
     * - O segundo jogador ('O') é posicionado na última Linha (índice N-1) em uma coluna aleatória.
     */
    public void posicionarPecasIniciaisAleatorias(char simboloP1, char simboloP2) {
        Random rand = new Random();
        int colP1 = rand.nextInt(coluna);
        int colP2 = rand.nextInt(coluna);

        this.tabuleiro[0][colP1] = simboloP1;
        this.tabuleiro[linha - 1][colP2] = simboloP2;
    }

    public void exibirTabuleiro() {
        System.out.println();
        // Exibe o número das colunas no topo (1 a N)
        System.out.print("    ");
        for (int j = 0; j < coluna; j++) {
            System.out.print(" " + (j + 1) + "  ");
        }
        System.out.println();

        for (int i = 0; i < linha; i++) {
            // Exibe o número da linha na lateral esquerda (1 a N)
            System.out.print(" " + (i + 1) + " ");
            for (int j = 0; j < coluna; j++) {
                System.out.print(" " + this.tabuleiro[i][j] + " ");
                if (j < coluna - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();

            // Linha divisória
            if (i < linha - 1) {
                System.out.print("   ");
                for (int k = 0; k < coluna; k++) {
                    System.out.print("---");
                    if (k < coluna - 1) {
                        System.out.print("+");
                    }
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    public boolean posicaoVazia(int l, int c) {
        if (posicaoValida(l, c)) {
            return tabuleiro[l][c] == ' ';
        }
        return false;
    }

    /**
     * Valida se a posição escolhida é VAZIA e ADJACENTE a pelo menos
     * uma peça do PRÓPRIO SÍMBOLO do jogador atual.
     */
    public boolean ePosicaoAdjacenteAoSimboloValida(int l, int c, char simbolo) {
        if (!posicaoVazia(l, c)) {
            return false;
        }

        // Verifica se o jogador possui alguma peça ativa no tabuleiro
        boolean temPecaNoTabuleiro = false;
        for (int i = 0; i < linha; i++) {
            for (int j = 0; j < coluna; j++) {
                if (tabuleiro[i][j] == simbolo) {
                    temPecaNoTabuleiro = true;
                    break;
                }
            }
            if (temPecaNoTabuleiro) break;
        }

        // Se o jogador perdeu todas as peças por captura, pode recomeçar na sua linha de base
        if (!temPecaNoTabuleiro) {
            int linhaBase = (simbolo == 'X') ? 0 : (linha - 1);
            return l == linhaBase;
        }

        // Valida adjacência em 8 direções ao próprio símbolo
        int[][] direcoes = {
                {-1, -1}, {-1, 0}, {-1, 1},
                { 0, -1},          { 0, 1},
                { 1, -1}, { 1, 0}, { 1, 1}
        };

        for (int[] d : direcoes) {
            int r = l + d[0];
            int col = c + d[1];
            if (posicaoValida(r, col) && tabuleiro[r][col] == simbolo) {
                return true; // Encontrou vizinho do próprio símbolo
            }
        }

        return false; // Tentativa de jogada isolada
    }

    public boolean marcarPosicao(int l, int c, char simbolo) {
        if (posicaoVazia(l, c)) {
            this.tabuleiro[l][c] = simbolo;
            return true;
        }
        return false;
    }

    public int marcarECapturar(int l, int c, char simbolo) {
        if (!posicaoVazia(l, c)) return 0;

        this.tabuleiro[l][c] = simbolo;
        int eliminadas = 0;
        char oponente = (simbolo == 'X') ? 'O' : 'X';

        // 1. CAPTURA ATIVA: Encurrala peça do oponente
        int[][] direcoes = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] d : direcoes) {
            int dr = d[0], dc = d[1];
            int rAdj = l + dr;
            int cAdj = c + dc;
            int rSandwich = l + 2 * dr;
            int cSandwich = c + 2 * dc;

            if (posicaoValida(rAdj, cAdj) && tabuleiro[rAdj][cAdj] == oponente &&
                    posicaoValida(rSandwich, cSandwich) && tabuleiro[rSandwich][cSandwich] == simbolo) {

                tabuleiro[rAdj][cAdj] = ' ';
                eliminadas++;
            }
        }

        // 2. CAPTURA PASSIVA: Entrada voluntária em armadilha
        int[][] paresOpostos = {
                {0, 1, 0, -1},  // Horizontal
                {1, 0, -1, 0},  // Vertical
                {1, 1, -1, -1}, // Diagonal Principal
                {1, -1, -1, 1}  // Diagonal Secundária
        };

        for (int[] p : paresOpostos) {
            int r1 = l + p[0], c1 = c + p[1];
            int r2 = l + p[2], c2 = p[3];

            if (posicaoValida(r1, c1) && tabuleiro[r1][c1] == oponente &&
                    posicaoValida(r2, c2) && tabuleiro[r2][c2] == oponente) {

                tabuleiro[l][c] = ' '; // Peça é destruída na armadilha
                break;
            }
        }

        return eliminadas;
    }

    public boolean posicaoValida(int r, int c) {
        return r >= 0 && r < linha && c >= 0 && c < coluna;
    }

    public char getPosicao(int l, int c) {
        return this.tabuleiro[l][c];
    }

    public int getLinha() {
        return this.linha;
    }

    public int getColuna() {
        return this.coluna;
    }

    public char getSimbolo(int l, int c) {
        return this.tabuleiro[l][c];
    }
}