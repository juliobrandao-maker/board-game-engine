import java.util.List;

public class ReachPositionWinCondition implements WinCondition {
    private char simboloJogador1 = 'X';

    @Override
    public String getDescricaoRegras() {
        return "Cada jogador inicia com 1 peça na sua linha de base (topo ou base).\n" +
                "Movimento: Coloque novas peças apenas em casas vazias ADJACENTES (8 direções) a uma peça sua.\n" +
                "Objetivo: Construa um caminho conectado até a linha oposta do tabuleiro para vencer.";
    }

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