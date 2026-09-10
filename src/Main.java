import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        File pastaGames = new File("Games");

        // 1. Verifica se a pasta "Games" existe no diretório raiz
        if (!pastaGames.exists() || !pastaGames.isDirectory()) {
            System.err.println("Erro: A pasta 'Games' não foi encontrada no diretório raiz do projeto.");
            return;
        }

        // 2. Mapeia e filtra apenas os arquivos .json contidos na pasta
        File[] arquivosJson = pastaGames.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (arquivosJson == null || arquivosJson.length == 0) {
            System.err.println("Erro: Nenhum arquivo .json de configuração foi encontrado na pasta 'Games'.");
            return;
        }

        // 3. Exibe o menu dinâmico com as opções de jogos encontrados
        System.out.println("====================================");
        System.out.println("        BOARD GAME ENGINE           ");
        System.out.println("====================================");
        System.out.println("Selecione um jogo para carregar:\n");

        for (int i = 0; i < arquivosJson.length; i++) {
            System.out.println((i + 1) + ". " + arquivosJson[i].getName());
        }

        System.out.print("\nDigite a opção desejada: ");

        // Lê a linha completa contendo a opção e limpa o buffer
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

        // 4. Recupera o caminho completo do arquivo selecionado
        String caminhoArquivo = arquivosJson[opcao - 1].getPath();

        try {
            // 5. Lê e valida a configuração JSON através do GameLoader
            GameConfig config = GameLoader.LoadConfig(caminhoArquivo);

            // 6. Instancia o Tabuleiro dinamicamente com base nas linhas e colunas
            Board tabuleiro = new Board(
                    config.getTabuleiro().getLinhas(),
                    config.getTabuleiro().getColunas()
            );

            // 7. Constrói a lista de jogadores solicitando a digitação do nome no terminal
            List<Player> jogadores = new ArrayList<>();
            List<String> simbolos = config.getJogador().getSimbolo();

            System.out.println("\n--- CADASTRO DE JOGADORES ---");
            for (int i = 0; i < simbolos.size(); i++) {
                String simboloStr = simbolos.get(i);
                char simboloChar = simboloStr.charAt(0);

                System.out.print("Digite o nome para o Jogador " + (i + 1) + " (Símbolo '" + simboloChar + "'): ");
                String nome = scanner.nextLine().trim();

                if (nome.isEmpty()) {
                    nome = "Jogador " + (i + 1);
                }

                jogadores.add(new Player(nome, simboloChar));
            }

            // 8. Instancia a condição de vitória concreta acoplada à interface WinCondition
            int quantidadeMeta = config.getRegras().getCondicaodeVitoria().getQuantidade();
            WinCondition winCondition = new AlinhamentoWinCondition(quantidadeMeta);

            // 9. Instancia o motor do jogo com suas dependências e inicia a partida
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