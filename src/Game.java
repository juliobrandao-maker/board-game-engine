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

        System.out.println("\n=== INÍCIO DA PARTIDA ===");
        System.out.println("Sorteio realizado! Quem começa é o jogador: " + jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + ")\n");

        boolean jogoativ = true;
        int jogada = 0;
        int jogadasmax = tabuleiro.getLinha() * tabuleiro.getColuna();

        while (jogoativ) {
            tabuleiro.exibirTabuleiro();

            int linha = solicitarCoordenada(sc, jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a linha: ");
            int coluna = solicitarCoordenada(sc, jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a coluna: ");

            if (tabuleiro.marcarPosicao(linha, coluna, jogadoratual.getSimbolo())) {
                jogada++;

                if (winCondition.verificarVitoria(tabuleiro, linha, coluna, jogadoratual.getSimbolo())) {
                    tabuleiro.exibirTabuleiro();
                    System.out.println("\nParabéns!! " + jogadoratual.getNome() + " Você venceu!!");
                    jogoativ = false;
                } else if (jogada == jogadasmax) {
                    tabuleiro.exibirTabuleiro();
                    System.out.println("\nO jogo deu empate (velha)!");
                    jogoativ = false;
                } else {
                    indexAtual = (indexAtual + 1) % jogadores.size();
                    jogadoratual = jogadores.get(indexAtual);
                }
            } else {
                System.out.println("\n⚠️ Jogada inválida ou posição ocupada! Tente novamente.\n");
            }
        }
    }

    private int solicitarCoordenada(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                String entrada = sc.nextLine().trim();
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Entrada inválida! Digite apenas números inteiros.");
            }
        }
    }
}