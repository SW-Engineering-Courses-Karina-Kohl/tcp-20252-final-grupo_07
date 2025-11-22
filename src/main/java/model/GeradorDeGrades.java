package model;
import java.util.ArrayList;
import java.util.List;

import model.Disciplina;
import model.Grade;
import model.Turma;

public class GeradorDeGrades {
    private List<Grade> grades;
    private List<Disciplina> disciplinas;

    public GeradorDeGrades(List<Disciplina> disciplinas){
        this.grades = new ArrayList<>();
        this.disciplinas = disciplinas;
    }

    public void startCombina(){
        Grade gradeAux = new Grade();
        combina(gradeAux, 0);
    }

    public void combina(Grade gradeAux, int indexDisciplinas){
        Disciplina disciplinaAtual = disciplinas.get(indexDisciplinas);

        for(Turma turma : disciplinaAtual.getTurmas()){
            if(gradeAux.adicionarTurma(turma)){
                if(indexDisciplinas+1 < disciplinas.size()){
                    combina(gradeAux, indexDisciplinas+1);
                }
                else { 
                    Grade copiaGrade = new Grade(gradeAux);
                    grades.add(copiaGrade);
                }
                gradeAux.removerTurma(turma);
            }
        }
    }
}
