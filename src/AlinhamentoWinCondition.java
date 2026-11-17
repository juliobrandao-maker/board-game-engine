public class AlinhamentoWinCondition implements WinCondition {
    private int meta;

    public AlinhamentoWinCondition(int meta) {
        this.meta = meta;
    }

    @Override
    public String getDescricaoRegras() {
        return "Posicione uma peça por turno em qualquer casa vazia.\n" +
                "Objetivo: Alinhe " + meta + " peças consecutivas do seu símbolo na horizontal, vertical ou diagonal.";
    }

    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        return checarDirecao(tabuleiro, ultimaLinha, ultimaColuna, 0, 1, simbolo) ||
                checarDirecao(tabuleiro, ultimaLinha, ultimaColuna, 1, 0, simbolo) ||
                checarDirecao(tabuleiro, ultimaLinha, ultimaColuna, 1, 1, simbolo) ||
                checarDirecao(tabuleiro, ultimaLinha, ultimaColuna, 1, -1, simbolo);
    }

    private boolean checarDirecao(Board tabuleiro, int r, int c, int dr, int dc, char simbolo) {
        int contagem = 1;
        contagem += contarEmDirecao(tabuleiro, r, c, dr, dc, simbolo);
        contagem += contarEmDirecao(tabuleiro, r, c, -dr, -dc, simbolo);
        return contagem >= meta;
    }

    private int contarEmDirecao(Board tabuleiro, int r, int c, int dr, int dc, char simbolo) {
        int contagem = 0;
        int nr = r + dr;
        int nc = c + dc;

        while (nr >= 0 && nr < tabuleiro.getLinha() &&
                nc >= 0 && nc < tabuleiro.getColuna() &&
                tabuleiro.getSimbolo(nr, nc) == simbolo) {
            contagem++;
            nr += dr;
            nc += dc;
        }
        return contagem;
    }
}