import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. Carrega as configurações
            GameConfig config = GameLoader.LoadConfig("Games/jogo_da_velha.json");

            // 2. Instancia o Tabuleiro
            int linhas = config.getTabuleiro().getLinhas();
            int colunas = config.getTabuleiro().getColunas();
            Board tabuleiro = new Board(linhas, colunas);

            // 3. Cadastra os Jogadores
            List<Player> jogadores = new ArrayList<>();
            List<String> simbolos = config.getJogador().getSimbolo();

            System.out.println("=== " + config.getJogo().toUpperCase() + " ===");

            for (int i = 0; i < simbolos.size(); i++) {
                char simboloChar = simbolos.get(i).charAt(0);
                System.out.print("Digite o nome do Jogador " + (i + 1) + " (Símbolo '" + simboloChar + "'): ");
                String nome = scanner.nextLine();

                if (nome.trim().isEmpty()) {
                    nome = "Jogador " + (i + 1);
                }

                jogadores.add(new Player(nome, simboloChar));
            }

            // 4. Inicia a Partida
            Game jogo = new Game(tabuleiro, jogadores, config);
            jogo.iniciarJogo(scanner);

        } catch (Exception e) {
            System.err.println("Erro durante a execução do jogo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}