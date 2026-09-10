import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class GameLoader {
    public static GameConfig LoadConfig(String caminhodoarquivo) throws ConfiguraçãoIvalidaException {
        File file = new File(caminhodoarquivo);
        if (!file.exists()) {
            throw new ConfiguraçãoIvalidaException("Arquivo de configuração não encontrado: " + caminhodoarquivo);
        }
        try (FileReader reader = new FileReader(file)){
            Gson gson = new Gson();
            GameConfig config = gson.fromJson(reader, GameConfig.class);
            validarConfig(config);
            return config;
        }
        catch (Exception e) {
            if (e instanceof ConfiguraçãoIvalidaException) {
                throw (ConfiguraçãoIvalidaException) e;
            }
            throw new ConfiguraçãoIvalidaException("Erro ao processar o JSON: " + e.getMessage());
        }
    }
    private static void validarConfig(GameConfig config) throws ConfiguraçãoIvalidaException {
        if (config == null || config.getTabuleiro() == null || config.getJogador() == null || config.getRegras() == null) {
            throw new ConfiguraçãoIvalidaException("O arquivo de configuração possui estruturas ausentes.");
        }
        if  (config.getTabuleiro().getLinhas() <=0 || config.getTabuleiro().getColunas() <=0) {
            throw new ConfiguraçãoIvalidaException("As dimensões do tabuleiro devem ser maiores que zero.");
        }
        if (config.getJogador().getSimbolo() == null || config.getJogador().getSimbolo().size() <2) {
            throw new ConfiguraçãoIvalidaException("O jogo deve ter pelo menos 2 símbolos de jogadores definidos.");
        }
        if(config.getRegras().getCondicaodeVitoria() == null){
            throw new ConfiguraçãoIvalidaException("Condição de vitória não definida no arquivo.");
        }
        String tipoVitoria = config.getRegras().getCondicaodeVitoria().getTipo();
        if (!tipoVitoria.equalsIgnoreCase("ALINHAMENTO" )&& !tipoVitoria.equalsIgnoreCase("Alignment")) {
            throw new ConfiguraçãoIvalidaException("Regra de vitória '" + tipoVitoria + "' não é reconhecida pela Engine.");
        }
        if (config.getRegras().getCondicaodeVitoria().getQuantidade()<=0){
            throw new ConfiguraçãoIvalidaException("A quantidade para a meta deve ser maior que zero.");
        }
    }


}

