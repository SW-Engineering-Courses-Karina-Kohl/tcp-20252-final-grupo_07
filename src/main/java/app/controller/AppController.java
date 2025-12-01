package app.controller;

import javax.swing.*;

import app.view.GradeGUI;
import app.view.InsercaoGUI;
import app.view.MenuInicialGUI;
import app.view.PreferenciasGUI;
import app.view.SelecaoDisciplinasGUI;

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

    private final List<Turma> turmasCriadas = new ArrayList<>();
    private final List<Grade> gradesGeradas = new ArrayList<>();
    private Preferencias preferencias = null;

    // disciplinas carregadas do aluno.csv (ou outro CSV para seleção)
    private List<Disciplina> disciplinasCarregadas = new ArrayList<>();

    public AppController() {
        frame = new JFrame("MATRICULADOR INF/UFRGS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);

        layout = new CardLayout();
        container = new JPanel(layout);

        // registra as telas
        container.add(MenuInicialGUI.criarTela(this), "MENU");
        container.add(InsercaoGUI.criarTela(this), "INSERCAO");
        container.add(PreferenciasGUI.criarTela(this), "PREFERENCIAS");
        container.add(GradeGUI.criarTela(this), "GRADE");
        container.add(SelecaoDisciplinasGUI.criarTela(this), "SELEC_DISC");

        frame.setContentPane(container);
        frame.setVisible(true);

        mostrarMenuInicial();
    }

    //getters e setters para ui

    public List<Turma> getTurmas() {
        return new ArrayList<>(turmasCriadas);
    }

    public boolean temTurmas() {
        return !turmasCriadas.isEmpty();
    }

    public void adicionarTurma(Turma turma) {
        if (turma != null) {
            turmasCriadas.add(turma);
        }
    }

    public void limparTurmas() {
        turmasCriadas.clear();
    }

    public List<Grade> getGradesGeradas() {
        return new ArrayList<>(gradesGeradas);
    }

    public Preferencias getPreferencias() {
        return preferencias;
    }

    public void definirPreferencias(Preferencias prefs) {
        this.preferencias = prefs;
    }

    //Disciplinas existem baseadas nas turmas lidas pelo arquivo
    public List<Disciplina> getDisciplinasCarregadas() {
        return new ArrayList<>(disciplinasCarregadas);
    }

    ////Disciplinas existem baseadas nas turmas lidas selecionadas pelo usuário
    public List<Disciplina> getDisciplinasSelecionadas() {
        List<Disciplina> disciplinasSelecionadas = new ArrayList<>();
        List<String> codigosVistos = new ArrayList<>();

        for (Turma t : turmasCriadas) {
            Disciplina d = t.getDisciplina();
            if (d != null && !codigosVistos.contains(d.getCodigo())) {
                disciplinasSelecionadas.add(d);
                codigosVistos.add(d.getCodigo());
            }
        }
        return disciplinasSelecionadas;
    }

    public int getNumDisciplinas() {
        int quant = getDisciplinasSelecionadas().size();
        if (quant == 0)
            quant = 1;
        return quant; //retorna 1 se vazio para nao quebrar o spinner nas preferencias
    }

    public void mostrarMenuInicial() {layout.show(container, "MENU");}

    public void mostrarInsercao() {layout.show(container, "INSERCAO");}

    public void mostrarPreferencias() {
        //atualiza lista de professores com base nas turmas atuais
        PreferenciasGUI.atualizarProfessores(this);
        layout.show(container, "PREFERENCIAS");
    }

    public void mostrarGrade() {layout.show(container, "GRADE");}

    public void mostrarSelecaoDisciplinas() {
        SelecaoDisciplinasGUI.atualizarLista(this);
        layout.show(container, "SELEC_DISC");
    }

    //-------------------------------------------------------------------------
    //fluxo de carregamento csv e aluno inf
    ///-------------------------------------------------------------------------
     
    /*public void iniciarFluxoAlunoUfrgs(String caminho) {
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
    } */
    
    //usuario escohe o arquivo no proprio pc
    public void carregarTurmasDeCsv(String caminhoArquivo) {
        ExtracaoDados extrator = new ExtracaoDados();
        List<Disciplina> disciplinas = extrator.carregarDisciplinas(caminhoArquivo);

        if (disciplinas == null || disciplinas.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Não foi possível carregar disciplinas do arquivo selecionado.",
                    "Erro ao carregar CSV",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        disciplinasCarregadas = new ArrayList<>(disciplinas);

        // a partir das disciplinas, pega todas as turmas
        turmasCriadas.clear();
        for (Disciplina d : disciplinas) {
            if (d.getTurmas() != null) {
                turmasCriadas.addAll(d.getTurmas());
            }
        }
        //vai direto para a tela de preferências
        mostrarSelecaoDisciplinas();
    }

    //chamado depois da tela de seleção de disciplinas (Aluno INF) //apagar!!!!!!!!!!!!!!!!!!!!!
    public void definirDisciplinasSelecionadas(List<Disciplina> selecionadas) {
        turmasCriadas.clear();

        if (selecionadas != null) {
            for (Disciplina d : selecionadas) {
                if (d.getTurmas() != null) {
                    turmasCriadas.addAll(d.getTurmas());
                }
            }
        }
        mostrarPreferencias();
    }

    public void gerarGrades() {
        if (preferencias == null) {
            JOptionPane.showMessageDialog(frame,
                    "Erro: Nenhuma preferência definida.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (turmasCriadas.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Nenhuma turma cadastrada (CSV ou manual).",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Disciplina> DIsciplinasOrganizadas = organizaTurmasEmDisciplinas();
        GeradorDeGrades gerador = new GeradorDeGrades(DIsciplinasOrganizadas, preferencias);
        gerador.gerarGrades();

        gradesGeradas.clear();
        gradesGeradas.addAll(gerador.getGrades());

        if (gradesGeradas.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Nenhuma grade possível com as restrições.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mostrarGrade();
    }

    //converte lista de turmas em lista de disciplinas (agrupando por código)
    private List<Disciplina> organizaTurmasEmDisciplinas(){
        List<Disciplina> disciplinas = new ArrayList<>();

        for (Turma t : turmasCriadas) {
            Disciplina d = t.getDisciplina();
            if (d == null) continue;

            Disciplina existente = null;
            for (Disciplina disciplina : disciplinas) {
                if (disciplina.getCodigo().equals(d.getCodigo())) {
                    existente = disciplina;
                    break;
                }
            }

            if (existente == null) {
                Disciplina nova = new Disciplina(d.getCodigo(), d.getNome(), d.getCreditos());
                //garante que a lista de turmas da disciplina nova receba essa turma
                nova.getTurmas().add(t);
                disciplinas.add(nova);
            } else {
                existente.getTurmas().add(t);
            }
        }
        return disciplinas;   
    }
}