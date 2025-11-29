// Classe que controla as diferentes paginas e faz a integracao entre as diferentes interfaces

package app.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import model.*;

public class AppController {

    private JFrame frame;
    private CardLayout layout;
    private JPanel container;

    // variaveis "globais" para integracao
    public List<Turma> turmasCriadas = new ArrayList<>();
    public Preferencias preferencias = null;
    public List<Grade> gradesGeradas = new ArrayList<>();

    public AppController() {

        frame = new JFrame("Matriculador Master Blaster 9000");
        layout = new CardLayout();
        container = new JPanel(layout);
        frame.add(container);

        container.add(InsercaoGUI.criarTela(this), "INSERCAO");
        container.add(PreferenciasGUI.criarTela(this), "PREFERENCIAS");
        container.add(GradeGUI.criarTela(this), "GRADE");

        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        mostrarInsercao();
    }

    // diferentes interfaes

    public void mostrarInsercao() {
        layout.show(container, "INSERCAO");
    }

    public void mostrarPreferencias() {
        layout.show(container, "PREFERENCIAS");
    }

    public void mostrarGrade() {
        layout.show(container, "GRADE");
    }

    // criacao de grades a partir das turmas e preferencias, com verificacao de alguns dados
    public void gerarGrades() {

        if (preferencias == null) {
            JOptionPane.showMessageDialog(null,
                "Erro: Nenhuma preferência definida.",
                "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Agrupa disciplinas a partir do cadastro de turmas
        List<Disciplina> disciplinas = new ArrayList<>();

        for (Turma t : turmasCriadas) {
            Disciplina d = t.getDisciplina();

            Disciplina existente = disciplinas.stream()
                    .filter(x -> x.getCodigo().equals(d.getCodigo()))
                    .findFirst()
                    .orElse(null);

            if (existente == null) {
                Disciplina nova = new Disciplina(d.getCodigo(), d.getNome(), d.getCreditos());
                nova.getTurmas().add(t);
                disciplinas.add(nova);
            } else {
                existente.getTurmas().add(t);
            }
        }

        GeradorDeGrades gerador = new GeradorDeGrades(disciplinas, preferencias);
        gerador.startCombina();

        gradesGeradas = gerador.getGrades();

        if (gradesGeradas.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Nenhuma grade possível com as restrições.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        mostrarGrade();
    }
}
