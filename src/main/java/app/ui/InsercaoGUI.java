package app.ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.HierarchyEvent;

import model.*;

public class InsercaoGUI {

    // campos de texto para insercao de informacoes
    private static JTextField fieldNomeDisc, fieldCodDisc, fieldCreditos;
    private static JTextField fieldNomeProf, fieldVagas, fieldCodTurma;
    private static JTextField fieldSala, fieldHoraInicio, fieldHoraFim;

    // checkboxes para selecao de dias
    private static JCheckBox chkSeg, chkTer, chkQua, chkQui, chkSex, chkSab;

    // horários temporários antes de criar a turma
    private static List<Horario> horariosTemp = new ArrayList<>();

    // lista lateral de turmas criadas
    private static DefaultListModel<String> listaTurmasModel = new DefaultListModel<>();
    private static JList<String> listaTurmas = new JList<>(listaTurmasModel);

    public static JPanel criarTela(AppController app) {

        JPanel root = new JPanel(new BorderLayout());

        JPanel painelInsercao = new JPanel();
        painelInsercao.setLayout(new BoxLayout(painelInsercao, BoxLayout.Y_AXIS));
        painelInsercao.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Cadastro de Turmas");
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        painelInsercao.add(titulo);
        painelInsercao.add(Box.createVerticalStrut(20));

        // Campos
        fieldNomeDisc = criarCampo(painelInsercao, "Nome da Disciplina:", 20);
        fieldCodDisc  = criarCampo(painelInsercao, "Codigo da Disciplina:", 20);
        fieldCreditos = criarCampo(painelInsercao, "Creditos:", 5);
        fieldNomeProf = criarCampo(painelInsercao, "Nome do Professor:", 20);
        fieldVagas    = criarCampo(painelInsercao, "Vagas:", 5);
        fieldCodTurma = criarCampo(painelInsercao, "Codigo da Turma:", 10);
        fieldSala     = criarCampo(painelInsercao, "Sala:", 10);

        // Dias da Semana
        painelInsercao.add(new JLabel("Dias da Semana:"));
        JPanel painelDias = new JPanel(new FlowLayout(FlowLayout.LEFT));

        chkSeg = new JCheckBox("Segunda");
        chkTer = new JCheckBox("Terca");
        chkQua = new JCheckBox("Quarta");
        chkQui = new JCheckBox("Quinta");
        chkSex = new JCheckBox("Sexta");
        chkSab = new JCheckBox("Sabado");

        painelDias.add(chkSeg); painelDias.add(chkTer); painelDias.add(chkQua);
        painelDias.add(chkQui); painelDias.add(chkSex); painelDias.add(chkSab);

        painelInsercao.add(painelDias);

        // Horarios
        fieldHoraInicio = criarCampo(painelInsercao, "Inicio (HH:mm):", 6);
        fieldHoraFim    = criarCampo(painelInsercao, "Fim (HH:mm):", 6);

        // Botoes
        JButton btnAddHorario = new JButton("Inserir Horario");
        btnAddHorario.addActionListener(e -> criarHorario());

        JButton btnCriarTurma = new JButton("Criar Turma");
        btnCriarTurma.addActionListener(e -> criarTurma(app));

        JButton btnPronto = new JButton("Pronto");
        btnPronto.addActionListener(e -> app.mostrarPreferencias());

        JPanel linhaBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        linhaBtns.add(btnAddHorario);
        linhaBtns.add(btnCriarTurma);
        linhaBtns.add(btnPronto);

        painelInsercao.add(Box.createVerticalStrut(15));
        painelInsercao.add(linhaBtns);

        // lista de turmas criadas
        JPanel painelLista = new JPanel(new BorderLayout());
        painelLista.setPreferredSize(new Dimension(350, 0));
        painelLista.setBorder(BorderFactory.createTitledBorder("Turmas Cadastradas"));

        listaTurmas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelLista.add(new JScrollPane(listaTurmas), BorderLayout.CENTER);

        root.add(painelInsercao, BorderLayout.CENTER);
        root.add(painelLista, BorderLayout.EAST);

        // Atualizar lista sempre que a tela for mostrada novamente
        root.addHierarchyListener(h -> {
            if ((h.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && root.isShowing()) {
                atualizarLista(app);
            }
        });

        return root;
    }

    private static void atualizarLista(AppController app) {
        listaTurmasModel.clear();
        for (Turma t : app.turmasCriadas)
            listaTurmasModel.addElement(t.getDisciplina().getNome());
    }

    private static void criarTurma(AppController app) {
        try {
            Disciplina disc = new Disciplina(
                    fieldCodDisc.getText().trim(),
                    fieldNomeDisc.getText().trim(),
                    Integer.parseInt(fieldCreditos.getText().trim())
            );

            Professor prof = new Professor(fieldNomeProf.getText().trim());

            Turma turma = new Turma(
                    fieldCodTurma.getText().trim(),
                    prof,
                    disc,
                    Integer.parseInt(fieldVagas.getText().trim()),
                    fieldSala.getText().trim()
            );

            // Adicionar horários à turma
            for (Horario h : horariosTemp)
                turma.getHorarios().add(h);

            horariosTemp.clear();

            // Salvar no AppController
            app.turmasCriadas.add(turma);

            // Atualizar imediatamente a lista lateral
            listaTurmasModel.addElement(turma.getDisciplina().getNome());

            limparCampos();
            JOptionPane.showMessageDialog(null, "Turma criada!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }
    }

    // metodo que armazena os 
    private static void criarHorario() {
        try {
            LocalTime ini = LocalTime.parse(fieldHoraInicio.getText().trim());
            LocalTime fim = LocalTime.parse(fieldHoraFim.getText().trim());

            if (chkSeg.isSelected()) horariosTemp.add(new Horario(ini, fim, DiaSemana.SEGUNDA));
            if (chkTer.isSelected()) horariosTemp.add(new Horario(ini, fim, DiaSemana.TERCA));
            if (chkQua.isSelected()) horariosTemp.add(new Horario(ini, fim, DiaSemana.QUARTA));
            if (chkQui.isSelected()) horariosTemp.add(new Horario(ini, fim, DiaSemana.QUINTA));
            if (chkSex.isSelected()) horariosTemp.add(new Horario(ini, fim, DiaSemana.SEXTA));
            if (chkSab.isSelected()) horariosTemp.add(new Horario(ini, fim, DiaSemana.SABADO));

            JOptionPane.showMessageDialog(null, "Horario adicionado!");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(null, "Formato invalido! Use HH:mm");
        }
    }

    // -------------------------------------------------------------------------
    private static JTextField criarCampo(JPanel painel, String label, int size) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lbl = new JLabel(label);
        JTextField txt = new JTextField(size);
        linha.add(lbl);
        linha.add(txt);
        painel.add(linha);
        return txt;
    }

    // -------------------------------------------------------------------------
    private static void limparCampos() {
        fieldNomeDisc.setText("");
        fieldCodDisc.setText("");
        fieldCreditos.setText("");
        fieldNomeProf.setText("");
        fieldVagas.setText("");
        fieldCodTurma.setText("");
        fieldSala.setText("");
        fieldHoraInicio.setText("");
        fieldHoraFim.setText("");

        chkSeg.setSelected(false);
        chkTer.setSelected(false);
        chkQua.setSelected(false);
        chkQui.setSelected(false);
        chkSex.setSelected(false);
        chkSab.setSelected(false);

        horariosTemp.clear();
    }
}
