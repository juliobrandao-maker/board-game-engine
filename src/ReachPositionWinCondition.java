import java.util.List;

public class ReachPositionWinCondition implements WinCondition {
    private char simboloJogador1 = 'X';

    @Override
    public void inicializarJogo(Board tabuleiro, List<Player> jogadores) {
        if (jogadores.size() >= 2) {
            this.simboloJogador1 = jogadores.get(0).getSimbolo();
            tabuleiro.posicionarPecasIniciaisAleatorias(
                    jogadores.get(0).getSimbolo(),
                    jogadores.get(1).getSimbolo()
            );
        }
    }

    @Override
    public boolean eJogadaValida(Board tabuleiro, int linha, int coluna, char simbolo) {
        int linhaBase = (simbolo == simboloJogador1) ? 0 : (tabuleiro.getLinha() - 1);
        return tabuleiro.ePosicaoAdjacenteAoSimboloValida(linha, coluna, simbolo, linhaBase);
    }

    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        if (simbolo == simboloJogador1) {
            return ultimaLinha == (tabuleiro.getLinha() - 1);
        } else {
            return ultimaLinha == 0;
        }
    }
}