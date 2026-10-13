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
            case "CANTOS":
            case "CORNERS":
                return new CantosWinCondition();
            case "ELIMINACAO":
            case "ELIMINATION":
            case "MAIORIA":
                return new EliminationWinCondition(config.getQuantidade());
            case "REACH_POSITION":
            case "POSICAO":
            case "TRAVESSIA":
                return new ReachPositionWinCondition();

            default:
                throw new ConfiguraçãoIvalidaException("Regra de vitória '" + config.getTipo() + "' não reconhecida.");
        }
    }
}