public class Main {
    public static void main(String[] args) {
        Board tabuleiro = new Board(3,3);
        tabuleiro.exibirtabuleiro();
        Player jogador = new Player("Julio", 'X');
        tabuleiro.marcarposicao(0,0, jogador.getSimbolo());
        tabuleiro.exibirtabuleiro();


    }
}
