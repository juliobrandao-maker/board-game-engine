public class Main {
    public static void main(String[] args) {
        try {
            GameConfig config = GameLoader.LoadConfig("Games/jogo_da_velha.json");

            int linhas = config.getTabuleiro().getLinhas();
            int colunas = config.getTabuleiro().getColunas();

            Board tabuleiro = new Board(linhas, colunas);

            System.out.println("=== TABULEIRO INICIALIZADO VIA JSON (" + linhas + "x" + colunas + ") ===");
            tabuleiro.exibirTabuleiro();

        } catch (Exception e) {
            System.err.println("Erro ao carregar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}