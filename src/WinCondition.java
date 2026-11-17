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

    default void registrarCapturaExterna(char simbolo, int quantidade) {}

    default String getTextoPlacar(List<Player> jogadores) {
        return "";
    }

    default void exibirInfoPlacar(List<Player> jogadores) {
        String placar = getTextoPlacar(jogadores);
        if (!placar.isEmpty()) {
            System.out.println(placar);
        }
    }

    default String getDescricaoRegras() {
        return "Posicione suas peças em casas vazias e cumpra o objetivo da partida para vencer.";
    }
}