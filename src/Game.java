import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Board tabuleiro;
    private List<Player> jogadores;
    private GameConfig config;
    private WinCondition winCondition;
    private Player jogadoratual;
    private int indexAtual;

    public Game(Board tabuleiro, List<Player> jogadores, GameConfig config, WinCondition winCondition) {
        this.tabuleiro = tabuleiro;
        this.jogadores = jogadores;
        this.winCondition = winCondition;
        this.config = config;
    }

    public Board getTabuleiro() { return tabuleiro; }
    public List<Player> getJogadores() { return jogadores; }
    public GameConfig getConfig() { return config; }
    public Player getJogadorAtual() { return jogadoratual; }

    public String getTextoPlacar() {
        return winCondition.getTextoPlacar(jogadores);
    }

    public void prepararPartida() {
        Random gerador = new Random();
        this.indexAtual = gerador.nextInt(jogadores.size());
        this.jogadoratual = jogadores.get(indexAtual);

        // Inicialização polimórfica da regra de vitória
        winCondition.inicializarJogo(tabuleiro, jogadores);

        if ("ADJACENT_SYMBOL".equalsIgnoreCase(config.getRegras().getPosicionamento())
                && !(winCondition instanceof ReachPositionWinCondition)
                && jogadores.size() >= 2) {
            tabuleiro.posicionarPecasIniciaisAleatorias(jogadores.get(0).getSimbolo(), jogadores.get(1).getSimbolo());
        }
    }

    public void avancarTurno() {
        indexAtual = (indexAtual + 1) % jogadores.size();
        jogadoratual = jogadores.get(indexAtual);
    }

    public boolean verificarVitoriaAtual(int ultimaLinha, int ultimaColuna) {
        return winCondition.verificarVitoria(tabuleiro, ultimaLinha, ultimaColuna, jogadoratual.getSimbolo());
    }

    public String obterResumoRegras() {
        String titulo = (config.getJogo() != null && !config.getJogo().trim().isEmpty())
                ? config.getJogo()
                : "Jogo de Tabuleiro";

        String textoRegras;
        if (config.getDescricao() != null && !config.getDescricao().trim().isEmpty()) {
            textoRegras = config.getDescricao();
        } else {
            StringBuilder fallback = new StringBuilder(winCondition.getDescricaoRegras());
            if ("ADJACENT_SYMBOL".equalsIgnoreCase(config.getRegras().getPosicionamento())) {
                fallback.append("\nRegra extra: Posicionamento permitido apenas adjacente ao próprio símbolo.");
            }
            if ("SANDWICH".equalsIgnoreCase(config.getRegras().getCaptura())) {
                fallback.append("\nRegra extra: Captura ativa por cerco (sanduíche).");
            }
            textoRegras = fallback.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("JOGO: ").append(titulo).append("\n");
        sb.append("TABULEIRO: ").append(tabuleiro.getLinha()).append("x").append(tabuleiro.getColuna()).append("\n");
        sb.append("JOGADORES: ");
        for (int i = 0; i < jogadores.size(); i++) {
            Player p = jogadores.get(i);
            sb.append(p.getNome()).append(" (").append(p.getSimbolo()).append(")");
            if (i < jogadores.size() - 1) sb.append(" vs ");
        }
        sb.append("\n\nREGRAS E OBJETIVO:\n");
        sb.append(textoRegras);
        return sb.toString();
    }

    private void exibirPainelRegras(Scanner sc) {
        limparTelaSeSuportado();
        System.out.println("====================================================");
        System.out.println("                 REGRAS DA PARTIDA                  ");
        System.out.println("====================================================");
        System.out.println(obterResumoRegras());
        System.out.println("\n====================================================");
        System.out.print("Pressione ENTER para continuar e iniciar a partida...");
        sc.nextLine();
    }

    public void iniciarJogo(Scanner sc) {
        prepararPartida();
        exibirPainelRegras(sc);

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

                if (verificarVitoriaAtual(linhaInterna, colunaInterna)) {
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
                    avancarTurno();
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

    public void validarMovimento(int linha, int coluna, char simbolo) throws MovimentaçãoInvalidaException {
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

    public void aplicarMovimento(int linha, int coluna, char simbolo) {
        String regraCap = config.getRegras().getCaptura();

        if ("SANDWICH".equalsIgnoreCase(regraCap)) {
            int capturadas = tabuleiro.marcarECapturar(linha, coluna, simbolo);
            if (capturadas > 0) {
                winCondition.registrarCapturaExterna(simbolo, capturadas);
                System.out.println(" Jogada Incrível! Capturou " + capturadas + " peça(s) do adversário!\n");
            }
        } else if ("NONE".equalsIgnoreCase(regraCap)) {
            tabuleiro.marcarPosicao(linha, coluna, simbolo);
        } else {
            winCondition.executarJogada(tabuleiro, linha, coluna, simbolo);
        }
    }

    public boolean existeJogadaValidaPara(char simbolo) {
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

    public boolean tabuleiroCheio() {
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