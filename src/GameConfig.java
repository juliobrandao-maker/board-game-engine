import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GameConfig {
    @SerializedName(value = "jogo", alternate = {"game", "nome"})
    private String jogo;

    @SerializedName(value = "tabuleiro", alternate = {"board"})
    private TabuleiroConfig tabuleiro;

    @SerializedName(value = "jogador", alternate = {"players", "player", "jogadores"})
    private PlayerConfig jogador;

    @SerializedName(value = "regras", alternate = {"rules", "rule"})
    private RegrasConfig regras;

    public String getJogo(){
        return jogo;
    }

    public TabuleiroConfig getTabuleiro() {
        return tabuleiro;
    }

    public PlayerConfig getJogador() {
        return jogador;
    }

    public RegrasConfig getRegras() {
        return regras;
    }

    public static class TabuleiroConfig {
        @SerializedName(value = "linhas", alternate = {"rows", "linha"})
        private int linhas;

        @SerializedName(value = "colunas", alternate = {"columns", "coluna"})
        private int colunas;

        public int getLinhas(){
            return linhas;
        }

        public int getColunas() {
            return colunas;
        }
    }

    public static class PlayerConfig {
        @SerializedName(value = "quantidadedeJogador", alternate = {"count", "quantidade", "quantidadeDeJogador"})
        private int quantidadedeJogador;

        @SerializedName(value = "simbolo", alternate = {"symbols", "simbolos", "symbol"})
        private List<String> simbolo;

        public int getQuantidadedeJogador() {
            return quantidadedeJogador;
        }

        public List<String> getSimbolo() {
            return simbolo;
        }
    }

    public static class RegrasConfig {
        @SerializedName(value = "condicaodeVitoria", alternate = {"winCondition", "condicaoDeVitoria", "condicao_de_vitoria"})
        private CondicaodeVitoriaConfig condicaodeVitoria;

        public CondicaodeVitoriaConfig getCondicaodeVitoria() {
            return condicaodeVitoria;
        }

        public static class CondicaodeVitoriaConfig {
            @SerializedName(value = "tipo", alternate = {"type"})
            private String tipo;

            @SerializedName(value = "quantidade", alternate = {"quantity"})
            private int quantidade;

            public int getQuantidade(){
                return quantidade;
            }

            public String getTipo() {
                return tipo;
            }
        }
    }
}