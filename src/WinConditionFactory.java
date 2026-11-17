public class WinConditionFactory {

    public static WinCondition criar(GameConfig.RegrasConfig.CondicaodeVitoriaConfig config) throws ConfiguraçãoIvalidaException {
        if (config == null || config.getTipo() == null) {
            throw new ConfiguraçãoIvalidaException("O tipo da condição de vitória não foi informado no arquivo de configuração.");
        }

        String tipo = config.getTipo().toUpperCase().trim();

        switch (tipo) {
            case "ALINHAMENTO":
            case "ALIGNMENT":
                return new AlinhamentoWinCondition(config.getQuantidade());
            case "ELIMINACAO":
            case "ELIMINATION":
                return new EliminationWinCondition(config.getQuantidade());
            case "REVERSI":
            case "OTHELLO":
            case "MAJORITY":
            case "MAIORIA":
                return new ReversiWinCondition();
            case "HEX":
            case "CONNECTION":
            case "CONEXAO":
                return new HexWinCondition();
            case "ATARI_GO":
            case "GO":
            case "CAPTURE_GROUP":
            case "CAPTURA_GRUPO":
                return new AtariGoWinCondition(config.getQuantidade());

            default:
                throw new ConfiguraçãoIvalidaException("Regra de vitória '" + config.getTipo() + "' não reconhecida.");
        }
    }
}