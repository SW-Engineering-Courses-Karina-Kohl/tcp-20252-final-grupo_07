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

    /*  
    TODO: Ajustar a funçaõ de conflito entre turmas 
    private boolean conflitaCom(Turma novaTurma) {
        
    }

    TODO: Ajustar a função de adicionar uma turma
    public boolean adicionarTurma(Turma turma){
        if(!turma.conflitaCom(this.turmasSelecionadas)){
            this.turmasSelecionadas.add(turma);
            atualizaCreditosTotais();
            return true;
        }
        return false;
    } 

    TODO: Ajustar a função de remover uma turma
    public void removerTurma(Turma turma) {
        this.turmasSelecionadas.remove(turma);
        atualizaCreditosTotais();
    } */ 

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


