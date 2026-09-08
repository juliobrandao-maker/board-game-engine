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
    public void exibirTabuleiro(){
        for (int i = 0; i < linha; i++) {
            for (int j = 0; j < coluna; j++) {
                System.out.print(" " +this.tabuleiro[i][j] + " ");
                if (j < coluna - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i<linha-1){
                for (int k = 0; k<coluna;k++){
                    System.out.print("---");
                    if (k<coluna-1){
                        System.out.print("+");
                    }
                }
                System.out.println();
            }
        }
    }
    public boolean posicaoVazia(int l, int c){
        if (l>=0 && l<linha && c>=0 && c < coluna){
            return tabuleiro[l][c] == ' ';
        }
        return false;
    }
    public boolean marcarPosicao(int l, int c, char simbolo){
        if (posicaoVazia(l,c)){
            this.tabuleiro[l][c] = simbolo;
            return true;
        }
        return false;
    }
    public int getLinha(){
        return this.linha;
    }
    public int getColuna(){
        return this.coluna;
    }
    public char getSimbolo(int l, int c){
        return this.tabuleiro[l][c];
    }
}
