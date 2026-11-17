import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HexWinCondition implements WinCondition {
    private char simboloJogador1 = 'X';

    // Vizinhança hexagonal de 6 direções
    private static final int[][] VIZINHOS_HEX = {
            {-1, 0}, {-1, 1},
            { 0, -1}, { 0, 1},
            { 1, -1}, { 1, 0}
    };

    // Metade das direções para desenhar cada segmento de conexão uma única vez
    private static final int[][] DIRECOES_CONEXAO_UNICA = {
            {0, 1},   // Direita
            {1, 0},   // Baixo
            {1, -1}   // Diagonal baixo-esquerda (/)
    };

    @Override
    public String getDescricaoRegras() {
        return "Hex (Conexão de Bordas): Cada jogador coloca uma peça por turno em qualquer casa livre.\n" +
                "A ponte conecta peças vizinhas na horizontal, vertical e na diagonal ( / ).\n" +
                "Linhas coloridas mostram em tempo real as conexões formadas no tabuleiro!\n" +
                "Objetivo Jogador 1: Conectar a BORDA SUPERIOR (Topo) à BORDA INFERIOR (Base).\n" +
                "Objetivo Jogador 2: Conectar a BORDA ESQUERDA à BORDA DIREITA.";
    }

    @Override
    public void inicializarJogo(Board tabuleiro, List<Player> jogadores) {
        if (!jogadores.isEmpty()) {
            this.simboloJogador1 = jogadores.get(0).getSimbolo();
        }
    }

    @Override
    public boolean mostrarBordasJogadores() {
        return true;
    }

    @Override
    public String getTextoPlacar(List<Player> jogadores, Board tabuleiro) {
        if (jogadores.size() >= 2) {
            Player p1 = jogadores.get(0);
            Player p2 = jogadores.get(1);
            return "Conexão Hex — " + p1.getNome() + " (" + p1.getSimbolo() + "): Topo ↔ Base  |  " +
                    p2.getNome() + " (" + p2.getSimbolo() + "): Esquerda ↔ Direita";
        }
        return "";
    }

    @Override
    public List<int[]> obterConexoesVisuais(Board tabuleiro) {
        List<int[]> conexoes = new ArrayList<>();
        int linhas = tabuleiro.getLinha();
        int colunas = tabuleiro.getColuna();

        for (int r = 0; r < linhas; r++) {
            for (int c = 0; c < colunas; c++) {
                char s = tabuleiro.getSimbolo(r, c);
                if (s == ' ') continue;

                for (int[] d : DIRECOES_CONEXAO_UNICA) {
                    int nr = r + d[0];
                    int nc = c + d[1];
                    if (tabuleiro.posicaoValida(nr, nc) && tabuleiro.getSimbolo(nr, nc) == s) {
                        conexoes.add(new int[]{r, c, nr, nc});
                    }
                }
            }
        }
        return conexoes;
    }

    @Override
    public List<int[]> obterConexoesPreview(Board tabuleiro, int linha, int coluna, char simbolo) {
        List<int[]> preview = new ArrayList<>();
        if (!tabuleiro.posicaoVazia(linha, coluna)) {
            return preview;
        }

        for (int[] d : VIZINHOS_HEX) {
            int nr = linha + d[0];
            int nc = coluna + d[1];
            if (tabuleiro.posicaoValida(nr, nc) && tabuleiro.getSimbolo(nr, nc) == simbolo) {
                preview.add(new int[]{linha, coluna, nr, nc});
            }
        }
        return preview;
    }

    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        return !obterCaminhoVitoria(tabuleiro, ultimaLinha, ultimaColuna, simbolo).isEmpty();
    }

    @Override
    public List<int[]> obterCaminhoVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        int linhas = tabuleiro.getLinha();
        int colunas = tabuleiro.getColuna();
        boolean[][] visitado = new boolean[linhas][colunas];
        int[][] paiR = new int[linhas][colunas];
        int[][] paiC = new int[linhas][colunas];

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                paiR[i][j] = -1;
                paiC[i][j] = -1;
            }
        }

        Queue<int[]> fila = new LinkedList<>();
        boolean conectaVertical = (simbolo == simboloJogador1);

        if (conectaVertical) {
            for (int c = 0; c < colunas; c++) {
                if (tabuleiro.getSimbolo(0, c) == simbolo) {
                    fila.add(new int[]{0, c});
                    visitado[0][c] = true;
                }
            }
        } else {
            for (int r = 0; r < linhas; r++) {
                if (tabuleiro.getSimbolo(r, 0) == simbolo) {
                    fila.add(new int[]{r, 0});
                    visitado[r][0] = true;
                }
            }
        }

        while (!fila.isEmpty()) {
            int[] atual = fila.poll();
            int r = atual[0];
            int c = atual[1];

            if ((conectaVertical && r == linhas - 1) || (!conectaVertical && c == colunas - 1)) {
                List<int[]> caminho = new ArrayList<>();
                int cr = r;
                int cc = c;
                while (cr != -1 && cc != -1) {
                    caminho.add(new int[]{cr, cc});
                    int pr = paiR[cr][cc];
                    int pc = paiC[cr][cc];
                    cr = pr;
                    cc = pc;
                }
                Collections.reverse(caminho);
                return caminho;
            }

            for (int[] d : VIZINHOS_HEX) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (tabuleiro.posicaoValida(nr, nc) && !visitado[nr][nc] && tabuleiro.getSimbolo(nr, nc) == simbolo) {
                    visitado[nr][nc] = true;
                    paiR[nr][nc] = r;
                    paiC[nr][nc] = c;
                    fila.add(new int[]{nr, nc});
                }
            }
        }

        return Collections.emptyList();
    }
}