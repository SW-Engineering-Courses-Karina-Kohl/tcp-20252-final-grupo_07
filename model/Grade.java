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

    public boolean adicionarTurma(Turma turma){
        if(!turma.conflitaCom(this.turmasSelecionadas)){
            this.turmasSelecionadas.add(turma);
            atualizaCreditosTotais();
            return true;
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


