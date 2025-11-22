package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GeradorDeGrades {
    private List<Grade> grades;
    private List<Disciplina> disciplinas;
    private Preferencias preferencias;

    public GeradorDeGrades(List<Disciplina> disciplinas, Preferencias preferencias){
        this.grades = new ArrayList<>();
        this.disciplinas = disciplinas;
        this.preferencias = preferencias;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void startCombina(){
        Grade gradeAux = new Grade();
        combina(gradeAux, 0);
        ordenarGradesPorPreferencia();
    }

    private void combina(Grade gradeAux, int indexDisciplinas){
        Disciplina disciplinaAtual = disciplinas.get(indexDisciplinas);

        for(Turma turma : disciplinaAtual.getTurmas()){
            
            //novo--- verificar restricoes
            if (violaRestricoes(turma, gradeAux)) {
                continue; // pula essa turma
            }

            if(gradeAux.adicionarTurma(turma)){
                if(indexDisciplinas + 1 < disciplinas.size()){
                    combina(gradeAux, indexDisciplinas + 1);
                }
                else { 
                    Grade copiaGrade = new Grade(gradeAux);
                    grades.add(copiaGrade);
                }
                gradeAux.removerTurma(turma);
            }
        }
    }

    //logica de restricoes (hard constraints)
    private boolean violaRestricoes(Turma turma, Grade gradeAtual) {
        if (preferencias == null) return false;

        // professor evitado
        String nomeProf = turma.getProfessor().getNome().trim();
        for (String evitado : preferencias.getProfessoresEvitados()) {
            if (nomeProf.equalsIgnoreCase(evitado.trim())) {
                return true; // bloqueia
            }
        }

        // limite de creditos
        // se adicionar essa turma vai estourar os creditos, bloqueia
        int creditosFuturos = gradeAtual.getCreditosTotais() + turma.getDisciplina().getCreditos();
        if (creditosFuturos > preferencias.getMaxCreditos()) {
            return true; // bloqueia
        }

        //horarios evitados
        for (Horario horarioTurma : turma.getHorarios()) {
            for (Horario horarioBloqueado : preferencias.getHorariosBloqueados()) {
                if (horarioTurma.conflitaCom(horarioBloqueado)) {
                    return true; // bloqueia
                }
            }
        }

        return false; // passou no teste
    }

    // pontuacao das grades (soft constraints)
    private void ordenarGradesPorPreferencia() {
        if (this.preferencias == null) return;

        Collections.sort(this.grades, new Comparator<Grade>() {
            @Override
            public int compare(Grade g1, Grade g2) {
                return calcularPontuacao(g2) - calcularPontuacao(g1); 
            }
        });
    }
    /*
    calcula a pontuacao e ordena as grades usando o modelo de soma ponderada(cade criterio tem um peso diferente,
    por enquanto, professor preferido tem peso 10 e turno preferido peso 1, ou seja as grades que tiverem o professor preferido
    terao uma pontuacao maior, em caso de empate, a grade que tiver mais aulas no turno preferido tera uma pontuacao maior)
    criterio provisorio, pode ser alterado futuramente. apenas para termos uma logica mais solida para comecar a implementar o front-end
    */
    private int calcularPontuacao(Grade grade) {
        int pontos = 0;
        for (Turma turma : grade.getTurmasSelecionadas()) {
            // professor Preferido +10 (muito peso)
            String nomeProf = turma.getProfessor().getNome().trim();
            for (String favorito : preferencias.getProfessoresPreferidos()) {
                if (nomeProf.equalsIgnoreCase(favorito.trim())) {
                    pontos += 10;
                    break;
                }
            }

            // turno Preferido (+1 por aula, pouco peso)
            if (preferencias.getTurnoPreferido() != null) {
                for (Horario h : turma.getHorarios()) {
                    if (ehDoTurno(h, preferencias.getTurnoPreferido())) {
                        pontos += 1;
                    }
                }
            }
        }
        return pontos;
    }

    private boolean ehDoTurno(Horario horario, Turno turno) {
        LocalTime inicio = horario.getInicio();
        switch (turno) {
            case MANHA: return inicio.isBefore(LocalTime.of(12, 0));
            case TARDE: return !inicio.isBefore(LocalTime.of(12, 0)) && inicio.isBefore(LocalTime.of(18, 0));
            case NOITE: return !inicio.isBefore(LocalTime.of(18, 0));
            default: return false;
        }
    }
}