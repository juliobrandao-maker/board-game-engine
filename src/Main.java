import javax.swing.SwingUtilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (args != null && args.length > 0) {
            if ("--gui".equalsIgnoreCase(args[0])) {
                abrirInterfaceGrafica();
                return;
            }
            executarPartidaTerminal(args[0], scanner);
            return;
        }

        System.out.println("====================================");
        System.out.println("        BOARD GAME ENGINE           ");
        System.out.println("====================================");
        System.out.println("Selecione o modo de execução:");
        System.out.println("1. Modo Terminal (CLI)");
        System.out.println("2. Modo Interface Gráfica (Swing)");
        System.out.print("\nDigite a opção desejada: ");

        String modo = scanner.nextLine().trim();

        if ("2".equals(modo)) {
            abrirInterfaceGrafica();
            return;
        }

        File pastaGames = new File("Games");

        if (!pastaGames.exists() || !pastaGames.isDirectory()) {
            System.err.println("Erro: A pasta 'Games' não foi encontrada no diretório raiz do projeto.");
            return;
        }

        File[] arquivosJson = pastaGames.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (arquivosJson == null || arquivosJson.length == 0) {
            System.err.println("Erro: Nenhum arquivo .json de configuração foi encontrado na pasta 'Games'.");
            return;
        }

        System.out.println("\nSelecione um jogo para carregar:\n");
        for (int i = 0; i < arquivosJson.length; i++) {
            System.out.println((i + 1) + ". " + arquivosJson[i].getName());
        }

        System.out.print("\nDigite a opção desejada: ");
        int opcao;
        try {
            opcao = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida! Digite apenas números.");
            return;
        }

        if (opcao < 1 || opcao > arquivosJson.length) {
            System.out.println("Opção inválida! Encerrando a aplicação.");
            return;
        }

        executarPartidaTerminal(arquivosJson[opcao - 1].getPath(), scanner);
    }

    private static void abrirInterfaceGrafica() {
        SwingUtilities.invokeLater(() -> {
            GameGUI gui = new GameGUI();
            gui.setVisible(true);
        });
    }

    private static void executarPartidaTerminal(String caminhoArquivo, Scanner scanner) {
        try {
            GameConfig config = GameLoader.LoadConfig(caminhoArquivo);

            Board tabuleiro = new Board(
                    config.getTabuleiro().getLinhas(),
                    config.getTabuleiro().getColunas()
            );

            List<Player> jogadores = new ArrayList<>();
            List<String> simbolos = config.getJogador().getSimbolo();

            System.out.println("\n--- CADASTRO DE JOGADORES ---");
            for (int i = 0; i < simbolos.size(); i++) {
                String simboloStr = simbolos.get(i);
                char simboloChar = simboloStr.trim().charAt(0);

                System.out.print("Digite o nome para o Jogador " + (i + 1) + " (Símbolo '" + simboloChar + "'): ");
                String nome = scanner.nextLine().trim();

                if (nome.isEmpty()) {
                    nome = "Jogador " + (i + 1);
                }

                jogadores.add(new Player(nome, simboloChar));
            }

            WinCondition winCondition = WinConditionFactory.criar(config.getRegras().getCondicaodeVitoria());

            Game jogo = new Game(tabuleiro, jogadores, config, winCondition);
            jogo.iniciarJogo(scanner);

        } catch (ConfiguraçãoIvalidaException e) {
            System.out.println("\n------------------------------------");
            System.out.println("Erro ao carregar o jogo:");
            System.out.println(e.getMessage());
            System.out.println("------------------------------------");
        }
    }
}