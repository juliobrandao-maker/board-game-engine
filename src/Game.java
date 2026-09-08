import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class Game {
    private Board tabuleiro;
    private List<Player> jogadores;
    private GameConfig config;
    private Player jogadoratual;

    // Construtor recebendo as configurações, o tabuleiro e os jogadores já montados
    public Game(Board tabuleiro, List<Player> jogadores, GameConfig config) {
        this.tabuleiro = tabuleiro;
        this.jogadores = jogadores;
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
                if (verificarvitoria(linha, coluna, jogadoratual.getSimbolo())) {
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

    // Verificação de vitória dinâmica para qualquer tamanho de tabuleiro e regra do JSON
    public boolean verificarvitoria(int ultimaLinha, int ultimaColuna, char simbolo) {
        int quantidadeNecessaria = config.getRegras().getCondicaodeVitoria().getQuantidade();

        return checarDirecao(ultimaLinha, ultimaColuna, 0, 1, simbolo, quantidadeNecessaria) ||  // Horizontal
                checarDirecao(ultimaLinha, ultimaColuna, 1, 0, simbolo, quantidadeNecessaria) ||  // Vertical
                checarDirecao(ultimaLinha, ultimaColuna, 1, 1, simbolo, quantidadeNecessaria) ||  // Diagonal Principal
                checarDirecao(ultimaLinha, ultimaColuna, 1, -1, simbolo, quantidadeNecessaria);   // Diagonal Secundária
    }

    private boolean checarDirecao(int r, int c, int dr, int dc, char simbolo, int meta) {
        int contagem = 1;
        contagem += contarEmDirecao(r, c, dr, dc, simbolo);
        contagem += contarEmDirecao(r, c, -dr, -dc, simbolo);
        return contagem >= meta;
    }

    private int contarEmDirecao(int r, int c, int dr, int dc, char simbolo) {
        int contagem = 0;
        int nr = r + dr;
        int nc = c + dc;

        while (nr >= 0 && nr < tabuleiro.getLinha() &&
                nc >= 0 && nc < tabuleiro.getColuna() &&
                tabuleiro.getSimbolo(nr, nc) == simbolo) {
            contagem++;
            nr += dr;
            nc += dc;
        }
        return contagem;
    }
}