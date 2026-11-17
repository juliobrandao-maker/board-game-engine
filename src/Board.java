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

    public void posicionarPecasIniciaisAleatorias(char simboloP1, char simboloP2) {
        Random rand = new Random();
        int colP1 = rand.nextInt(coluna);
        int colP2 = rand.nextInt(coluna);

        this.tabuleiro[0][colP1] = simboloP1;
        this.tabuleiro[linha - 1][colP2] = simboloP2;
    }

    public void exibirTabuleiro() {
        System.out.println();
        System.out.print("    ");
        for (int j = 0; j < coluna; j++) {
            System.out.print(" " + (j + 1) + "  ");
        }
        System.out.println();

        for (int i = 0; i < linha; i++) {
            System.out.print(" " + (i + 1) + " ");
            for (int j = 0; j < coluna; j++) {
                System.out.print(" " + this.tabuleiro[i][j] + " ");
                if (j < coluna - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();

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

    public boolean ePosicaoAdjacenteAoSimboloValida(int l, int c, char simbolo, int linhaBase) {
        if (!posicaoVazia(l, c)) {
            return false;
        }

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

        if (!temPecaNoTabuleiro) {
            return l == linhaBase;
        }

        int[][] direcoes = {
                {-1, -1}, {-1, 0}, {-1, 1},
                { 0, -1},          { 0, 1},
                { 1, -1}, { 1, 0}, { 1, 1}
        };

        for (int[] d : direcoes) {
            int r = l + d[0];
            int col = c + d[1];
            if (posicaoValida(r, col) && tabuleiro[r][col] == simbolo) {
                return true;
            }
        }

        return false;
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

        int[][] direcoes = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] d : direcoes) {
            int dr = d[0], dc = d[1];
            int rAdj = l + dr;
            int cAdj = c + dc;
            int rSandwich = l + 2 * dr;
            int cSandwich = c + 2 * dc;

            if (posicaoValida(rAdj, cAdj) && posicaoValida(rSandwich, cSandwich)) {
                char pecaAdj = tabuleiro[rAdj][cAdj];
                if (pecaAdj != ' ' && pecaAdj != simbolo && tabuleiro[rSandwich][cSandwich] == simbolo) {
                    tabuleiro[rAdj][cAdj] = ' ';
                    eliminadas++;
                }
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