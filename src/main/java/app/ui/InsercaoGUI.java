package app.ui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class InsercaoGUI {

    // Campos de texto
    private static JTextField fieldNomeDisc;
    private static JTextField fieldCodDisc;
    private static JTextField fieldCreditos;

    private static JTextField fieldCodTurma;
    private static JTextField fieldNomeProf;
    private static JTextField fieldVagas;
    private static JTextField fieldSala;

    private static JTextField fieldHoraInicio;
    private static JTextField fieldHoraFim;

    // Checkboxes de dia da semana
    private static JCheckBox cbSeg;
    private static JCheckBox cbTer;
    private static JCheckBox cbQua;
    private static JCheckBox cbQui;
    private static JCheckBox cbSex;

    // Lista de turmas criadas só para exibir na tela
    private static DefaultListModel<String> modeloListaTurmas = new DefaultListModel<>();

    public static JPanel criarTela(AppController controller) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Inserção manual de disciplinas e turmas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        painel.add(titulo, BorderLayout.NORTH);

        // ====================== CENTRO: FORMULÁRIOS ===========================
        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 0));

        // ----------- Lado esquerdo: dados da disciplina e turma --------------
        JPanel painelForm = new JPanel();
        painelForm.setLayout(new BoxLayout(painelForm, BoxLayout.Y_AXIS));

        // DISCIPLINA
        JPanel painelDisciplina = new JPanel(new GridLayout(3, 2, 5, 5));
        painelDisciplina.setBorder(BorderFactory.createTitledBorder("Disciplina"));

        fieldNomeDisc = new JTextField();
        fieldCodDisc = new JTextField();
        fieldCreditos = new JTextField();

        painelDisciplina.add(new JLabel("Nome da disciplina:"));
        painelDisciplina.add(fieldNomeDisc);

        painelDisciplina.add(new JLabel("Código da disciplina:"));
        painelDisciplina.add(fieldCodDisc);

        painelDisciplina.add(new JLabel("Créditos:"));
        painelDisciplina.add(fieldCreditos);

        painelForm.add(painelDisciplina);
        painelForm.add(Box.createVerticalStrut(10));

        // TURMA
        JPanel painelTurma = new JPanel(new GridLayout(4, 2, 5, 5));
        painelTurma.setBorder(BorderFactory.createTitledBorder("Turma"));

        fieldCodTurma = new JTextField();
        fieldNomeProf = new JTextField();
        fieldVagas = new JTextField();
        fieldSala = new JTextField();

        painelTurma.add(new JLabel("Código da turma:"));
        painelTurma.add(fieldCodTurma);

        painelTurma.add(new JLabel("Professor:"));
        painelTurma.add(fieldNomeProf);

        painelTurma.add(new JLabel("Vagas ofertadas:"));
        painelTurma.add(fieldVagas);

        painelTurma.add(new JLabel("Sala:"));
        painelTurma.add(fieldSala);

        painelForm.add(painelTurma);
        painelForm.add(Box.createVerticalStrut(10));

        // HORÁRIOS
        JPanel painelHorario = new JPanel(new GridLayout(3, 2, 5, 5));
        painelHorario.setBorder(BorderFactory.createTitledBorder("Horário (para esta turma)"));

        fieldHoraInicio = new JTextField("08:30");
        fieldHoraFim = new JTextField("10:10");

        cbSeg = new JCheckBox("Segunda");
        cbTer = new JCheckBox("Terça");
        cbQua = new JCheckBox("Quarta");
        cbQui = new JCheckBox("Quinta");
        cbSex = new JCheckBox("Sexta");

        JPanel painelDias = new JPanel(new GridLayout(1, 5));
        painelDias.add(cbSeg);
        painelDias.add(cbTer);
        painelDias.add(cbQua);
        painelDias.add(cbQui);
        painelDias.add(cbSex);

        painelHorario.add(new JLabel("Hora início (HH:MM):"));
        painelHorario.add(fieldHoraInicio);

        painelHorario.add(new JLabel("Hora fim (HH:MM):"));
        painelHorario.add(fieldHoraFim);

        painelHorario.add(new JLabel("Dias da semana:"));
        painelHorario.add(painelDias);

        painelForm.add(painelHorario);

        centro.add(painelForm);

        // ----------- Lado direito: lista das turmas já cadastradas -----------

        JPanel painelLista = new JPanel(new BorderLayout());
        painelLista.setBorder(BorderFactory.createTitledBorder("Turmas cadastradas (sessão atual)"));

        JList<String> listaTurmas = new JList<>(modeloListaTurmas);
        JScrollPane scrollLista = new JScrollPane(listaTurmas);

        painelLista.add(scrollLista, BorderLayout.CENTER);

        centro.add(painelLista);

        painel.add(centro, BorderLayout.CENTER);

        // ====================== RODAPÉ: BOTÕES ===============================

        JPanel painelBotoes = new JPanel();

        // BOTÃO ADICIONAR TURMA
        JButton btnAdicionar = new JButton("Adicionar turma");
        btnAdicionar.addActionListener(e -> adicionarTurma(controller));
        painelBotoes.add(btnAdicionar);

        // BOTÃO LIMPAR CAMPOS
        JButton btnLimpar = new JButton("Limpar campos");
        btnLimpar.addActionListener(e -> limparCampos());
        painelBotoes.add(btnLimpar);

        // BOTÃO IR PARA PREFERÊNCIAS
        JButton btnIrPreferencias = new JButton("Ir para preferências");
        btnIrPreferencias.addActionListener(e -> {
            if (controller.turmasCriadas.isEmpty()) {
                JOptionPane.showMessageDialog(painel,
                        "Cadastre pelo menos uma turma antes de continuar.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.mostrarPreferencias();
        });
        painelBotoes.add(btnIrPreferencias);

        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    // ===================== MÉTODOS AUXILIARES ===============================

    private static void adicionarTurma(AppController controller) {
        try {
            // valida campos básicos
            String nomeDisc = fieldNomeDisc.getText().trim();
            String codDisc = fieldCodDisc.getText().trim();
            String strCred = fieldCreditos.getText().trim();

            String codTurma = fieldCodTurma.getText().trim();
            String nomeProf = fieldNomeProf.getText().trim();
            String strVagas = fieldVagas.getText().trim();
            String sala = fieldSala.getText().trim();

            String strInicio = fieldHoraInicio.getText().trim();
            String strFim = fieldHoraFim.getText().trim();

            if (nomeDisc.isEmpty() || codDisc.isEmpty() || strCred.isEmpty() ||
                codTurma.isEmpty() || nomeProf.isEmpty() || strVagas.isEmpty() ||
                sala.isEmpty() || strInicio.isEmpty() || strFim.isEmpty()) {

                JOptionPane.showMessageDialog(null,
                        "Preencha todos os campos antes de adicionar.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int creditos = Integer.parseInt(strCred);
            int vagas = Integer.parseInt(strVagas);

            LocalTime inicio = LocalTime.parse(strInicio);
            LocalTime fim = LocalTime.parse(strFim);

            // ao menos um dia precisa estar marcado
            List<DiaSemana> diasSelecionados = new ArrayList<>();
            if (cbSeg.isSelected()) diasSelecionados.add(DiaSemana.SEGUNDA);
            if (cbTer.isSelected()) diasSelecionados.add(DiaSemana.TERCA);
            if (cbQua.isSelected()) diasSelecionados.add(DiaSemana.QUARTA);
            if (cbQui.isSelected()) diasSelecionados.add(DiaSemana.QUINTA);
            if (cbSex.isSelected()) diasSelecionados.add(DiaSemana.SEXTA);

            if (diasSelecionados.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Selecione ao menos um dia da semana.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Reaproveita disciplina se já existir nas turmas criadas
            Disciplina disciplina = null;
            for (Turma t : controller.turmasCriadas) {
                if (t.getDisciplina().getCodigo().equalsIgnoreCase(codDisc)) {
                    disciplina = t.getDisciplina();
                    break;
                }
            }
            if (disciplina == null) {
                disciplina = new Disciplina(codDisc, nomeDisc, creditos);
            }

            Professor professor = new Professor(nomeProf);
            Turma turma = new Turma(codTurma, professor, disciplina, vagas, sala);

            // adiciona horários (um para cada dia marcado)
            for (DiaSemana dia : diasSelecionados) {
                turma.addHorario(new Horario(inicio, fim, dia));
            }

            // liga turma à disciplina
            disciplina.adicionarTurma(turma);

            // adiciona ao controlador (backend global)
            controller.turmasCriadas.add(turma);

            // adiciona na lista visual
            modeloListaTurmas.addElement(
                    String.format("%s - %s | Turma %s | Prof. %s",
                            disciplina.getCodigo(),
                            disciplina.getNome(),
                            turma.getCodigo(),
                            professor.getNome())
            );

            JOptionPane.showMessageDialog(null,
                    "Turma adicionada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparCampos();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erro ao adicionar turma:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void limparCampos() {
        fieldNomeDisc.setText("");
        fieldCodDisc.setText("");
        fieldCreditos.setText("");

        fieldCodTurma.setText("");
        fieldNomeProf.setText("");
        fieldVagas.setText("");
        fieldSala.setText("");

        fieldHoraInicio.setText("08:30");
        fieldHoraFim.setText("10:10");

        cbSeg.setSelected(false);
        cbTer.setSelected(false);
        cbQua.setSelected(false);
        cbQui.setSelected(false);
        cbSex.setSelected(false);
    }
}
