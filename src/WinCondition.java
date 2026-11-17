import java.util.Collections;
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

    default Player verificarVencedorFinal(Board tabuleiro, List<Player> jogadores) {
        return null;
    }

    default String consumirUltimoEvento() {
        return "";
    }

    default String getTextoPlacar(List<Player> jogadores, Board tabuleiro) {
        return "";
    }

    default void exibirInfoPlacar(List<Player> jogadores, Board tabuleiro) {
        String placar = getTextoPlacar(jogadores, tabuleiro);
        if (!placar.isEmpty()) {
            System.out.println(placar);
        }
    }

    default List<int[]> obterConexoesVisuais(Board tabuleiro) {
        return Collections.emptyList();
    }

    default List<int[]> obterConexoesPreview(Board tabuleiro, int linha, int coluna, char simbolo) {
        return Collections.emptyList();
    }

    default List<int[]> obterCaminhoVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        return Collections.emptyList();
    }

    default boolean mostrarBordasJogadores() {
        return false;
    }

    default String getDescricaoRegras() {
        return "Posicione suas peças em casas vazias e cumpra o objetivo da partida para vencer.";
    }
}