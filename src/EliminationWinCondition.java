import java.util.HashMap;
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
    public boolean verificarVitoria(Board tabuleiro, int ultimaLinha, int ultimaColuna, char simbolo) {
        return pontuacaoCapturas.getOrDefault(simbolo, 0) >= metaCapturas;
    }
}