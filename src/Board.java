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

    public void exibirTabuleiro() {
        System.out.println();
        System.out.print("    ");
        for (int j = 0; j < coluna; j++) {
            if (j + 1 < 10) {
                System.out.print(" " + (j + 1) + "  ");
            } else {
                System.out.print(" " + (j + 1) + " ");
            }
        }
        System.out.println();

        for (int i = 0; i < linha; i++) {
            if (i + 1 < 10) {
                System.out.print(" " + (i + 1) + " ");
            } else {
                System.out.print((i + 1) + " ");
            }
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

    public boolean marcarPosicao(int l, int c, char simbolo) {
        if (posicaoVazia(l, c)) {
            this.tabuleiro[l][c] = simbolo;
            return true;
        }
        return false;
    }

    public void definirSimbolo(int l, int c, char simbolo) {
        if (posicaoValida(l, c)) {
            this.tabuleiro[l][c] = simbolo;
        }
    }

    public void limparPosicao(int l, int c) {
        if (posicaoValida(l, c)) {
            this.tabuleiro[l][c] = ' ';
        }
    }

    public int contarPecas(char simbolo) {
        int total = 0;
        for (int i = 0; i < linha; i++) {
            for (int j = 0; j < coluna; j++) {
                if (this.tabuleiro[i][j] == simbolo) {
                    total++;
                }
            }
        }
        return total;
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