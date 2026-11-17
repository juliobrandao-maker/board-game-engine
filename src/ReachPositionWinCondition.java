import java.util.List;

public class ReachPositionWinCondition implements WinCondition {

    @Override
    public void inicializarJogo(Board tabuleiro, List<Player> jogadores) {
        if (jogadores.size() >= 2) {
            tabuleiro.posicionarPecasIniciaisAleatorias(jogadores.get(0).getSimbolo(), jogadores.get(1).getSimbolo());
        }
    }

    @Override
    public boolean eJogadaValida(Board tabuleiro, int linha, int coluna, char simbolo) {
        return tabuleiro.ePosicaoAdjacenteAoSimboloValida(linha, coluna, simbolo);
    }

    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        if (simbolo == 'X') {
            return ultimaLinha == (tabuleiro.getLinha() - 1);
        } else {
            return ultimaLinha == 0;
        }
    }
}