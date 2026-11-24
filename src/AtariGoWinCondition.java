import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class AtariGoWinCondition extends AbstractCaptureWinCondition {

    private static final int[][] ORTOGONAIS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    public AtariGoWinCondition(int metaCapturas) {
        super(metaCapturas);
    }

    @Override
    public String getDescricaoRegras() {
        return "Atari Go (Go de Captura): Coloque uma pedra por turno em uma casa vazia.\n" +
                "Quando um grupo conectado de peças inimigas ficar totalmente cercado (sem casas vazias ortogonais ao redor), todo o grupo é capturado!\n" +
                "Jogadas suicidas (sem saída e sem captura) são proibidas.\n" +
                "Objetivo: Seja o primeiro a capturar " + metaCapturas + " peça(s) do adversário.";
    }

    @Override
    public boolean eJogadaValida(Board tabuleiro, int linha, int coluna, char simbolo) {
        if (!tabuleiro.posicaoVazia(linha, coluna)) {
            return false;
        }

        tabuleiro.definirSimbolo(linha, coluna, simbolo);
        boolean capturaAlgumGrupo = false;

        for (int[] d : ORTOGONAIS) {
            int nr = linha + d[0];
            int nc = coluna + d[1];
            if (tabuleiro.posicaoValida(nr, nc)) {
                char viz = tabuleiro.getSimbolo(nr, nc);
                if (viz != ' ' && viz != simbolo) {
                    if (analisarGrupo(tabuleiro, nr, nc).liberdades == 0) {
                        capturaAlgumGrupo = true;
                        break;
                    }
                }
            }
        }

        boolean temLiberdadePropria = analisarGrupo(tabuleiro, linha, coluna).liberdades > 0;
        tabuleiro.limparPosicao(linha, coluna);

        return capturaAlgumGrupo || temLiberdadePropria;
    }

    @Override
    public void executarJogada(Board tabuleiro, int linha, int coluna, char simbolo) {
        tabuleiro.marcarPosicao(linha, coluna, simbolo);
        int totalCapturadas = 0;

        for (int[] d : ORTOGONAIS) {
            int nr = linha + d[0];
            int nc = coluna + d[1];
            if (tabuleiro.posicaoValida(nr, nc)) {
                char viz = tabuleiro.getSimbolo(nr, nc);
                if (viz != ' ' && viz != simbolo) {
                    ResultadoGrupo res = analisarGrupo(tabuleiro, nr, nc);
                    if (res.liberdades == 0) {
                        for (int[] celula : res.pecas) {
                            tabuleiro.limparPosicao(celula[0], celula[1]);
                        }
                        totalCapturadas += res.pecas.size();
                    }
                }
            }
        }

        if (totalCapturadas > 0) {
            registrarCaptura(simbolo, totalCapturadas);
            this.ultimoEvento = "Cercou e capturou " + totalCapturadas + " peça(s) do adversário!";
        }
    }

    @Override
    public String getTextoPlacar(List<Player> jogadores, Board tabuleiro) {
        return formatarPlacarCapturas("Capturas Atari Go", jogadores);
    }

    private static class ResultadoGrupo {
        List<int[]> pecas = new ArrayList<>();
        int liberdades = 0;
    }

    private ResultadoGrupo analisarGrupo(Board tabuleiro, int startR, int startC) {
        ResultadoGrupo resultado = new ResultadoGrupo();
        char alvo = tabuleiro.getSimbolo(startR, startC);
        int linhas = tabuleiro.getLinha();
        int colunas = tabuleiro.getColuna();

        boolean[][] visitado = new boolean[linhas][colunas];
        Set<String> liberdadesUnicas = new HashSet<>();
        Queue<int[]> fila = new LinkedList<>();

        fila.add(new int[]{startR, startC});
        visitado[startR][startC] = true;

        while (!fila.isEmpty()) {
            int[] atual = fila.poll();
            int r = atual[0];
            int c = atual[1];
            resultado.pecas.add(atual);

            for (int[] d : ORTOGONAIS) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (tabuleiro.posicaoValida(nr, nc)) {
                    char v = tabuleiro.getSimbolo(nr, nc);
                    if (v == ' ') {
                        liberdadesUnicas.add(nr + "," + nc);
                    } else if (v == alvo && !visitado[nr][nc]) {
                        visitado[nr][nc] = true;
                        fila.add(new int[]{nr, nc});
                    }
                }
            }
        }

        resultado.liberdades = liberdadesUnicas.size();
        return resultado;
    }
}