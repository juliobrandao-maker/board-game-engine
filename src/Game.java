import java.util.List;
import java.util.Scanner;
import java.util.Random;

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

        limparTelaSeSuportado();
        System.out.println("=== INÍCIO DA PARTIDA ===");
        System.out.println("Sorteio realizado! Quem começa é o jogador: " + jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + ")\n");

        boolean jogoativ = true;
        int jogada = 0;
        int jogadasmax = tabuleiro.getLinha() * tabuleiro.getColuna();

        while (jogoativ) {
            tabuleiro.exibirTabuleiro();

            int maxLinhas = tabuleiro.getLinha();
            int maxColunas = tabuleiro.getColuna();

            // Entrada amigável de 1 a N
            int linhaDigitada = solicitarCoordenada(sc, jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a linha (1-" + maxLinhas + "): ", 1, maxLinhas);
            int colunaDigitada = solicitarCoordenada(sc, jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a coluna (1-" + maxColunas + "): ", 1, maxColunas);

            // Conversão de base 1 (usuário) para base 0 (matriz Java)
            int linhaInterna = linhaDigitada - 1;
            int colunaInterna = colunaDigitada - 1;

            if (tabuleiro.marcarPosicao(linhaInterna, colunaInterna, jogadoratual.getSimbolo())) {
                jogada++;

                if (winCondition.verificarVitoria(tabuleiro, linhaInterna, colunaInterna, jogadoratual.getSimbolo())) {
                    limparTelaSeSuportado();
                    tabuleiro.exibirTabuleiro();
                    System.out.println("\n🎉 Parabéns!! " + jogadoratual.getNome() + " você venceu!");
                    jogoativ = false;
                } else if (jogada == jogadasmax) {
                    limparTelaSeSuportado();
                    tabuleiro.exibirTabuleiro();
                    System.out.println("\n🤝 O jogo empatou (velha)!");
                    jogoativ = false;
                } else {
                    indexAtual = (indexAtual + 1) % jogadores.size();
                    jogadoratual = jogadores.get(indexAtual);
                    limparTelaSeSuportado();
                }
            } else {
                limparTelaSeSuportado();
                System.out.println("⚠️ Posição já ocupada! Escolha outra célula no tabuleiro.\n");
            }
        }
    }

    private int solicitarCoordenada(Scanner sc, String mensagem, int min, int max) {
        while (true) {
            System.out.print(mensagem);
            try {
                String entrada = sc.nextLine().trim();
                int valor = Integer.parseInt(entrada);

                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("⚠️ Digite um número entre " + min + " e " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Entrada inválida! Digite apenas números inteiros.");
            }
        }
    }

    private void limparTelaSeSuportado() {
        // Verifica se o ambiente possui um console interativo real (TTY)
        if (System.console() != null) {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } else {
                    new ProcessBuilder("clear").inheritIO().start().waitFor();
                }
            } catch (Exception e) {
                // Se o processo falhar, não faz nada para evitar desformatar a tela
            }
        }
        // Se System.console() for null (aba Run do IntelliJ), NÃO faz nada.
        // Mantém a exibição contínua sem saltos nem espaços gigantes em branco.
    }
}