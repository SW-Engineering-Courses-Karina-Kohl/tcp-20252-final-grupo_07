package app.ui;

import model.*;

import javax.swing.*;
import java.awt.*;
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
            DiaSemana.SEXTA
    };

    private static final LocalTime[] INICIOS = {
            LocalTime.of(8, 30),
            LocalTime.of(10, 30),
            LocalTime.of(13, 30),
            LocalTime.of(15, 30),
            LocalTime.of(17, 30),
            LocalTime.of(19, 30),
            LocalTime.of(21, 30)
    };

    private static final LocalTime[] FINS = {
            LocalTime.of(10, 10),
            LocalTime.of(12, 10),
            LocalTime.of(15, 10),
            LocalTime.of(17, 10),
            LocalTime.of(19, 10),
            LocalTime.of(21, 10),
            LocalTime.of(23, 10)
    };

    //checkboxes de professores
    private static Map<String, JCheckBox> mapaPref = new HashMap<>();
    private static Map<String, JCheckBox> mapaEv = new HashMap<>();

    // painéis que contêm os checkboxes (pra poder atualizar depois)
    private static JPanel painelPref;
    private static JPanel painelEv;

    //acessar em carregar/limpar prefs
    private static JRadioButton rbManha;
    private static JRadioButton rbTarde;
    private static JRadioButton rbNoite;
    private static JSpinner spinnerNumCad;

    public static JPanel criarTela(AppController controller) {
        JPanel painel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Preferências do Usuário", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        painel.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2));

        //lado esquerdo- horários bloqueados
        JPanel painelHorarios = criarPainelHorarios();
        centro.add(painelHorarios);

        //lado direito- turno + professores + nº cadeiras 
        JPanel painelDireito = new JPanel();
        painelDireito.setLayout(new BoxLayout(painelDireito, BoxLayout.Y_AXIS));

        //turno preferido
        JPanel painelTurno = new JPanel();
        painelTurno.setBorder(BorderFactory.createTitledBorder("Turno preferido"));
        rbManha = new JRadioButton("Manhã");
        rbTarde = new JRadioButton("Tarde");
        rbNoite = new JRadioButton("Noite");
        ButtonGroup grupoTurno = new ButtonGroup();
        grupoTurno.add(rbManha);
        grupoTurno.add(rbTarde);
        grupoTurno.add(rbNoite);
        painelTurno.add(rbManha);
        painelTurno.add(rbTarde);
        painelTurno.add(rbNoite);

        painelDireito.add(painelTurno);

        //num cadeiras
        JPanel painelNumCad = new JPanel();
        painelNumCad.setBorder(BorderFactory.createTitledBorder("Número de cadeiras desejado"));
        spinnerNumCad = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        painelNumCad.add(spinnerNumCad);
        painelDireito.add(painelNumCad);

        //bloco dos professores
        JPanel painelProfessores = new JPanel();
        painelProfessores.setLayout(new GridLayout(2, 1));

        // preferidos
        painelPref = new JPanel();
        painelPref.setLayout(new BoxLayout(painelPref, BoxLayout.Y_AXIS));
        painelPref.setBorder(BorderFactory.createTitledBorder("Professores preferidos"));

        // evitados
        painelEv = new JPanel();
        painelEv.setLayout(new BoxLayout(painelEv, BoxLayout.Y_AXIS));
        painelEv.setBorder(BorderFactory.createTitledBorder("Professores evitados"));

        painelProfessores.add(new JScrollPane(painelPref));
        painelProfessores.add(new JScrollPane(painelEv));

        //monta a lista inicial (vai ser atualizada sempre que entrar na tela)
        atualizarProfessores(controller);

        painelDireito.add(painelProfessores);

        centro.add(painelDireito);

        painel.add(centro, BorderLayout.CENTER);

        //botoes
        JPanel painelBotoes = new JPanel();

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBackground(Color.WHITE);
        btnVoltar.addActionListener(e -> controller.mostrarMenu());

        JButton btnCarregar = new JButton("Carregar preferências");
        btnCarregar.setBackground(Color.WHITE);
        btnCarregar.addActionListener(e -> carregarPreferenciasNaTela(controller));

        JButton btnLimpar = new JButton("Limpar preferências");
        btnLimpar.setBackground(Color.WHITE);
        btnLimpar.addActionListener(e -> {
            Preferencias vazias = new Preferencias(); // construtor zera listas e usa defaults
            aplicarPreferenciasNaTela(controller, vazias);
            // também sobrescreve o arquivo com tudo vazio
            GerenciadorDePreferencias ger = new GerenciadorDePreferencias();
            ger.salvarPreferencias(CAMINHO_PREFS, vazias);
            JOptionPane.showMessageDialog(painel,
                    "Preferências limpas e arquivo sobrescrito.",
                    "Informação", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnGerar = new JButton("Gerar grades");
        btnGerar.setBackground(Color.WHITE);
        btnGerar.addActionListener(e -> {

            if (controller.turmasCriadas.isEmpty()) {
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
            prefs.setNumeroCadeiras(numCadeiras);

            //horários bloqueados
            List<Horario> bloqueados = obterHorariosBloqueados();
            for (Horario h : bloqueados) {
                prefs.adicionarHorarioBloqueado(h);
            }

            //professores preferidos
            for (String nomeProf : mapaPref.keySet()) {
                JCheckBox cb = mapaPref.get(nomeProf);
                if (cb.isSelected()) {
                    prefs.adicionarProfessorPreferido(nomeProf);
                }
            }

            //professores evitados
            for (String nomeProf : mapaEv.keySet()) {
                JCheckBox cb = mapaEv.get(nomeProf);
                if (cb.isSelected()) {
                    prefs.adicionarProfessorEvitado(nomeProf);
                }
            }

            //salva no controller
            controller.preferencias = prefs;

            // salva no CSV
            GerenciadorDePreferencias ger = new GerenciadorDePreferencias();
            ger.salvarPreferencias(CAMINHO_PREFS, prefs);

            controller.gerarGrades();
        });

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnCarregar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnGerar);

        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    // matriz de horários bloqueados
    private static JPanel criarPainelHorarios() {
        String[] dias = {"SEG", "TER", "QUA", "QUI", "SEX"};

        String[] horariosLabel = {
                "08:30", "10:30", "13:30", "15:30",
                "17:30", "19:30", "21:30"
        };

        int linhas = horariosLabel.length;
        int colunas = dias.length;

        matrizDisponibilidade = new JCheckBox[linhas][colunas];

        JPanel painel = new JPanel(new GridLayout(linhas + 1, colunas + 1));
        painel.setBorder(BorderFactory.createTitledBorder("Marque os horários em que NÃO deseja ter aula"));

        painel.add(new JLabel("")); // canto vazio

        for (String dia : dias) {
            painel.add(new JLabel(dia, SwingConstants.CENTER));
        }

        for (int i = 0; i < linhas; i++) {
            painel.add(new JLabel(horariosLabel[i], SwingConstants.CENTER));

            for (int j = 0; j < colunas; j++) {
                JCheckBox cb = new JCheckBox();
                cb.setHorizontalAlignment(SwingConstants.CENTER);
                cb.setBorderPainted(true);
                matrizDisponibilidade[i][j] = cb;
                painel.add(cb);
            }
        }

        return painel;
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

    //atualiza a lista de professores com base nas turmas atuais
    public static void atualizarProfessores(AppController controller) {
        if (painelPref == null || painelEv == null) return;

        painelPref.removeAll();
        painelEv.removeAll();

        mapaPref.clear();
        mapaEv.clear();

        Set<String> nomesProfessores = new TreeSet<>();

        for (Turma t : controller.turmasCriadas) {
            if (t.getProfessor() != null) {
                nomesProfessores.add(t.getProfessor().getNome().trim());
            }
        }

        for (String nome : nomesProfessores) {
            JCheckBox cbPref = new JCheckBox(nome);
            JCheckBox cbEv   = new JCheckBox(nome);

            // se marcar preferido, desmarca evitado
            cbPref.addActionListener(e -> {
                if (cbPref.isSelected()) {
                    cbEv.setSelected(false);
                }
            });

            // se marcar evitado, desmarca preferido
            cbEv.addActionListener(e -> {
                if (cbEv.isSelected()) {
                    cbPref.setSelected(false);
                }
            });

            mapaPref.put(nome, cbPref);
            mapaEv.put(nome, cbEv);

            painelPref.add(cbPref);
            painelEv.add(cbEv);
        }

        painelPref.revalidate();
        painelPref.repaint();
        painelEv.revalidate();
        painelEv.repaint();
    }

    //carregar preferências do CSV e jogar na tela 
    private static void carregarPreferenciasNaTela(AppController controller) {

    GerenciadorDePreferencias ger = new GerenciadorDePreferencias();
    Preferencias prefs = ger.carregarPreferencias(CAMINHO_PREFS);

    //verifica se o arquivo existe e ta vazio
    boolean semTurno = (prefs.getTurnoPreferido() == null);
    boolean semProfPref = prefs.getProfessoresPreferidos().isEmpty();
    boolean semProfEv = prefs.getProfessoresEvitados().isEmpty();
    boolean semHorarios = prefs.getHorariosBloqueados().isEmpty();
    boolean numCadeirasZerado = prefs.getNumeroCadeiras() <= 0;

    boolean arquivoVazio =
            semTurno && semProfPref && semProfEv && semHorarios && numCadeirasZerado;

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
        rbManha.setSelected(false);
        rbTarde.setSelected(false);
        rbNoite.setSelected(false);

        if (prefs.getTurnoPreferido() != null) {
            switch (prefs.getTurnoPreferido()) {
                case MANHA -> rbManha.setSelected(true);
                case TARDE -> rbTarde.setSelected(true);
                case NOITE -> rbNoite.setSelected(true);
            }
        }

        //num cadeiras
        if (prefs.getNumeroCadeiras() > 0) {
            spinnerNumCad.setValue(prefs.getNumeroCadeiras());
        } else {
            spinnerNumCad.setValue(1);
        }

        //horarios bloqueados
        // limpa
        for (int i = 0; i < matrizDisponibilidade.length; i++) {
            for (int j = 0; j < matrizDisponibilidade[i].length; j++) {
                matrizDisponibilidade[i][j].setSelected(false);
            }
        }

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

        //professores
        // limpa tudo
        for (JCheckBox cb : mapaPref.values()) cb.setSelected(false);
        for (JCheckBox cb : mapaEv.values()) cb.setSelected(false);

        for (String nome : prefs.getProfessoresPreferidos()) {
            JCheckBox cb = mapaPref.get(nome);
            if (cb != null) cb.setSelected(true);
        }

        for (String nome : prefs.getProfessoresEvitados()) {
            JCheckBox cb = mapaEv.get(nome);
            if (cb != null) cb.setSelected(true);
        }
    }
}
