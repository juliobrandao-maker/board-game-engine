import java.util.List;

public class EliminationWinCondition extends AbstractCaptureWinCondition {

    public EliminationWinCondition(int metaCapturas) {
        super(metaCapturas);
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
            this.ultimoEvento = "Capturou " + capturadas + " peça(s) do adversário!";
        }
    }

    @Override
    public String getTextoPlacar(List<Player> jogadores, Board tabuleiro) {
        return formatarPlacarCapturas("Placar de Capturas", jogadores);
    }
}