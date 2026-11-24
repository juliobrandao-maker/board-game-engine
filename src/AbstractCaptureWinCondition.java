import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCaptureWinCondition implements WinCondition {
    protected int metaCapturas;
    protected Map<Character, Integer> pontuacaoCapturas = new HashMap<>();
    protected String ultimoEvento = "";

    public AbstractCaptureWinCondition(int metaCapturas) {
        this.metaCapturas = (metaCapturas > 0) ? metaCapturas : 3;
    }

    public void registrarCaptura(char simbolo, int quantidade) {
        pontuacaoCapturas.put(simbolo, pontuacaoCapturas.getOrDefault(simbolo, 0) + quantidade);
    }

    @Override
    public void registrarCapturaExterna(char simbolo, int quantidade) {
        registrarCaptura(simbolo, quantidade);
        this.ultimoEvento = "Capturou " + quantidade + " peça(s) do adversário!";
    }

    @Override
    public String consumirUltimoEvento() {
        String msg = this.ultimoEvento;
        this.ultimoEvento = "";
        return msg;
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

    protected String formatarPlacarCapturas(String titulo, List<Player> jogadores) {
        StringBuilder sb = new StringBuilder(titulo + " (Meta: " + metaCapturas + ") — ");
        for (int i = 0; i < jogadores.size(); i++) {
            Player p = jogadores.get(i);
            sb.append(p.getNome()).append(" (").append(p.getSimbolo()).append("): ")
                    .append(getCapturas(p.getSimbolo()));
            if (i < jogadores.size() - 1) sb.append("  |  ");
        }
        return sb.toString();
    }
}