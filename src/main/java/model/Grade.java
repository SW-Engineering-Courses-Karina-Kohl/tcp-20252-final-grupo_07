package model;

import java.util.List;
import java.util.ArrayList;

public class Grade {
    private List<Turma> turmasSelecionadas;
    private int creditosTotais;

    public Grade() {
        this.turmasSelecionadas = new ArrayList<>();
        this.creditosTotais = 0;
    }

    public Grade(Grade grade){
        this.turmasSelecionadas = new ArrayList<>();
        for (Turma turma : grade.getTurmasSelecionadas()){
            this.turmasSelecionadas.add(turma);
        }
        this.creditosTotais = grade.creditosTotais;
    }

    public boolean adicionarTurma(Turma turma){
        if(!this.conflitoNaGrade(turma)){
            this.turmasSelecionadas.add(turma);
            atualizaCreditosTotais();
            return true;
        }
        return false;
    } 

    private boolean conflitoNaGrade(Turma novaTurma) {
        for (Turma turmaNaGrade : this.turmasSelecionadas){
            if(novaTurma.conflitaCom(turmaNaGrade)){
               return true; 
            }

            String codigoNovaTurma = novaTurma.getDisciplina().getCodigo().trim();
            String codigoTurmaNaGrade = turmaNaGrade.getDisciplina().getCodigo().trim();
            if (codigoNovaTurma.equalsIgnoreCase(codigoTurmaNaGrade)) {
                return true;
            }
        }
        return false;
    }

    public void removerTurma(Turma turma) {
        this.turmasSelecionadas.remove(turma);
        atualizaCreditosTotais();
    } 

    private void atualizaCreditosTotais(){
        this.creditosTotais = 0;
        for(Turma turma : turmasSelecionadas){
            this.creditosTotais += turma.getDisciplina().getCreditos();
        }
    }

    public List<Turma> getTurmasSelecionadas() {
        return turmasSelecionadas;
    }

    public int getCreditosTotais() {
        return creditosTotais;
    }
}


