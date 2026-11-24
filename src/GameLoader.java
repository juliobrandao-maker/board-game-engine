import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameLoader {
    public static GameConfig LoadConfig(String caminhodoarquivo) throws ConfiguraçãoIvalidaException {
        File file = new File(caminhodoarquivo);
        if (!file.exists()) {
            throw new ConfiguraçãoIvalidaException("Arquivo de configuração não encontrado: " + caminhodoarquivo);
        }
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            GameConfig config = gson.fromJson(reader, GameConfig.class);
            validarConfig(config);
            return config;
        } catch (Exception e) {
            if (e instanceof ConfiguraçãoIvalidaException) {
                throw (ConfiguraçãoIvalidaException) e;
            }
            throw new ConfiguraçãoIvalidaException("Erro ao processar o JSON: " + e.getMessage());
        }
    }

    private static void validarConfig(GameConfig config) throws ConfiguraçãoIvalidaException {
        if (config == null || config.getTabuleiro() == null || config.getJogador() == null || config.getRegras() == null) {
            throw new ConfiguraçãoIvalidaException("O arquivo de configuração possui estruturas ausentes (tabuleiro, jogador ou regras).");
        }

        int linhas = config.getTabuleiro().getLinhas();
        int colunas = config.getTabuleiro().getColunas();
        if (linhas <= 0 || colunas <= 0) {
            throw new ConfiguraçãoIvalidaException("As dimensões do tabuleiro devem ser maiores que zero.");
        }

        List<String> simbolos = config.getJogador().getSimbolo();
        int qtdJogadores = config.getJogador().getQuantidadedeJogador();
        if (simbolos == null || simbolos.size() < 2) {
            throw new ConfiguraçãoIvalidaException("O jogo deve ter pelo menos 2 símbolos de jogadores definidos.");
        }
        if (qtdJogadores != 0 && qtdJogadores != simbolos.size()) {
            throw new ConfiguraçãoIvalidaException(
                    "A quantidade de jogadores (" + qtdJogadores + ") é incompatível com a quantidade de símbolos (" + simbolos.size() + ")."
            );
        }

        Set<Character> simbolosUnicos = new HashSet<>();
        for (String s : simbolos) {
            if (s == null || s.trim().isEmpty()) {
                throw new ConfiguraçãoIvalidaException("Os símbolos dos jogadores não podem ser vazios.");
            }
            char c = s.trim().charAt(0);
            if (!simbolosUnicos.add(c)) {
                throw new ConfiguraçãoIvalidaException("Símbolo duplicado encontrado na configuração: '" + c + "'.");
            }
        }

        GameConfig.RegrasConfig.CondicaodeVitoriaConfig cv = config.getRegras().getCondicaodeVitoria();
        if (cv == null || cv.getTipo() == null || cv.getTipo().trim().isEmpty()) {
            throw new ConfiguraçãoIvalidaException("Condição de vitória não definida no arquivo.");
        }

        String tipo = cv.getTipo().toUpperCase().trim();
        if (tipo.equals("ALINHAMENTO") || tipo.equals("ALIGNMENT")) {
            int meta = cv.getQuantidade();
            if (meta < 2 || meta > Math.max(linhas, colunas)) {
                throw new ConfiguraçãoIvalidaException("Meta de alinhamento inválida (" + meta + ") para um tabuleiro " + linhas + "x" + colunas + ".");
            }
        }
        if (tipo.equals("ELIMINACAO") || tipo.equals("ELIMINATION")
                || tipo.equals("ATARI_GO") || tipo.equals("GO")
                || tipo.equals("CAPTURE_GROUP") || tipo.equals("CAPTURA_GRUPO")) {
            if (cv.getQuantidade() <= 0) {
                throw new ConfiguraçãoIvalidaException("A meta de capturas deve ser maior que zero.");
            }
        }
        if (tipo.equals("REVERSI") || tipo.equals("OTHELLO")) {
            if (linhas < 4 || colunas < 4) {
                throw new ConfiguraçãoIvalidaException("Othello/Reversi exige um tabuleiro de pelo menos 4x4.");
            }
            if (simbolos.size() != 2) {
                throw new ConfiguraçãoIvalidaException("Othello/Reversi exige exatamente 2 jogadores.");
            }
        }
        if ((tipo.equals("HEX") || tipo.equals("CONNECTION") || tipo.equals("CONEXAO")) && simbolos.size() != 2) {
            throw new ConfiguraçãoIvalidaException("O jogo Hex exige exatamente 2 jogadores.");
        }

        String pos = config.getRegras().getPosicionamento();
        if (pos != null && !pos.trim().isEmpty()) {
            String pNorm = pos.toUpperCase().trim();
            if (!pNorm.equals("EMPTY_POSITION") && !pNorm.equals("GRAVITY")) {
                throw new ConfiguraçãoIvalidaException("Regra de posicionamento '" + pos + "' não reconhecida.");
            }
        }

        String cap = config.getRegras().getCaptura();
        if (cap != null && !cap.trim().isEmpty()) {
            String cNorm = cap.toUpperCase().trim();
            if (!cNorm.equals("NONE") && !cNorm.equals("SANDWICH")) {
                throw new ConfiguraçãoIvalidaException("Regra de captura '" + cap + "' não reconhecida.");
            }
        }

        String turnos = config.getRegras().getTurnos();
        if (turnos != null && !turnos.trim().isEmpty()) {
            String tNorm = turnos.toUpperCase().trim();
            if (!tNorm.equals("RANDOM") && !tNorm.equals("ALEATORIO")
                    && !tNorm.equals("SEQUENTIAL") && !tNorm.equals("SEQUENCIAL")) {
                throw new ConfiguraçãoIvalidaException("Regra de turnos '" + turnos + "' não reconhecida.");
            }
        }

        List<List<String>> tabInicial = config.getTabuleiroInicial();
        if (tabInicial != null) {
            if (tabInicial.size() != linhas) {
                throw new ConfiguraçãoIvalidaException("O tabuleiro inicial deve possuir exatamente " + linhas + " linhas.");
            }
            for (int i = 0; i < linhas; i++) {
                List<String> linhaCfg = tabInicial.get(i);
                if (linhaCfg == null || linhaCfg.size() != colunas) {
                    throw new ConfiguraçãoIvalidaException("A linha " + (i + 1) + " do tabuleiro inicial deve possuir " + colunas + " colunas.");
                }
                for (String cel : linhaCfg) {
                    if (cel != null && !cel.trim().isEmpty()) {
                        char c = cel.trim().charAt(0);
                        if (c != '.' && c != '-' && !simbolosUnicos.contains(c)) {
                            throw new ConfiguraçãoIvalidaException("Símbolo inválido no tabuleiro inicial: '" + c + "'.");
                        }
                    }
                }
            }
        }
    }
}