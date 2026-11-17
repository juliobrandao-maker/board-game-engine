import java.util.ArrayList;
import java.util.List;

public class ReversiWinCondition implements WinCondition {
    private String ultimoEvento = "";

    private static final int[][] DIRECOES = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };

    @Override
    public String getDescricaoRegras() {
        return "Othello / Reversi: O jogo começa com 4 peças cruzadas no centro do tabuleiro.\n" +
                "Regra de Movimento: Você só pode jogar em uma casa vazia que cerque (flanqueie) peças do oponente em linha reta ou diagonal.\n" +
                "Todas as peças cercadas são convertidas para o seu símbolo! Se não houver jogada válida, você passa a vez.\n" +
                "Objetivo: Ao final da partida, vence quem dominar a MAIORIA de peças no tabuleiro.";
    }

    @Override
    public void inicializarJogo(Board tabuleiro, List<Player> jogadores) {
        if (jogadores.size() >= 2 && tabuleiro.getLinha() >= 4 && tabuleiro.getColuna() >= 4) {
            char p1 = jogadores.get(0).getSimbolo();
            char p2 = jogadores.get(1).getSimbolo();
            int midR = tabuleiro.getLinha() / 2;
            int midC = tabuleiro.getColuna() / 2;

            tabuleiro.marcarPosicao(midR - 1, midC - 1, p2);
            tabuleiro.marcarPosicao(midR - 1, midC, p1);
            tabuleiro.marcarPosicao(midR, midC - 1, p1);
            tabuleiro.marcarPosicao(midR, midC, p2);
        }
    }

    @Override
    public boolean eJogadaValida(Board tabuleiro, int linha, int coluna, char simbolo) {
        if (!tabuleiro.posicaoVazia(linha, coluna)) {
            return false;
        }
        return !obterPecasParaConverter(tabuleiro, linha, coluna, simbolo).isEmpty();
    }

    @Override
    public void executarJogada(Board tabuleiro, int linha, int coluna, char simbolo) {
        List<int[]> paraConverter = obterPecasParaConverter(tabuleiro, linha, coluna, simbolo);
        tabuleiro.marcarPosicao(linha, coluna, simbolo);

        for (int[] pos : paraConverter) {
            tabuleiro.definirSimbolo(pos[0], pos[1], simbolo);
        }

        if (!paraConverter.isEmpty()) {
            this.ultimoEvento = "Converteu " + paraConverter.size() + " peça(s) do adversário!";
        }
    }

    @Override
    public String consumirUltimoEvento() {
        String msg = this.ultimoEvento;
        this.ultimoEvento = "";
        return msg;
    }

    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        int minhasPecas = tabuleiro.contarPecas(simbolo);
        int outrasPecas = 0;
        for (int i = 0; i < tabuleiro.getLinha(); i++) {
            for (int j = 0; j < tabuleiro.getColuna(); j++) {
                char c = tabuleiro.getSimbolo(i, j);
                if (c != ' ' && c != simbolo) {
                    outrasPecas++;
                }
            }
        }
        return minhasPecas > 0 && outrasPecas == 0;
    }

    @Override
    public Player verificarVencedorFinal(Board tabuleiro, List<Player> jogadores) {
        Player vencedor = null;
        int maiorContagem = -1;
        boolean empate = false;

        for (Player p : jogadores) {
            int qtd = tabuleiro.contarPecas(p.getSimbolo());
            if (qtd > maiorContagem) {
                maiorContagem = qtd;
                vencedor = p;
                empate = false;
            } else if (qtd == maiorContagem) {
                empate = true;
            }
        }
        return empate ? null : vencedor;
    }

    @Override
    public String getTextoPlacar(List<Player> jogadores, Board tabuleiro) {
        StringBuilder sb = new StringBuilder("Domínio no Tabuleiro — ");
        for (int i = 0; i < jogadores.size(); i++) {
            Player p = jogadores.get(i);
            sb.append(p.getNome()).append(" (").append(p.getSimbolo()).append("): ")
                    .append(tabuleiro.contarPecas(p.getSimbolo())).append(" peças");
            if (i < jogadores.size() - 1) sb.append("  |  ");
        }
        return sb.toString();
    }

    private List<int[]> obterPecasParaConverter(Board tabuleiro, int l, int c, char simbolo) {
        List<int[]> total = new ArrayList<>();

        for (int[] d : DIRECOES) {
            List<int[]> candidatas = new ArrayList<>();
            int r = l + d[0];
            int col = c + d[1];

            while (tabuleiro.posicaoValida(r, col)) {
                char atual = tabuleiro.getSimbolo(r, col);
                if (atual == ' ') {
                    candidatas.clear();
                    break;
                } else if (atual != simbolo) {
                    candidatas.add(new int[]{r, col});
                } else {
                    total.addAll(candidatas);
                    break;
                }
                r += d[0];
                col += d[1];
            }
        }
        return total;
    }
}