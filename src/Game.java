import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class Game {
    private Board tabuleiro;
    private List<Player> jogadores;
    private GameConfig config;
    private WinCondition winCondition;
    private Player jogadoratual;

    // Construtor recebendo as configurações, o tabuleiro e os jogadores já montados
    public Game(Board tabuleiro, List<Player> jogadores, GameConfig config,  WinCondition winCondition) {
        this.tabuleiro = tabuleiro;
        this.jogadores = jogadores;
        this.winCondition = winCondition;
        this.config = config;
    }

    public void iniciarJogo(Scanner sc) {
        // Sorteio de quem começa utilizando a lista dinâmica de jogadores
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

            System.out.print(jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a linha: ");
            int linha = sc.nextInt();
            System.out.print(jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a coluna: ");
            int coluna = sc.nextInt();

            if (tabuleiro.marcarPosicao(linha, coluna, jogadoratual.getSimbolo())) {
                jogada++;

                // Passa a posição da jogada atual para checar a vitória dinamicamente
                if (winCondition.verificarVitoria(tabuleiro,linha, coluna, jogadoratual.getSimbolo())) {
                    tabuleiro.exibirTabuleiro();
                    System.out.println("\nParabéns!! " + jogadoratual.getNome() + " Você venceu!!");
                    jogoativ = false;
                } else if (jogada == jogadasmax) {
                    tabuleiro.exibirTabuleiro();
                    System.out.println("\nO jogo deu empate (velha)!");
                    jogoativ = false;
                } else {
                    // Alterna para o próximo jogador da lista (suporta 2 ou mais jogadores)
                    indexAtual = (indexAtual + 1) % jogadores.size();
                    jogadoratual = jogadores.get(indexAtual);
                }
            } else {
                System.out.println("Jogada inválida ou posição ocupada! Tente novamente.\n");
            }
        }
    }


    }
