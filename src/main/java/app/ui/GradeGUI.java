package app.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.List;

import model.*;

public class GradeGUI {

    public static final int DIAS = 6;      // Segunda–Sábado
    public static final int HORAS = 16;    // 07h–22h

    private static JPanel painelGrade;     // grade visual
    private static JPanel painelTopo;      // botões superiores

    private static List<Grade> grades;     // recebidas do AppController
    private static Grade gradeAtual;

    public static JPanel criarTela(AppController app) {

        JPanel root = new JPanel(new BorderLayout());

        painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        root.add(painelTopo, BorderLayout.NORTH);

        painelGrade = new JPanel();
        painelGrade.setLayout(new GridLayout(HORAS + 1, DIAS + 1, 5, 5));
        painelGrade.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        root.add(new JScrollPane(painelGrade), BorderLayout.CENTER);

        root.addHierarchyListener(h -> {
            if ((h.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (root.isShowing()) {
                    atualizar(app);
                }
            }
        });

        return root;
    }


    private static void atualizar(AppController app) {

        painelTopo.removeAll();
        painelGrade.removeAll();

        grades = app.gradesGeradas;

        if (grades == null || grades.isEmpty()) {
            painelTopo.add(new JLabel("Nenhuma grade gerada ainda."));
            painelTopo.revalidate();
            painelTopo.repaint();
            return;
        }

        gradeAtual = grades.get(0);


        for (int i = 0; i < grades.size(); i++) {

            final int idx = i;
            JButton btn = new JButton("Grade " + (i + 1));

            btn.addActionListener(e -> {
                gradeAtual = grades.get(idx);
                desenharGrade(gradeAtual);
            });

            painelTopo.add(btn);
        }


        JButton btnPreferencias = new JButton("Preferencias");
        btnPreferencias.addActionListener(e -> app.mostrarPreferencias());

        painelTopo.add(btnPreferencias);


        JButton btnAdicionar = new JButton("+");
        btnAdicionar.addActionListener(e -> app.mostrarInsercao());

        painelTopo.add(btnAdicionar);

        painelTopo.revalidate();
        painelTopo.repaint();


        desenharGrade(gradeAtual);
    }


    private static void desenharGrade(Grade grade) {

        painelGrade.removeAll();

        // Cabeçalho
        String[] labels = { "Horário", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb" };
        for (String lab : labels) {
            painelGrade.add(new JLabel(lab, JLabel.CENTER));
        }

        // Cria matriz vazia
        Turma[][] matriz = new Turma[HORAS][DIAS];

        for (Turma turma : grade.getTurmasSelecionadas()) {
            for (Horario h : turma.getHorarios()) {

                int linha = h.getInicio().getHour() - 7; // começa às 07h
                int dur = h.getFim().getHour() - h.getInicio().getHour();
                int coluna = diaParaColuna(h.getDiaSemana());

                if (linha >= 0 && linha < HORAS && coluna >= 0 && coluna < DIAS) {
                    for (int k = 0; k < dur; k++) {
                        matriz[linha + k][coluna] = turma;
                    }
                }
            }
        }

        // Preenche visualmente a tabela
        for (int i = 0; i < HORAS; i++) {

            int hora = 7 + i;
            painelGrade.add(new JLabel(hora + ":00", JLabel.CENTER));

            for (int j = 0; j < DIAS; j++) {

                Turma t = matriz[i][j];

                if (t == null) {
                    JButton vazio = new JButton();
                    vazio.setEnabled(false);
                    painelGrade.add(vazio);
                } else {

                    JButton btn = new JButton(t.getDisciplina().getNome());
                    Turma turmaRef = t;

                    btn.addActionListener(e -> mostrarInfoTurma(turmaRef));

                    painelGrade.add(btn);
                }
            }
        }

        painelGrade.revalidate();
        painelGrade.repaint();
    }

    private static int diaParaColuna(DiaSemana d) {
        switch (d) {
            case SEGUNDA: return 0;
            case TERCA:   return 1;
            case QUARTA:  return 2;
            case QUINTA:  return 3;
            case SEXTA:   return 4;
            case SABADO:  return 5;
        }
        return -1;
    }


    private static void mostrarInfoTurma(Turma turma) {

        Disciplina d = turma.getDisciplina();
        Professor p = turma.getProfessor();

        String mensagem =
                d.getCodigo() + "\n" +
                d.getNome() + "\n" +
                "Professor: " + (p != null ? p.getNome() : "-") + "\n" +
                "Vagas ofertadas: " + turma.getVagasOfertadas() + "\n" +
                "Sala: " + turma.getSala() + "\n" +
                "Creditos: " + d.getCreditos();

        JOptionPane.showMessageDialog(
                null,
                mensagem,
                "Informacoes da Turma",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
