import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel painelPrincipal;

    private GameConfig configAtual;
    private Game jogoAtual;
    private List<JTextField> camposNomesJogadores;
    private JPanel painelInputsJogadores;

    private JLabel lblTituloRegras;
    private JTextArea txtAreaRegras;

    private JLabel lblTituloJogo;
    private JLabel lblTurno;
    private JLabel lblPlacar;
    private JLabel lblStatus;
    private PainelTabuleiroVisual painelGrade;
    private JButton[][] botoesTabuleiro;
    private boolean partidaEncerrada;

    private int hoverLinha = -1;
    private int hoverColuna = -1;
    private List<int[]> caminhoVitoriaAtual = Collections.emptyList();

    private static final Color COR_FUNDO = new Color(245, 247, 250);
    private static final Color COR_PRIMARIA = new Color(30, 64, 175);
    private static final Color COR_SUCESSO = new Color(22, 163, 74);
    private static final Color COR_CASA_VAZIA = Color.WHITE;
    private static final Color[] CORES_JOGADORES = {
            new Color(29, 78, 216),   // Azul (Jogador 1)
            new Color(220, 38, 38),   // Vermelho (Jogador 2)
            new Color(22, 163, 74),   // Verde (Jogador 3)
            new Color(217, 119, 6)    // Laranja (Jogador 4)
    };

    public GameGUI() {
        super("Board Game Engine — UPE/POLI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 700);
        setMinimumSize(new Dimension(620, 560));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        painelPrincipal.add(criarTelaSelecao(), "SELECAO");
        painelPrincipal.add(criarTelaCadastro(), "CADASTRO");
        painelPrincipal.add(criarTelaRegras(), "REGRAS");
        painelPrincipal.add(criarTelaJogo(), "JOGO");

        add(painelPrincipal);
        cardLayout.show(painelPrincipal, "SELECAO");
    }

    private JPanel criarTelaSelecao() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("BOARD GAME ENGINE", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(COR_PRIMARIA);

        JLabel subtitulo = new JLabel("Selecione um arquivo de configuração JSON na pasta 'Games' ou carregue um externo:", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel topo = new JPanel(new GridLayout(2, 1, 5, 5));
        topo.setOpaque(false);
        topo.add(titulo);
        topo.add(subtitulo);
        painel.add(topo, BorderLayout.NORTH);

        DefaultListModel<File> modeloLista = new DefaultListModel<>();
        carregarArquivosPastaGames(modeloLista);

        JList<File> listaJogos = new JList<>(modeloLista);
        listaJogos.setFont(new Font("SansSerif", Font.PLAIN, 16));
        listaJogos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJogos.setFixedCellHeight(36);
        listaJogos.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof File) {
                    setText("  " + ((File) value).getName());
                }
                return this;
            }
        });
        if (!modeloLista.isEmpty()) {
            listaJogos.setSelectedIndex(0);
        }

        JScrollPane scroll = new JScrollPane(listaJogos);
        painel.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        rodape.setOpaque(false);

        JButton btnCarregar = estilizarBotao("Carregar Jogo Selecionado", COR_PRIMARIA);
        JButton btnProcurar = estilizarBotao("Abrir JSON Externo...", new Color(71, 85, 105));
        JButton btnAtualizar = estilizarBotao("Atualizar Lista", new Color(100, 116, 139));

        btnCarregar.addActionListener(e -> {
            File selecionado = listaJogos.getSelectedValue();
            if (selecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um arquivo JSON da lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            processarArquivoConfig(selecionado.getPath());
        });

        btnProcurar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setDialogTitle("Selecione o arquivo JSON do jogo");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                processarArquivoConfig(chooser.getSelectedFile().getPath());
            }
        });

        btnAtualizar.addActionListener(e -> carregarArquivosPastaGames(modeloLista));

        rodape.add(btnCarregar);
        rodape.add(btnProcurar);
        rodape.add(btnAtualizar);
        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    private void carregarArquivosPastaGames(DefaultListModel<File> modelo) {
        modelo.clear();
        File pastaGames = new File("Games");
        if (pastaGames.exists() && pastaGames.isDirectory()) {
            File[] arquivos = pastaGames.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (arquivos != null) {
                for (File f : arquivos) {
                    modelo.addElement(f);
                }
            }
        }
    }

    private void processarArquivoConfig(String caminho) {
        try {
            this.configAtual = GameLoader.LoadConfig(caminho);
            WinConditionFactory.criar(configAtual.getRegras().getCondicaodeVitoria());
            prepararCamposCadastroJogadores();
            cardLayout.show(painelPrincipal, "CADASTRO");
        } catch (ConfiguraçãoIvalidaException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar o jogo:\n" + ex.getMessage(),
                    "Configuração Inválida",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel criarTelaCadastro() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(30, 60, 30, 60));

        JLabel titulo = new JLabel("CADASTRO DE JOGADORES", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(COR_PRIMARIA);
        painel.add(titulo, BorderLayout.NORTH);

        painelInputsJogadores = new JPanel();
        painelInputsJogadores.setLayout(new BoxLayout(painelInputsJogadores, BoxLayout.Y_AXIS));
        painelInputsJogadores.setOpaque(false);

        JScrollPane scroll = new JScrollPane(painelInputsJogadores);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        painel.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        rodape.setOpaque(false);

        JButton btnVoltar = estilizarBotao("Voltar", new Color(100, 116, 139));
        JButton btnAvancar = estilizarBotao("Ver Regras do Jogo", COR_PRIMARIA);

        btnVoltar.addActionListener(e -> cardLayout.show(painelPrincipal, "SELECAO"));
        btnAvancar.addActionListener(e -> montarPartidaEExibirRegras());

        rodape.add(btnVoltar);
        rodape.add(btnAvancar);
        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    private void prepararCamposCadastroJogadores() {
        painelInputsJogadores.removeAll();
        camposNomesJogadores = new ArrayList<>();

        List<String> simbolos = configAtual.getJogador().getSimbolo();
        for (int i = 0; i < simbolos.size(); i++) {
            char simbolo = simbolos.get(i).trim().charAt(0);

            JPanel linha = new JPanel(new BorderLayout(10, 5));
            linha.setOpaque(false);
            linha.setMaximumSize(new Dimension(500, 65));
            linha.setBorder(new EmptyBorder(8, 0, 8, 0));

            JLabel lbl = new JLabel("Nome do Jogador " + (i + 1) + " (Símbolo '" + simbolo + "'):");
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));

            JTextField txt = new JTextField("Jogador " + (i + 1));
            txt.setFont(new Font("SansSerif", Font.PLAIN, 15));

            linha.add(lbl, BorderLayout.NORTH);
            linha.add(txt, BorderLayout.CENTER);

            camposNomesJogadores.add(txt);
            painelInputsJogadores.add(linha);
        }

        painelInputsJogadores.revalidate();
        painelInputsJogadores.repaint();
    }

    private void montarPartidaEExibirRegras() {
        try {
            List<Player> jogadores = new ArrayList<>();
            List<String> simbolos = configAtual.getJogador().getSimbolo();

            for (int i = 0; i < simbolos.size(); i++) {
                char simboloChar = simbolos.get(i).trim().charAt(0);
                String nome = camposNomesJogadores.get(i).getText().trim();
                if (nome.isEmpty()) {
                    nome = "Jogador " + (i + 1);
                }
                jogadores.add(new Player(nome, simboloChar));
            }

            Board tabuleiro = new Board(
                    configAtual.getTabuleiro().getLinhas(),
                    configAtual.getTabuleiro().getColunas()
            );
            WinCondition winCondition = WinConditionFactory.criar(configAtual.getRegras().getCondicaodeVitoria());

            this.jogoAtual = new Game(tabuleiro, jogadores, configAtual, winCondition);
            this.jogoAtual.prepararPartida();

            String nomeJogo = (configAtual.getJogo() != null && !configAtual.getJogo().trim().isEmpty())
                    ? configAtual.getJogo().toUpperCase()
                    : "REGRAS DA PARTIDA";

            lblTituloRegras.setText("REGRAS: " + nomeJogo);
            txtAreaRegras.setText(jogoAtual.obterResumoRegras());
            txtAreaRegras.setCaretPosition(0);

            cardLayout.show(painelPrincipal, "REGRAS");
        } catch (ConfiguraçãoIvalidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarTelaRegras() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(30, 50, 30, 50));

        lblTituloRegras = new JLabel("REGRAS DA PARTIDA", SwingConstants.CENTER);
        lblTituloRegras.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTituloRegras.setForeground(COR_PRIMARIA);
        painel.add(lblTituloRegras, BorderLayout.NORTH);

        txtAreaRegras = new JTextArea();
        txtAreaRegras.setEditable(false);
        txtAreaRegras.setLineWrap(true);
        txtAreaRegras.setWrapStyleWord(true);
        txtAreaRegras.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtAreaRegras.setBackground(Color.WHITE);
        txtAreaRegras.setBorder(new EmptyBorder(18, 18, 18, 18));

        JScrollPane scroll = new JScrollPane(txtAreaRegras);
        painel.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        rodape.setOpaque(false);

        JButton btnVoltar = estilizarBotao("Voltar", new Color(100, 116, 139));
        JButton btnContinuar = estilizarBotao("Continuar", COR_SUCESSO);

        btnVoltar.addActionListener(e -> cardLayout.show(painelPrincipal, "CADASTRO"));
        btnContinuar.addActionListener(e -> {
            construirGradeVisual();
            cardLayout.show(painelPrincipal, "JOGO");
        });

        rodape.add(btnVoltar);
        rodape.add(btnContinuar);
        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarTelaJogo() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(18, 25, 18, 25));

        JPanel topo = new JPanel(new GridLayout(3, 1, 4, 4));
        topo.setOpaque(false);

        lblTituloJogo = new JLabel("JOGO", SwingConstants.CENTER);
        lblTituloJogo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTituloJogo.setForeground(COR_PRIMARIA);

        lblTurno = new JLabel("Turno: -", SwingConstants.CENTER);
        lblTurno.setFont(new Font("SansSerif", Font.BOLD, 16));

        lblPlacar = new JLabel("", SwingConstants.CENTER);
        lblPlacar.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblPlacar.setForeground(new Color(180, 83, 9));

        topo.add(lblTituloJogo);
        topo.add(lblTurno);
        topo.add(lblPlacar);
        painel.add(topo, BorderLayout.NORTH);

        painelGrade = new PainelTabuleiroVisual();
        painelGrade.setBackground(new Color(203, 213, 225));
        painelGrade.setBorder(new EmptyBorder(10, 10, 10, 10));
        painel.add(painelGrade, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(10, 10));
        inferior.setOpaque(false);

        lblStatus = new JLabel("Clique em uma casa do tabuleiro para jogar.", SwingConstants.CENTER);
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel botoesRodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        botoesRodape.setOpaque(false);

        JButton btnReiniciar = estilizarBotao("Reiniciar Partida", COR_PRIMARIA);
        JButton btnMenu = estilizarBotao("Trocar de Jogo", new Color(100, 116, 139));

        btnReiniciar.addActionListener(e -> {
            montarPartidaEExibirRegras();
            construirGradeVisual();
            cardLayout.show(painelPrincipal, "JOGO");
        });

        btnMenu.addActionListener(e -> cardLayout.show(painelPrincipal, "SELECAO"));

        botoesRodape.add(btnReiniciar);
        botoesRodape.add(btnMenu);

        inferior.add(lblStatus, BorderLayout.NORTH);
        inferior.add(botoesRodape, BorderLayout.SOUTH);
        painel.add(inferior, BorderLayout.SOUTH);

        return painel;
    }

    private void construirGradeVisual() {
        Board board = jogoAtual.getTabuleiro();
        int linhas = board.getLinha();
        int colunas = board.getColuna();

        this.partidaEncerrada = false;
        this.hoverLinha = -1;
        this.hoverColuna = -1;
        this.caminhoVitoriaAtual = Collections.emptyList();

        painelGrade.removeAll();
        painelGrade.setLayout(new GridLayout(linhas, colunas, 4, 4));
        botoesTabuleiro = new JButton[linhas][colunas];

        int maiorDim = Math.max(linhas, colunas);
        int tamanhoFonte = (maiorDim >= 12) ? 14 : (maiorDim >= 7 ? 20 : 28);
        Font fontePeca = new Font("SansSerif", Font.BOLD, tamanhoFonte);

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                final int r = i;
                final int c = j;

                JButton btn = new JButton("");
                btn.setFont(fontePeca);
                btn.setFocusPainted(false);
                btn.setMargin(new Insets(1, 1, 1, 1));
                btn.setBackground(COR_CASA_VAZIA);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                btn.addActionListener(e -> processarJogadaVisual(r, c));
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hoverLinha = r;
                        hoverColuna = c;
                        painelGrade.repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (hoverLinha == r && hoverColuna == c) {
                            hoverLinha = -1;
                            hoverColuna = -1;
                            painelGrade.repaint();
                        }
                    }
                });

                botoesTabuleiro[i][j] = btn;
                painelGrade.add(btn);
            }
        }

        String titulo = (configAtual.getJogo() != null && !configAtual.getJogo().trim().isEmpty())
                ? configAtual.getJogo().toUpperCase()
                : "PARTIDA";
        lblTituloJogo.setText(titulo);

        atualizarInterfacePartida("Sorteio realizado! Começa: " + jogoAtual.getJogadorAtual().getNome());
        painelGrade.revalidate();
        painelGrade.repaint();
    }

    private void processarJogadaVisual(int linha, int coluna) {
        if (partidaEncerrada) return;

        Player atual = jogoAtual.getJogadorAtual();
        char simbolo = atual.getSimbolo();

        try {
            int[] destino = jogoAtual.resolverDestinoJogada(linha, coluna);
            jogoAtual.validarMovimento(destino[0], destino[1], simbolo);
            jogoAtual.aplicarMovimento(destino[0], destino[1], simbolo);
            atualizarTabuleiroVisual();

            String evento = jogoAtual.consumirUltimoEvento();

            if (jogoAtual.verificarVitoriaAtual(destino[0], destino[1])) {
                partidaEncerrada = true;
                this.caminhoVitoriaAtual = jogoAtual.obterCaminhoVitoria(destino[0], destino[1]);
                atualizarTabuleiroVisual();
                painelGrade.repaint();

                String msgVitoria = "PARABÉNS!! " + atual.getNome().toUpperCase() + " (" + simbolo + ") VENCEU A PARTIDA!";
                lblTurno.setText(msgVitoria);
                lblTurno.setForeground(COR_SUCESSO);
                lblStatus.setText(evento.isEmpty() ? "Partida encerrada." : evento);
                lblPlacar.setText(jogoAtual.getTextoPlacar());
                JOptionPane.showMessageDialog(this, msgVitoria, "Vitória!", JOptionPane.INFORMATION_MESSAGE);
            } else if (jogoAtual.tabuleiroCheio()) {
                encerrarPartidaSemMovimentosVisual();
            } else {
                jogoAtual.avancarTurno();
                if (!jogoAtual.existeJogadaValidaPara(jogoAtual.getJogadorAtual().getSimbolo())) {
                    String nomePassou = jogoAtual.getJogadorAtual().getNome();
                    jogoAtual.avancarTurno();
                    if (!jogoAtual.existeJogadaValidaPara(jogoAtual.getJogadorAtual().getSimbolo())) {
                        encerrarPartidaSemMovimentosVisual();
                    } else {
                        String avisoPassou = nomePassou + " não possui jogadas válidas e passou a vez!";
                        atualizarInterfacePartida(avisoPassou);
                        JOptionPane.showMessageDialog(this, avisoPassou, "Passou a Vez", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    String statusJogada = "Última jogada: " + atual.getNome() + " em (" + (destino[0] + 1) + ", " + (destino[1] + 1) + ")";
                    if (!evento.isEmpty()) {
                        statusJogada += " — " + evento;
                    }
                    atualizarInterfacePartida(statusJogada);
                }
            }
        } catch (MovimentaçãoInvalidaException ex) {
            lblStatus.setText("Aviso: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Jogada Inválida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void encerrarPartidaSemMovimentosVisual() {
        partidaEncerrada = true;
        atualizarTabuleiroVisual();
        lblPlacar.setText(jogoAtual.getTextoPlacar());
        Player vencedorFinal = jogoAtual.obterVencedorFinal();

        if (vencedorFinal != null) {
            String msg = "FIM DE JOGO! VENCEDOR POR MAIORIA: " + vencedorFinal.getNome().toUpperCase() + " (" + vencedorFinal.getSimbolo() + ")!";
            lblTurno.setText(msg);
            lblTurno.setForeground(COR_SUCESSO);
            lblStatus.setText("Partida encerrada.");
            JOptionPane.showMessageDialog(this, msg, "Vitória por Maioria!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            lblTurno.setText("O JOGO TERMINOU EM EMPATE!");
            lblTurno.setForeground(Color.DARK_GRAY);
            lblStatus.setText("Sem movimentos restantes.");
            JOptionPane.showMessageDialog(this, "O jogo terminou em empate!", "Empate", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void atualizarInterfacePartida(String mensagemStatus) {
        atualizarTabuleiroVisual();
        Player atual = jogoAtual.getJogadorAtual();
        lblTurno.setText("Vez de: " + atual.getNome() + " (" + atual.getSimbolo() + ")");
        lblTurno.setForeground(obterCorSimbolo(atual.getSimbolo()));
        lblPlacar.setText(jogoAtual.getTextoPlacar());
        lblStatus.setText(mensagemStatus);
        painelGrade.repaint();
    }

    private void atualizarTabuleiroVisual() {
        Board board = jogoAtual.getTabuleiro();
        for (int i = 0; i < board.getLinha(); i++) {
            for (int j = 0; j < board.getColuna(); j++) {
                char s = board.getSimbolo(i, j);
                JButton btn = botoesTabuleiro[i][j];
                boolean celulaVencedora = estaNoCaminhoVitoria(i, j);

                if (s == ' ') {
                    btn.setText("");
                    btn.setBackground(COR_CASA_VAZIA);
                } else {
                    btn.setText(String.valueOf(s));
                    btn.setForeground(obterCorSimbolo(s));
                    btn.setBackground(celulaVencedora ? new Color(220, 252, 231) : new Color(241, 245, 249));
                }
            }
        }
    }

    private boolean estaNoCaminhoVitoria(int r, int c) {
        for (int[] pos : caminhoVitoriaAtual) {
            if (pos[0] == r && pos[1] == c) return true;
        }
        return false;
    }

    private Color obterCorSimbolo(char simbolo) {
        List<Player> jogadores = jogoAtual.getJogadores();
        for (int i = 0; i < jogadores.size(); i++) {
            if (jogadores.get(i).getSimbolo() == simbolo) {
                return CORES_JOGADORES[i % CORES_JOGADORES.length];
            }
        }
        return Color.BLACK;
    }

    private JButton estilizarBotao(String texto, Color corFundo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(corFundo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private class PainelTabuleiroVisual extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (jogoAtual == null || botoesTabuleiro == null) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Bordas coloridas indicativas (ex.: Topo/Base Azul e Esquerda/Direita Vermelho no Hex)
            if (jogoAtual.mostrarBordasJogadores()) {
                int espessura = 7;
                int w = getWidth();
                int h = getHeight();

                Color corP1 = CORES_JOGADORES[0];
                Color corP2 = CORES_JOGADORES[1];

                g2.setColor(corP1);
                g2.fillRect(0, 0, w, espessura);
                g2.fillRect(0, h - espessura, w, espessura);

                g2.setColor(corP2);
                g2.fillRect(0, espessura, espessura, h - 2 * espessura);
                g2.fillRect(w - espessura, espessura, espessura, h - 2 * espessura);
            }

            Board board = jogoAtual.getTabuleiro();

            // 2. Linhas sólidas das conexões já formadas entre peças do mesmo jogador
            List<int[]> conexoes = jogoAtual.obterConexoesVisuais();
            g2.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int[] seg : conexoes) {
                JButton b1 = botoesTabuleiro[seg[0]][seg[1]];
                JButton b2 = botoesTabuleiro[seg[2]][seg[3]];
                char simbolo = board.getSimbolo(seg[0], seg[1]);
                Color base = obterCorSimbolo(simbolo);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 170));

                int x1 = b1.getX() + b1.getWidth() / 2;
                int y1 = b1.getY() + b1.getHeight() / 2;
                int x2 = b2.getX() + b2.getWidth() / 2;
                int y2 = b2.getY() + b2.getHeight() / 2;

                g2.drawLine(x1, y1, x2, y2);
                g2.fillOval(x1 - 4, y1 - 4, 8, 8);
                g2.fillOval(x2 - 4, y2 - 4, 8, 8);
            }

            // 3. Preview tracejado ao passar o mouse em uma casa livre
            if (!partidaEncerrada && hoverLinha >= 0 && hoverColuna >= 0) {
                char simboloTurno = jogoAtual.getJogadorAtual().getSimbolo();
                List<int[]> previews = jogoAtual.obterConexoesPreview(hoverLinha, hoverColuna, simboloTurno);
                if (!previews.isEmpty()) {
                    float[] tracejado = {6.0f, 6.0f};
                    g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, tracejado, 0.0f));
                    Color base = obterCorSimbolo(simboloTurno);
                    g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 185));

                    for (int[] seg : previews) {
                        JButton b1 = botoesTabuleiro[seg[0]][seg[1]];
                        JButton b2 = botoesTabuleiro[seg[2]][seg[3]];
                        int x1 = b1.getX() + b1.getWidth() / 2;
                        int y1 = b1.getY() + b1.getHeight() / 2;
                        int x2 = b2.getX() + b2.getWidth() / 2;
                        int y2 = b2.getY() + b2.getHeight() / 2;
                        g2.drawLine(x1, y1, x2, y2);
                    }
                }
            }

            // 4. Linha destacada da ponte/alinhamento vencedor ao encerrar a partida
            if (caminhoVitoriaAtual != null && caminhoVitoriaAtual.size() >= 2) {
                g2.setStroke(new BasicStroke(7.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(22, 163, 74, 210));
                for (int i = 0; i < caminhoVitoriaAtual.size() - 1; i++) {
                    int[] pA = caminhoVitoriaAtual.get(i);
                    int[] pB = caminhoVitoriaAtual.get(i + 1);
                    JButton b1 = botoesTabuleiro[pA[0]][pA[1]];
                    JButton b2 = botoesTabuleiro[pB[0]][pB[1]];
                    int x1 = b1.getX() + b1.getWidth() / 2;
                    int y1 = b1.getY() + b1.getHeight() / 2;
                    int x2 = b2.getX() + b2.getWidth() / 2;
                    int y2 = b2.getY() + b2.getHeight() / 2;
                    g2.drawLine(x1, y1, x2, y2);
                }
            }

            g2.dispose();
        }
    }
}