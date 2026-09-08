import java.util.Scanner;
import java.util.Random;

public class Game {
    private Board tabuleiro;
    private Player jogador1;
    private Player jogador2;
    private Player jogadoratual;

    public  Game() {
    }
    public boolean verificarvitoria(char simbolo){
        return verificarlinha(simbolo) || verificarcoluna (simbolo) || verificardiagonal(simbolo);
    }
    public boolean verificarlinha (char simbolo){
        for (int l = 0; l < tabuleiro.getLinha(); l++){
            if (tabuleiro.getSimbolo(l,0) == simbolo && tabuleiro.getSimbolo(l,1) == simbolo && tabuleiro.getSimbolo(l,2) == simbolo){
                return true;
            }
        }
        return false;
    }
    public boolean verificarcoluna (char simbolo){
        for (int c = 0; c < tabuleiro.getColuna(); c++){
            if (tabuleiro.getSimbolo(0,c) == simbolo && tabuleiro.getSimbolo(1,c) == simbolo && tabuleiro.getSimbolo(2,c) == simbolo){
                return true;
            }
        }
        return false;
    }
    public boolean verificardiagonal (char simbolo){
        boolean principal =  tabuleiro.getSimbolo(0,0) == simbolo && tabuleiro.getSimbolo(1,1) == simbolo  && tabuleiro.getSimbolo(2,2) == simbolo;
        boolean secundaria = tabuleiro.getSimbolo(2,0 ) == simbolo && tabuleiro.getSimbolo(1,1) == simbolo && tabuleiro.getSimbolo(0,2) == simbolo;
        return principal || secundaria;
    }
    public void iniciarJogo(){
        Scanner sc = new Scanner(System.in);
        //Criação jogador 1
        System.out.println("Digite o nome do jogador1: ");
        String nomeJogador1 = sc.nextLine();
        System.out.println("Digite o simbolo do jogador1: ");
        char simboloJogador1 = sc.nextLine().charAt(0);
        this.jogador1 = new Player(nomeJogador1, simboloJogador1);
        // Criação jogador 2
        System.out.println("Digite o nome do jogador2: ");
        String nomeJogador2 = sc.nextLine();
        System.out.println("Digite o simbolo do jogador2: ");
        char simboloJogador2 = sc.nextLine().charAt(0);
        this.jogador2 = new Player(nomeJogador2, simboloJogador2);
        //Definindo tamanho do tabuleiro
        System.out.println("Digite o tamanho do tabuleiro: ");
        int tamanhotabuleiro = sc.nextInt();
        this.tabuleiro = new Board(tamanhotabuleiro,tamanhotabuleiro);
        //Definindo o jogador inicial
        java.util.Random gerador = new java.util.Random();
        boolean resultado = gerador.nextBoolean();
        if (resultado == true){
            System.out.println("Quem começa é o Jogador 1: " + jogador1.getNome());
            this.jogadoratual = jogador1;
        }
        else {
            System.out.println("Quem começaa é o jogador 2: " + jogador2.getNome());
            this.jogadoratual = jogador2;
        }
        // inicio do laço while para realizar o jogo
        boolean jogoativ= true;
        int jogada=0;
        int jogadasmax= tabuleiro.getLinha() * tabuleiro.getColuna();
        while(jogoativ){
            tabuleiro.exibirTabuleiro();

            System.out.println(jogadoratual.getNome() + " Digite a posição da linha que voce deseja jogar: ");
            int linha = sc.nextInt();
            System.out.println(jogadoratual.getNome() + " Digite a posição da coluna que voce deseja jogar: ");
            int coluna = sc.nextInt();
            if (tabuleiro.marcarPosicao(linha, coluna, jogadoratual.getSimbolo())) {
                jogada++;
                if (verificarvitoria(jogadoratual.getSimbolo())) {
                    tabuleiro.exibirTabuleiro();
                    System.out.println("Parabens!!" + jogadoratual.getNome() + "Voce venceu!!");
                    jogoativ = false;
                } else if (jogada == jogadasmax) {
                    System.out.println("O jogo deu empate (velha)");
                    break;
                } else {
                    if (jogadoratual == jogador1) {
                        jogadoratual = jogador2;
                    } else {
                        jogadoratual = jogador1;
                    }
                }
                }
            else{
                    System.out.println("Jogada inválida ou posição ocupada! Tente novamente.\n");
                }
            }



        }


    }



