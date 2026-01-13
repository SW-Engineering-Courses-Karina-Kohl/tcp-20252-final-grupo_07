package app.view;

import model.*;

import javax.swing.*;
import javax.swing.border.Border;

import app.controller.AppController;
import app.view.utils.BtnDefault;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class PreferenciasGUI {

    // Caminho do arquivo de preferências
    private static final String CAMINHO_PREFS = "preferencias.csv";

    // matriz horários bloqueados
    private static JCheckBox[][] matrizDisponibilidade;

    // grade de dias/horas usada em toda a classe
    private static final DiaSemana[] DIAS_SEMANA = {
            DiaSemana.SEGUNDA,
            DiaSemana.TERCA,
            DiaSemana.QUARTA,
            DiaSemana.QUINTA,
            DiaSemana.SEXTA,
            DiaSemana.SABADO
    };

    private static final LocalTime[] INICIOS = {
            LocalTime.of(7, 30),  
            LocalTime.of(8, 30),  
            LocalTime.of(9, 30),  
            LocalTime.of(10, 30), 
            LocalTime.of(11, 30), 
            LocalTime.of(13, 30), 
            LocalTime.of(14, 30), 
            LocalTime.of(15, 30), 
            LocalTime.of(16, 30), 
            LocalTime.of(17, 30), 
            LocalTime.of(18, 30), 
            LocalTime.of(19, 30), 
            LocalTime.of(20, 30),
            LocalTime.of(21,30)  
    };

    private static final LocalTime[] FINS = {
            LocalTime.of(8, 10),
            LocalTime.of(9, 10),
            LocalTime.of(10, 10),
            LocalTime.of(11, 10),
            LocalTime.of(12, 10),
            LocalTime.of(14, 10),
            LocalTime.of(15, 10),
            LocalTime.of(16, 10),
            LocalTime.of(17, 10),
            LocalTime.of(18, 10),
            LocalTime.of(19, 10),
            LocalTime.of(20, 10),
            LocalTime.of(21, 10),
            LocalTime.of(22, 10)
    };

    //checkboxes de professores
    private static Map<String, JCheckBox> mapaPref = new HashMap<>();
    private static Map<String, JCheckBox> mapaDesc = new HashMap<>();
    private static Map<String, Turma> mapaTurma = new HashMap<>();

    // painéis que contêm os checkboxes (pra poder atualizar depois)
    private static JPanel painelPref;
    private static JPanel painelDesc;

    //acessar em carregar/limpar prefs
    private static JRadioButton rbManha;
    private static JRadioButton rbTarde;
    private static JRadioButton rbNoite;
    private static ButtonGroup grupoTurno;
    private static JSpinner spinnerNumCad;

    public static JPanel criarTela(AppController controller) {
        JPanel painel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Preferencias do Usuario", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        painel.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2));

        //lado esquerdo- horários bloqueados
        JPanel painelHorarios = criarPainelHorarios();
        centro.add(painelHorarios);

        //lado direito- turno + turmas + nº cadeiras 
        JPanel painelDireito = new JPanel();
        painelDireito.setLayout(new BoxLayout(painelDireito, BoxLayout.Y_AXIS));

        //turno preferido
        JPanel painelTurno = new JPanel();
        painelTurno.setBorder(BorderFactory.createTitledBorder("Turno preferido"));
        rbManha = new JRadioButton("Manhã");
        rbTarde = new JRadioButton("Tarde");
        rbNoite = new JRadioButton("Noite");
        
        grupoTurno = new ButtonGroup();
        grupoTurno.add(rbManha);
        grupoTurno.add(rbTarde);
        grupoTurno.add(rbNoite);

        MouseAdapter toggleListener = new MouseAdapter() {
            boolean estavaSelecionado = false; 

            @Override
            public void mousePressed(MouseEvent e) {
                JRadioButton btn = (JRadioButton) e.getSource();
                estavaSelecionado = btn.isSelected();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JRadioButton btn = (JRadioButton) e.getSource();
                //limpa apenas se click do mouse foi solto dentro do btn
                if (estavaSelecionado && btn.contains(e.getPoint())) {
                    grupoTurno.clearSelection();
                }
            }
        };

        // Adiciona esse comportamento aos 3 botões
        rbManha.addMouseListener(toggleListener);
        rbTarde.addMouseListener(toggleListener);
        rbNoite.addMouseListener(toggleListener);

        painelTurno.add(rbManha);
        painelTurno.add(rbTarde);
        painelTurno.add(rbNoite);

        painelDireito.add(painelTurno);

        //num cadeiras
        JPanel painelNumCad = new JPanel();
        painelNumCad.setBorder(BorderFactory.createTitledBorder("Numero de disciplinas desejado (use as setinhas)"));
        spinnerNumCad = new JSpinner(new SpinnerNumberModel(controller.getNumDisciplinas(), 1, controller.getNumDisciplinas(), 1));
        painelNumCad.add(spinnerNumCad);
        painelDireito.add(painelNumCad);

        //bloco das turmas preferidas e descartadas
        JPanel painelTurmas = new JPanel();
        painelTurmas.setLayout(new GridLayout(2, 1));

        // preferidos
        painelPref = new JPanel();
        painelPref.setLayout(new BoxLayout(painelPref, BoxLayout.Y_AXIS));
        painelPref.setBorder(BorderFactory.createTitledBorder("Turmas preferidas"));

        //barra de scroll das turmas preferidas
        JScrollPane scrollpainelPref = new JScrollPane(painelPref);
        scrollpainelPref.getVerticalScrollBar().setUnitIncrement(8); //scroll anda 1 linha 
        painelTurmas.add(scrollpainelPref);

        // descartados
        painelDesc = new JPanel();
        painelDesc.setLayout(new BoxLayout(painelDesc, BoxLayout.Y_AXIS));
        painelDesc.setBorder(BorderFactory.createTitledBorder("Turmas descartadas"));

        //barra de scroll das turmas descartadas
        JScrollPane scrollpainelDesc = new JScrollPane(painelDesc);
        scrollpainelDesc.getVerticalScrollBar().setUnitIncrement(8); //scroll anda 1 linha 
        painelTurmas.add(scrollpainelDesc);

        //monta a lista inicial 
        atualizarTurmas(controller);
        
        painelDireito.add(painelTurmas);

        centro.add(painelDireito);

        painel.add(centro, BorderLayout.CENTER);

        //botoes
        JPanel painelBotoes = new JPanel();
        
        BtnDefault btnMenu = new BtnDefault("Ir para menu");
        btnMenu.addActionListener(e -> {
            /* 
            painelPref.removeAll();
            painelDesc.removeAll();

            mapaPref.clear();
            mapaDesc.clear();
            mapaTurma.clear();
        */
            controller.mostrarMenuInicial();
        });

        BtnDefault btnCarregar = new BtnDefault("Carregar preferências");
        btnCarregar.addActionListener(e -> carregarPreferenciasNaTela(controller));

        BtnDefault btnLimpar = new BtnDefault("Limpar preferências");
        btnLimpar.addActionListener(e -> {
            Preferencias vazias = new Preferencias(); // construtor zera listas e usa defaults
            aplicarPreferenciasNaTela(controller, vazias);

            // também sobrescreve o arquivo com tudo vazio
            //GerenciadorDePreferencias ger = new GerenciadorDePreferencias();
            //ger.salvarPreferencias(CAMINHO_PREFS, vazias);

            JOptionPane.showMessageDialog(painel,
                    "Preferências limpas.",
                    "Informação", JOptionPane.INFORMATION_MESSAGE);
        });

        BtnDefault btnGerar = new BtnDefault("Salvar e Gerar grades");
        btnGerar.addActionListener(e -> {

            if (!controller.temTurmas()) {
                JOptionPane.showMessageDialog(painel,
                "Nenhuma turma cadastrada (CSV ou manual).",
                "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Preferencias prefs = new Preferencias();

            // turno preferido
            if (rbManha.isSelected()) {
                prefs.setTurnoPreferido(Turno.MANHA);
            } else if (rbTarde.isSelected()) {
                prefs.setTurnoPreferido(Turno.TARDE);
            } else if (rbNoite.isSelected()) {
                prefs.setTurnoPreferido(Turno.NOITE);
            }

            //número de cadeiras
            int numCadeiras = (int) spinnerNumCad.getValue();
            prefs.setNumeroDisciplinas(numCadeiras);

            //horários bloqueados
            List<Horario> bloqueados = obterHorariosBloqueados();
            for (Horario h : bloqueados) {
                prefs.adicionarHorarioBloqueado(h);
            }

            //turmas preferidas
            for (String dadosTurma : mapaPref.keySet()) {
                JCheckBox cb = mapaPref.get(dadosTurma);

                if (cb.isSelected()) {
                    //recupera a turma associada a string (que vai no checkbox) de seus dados
                    Turma t = mapaTurma.get(dadosTurma);
                    if(t != null){
                        prefs.adicionarTurmaPreferida(t);
                    }
                    else{
                        System.err.println("Erro ao add turma preferida: A chave '" + dadosTurma + "' não foi encontrada no mapa de turmas.");                    }
                }
            }

            //turmas evitadas
            for (String dadosTurma : mapaDesc.keySet()) {
                JCheckBox cb = mapaDesc.get(dadosTurma);

                if (cb.isSelected()) {
                    //recupera a turma associada a string (que vai no checkbox) de seus dados
                    Turma t = mapaTurma.get(dadosTurma);
                    if(t != null){
                        prefs.adicionarTurmaDescartada(t);
                    }
                    else{
                        System.err.println("Erro ao add turma descartada: A chave '" + dadosTurma + "' não foi encontrada no mapa de turmas.");                    }
                }
            }

            //salva no controller
            controller.definirPreferencias(prefs);

            // salva no CSV
            GerenciadorDePreferencias ger = new GerenciadorDePreferencias();
            ger.salvarPreferencias(CAMINHO_PREFS, prefs);

            controller.gerarGrades();
        });

        painelBotoes.add(btnMenu);
        painelBotoes.add(btnCarregar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnGerar);

        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    // matriz de horários bloqueados
    private static JPanel criarPainelHorarios() {
        String[] dias = {"SEG", "TER", "QUA", "QUI", "SEX", "SÁB"};

        String[] horariosLabel = {
                "07:30", "08:30", "09:30", "10:30", "11:30", "13:30", "14:30", "15:30",
                "16:30", "17:30", "18:30", "19:30", "20:30", "21:30"
        };

        int linhas = horariosLabel.length;
        int colunas = dias.length;

        matrizDisponibilidade = new JCheckBox[linhas][colunas];

        JPanel painel = new JPanel(new GridLayout(linhas + 1, colunas + 1));
        painel.setBorder(BorderFactory.createTitledBorder("Marque os horarios em que NAO deseja ter aula"));

        painel.add(new JLabel("")); // canto vazio

        for (String dia : dias) {
            painel.add(new JLabel(dia, SwingConstants.CENTER));
        }

        for (int i = 0; i < linhas; i++) {
            painel.add(new JLabel(horariosLabel[i], SwingConstants.CENTER));

            for (int j = 0; j < colunas; j++) {
                JCheckBox cb = new JCheckBox();
                customizarCheckbox(cb);
                matrizDisponibilidade[i][j] = cb;
                painel.add(cb);
            }
        }

        return painel;
    }

    private static void customizarCheckbox(JCheckBox cb){
        Border bordaNormal = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        Border bordaFoco = BorderFactory.createLineBorder(Color.GRAY);

        cb.setHorizontalAlignment(SwingConstants.CENTER);
        cb.setBorderPainted(true); 
        cb.setBorder(bordaNormal); //define borda inicial

        cb.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                cb.setBorder(bordaFoco);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                cb.setBorder(bordaNormal);
            }
        });
    }

    private static List<Horario> obterHorariosBloqueados() {
        List<Horario> bloqueados = new ArrayList<>();

        for (int i = 0; i < matrizDisponibilidade.length; i++) {
            for (int j = 0; j < matrizDisponibilidade[i].length; j++) {
                if (matrizDisponibilidade[i][j].isSelected()) {
                    bloqueados.add(new Horario(INICIOS[i], FINS[i], DIAS_SEMANA[j]));
                }
            }
        }

        return bloqueados;
    }

    public static void atualizarTurmas(AppController controller) {
            
        //Atualiza valores do spinner
        if (spinnerNumCad != null) {
            int maximo = controller.getNumDisciplinas();
            int valorAtual = maximo;

            if (valorAtual > maximo) valorAtual = maximo;
            spinnerNumCad.setModel(new SpinnerNumberModel(valorAtual, 1, maximo, 1));
        }

        if (painelPref == null || painelDesc == null) return;

        painelPref.removeAll();
        painelDesc.removeAll();

        mapaPref.clear();
        mapaDesc.clear();
        mapaTurma.clear();

        grupoTurno.clearSelection();
        limpaHorariosBloqueados();

        Set<String> stringsTurmas = new TreeSet<>();

        for (Turma t : controller.getTurmas()) {
            if (t != null) {
                String strTurma = t.getDisciplina().getNome() + ": " + t.getCodigo() + " - " + t.getProfessor();
                mapaTurma.put(strTurma, t);
                stringsTurmas.add(strTurma);
            }
        }

        for (String nome : stringsTurmas) {
            JCheckBox cbPref = new JCheckBox(nome);
            JCheckBox cbDesc   = new JCheckBox(nome);

            // se marcar preferido, desmarca descartado
            cbPref.addActionListener(e -> {
                if (cbPref.isSelected()) {
                    cbDesc.setSelected(false);
                }
            });

            // se marcar descartado, desmarca preferido
            cbDesc.addActionListener(e -> {
                if (cbDesc.isSelected()) {
                    cbPref.setSelected(false);
                }
            });

            mapaPref.put(nome, cbPref);
            mapaDesc.put(nome, cbDesc);

            painelPref.add(cbPref);
            painelDesc.add(cbDesc);
        }

        painelPref.revalidate(); painelPref.repaint();
        painelDesc.revalidate(); painelDesc.repaint();
    }

    //carregar preferências do CSV e jogar na tela 
    private static void carregarPreferenciasNaTela(AppController controller) {
        GerenciadorDePreferencias ger = new GerenciadorDePreferencias();
        List <Turma> turmas = controller.getTurmas();
        Preferencias prefs = ger.carregarPreferencias(CAMINHO_PREFS, turmas);

        //verifica se o arquivo existe e ta vazio
        boolean semTurno = (prefs.getTurnoPreferido() == null);
        boolean semTurmaPref = prefs.getTurmasPreferidas().isEmpty();
        boolean semTurmaDesc = prefs.getTurmasDescartadas().isEmpty();
        boolean semHorarios = prefs.getHorariosBloqueados().isEmpty();
        boolean numCadeirasZerado = prefs.getNumeroDisciplinas() <= 0;

        boolean arquivoVazio = semTurno && semTurmaPref && semTurmaDesc && semHorarios && numCadeirasZerado;

        if (arquivoVazio) {
            JOptionPane.showMessageDialog(null,
                    "Nenhuma preferência salva encontrada.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        //se tiver preferencia aplica
        aplicarPreferenciasNaTela(controller, prefs);
    }

    private static void aplicarPreferenciasNaTela(AppController controller, Preferencias prefs) {
        //turno preferido
        grupoTurno.clearSelection();

        if (prefs.getTurnoPreferido() != null) {
            switch (prefs.getTurnoPreferido()) {
                case MANHA -> rbManha.setSelected(true);
                case TARDE -> rbTarde.setSelected(true);
                case NOITE -> rbNoite.setSelected(true);
            }
        }

        //num cadeiras
        if (prefs.getNumeroDisciplinas() > 0) {
            spinnerNumCad.setValue(prefs.getNumeroDisciplinas());
        } else {
            spinnerNumCad.setValue(1);
        }

        //horarios bloqueados
        // primeiro limpa e depois seleciona de acordo com as preferencias
        limpaHorariosBloqueados();

        for (Horario h : prefs.getHorariosBloqueados()) {
            int idxHora = -1;
            int idxDia  = -1;

            for (int i = 0; i < INICIOS.length; i++) {
                if (INICIOS[i].equals(h.getInicio())) {
                    idxHora = i;
                    break;
                }
            }

            for (int j = 0; j < DIAS_SEMANA.length; j++) {
                if (DIAS_SEMANA[j] == h.getDiaSemana()) {
                    idxDia = j;
                    break;
                }
            }

            if (idxHora != -1 && idxDia != -1) {
                matrizDisponibilidade[idxHora][idxDia].setSelected(true);
            }
        }

        //turmas
        // limpa tudo
        for (JCheckBox cb : mapaPref.values()) cb.setSelected(false);
        for (JCheckBox cb : mapaDesc.values()) cb.setSelected(false);

        for (Turma t : prefs.getTurmasPreferidas()) {
            String strTurma = t.getDisciplina().getNome() + ": " + t.getCodigo() + " - " + t.getProfessor();
            JCheckBox cb = mapaPref.get(strTurma);
            if (cb != null) cb.setSelected(true);
        }

        for (Turma t : prefs.getTurmasDescartadas()) {
            String strTurma = t.getDisciplina().getNome() + ": " + t.getCodigo() + " - " + t.getProfessor();
            JCheckBox cb = mapaDesc.get(strTurma);
            if (cb != null) cb.setSelected(true);
        }
    }

    public static void limpaHorariosBloqueados(){
        for (int i = 0; i < matrizDisponibilidade.length; i++) {
            for (int j = 0; j < matrizDisponibilidade[i].length; j++) {
                matrizDisponibilidade[i][j].setSelected(false);
            }
        }
    }
}