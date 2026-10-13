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

        // Se for jogo de Travessia, posiciona 1 peça inicial aleatória para cada jogador
        if (winCondition instanceof ReachPositionWinCondition) {
            tabuleiro.posicionarPecasIniciaisAleatorias(jogadores.get(0).getSimbolo(), jogadores.get(1).getSimbolo());
        }

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

            // Exibe informação complementar no placar para jogos de eliminação
            if (winCondition instanceof EliminationWinCondition) {
                EliminationWinCondition ew = (EliminationWinCondition) winCondition;
                System.out.println("--- PLACAR DE CAPTURAS (Meta: " + ew.getMetaCapturas() + " peças) ---");
                for (Player p : jogadores) {
                    System.out.println("  " + p.getNome() + " (" + p.getSimbolo() + "): " + ew.getCapturas(p.getSimbolo()) + " captura(s)");
                }
                System.out.println("-----------------------------------------------------");
            }

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

            // REGRAS DE POSICIONAMENTO
            if (winCondition instanceof ReachPositionWinCondition) {
                // Modo Travessia: Exige jogar adjacente a uma peça do seu próprio símbolo
                if (!tabuleiro.ePosicaoAdjacenteAoSimboloValida(linhaInterna, colunaInterna, jogadoratual.getSimbolo())) {
                    limparTelaSeSuportado();
                    System.out.println(" Jogada Inválida! Você só pode colocar peças em casas vazias AO LADO de uma peça do seu próprio símbolo ('" + jogadoratual.getSimbolo() + "').\n");
                    continue;
                }
            } else {
                if (!tabuleiro.posicaoVazia(linhaInterna, colunaInterna)) {
                    limparTelaSeSuportado();
                    System.out.println(" Jogada inválida! Posição já ocupada.\n");
                    continue;
                }
            }

            // Executa a jogada testando capturas ativas/passivas
            int capturadas = tabuleiro.marcarECapturar(linhaInterna, colunaInterna, jogadoratual.getSimbolo());

            if (winCondition instanceof EliminationWinCondition && capturadas > 0) {
                EliminationWinCondition ew = (EliminationWinCondition) winCondition;
                ew.registrarCaptura(jogadoratual.getSimbolo(), capturadas);
                limparTelaSeSuportado();
                System.out.println(" Jogada Incrível! " + jogadoratual.getNome() + " capturou " + capturadas + " peça(s) do adversário!\n");
            } else {
                limparTelaSeSuportado();
            }

            jogadasValidas++;

            // Verificação de Vitória
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
        } catch (Exception e) {
            // Fallback para IDE
        }
    }
}