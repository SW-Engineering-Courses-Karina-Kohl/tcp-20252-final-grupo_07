package app.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.time.LocalTime;
import java.util.*;
import model.*;

public class PreferenciasGUI {

    private static JCheckBox[][] matrizDisponibilidade;  // [hora][dia]

    // mapa para tri-estado
    private static Map<String, TriStateActionListener> estadosProfessores = new HashMap<>();

    // painel que será atualizado dinamicamente
    private static JPanel painelProfessores;

public static JPanel criarTela(AppController app) {

    JPanel root = new JPanel(new BorderLayout());

    JPanel painelPreferencias = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));

    JCheckBox tManha = new JCheckBox("Manha");
    JCheckBox tTarde = new JCheckBox("Tarde");
    JCheckBox tNoite = new JCheckBox("Noite");

    painelPreferencias.add(tManha);
    painelPreferencias.add(tTarde);
    painelPreferencias.add(tNoite);

    JLabel lblMaxCred = new JLabel("Max. Creditos:");
    JTextField fieldMaxCred = new JTextField(4);

    painelPreferencias.add(lblMaxCred);
    painelPreferencias.add(fieldMaxCred);

    root.add(painelPreferencias, BorderLayout.NORTH);

    // lista de professores com os três estados (neutro, preferido, evitado)
    JPanel painelProf = new JPanel();
    painelProf.setLayout(new BoxLayout(painelProf, BoxLayout.Y_AXIS));
    painelProf.setBorder(BorderFactory.createTitledBorder("Preferencias de Professores"));

    Map<String, TriStateActionListener> estados = new HashMap<>();

    root.add(new JScrollPane(painelProf), BorderLayout.CENTER);

    root.addHierarchyListener(ev -> {
        if ((ev.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && root.isShowing()) {
            painelProf.removeAll();
            estados.clear();

            for (Turma t : app.turmasCriadas) {
                String nome = t.getProfessor().getNome();
                JCheckBox cb = new JCheckBox(nome);
                TriStateActionListener tri = new TriStateActionListener(nome);
                cb.addActionListener(tri);

                estados.put(nome, tri);
                painelProf.add(cb);
            }

            painelProf.revalidate();
            painelProf.repaint();
        }
    });

    JPanel grid = criarGradeDisponibilidade();
    root.add(grid, BorderLayout.EAST);

    JButton btnGerar = new JButton("Gerar Grades");
    btnGerar.setPreferredSize(new Dimension(150, 50));

    btnGerar.addActionListener(e -> {
        Preferencias pref = new Preferencias();

        // turno preferido
        if (tManha.isSelected()) pref.setTurnoPreferido(Turno.MANHA);
        else if (tTarde.isSelected()) pref.setTurnoPreferido(Turno.TARDE);
        else if (tNoite.isSelected()) pref.setTurnoPreferido(Turno.NOITE);

        // créditos
        try {
            pref.setMaxCreditos(Integer.parseInt(fieldMaxCred.getText().trim()));
        } catch (Exception ex) { }

        // professores
        for (String nome : estados.keySet()) {
            int estado = estados.get(nome).getEstado();

            if (estado == 1) pref.adicionarProfessorPreferido(nome);
            if (estado == 2) pref.adicionarProfessorEvitado(nome);
        }

        // horários evitados
        for (int h = 0; h < 16; h++) {
            LocalTime ini = LocalTime.of(7 + h, 0);
            LocalTime fim = ini.plusHours(1);

            for (int d = 0; d < 6; d++) {
                if (matrizDisponibilidade[h][d].isSelected()) {
                    DiaSemana dia = DiaSemana.values()[d];
                    pref.adicionarHorarioBloqueado(new Horario(ini, fim, dia));
                }
            }
        }

        app.preferencias = pref;
        app.gerarGrades();
    });

    JPanel painelSul = new JPanel(new FlowLayout());
    painelSul.add(btnGerar);
    root.add(painelSul, BorderLayout.SOUTH);

    return root;
}


    private static JPanel criarGradeDisponibilidade() {

        String[] dias = { "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb" };
        matrizDisponibilidade = new JCheckBox[16][6];

        JPanel grid = new JPanel(new GridLayout(17, 7, 5, 5));
        grid.setBorder(BorderFactory.createTitledBorder("Indisponibilidade"));

        grid.add(new JLabel("Hora", SwingConstants.CENTER));

        for (String dia : dias) {
            JLabel lbl = new JLabel(dia, SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            grid.add(lbl);
        }

        for (int h = 0; h < 16; h++) {
            String horaTxt = String.format("%02d:00", 7 + h);
            JLabel lblHora = new JLabel(horaTxt, SwingConstants.CENTER);
            lblHora.setFont(lblHora.getFont().deriveFont(Font.BOLD));
            grid.add(lblHora);

            for (int d = 0; d < 6; d++) {
                JCheckBox cb = new JCheckBox();
                matrizDisponibilidade[h][d] = cb;
                grid.add(cb);
            }
        }

        return grid;
    }
}
