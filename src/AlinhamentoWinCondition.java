import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlinhamentoWinCondition implements WinCondition {
    private int meta;

    public AlinhamentoWinCondition(int meta) {
        this.meta = meta;
    }

    @Override
    public String getDescricaoRegras() {
        return "Posicione uma peça por turno no tabuleiro.\n" +
                "Objetivo: Alinhe " + meta + " peças consecutivas do seu símbolo na horizontal, vertical ou diagonal.";
    }

    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        return !obterCaminhoVitoria(tabuleiro, ultimaLinha, ultimaColuna, simbolo).isEmpty();
    }

    @Override
    public List<int[]> obterCaminhoVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        int[][] eixos = {
                {0, 1}, {1, 0}, {1, 1}, {1, -1}
        };

        for (int[] eixo : eixos) {
            List<int[]> linhaCompleta = coletarNaDirecao(tabuleiro, ultimaLinha, ultimaColuna, eixo[0], eixo[1], simbolo);
            if (linhaCompleta.size() >= meta) {
                return linhaCompleta;
            }
        }
        return Collections.emptyList();
    }

    private List<int[]> coletarNaDirecao(Board tabuleiro, int r, int c, int dr, int dc, char simbolo) {
        List<int[]> antes = new ArrayList<>();
        int nr = r - dr;
        int nc = c - dc;
        while (tabuleiro.posicaoValida(nr, nc) && tabuleiro.getSimbolo(nr, nc) == simbolo) {
            antes.add(new int[]{nr, nc});
            nr -= dr;
            nc -= dc;
        }
        Collections.reverse(antes);

        List<int[]> resultado = new ArrayList<>(antes);
        resultado.add(new int[]{r, c});

        nr = r + dr;
        nc = c + dc;
        while (tabuleiro.posicaoValida(nr, nc) && tabuleiro.getSimbolo(nr, nc) == simbolo) {
            resultado.add(new int[]{nr, nc});
            nr += dr;
            nc += dc;
        }
        return resultado;
    }
}