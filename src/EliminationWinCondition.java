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

    public int getCapturas(char simbolo) {
        return pontuacaoCapturas.getOrDefault(simbolo, 0);
    }

    public int getMetaCapturas() {
        return metaCapturas;
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
    public void exibirInfoPlacar(List<Player> jogadores) {
        System.out.println("--- PLACAR DE CAPTURAS (Meta: " + metaCapturas + " peças) ---");
        for (Player p : jogadores) {
            System.out.println("  " + p.getNome() + " (" + p.getSimbolo() + "): " + getCapturas(p.getSimbolo()) + " captura(s)");
        }
        System.out.println("-----------------------------------------------------");
    }

    @Override
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        return pontuacaoCapturas.getOrDefault(simbolo, 0) >= metaCapturas;
    }
}