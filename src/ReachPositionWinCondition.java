public class ReachPositionWinCondition implements WinCondition {

    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        // Regra de Travessia:
        // O jogador 'X' vence quando alcança a última linha (Linha 5 no UI / Índice N-1 na matriz)
        // O jogador 'O' vence quando alcança a primeira linha (Linha 1 no UI / Índice 0 na matriz)
        if (simbolo == 'X') {
            return ultimaLinha == (tabuleiro.getLinha() - 1);
        } else {
            return ultimaLinha == 0;
        }
    }
}