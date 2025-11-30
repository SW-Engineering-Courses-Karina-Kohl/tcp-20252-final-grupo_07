package app.ui;

import javax.swing.*;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import model.Disciplina;
import model.ExtracaoDados;
import model.GeradorDeGrades;
import model.Grade;
import model.Preferencias;
import model.Turma;

public class AppController {

    private JFrame frame;
    private CardLayout layout;
    private JPanel container;

    // dados globais
    public List<Turma> turmasCriadas = new ArrayList<>();
    public Preferencias preferencias = null;
    public List<Grade> gradesGeradas = new ArrayList<>();

    // disciplinas carregadas de aluno.csv (ou outro CSV completo)
    public List<Disciplina> disciplinasCarregadas = new ArrayList<>();

    public AppController() {
        frame = new JFrame("Matriculador Master Blaster 9000");
        layout = new CardLayout();
        container = new JPanel(layout);
        frame.setContentPane(container);

        container.add(MenuInicialGUI.criarTela(this),        "MENU");
        container.add(InsercaoGUI.criarTela(this),           "INSERCAO");
        container.add(PreferenciasGUI.criarTela(this),       "PREFERENCIAS");
        container.add(GradeGUI.criarTela(this),              "GRADE");
        container.add(SelecaoDisciplinasGUI.criarTela(this), "SELEC_DISC");

        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        mostrarMenuInicial();
    }

    // ===== navegação =====

    public void mostrarMenuInicial() {
        layout.show(container, "MENU");
    }

    public void mostrarInsercao() {
        layout.show(container, "INSERCAO");
    }

    public void mostrarPreferencias() {
        // IMPORTANTE: sempre recarrega professores com base nas turmas atuais
        PreferenciasGUI.atualizarProfessores(this);
        layout.show(container, "PREFERENCIAS");
    }

    public void mostrarGrade() {
        layout.show(container, "GRADE");
    }

    public void mostrarSelecaoDisciplinas() {
        // IMPORTANTE: monta a lista de disciplinas com base em disciplinasCarregadas
        SelecaoDisciplinasGUI.atualizarLista(this);
        layout.show(container, "SELEC_DISC");
    }

    // ===== fluxo ALUNO (aluno.csv) =====

    public void iniciarFluxoAlunoUfrgs() {
        String caminho = "aluno.csv"; // arquivo na pasta do projeto / ao lado do .jar

        ExtracaoDados extrator = new ExtracaoDados();
        disciplinasCarregadas = extrator.carregarDisciplinas(caminho);

        if (disciplinasCarregadas == null || disciplinasCarregadas.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Não foi possível carregar disciplinas do arquivo 'aluno.csv'.\n" +
                    "Verifique se o arquivo existe e segue o formato esperado.",
                    "Erro ao carregar CSV",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        mostrarSelecaoDisciplinas();
    }

    /**
     * Chamado pela tela de seleção de disciplinas quando o aluno clica em Continuar.
     */
    public void definirDisciplinasSelecionadas(List<Disciplina> selecionadas) {
        turmasCriadas.clear();

        if (selecionadas != null) {
            for (Disciplina d : selecionadas) {
                turmasCriadas.addAll(d.getTurmas());
            }
        }

        mostrarPreferencias();
    }

    // ===== gerar grades (igual antes) =====

    public void gerarGrades() {

        if (preferencias == null) {
            JOptionPane.showMessageDialog(null,
                "Erro: Nenhuma preferência definida.",
                "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
        gerador.gerarGrades();

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
