import java.util.List;

public interface WinCondition {
    boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo);

    default void inicializarJogo(Board tabuleiro, List<Player> jogadores) {}

    default boolean eJogadaValida(Board tabuleiro, int linha, int coluna, char simbolo) {
        return tabuleiro.posicaoVazia(linha, coluna);
    }

    default void executarJogada(Board tabuleiro, int linha, int coluna, char simbolo) {
        tabuleiro.marcarPosicao(linha, coluna, simbolo);
    }

    default void exibirInfoPlacar(List<Player> jogadores) {}
}