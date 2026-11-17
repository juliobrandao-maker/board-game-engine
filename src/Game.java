import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Board tabuleiro;
    private List<Player> jogadores;
    private GameConfig config;
    private WinCondition winCondition;
    private Player jogadoratual;

    public Game(Board tabuleiro, List<Player> jogadores, GameConfig config, WinCondition winCondition) {
        this.tabuleiro = tabuleiro;
        this.jogadores = jogadores;
        this.winCondition = winCondition;
        this.config = config;
    }

    public void iniciarJogo(Scanner sc) {
        Random gerador = new Random();
        int indexAtual = gerador.nextInt(jogadores.size());
        this.jogadoratual = jogadores.get(indexAtual);

        // POLIMORFISMO: Cada regra inicializa o que for necessário para ela
        winCondition.inicializarJogo(tabuleiro, jogadores);

        limparTelaSeSuportado();
        System.out.println("====================================");
        System.out.println("       INÍCIO DA PARTIDA           ");
        System.out.println("====================================");
        System.out.println("Sorteio realizado! Quem começa é: " + jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + ")\n");

        boolean jogoAtivo = true;
        int jogadasValidas = 0;
        int jogadasMaximas = tabuleiro.getLinha() * tabuleiro.getColuna();

        while (jogoAtivo) {
            tabuleiro.exibirTabuleiro();

            // POLIMORFISMO: Exibe informação complementar no placar se a regra possuir
            winCondition.exibirInfoPlacar(jogadores);

            int linhaUser = -1;
            int colunaUser = -1;

            try {
                System.out.print("\n" + jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a linha (1 a " + tabuleiro.getLinha() + "): ");
                linhaUser = Integer.parseInt(sc.nextLine().trim());

                System.out.print(jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a coluna (1 a " + tabuleiro.getColuna() + "): ");
                colunaUser = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                limparTelaSeSuportado();
                System.out.println(" Entrada inválida! Por favor, digite apenas números inteiros.\n");
                continue;
            }

            int linhaInterna = linhaUser - 1;
            int colunaInterna = colunaUser - 1;

            if (!tabuleiro.posicaoValida(linhaInterna, colunaInterna)) {
                limparTelaSeSuportado();
                System.out.println(" Posição fora dos limites do tabuleiro! Tente novamente.\n");
                continue;
            }

            // POLIMORFISMO: Delegado para a WinCondition validar a jogada
            if (!winCondition.eJogadaValida(tabuleiro, linhaInterna, colunaInterna, jogadoratual.getSimbolo())) {
                limparTelaSeSuportado();
                System.out.println(" Jogada inválida para as regras deste jogo! Tente novamente.\n");
                continue;
            }

            // Executa marcacao/captura e notifica a regra de forma polimorfica
            int capturadas = tabuleiro.marcarECapturar(linhaInterna, colunaInterna, jogadoratual.getSimbolo());
            winCondition.aoRealizarJogada(tabuleiro, linhaInterna, colunaInterna, jogadoratual.getSimbolo(), capturadas);

            jogadasValidas++;

            // POLIMORFISMO: Verificação de Vitória
            if (winCondition.verificarVitoria(tabuleiro, linhaInterna, colunaInterna, jogadoratual.getSimbolo())) {
                tabuleiro.exibirTabuleiro();
                System.out.println("====================================");
                System.out.println(" PARABÉNS!! " + jogadoratual.getNome().toUpperCase() + " VENCEU A PARTIDA!");
                System.out.println("====================================\n");
                jogoAtivo = false;
            } else if (jogadasValidas >= jogadasMaximas) {
                tabuleiro.exibirTabuleiro();
                System.out.println("====================================");
                System.out.println(" O JOGO TERMINOU EM EMPATE!");
                System.out.println("====================================\n");
                jogoAtivo = false;
            } else {
                indexAtual = (indexAtual + 1) % jogadores.size();
                jogadoratual = jogadores.get(indexAtual);
            }
        }
    }

    private void limparTelaSeSuportado() {
        try {
            if (System.console() != null) {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } else {
                    new ProcessBuilder("clear").inheritIO().start().waitFor();
                }
            }
        } catch (Exception e) {}
    }
}