import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EliminationWinCondition implements WinCondition {
    private int metaCapturas;
    private Map<Character, Integer> pontuacaoCapturas = new HashMap<>();

    public EliminationWinCondition(int metaCapturas) {
        this.metaCapturas = (metaCapturas > 0) ? metaCapturas : 3;
    }

    public void registrarCaptura(char simbolo, int quantidade) {
        pontuacaoCapturas.put(simbolo, pontuacaoCapturas.getOrDefault(simbolo, 0) + quantidade);
    }

    @Override
    public void registrarCapturaExterna(char simbolo, int quantidade) {
        registrarCaptura(simbolo, quantidade);
    }

    public int getCapturas(char simbolo) {
        return pontuacaoCapturas.getOrDefault(simbolo, 0);
    }

    public int getMetaCapturas() {
        return metaCapturas;
    }

    @Override
    public String getDescricaoRegras() {
        return "Posicione uma peça por turno para cercar o oponente (Captura em Sanduíche).\n" +
                "Quando uma peça inimiga ficar entre duas peças suas, ela é capturada.\n" +
                "Objetivo: Seja o primeiro a capturar " + metaCapturas + " peça(s) do adversário.";
    }

    @Override
    public void executarJogada(Board tabuleiro, int linha, int coluna, char simbolo) {
        int capturadas = tabuleiro.marcarECapturar(linha, coluna, simbolo);
        if (capturadas > 0) {
            registrarCaptura(simbolo, capturadas);
            System.out.println(" Jogada Incrível! Capturou " + capturadas + " peça(s) do adversário!\n");
        }
    }

    @Override
    public String getTextoPlacar(List<Player> jogadores) {
        StringBuilder sb = new StringBuilder("Placar de Capturas (Meta: " + metaCapturas + ") — ");
        for (int i = 0; i < jogadores.size(); i++) {
            Player p = jogadores.get(i);
            sb.append(p.getNome()).append(" (").append(p.getSimbolo()).append("): ")
                    .append(getCapturas(p.getSimbolo()));
            if (i < jogadores.size() - 1) sb.append("  |  ");
        }
        return sb.toString();
    }

    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        return pontuacaoCapturas.getOrDefault(simbolo, 0) >= metaCapturas;
    }
}