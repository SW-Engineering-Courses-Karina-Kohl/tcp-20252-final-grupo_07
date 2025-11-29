package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
gera grades de turmas a partir de disciplinas e preferências
usa backtracking para gerar combinações válidas
ordenar por qualidade e manter só as melhores
*/
public class GeradorDeGrades {

    private static final Logger logger = LogManager.getLogger(GeradorDeGrades.class);

    private static final int LIMITE_MAXIMO_RESULTADOS = 10;

    private List<Grade> gradesGeradas;
    private List<Disciplina> disciplinasDisponiveis;
    private Preferencias preferenciasUsuario;


    public GeradorDeGrades(List<Disciplina> disciplinas, Preferencias preferencias) {
        this.gradesGeradas = new ArrayList<>();
        this.disciplinasDisponiveis = disciplinas;
        this.preferenciasUsuario = preferencias;
    }

    public List<Grade> getGrades() {
        return gradesGeradas;
    }

    //comeca o algoritmo de geracao de grades(buscarCombinacoes)
    public void gerarGrades() {
        if (preferenciasUsuario == null || disciplinasDisponiveis == null) {
            logger.error("Tentativa de gerar grades com dados nulos (preferências ou disciplinas).");
            return;
        }

        int numCadeiras = preferenciasUsuario.getNumeroCadeiras();
        logger.info("Iniciando geração de grades com {} disciplinas.", numCadeiras);

        gradesGeradas.clear();

        Grade gradeInicial = new Grade();
        buscarCombinacoes(gradeInicial, 0);

        ordenarGradesPorQualidade();
        aplicarCorteDeSeguranca();

        logger.info("Geração finalizada. Total de opções válidas retornadas: {}", gradesGeradas.size());
    }

    //backtracking!!!!!
    private void buscarCombinacoes(Grade gradeAtual, int indexDisciplina) {
        int numCadeiras = preferenciasUsuario.getNumeroCadeiras();
        int qtdAtual = gradeAtual.getTurmasSelecionadas().size();

        // fez a grade completa. salva
        if (qtdAtual == numCadeiras) {
            gradesGeradas.add(new Grade(gradeAtual)); // faz uma cópia
            return;
        }

        //percorreu todas as disciplinas
        if (indexDisciplina == disciplinasDisponiveis.size()) {
            return;
        }

        // para por aqui. porque nem com todas as disciplinas restantes
        // da pra completar o numero de cadeiras desejado(pruning)
        int disciplinasRestantes = disciplinasDisponiveis.size() - indexDisciplina;
        if (qtdAtual + disciplinasRestantes < numCadeiras) {
            return;
        }

        Disciplina disciplina = disciplinasDisponiveis.get(indexDisciplina);

        for (Turma turma : disciplina.getTurmas()) {
            if (violaRestricoes(turma, gradeAtual)) {
                continue;
            }

            if (gradeAtual.adicionarTurma(turma)) {
                buscarCombinacoes(gradeAtual, indexDisciplina + 1);
                gradeAtual.removerTurma(turma);
            }
        }

        buscarCombinacoes(gradeAtual, indexDisciplina + 1);
    }

    //restricoes (rigidas!!!)
    private boolean violaRestricoes(Turma turma, Grade gradeAtual) {
        //professor evitado
        String nomeProf = turma.getProfessor().getNome().trim();
        for (String evitado : preferenciasUsuario.getProfessoresEvitados()) {
            if (nomeProf.equalsIgnoreCase(evitado.trim())) {
                return true;
            }
        }

        //limite de cadeiras
        if (gradeAtual.getTurmasSelecionadas().size() >= preferenciasUsuario.getNumeroCadeiras()) {
            return true;
        }

        //horarios bloqueados
        for (Horario horarioTurma : turma.getHorarios()) {
            for (Horario bloqueado : preferenciasUsuario.getHorariosBloqueados()) {
                if (horarioTurma.conflitaCom(bloqueado)) {
                    return true;
                }
            }
        }

        return false;
    }

    //ordena as grades
    private void ordenarGradesPorQualidade() {
        if (preferenciasUsuario == null) return;

        Collections.sort(gradesGeradas, new Comparator<Grade>() {
            @Override
            public int compare(Grade g1, Grade g2) {
                return calcularPontuacao(g2) - calcularPontuacao(g1); // ordem decrescente
            }
        });
    }

    //limita o numero de resultados para nao explodir tudo
    private void aplicarCorteDeSeguranca() {
        if (gradesGeradas.size() > LIMITE_MAXIMO_RESULTADOS) {
            logger.info("Muitas combinações encontradas ({}). Mantendo apenas as Top {}.",
                    gradesGeradas.size(), LIMITE_MAXIMO_RESULTADOS);

            gradesGeradas = new ArrayList<>(gradesGeradas.subList(0, LIMITE_MAXIMO_RESULTADOS));
        }
    }

    private int calcularPontuacao(Grade grade) {
        int pontos = 0;

        for (Turma turma : grade.getTurmasSelecionadas()) {
            String nomeProf = turma.getProfessor().getNome().trim();
            for (String favorito : preferenciasUsuario.getProfessoresPreferidos()) {
                if (nomeProf.equalsIgnoreCase(favorito.trim())) {
                    pontos += 100;
                    break;
                }
            }

            Turno turnoPref = preferenciasUsuario.getTurnoPreferido();
            if (turnoPref != null) {
                for (Horario h : turma.getHorarios()) {
                    if (ehDoTurno(h, turnoPref)) {
                        pontos += 10;
                    }
                }
            }
        }

        return pontos;
    }

    //verifica se tal horario é de tal turno
    private boolean ehDoTurno(Horario horario, Turno turno) {
        LocalTime inicio = horario.getInicio();
        switch (turno) {
            case MANHA:
                return inicio.isBefore(LocalTime.of(12, 0));
            case TARDE:
                return !inicio.isBefore(LocalTime.of(12, 0)) && inicio.isBefore(LocalTime.of(18, 0));
            case NOITE:
                return !inicio.isBefore(LocalTime.of(18, 0));
            default:
                return false;
        }
    }
}
