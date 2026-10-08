public class CantosWinCondition implements WinCondition{
    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int  ultimaColuna, char simbolo){
        int linhas = tabuleiro.getLinha();
        int colunas = tabuleiro.getColuna();

        if (linhas < 2 || colunas <2){
            return false;
        }
        return tabuleiro.getPosicao(0, 0) == simbolo &&
                tabuleiro.getPosicao(0, colunas - 1) == simbolo &&
                tabuleiro.getPosicao(linhas - 1, 0) == simbolo &&
                tabuleiro.getPosicao(linhas - 1, colunas - 1) == simbolo;
    }
}
