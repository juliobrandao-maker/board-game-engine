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
    public void exibirtabuleiro(){
        for (int i = 0; i < linha; i++) {
            for (int j = 0; j < coluna; j++) {
                System.out.print(" " +this.tabuleiro[i][j] + " ");
                if (j < coluna - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i<linha-1){
                System.out.println("---+---+---");
            }
        }
    }
    public boolean posicaovazia(int l, int c){
        if (l>=0 && l<linha && c>=0 && c < coluna){
            return tabuleiro[l][c] == ' ';
        }
        return false;
    }
    public boolean marcarposicao(int l, int c, char simbolo){
        if (posicaovazia(l,c)){
            this.tabuleiro[l][c] = simbolo;
            return true;
        }
        return false;
    }
}
