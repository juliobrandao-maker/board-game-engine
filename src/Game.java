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

        // Inicialização polimórfica da regra de vitória
        winCondition.inicializarJogo(tabuleiro, jogadores);

        // Se o JSON configurar explicitamente ADJACENT_SYMBOL em outra regra, inicializa peças base
        if ("ADJACENT_SYMBOL".equalsIgnoreCase(config.getRegras().getPosicionamento())
                && !(winCondition instanceof ReachPositionWinCondition)
                && jogadores.size() >= 2) {
            tabuleiro.posicionarPecasIniciaisAleatorias(jogadores.get(0).getSimbolo(), jogadores.get(1).getSimbolo());
        }

        String tituloJogo = (config.getJogo() != null && !config.getJogo().trim().isEmpty())
                ? config.getJogo().toUpperCase()
                : "PARTIDA";

        limparTelaSeSuportado();
        System.out.println("====================================");
        System.out.println("       INÍCIO: " + tituloJogo);
        System.out.println("====================================");
        System.out.println("Sorteio realizado! Quem começa é: " + jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + ")\n");

        boolean jogoAtivo = true;

        while (jogoAtivo) {
            tabuleiro.exibirTabuleiro();
            winCondition.exibirInfoPlacar(jogadores);

            // Evita loop infinito caso o tabuleiro fique cheio ou o jogador não tenha movimentos válidos
            if (!existeJogadaValidaPara(jogadoratual.getSimbolo())) {
                System.out.println("====================================");
                System.out.println(" SEM MOVIMENTOS VÁLIDOS! EMPATE!");
                System.out.println("====================================\n");
                break;
            }

            int linhaUser;
            int colunaUser;

            try {
                System.out.print("\n" + jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a linha (1 a " + tabuleiro.getLinha() + "): ");
                linhaUser = Integer.parseInt(sc.nextLine().trim());

                System.out.print(jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a coluna (1 a " + tabuleiro.getColuna() + "): ");
                colunaUser = Integer.parseInt(sc.nextLine().trim());

                int linhaInterna = linhaUser - 1;
                int colunaInterna = colunaUser - 1;

                validarMovimento(linhaInterna, colunaInterna, jogadoratual.getSimbolo());

                limparTelaSeSuportado();
                aplicarMovimento(linhaInterna, colunaInterna, jogadoratual.getSimbolo());

                if (winCondition.verificarVitoria(tabuleiro, linhaInterna, colunaInterna, jogadoratual.getSimbolo())) {
                    tabuleiro.exibirTabuleiro();
                    System.out.println("====================================");
                    System.out.println(" PARABÉNS!! " + jogadoratual.getNome().toUpperCase() + " VENCEU A PARTIDA!");
                    System.out.println("====================================\n");
                    jogoAtivo = false;
                } else if (tabuleiroCheio()) {
                    tabuleiro.exibirTabuleiro();
                    System.out.println("====================================");
                    System.out.println(" O JOGO TERMINOU EM EMPATE!");
                    System.out.println("====================================\n");
                    jogoAtivo = false;
                } else {
                    indexAtual = (indexAtual + 1) % jogadores.size();
                    jogadoratual = jogadores.get(indexAtual);
                }

            } catch (NumberFormatException e) {
                limparTelaSeSuportado();
                System.out.println(" Entrada inválida! Por favor, digite apenas números inteiros.\n");
            } catch (MovimentaçãoInvalidaException e) {
                limparTelaSeSuportado();
                System.out.println(" " + e.getMessage() + "\n");
            }
        }
    }

    private void validarMovimento(int linha, int coluna, char simbolo) throws MovimentaçãoInvalidaException {
        if (!tabuleiro.posicaoValida(linha, coluna)) {
            throw new MovimentaçãoInvalidaException("Posição fora dos limites do tabuleiro! Tente novamente.");
        }

        String regraPos = config.getRegras().getPosicionamento();
        boolean valido;

        if ("ADJACENT_SYMBOL".equalsIgnoreCase(regraPos)) {
            int linhaBase = (simbolo == jogadores.get(0).getSimbolo()) ? 0 : (tabuleiro.getLinha() - 1);
            valido = tabuleiro.ePosicaoAdjacenteAoSimboloValida(linha, coluna, simbolo, linhaBase);
        } else if ("EMPTY_POSITION".equalsIgnoreCase(regraPos)) {
            valido = tabuleiro.posicaoVazia(linha, coluna);
        } else {
            valido = winCondition.eJogadaValida(tabuleiro, linha, coluna, simbolo);
        }

        if (!valido) {
            throw new MovimentaçãoInvalidaException("Jogada inválida para as regras deste jogo! Tente novamente.");
        }
    }

    private void aplicarMovimento(int linha, int coluna, char simbolo) {
        String regraCap = config.getRegras().getCaptura();

        if ("SANDWICH".equalsIgnoreCase(regraCap)) {
            int capturadas = tabuleiro.marcarECapturar(linha, coluna, simbolo);
            if (capturadas > 0) {
                if (winCondition instanceof EliminationWinCondition) {
                    ((EliminationWinCondition) winCondition).registrarCaptura(simbolo, capturadas);
                }
                System.out.println(" Jogada Incrível! Capturou " + capturadas + " peça(s) do adversário!\n");
            }
        } else if ("NONE".equalsIgnoreCase(regraCap)) {
            tabuleiro.marcarPosicao(linha, coluna, simbolo);
        } else {
            winCondition.executarJogada(tabuleiro, linha, coluna, simbolo);
        }
    }

    private boolean existeJogadaValidaPara(char simbolo) {
        String regraPos = config.getRegras().getPosicionamento();
        for (int i = 0; i < tabuleiro.getLinha(); i++) {
            for (int j = 0; j < tabuleiro.getColuna(); j++) {
                if ("ADJACENT_SYMBOL".equalsIgnoreCase(regraPos)) {
                    int linhaBase = (simbolo == jogadores.get(0).getSimbolo()) ? 0 : (tabuleiro.getLinha() - 1);
                    if (tabuleiro.ePosicaoAdjacenteAoSimboloValida(i, j, simbolo, linhaBase)) return true;
                } else if ("EMPTY_POSITION".equalsIgnoreCase(regraPos)) {
                    if (tabuleiro.posicaoVazia(i, j)) return true;
                } else {
                    if (winCondition.eJogadaValida(tabuleiro, i, j, simbolo)) return true;
                }
            }
        }
        return false;
    }

    private boolean tabuleiroCheio() {
        for (int i = 0; i < tabuleiro.getLinha(); i++) {
            for (int j = 0; j < tabuleiro.getColuna(); j++) {
                if (tabuleiro.posicaoVazia(i, j)) return false;
            }
        }
        return true;
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