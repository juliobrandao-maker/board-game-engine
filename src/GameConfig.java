import java.util.List;
public class GameConfig {
    private String jogo;
    private TabuleiroConfig tabuleiro;
    private PlayerConfig jogador;
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
        private int linhas;
        private int colunas;
            public int getLinhas(){
                return linhas;
        }

            public int getColunas() {
                return colunas;
        }
    }
    public static class PlayerConfig {
        private int quantidadedeJogador;
        private List<String> simbolo;

        public int getQuantidadedeJogador() {
            return quantidadedeJogador;
        }

        public List<String> getSimbolo() {
            return simbolo;
        }
    }
    public static class RegrasConfig{
            private CondicaodeVitoriaConfig condicaodeVitoria;

        public CondicaodeVitoriaConfig getCondicaodeVitoria() {
            return condicaodeVitoria;
        }
        public static class CondicaodeVitoriaConfig{
            private  String tipo;
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
