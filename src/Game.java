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

    public boolean usaGravidade() {
        return "GRAVITY".equalsIgnoreCase(config.getRegras().getPosicionamento());
    }

    public String getTextoPlacar() {
        return winCondition.getTextoPlacar(jogadores, tabuleiro);
    }

    public String consumirUltimoEvento() {
        return winCondition.consumirUltimoEvento();
    }

    public Player obterVencedorFinal() {
        return winCondition.verificarVencedorFinal(tabuleiro, jogadores);
    }

    public List<int[]> obterConexoesVisuais() {
        return winCondition.obterConexoesVisuais(tabuleiro);
    }

    public List<int[]> obterConexoesPreview(int linha, int coluna, char simbolo) {
        return winCondition.obterConexoesPreview(tabuleiro, linha, coluna, simbolo);
    }

    public List<int[]> obterCaminhoVitoria(int ultimaLinha, int ultimaColuna) {
        return winCondition.obterCaminhoVitoria(tabuleiro, ultimaLinha, ultimaColuna, jogadoratual.getSimbolo());
    }

    public boolean mostrarBordasJogadores() {
        return winCondition.mostrarBordasJogadores();
    }

    public void prepararPartida() {
        String regraTurnos = config.getRegras().getTurnos();
        if ("SEQUENTIAL".equalsIgnoreCase(regraTurnos) || "SEQUENCIAL".equalsIgnoreCase(regraTurnos)) {
            this.indexAtual = 0;
        } else {
            Random gerador = new Random();
            this.indexAtual = gerador.nextInt(jogadores.size());
        }
        this.jogadoratual = jogadores.get(indexAtual);

        List<List<String>> tabInicial = config.getTabuleiroInicial();
        if (tabInicial != null) {
            for (int i = 0; i < tabInicial.size(); i++) {
                List<String> linhaCfg = tabInicial.get(i);
                for (int j = 0; j < linhaCfg.size(); j++) {
                    String cel = linhaCfg.get(j);
                    if (cel != null && !cel.trim().isEmpty()) {
                        char c = cel.trim().charAt(0);
                        if (c != '.' && c != '-') {
                            tabuleiro.marcarPosicao(i, j, c);
                        }
                    }
                }
            }
        }

        winCondition.inicializarJogo(tabuleiro, jogadores);
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
            if (usaGravidade()) {
                fallback.append("\nRegra extra: Gravidade ativada (a peça cai até a linha livre mais baixa da coluna).");
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
        System.out.println("Quem começa é: " + jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + ")\n");

        boolean jogoAtivo = true;

        while (jogoAtivo) {
            tabuleiro.exibirTabuleiro();
            winCondition.exibirInfoPlacar(jogadores, tabuleiro);

            if (!existeJogadaValidaPara(jogadoratual.getSimbolo())) {
                String nomeSemJogada = jogadoratual.getNome();
                avancarTurno();
                if (!existeJogadaValidaPara(jogadoratual.getSimbolo())) {
                    encerrarSemMovimentos();
                    break;
                } else {
                    System.out.println(" " + nomeSemJogada + " não possui jogadas válidas e passou a vez para " + jogadoratual.getNome() + "!\n");
                    continue;
                }
            }

            try {
                int linhaInterna;
                int colunaInterna;

                if (usaGravidade()) {
                    System.out.print("\n" + jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a coluna (1 a " + tabuleiro.getColuna() + "): ");
                    int colunaUser = Integer.parseInt(sc.nextLine().trim());
                    linhaInterna = 0;
                    colunaInterna = colunaUser - 1;
                } else {
                    System.out.print("\n" + jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a linha (1 a " + tabuleiro.getLinha() + "): ");
                    int linhaUser = Integer.parseInt(sc.nextLine().trim());

                    System.out.print(jogadoratual.getNome() + " (" + jogadoratual.getSimbolo() + "), digite a coluna (1 a " + tabuleiro.getColuna() + "): ");
                    int colunaUser = Integer.parseInt(sc.nextLine().trim());

                    linhaInterna = linhaUser - 1;
                    colunaInterna = colunaUser - 1;
                }

                int[] destino = resolverDestinoJogada(linhaInterna, colunaInterna);
                validarMovimento(destino[0], destino[1], jogadoratual.getSimbolo());

                limparTelaSeSuportado();
                aplicarMovimento(destino[0], destino[1], jogadoratual.getSimbolo());

                String evento = consumirUltimoEvento();
                if (!evento.isEmpty()) {
                    System.out.println(" " + evento + "\n");
                }

                if (verificarVitoriaAtual(destino[0], destino[1])) {
                    tabuleiro.exibirTabuleiro();
                    winCondition.exibirInfoPlacar(jogadores, tabuleiro);
                    System.out.println("====================================");
                    System.out.println(" PARABÉNS!! " + jogadoratual.getNome().toUpperCase() + " VENCEU A PARTIDA!");
                    System.out.println("====================================\n");
                    jogoAtivo = false;
                } else if (tabuleiroCheio()) {
                    encerrarSemMovimentos();
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

    private void encerrarSemMovimentos() {
        tabuleiro.exibirTabuleiro();
        winCondition.exibirInfoPlacar(jogadores, tabuleiro);
        Player vencedorFinal = obterVencedorFinal();
        System.out.println("====================================");
        if (vencedorFinal != null) {
            System.out.println(" FIM DE JOGO! VENCEDOR POR MAIORIA: " + vencedorFinal.getNome().toUpperCase() + "!");
        } else {
            System.out.println(" O JOGO TERMINOU EM EMPATE!");
        }
        System.out.println("====================================\n");
    }

    public int[] resolverDestinoJogada(int linha, int coluna) throws MovimentaçãoInvalidaException {
        if (coluna < 0 || coluna >= tabuleiro.getColuna()) {
            throw new MovimentaçãoInvalidaException("Coluna fora dos limites do tabuleiro! Tente novamente.");
        }

        if (usaGravidade()) {
            for (int r = tabuleiro.getLinha() - 1; r >= 0; r--) {
                if (tabuleiro.posicaoVazia(r, coluna)) {
                    return new int[]{r, coluna};
                }
            }
            throw new MovimentaçãoInvalidaException("Coluna cheia! Escolha outra coluna.");
        }

        if (!tabuleiro.posicaoValida(linha, coluna)) {
            throw new MovimentaçãoInvalidaException("Posição fora dos limites do tabuleiro! Tente novamente.");
        }
        return new int[]{linha, coluna};
    }

    public void validarMovimento(int linha, int coluna, char simbolo) throws MovimentaçãoInvalidaException {
        if (!tabuleiro.posicaoValida(linha, coluna)) {
            throw new MovimentaçãoInvalidaException("Posição fora dos limites do tabuleiro! Tente novamente.");
        }

        String regraPos = config.getRegras().getPosicionamento();
        boolean valido;

        if ("EMPTY_POSITION".equalsIgnoreCase(regraPos) || "GRAVITY".equalsIgnoreCase(regraPos)) {
            valido = tabuleiro.posicaoVazia(linha, coluna) && winCondition.eJogadaValida(tabuleiro, linha, coluna, simbolo);
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
            }
        } else {
            winCondition.executarJogada(tabuleiro, linha, coluna, simbolo);
        }
    }

    public boolean existeJogadaValidaPara(char simbolo) {
        String regraPos = config.getRegras().getPosicionamento();
        for (int i = 0; i < tabuleiro.getLinha(); i++) {
            for (int j = 0; j < tabuleiro.getColuna(); j++) {
                if ("EMPTY_POSITION".equalsIgnoreCase(regraPos) || "GRAVITY".equalsIgnoreCase(regraPos)) {
                    if (tabuleiro.posicaoVazia(i, j) && winCondition.eJogadaValida(tabuleiro, i, j, simbolo)) return true;
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