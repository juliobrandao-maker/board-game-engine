import java.util.List;

public interface WinCondition {
    // Método obrigatório da interface
    boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo);

    // Métodos opcionais com implementação padrão (default)
    default void inicializarJogo(Board tabuleiro, List<Player> jogadores) {}

    default boolean eJogadaValida(Board tabuleiro, int linha, int coluna, char simbolo) {
        return tabuleiro.posicaoVazia(linha, coluna);
    }

    default void aoRealizarJogada(Board tabuleiro, int linha, int coluna, char simbolo, int capturadas) {}

    default void exibirInfoPlacar(List<Player> jogadores) {}
}