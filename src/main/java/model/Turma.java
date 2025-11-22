package model;

import java.util.ArrayList;
import java.util.List;

public class Turma {
    private String codigo;
    private Professor professor;
    private Disciplina disciplina;
    private List<Horario> horarios;
    private int vagasOfertadas;
    private String sala;
    private String cor; //interface 

    public Turma(String codigo, Professor professor, Disciplina disciplina,int vagasOfertadas, String sala) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código da turma não pode ser nulo ou vazio.");
        }

        if (vagasOfertadas <= 0) {
            throw new IllegalArgumentException("A quantidade de vagas ofertadas deve ser maior do que zero.");
        }

        if (sala == null || sala.trim().isEmpty()) {
            throw new IllegalArgumentException("A sala da turma não pode ser nula ou vazia.");
        }

        if (professor == null) {
        throw new IllegalArgumentException("O professor da turma não pode ser nulo.");
        }
        if (disciplina == null) {
            throw new IllegalArgumentException("A disciplina da turma não pode ser nula.");
        }

        this.codigo = codigo;
        this.professor = professor;
        this.disciplina = disciplina;
        this.vagasOfertadas = vagasOfertadas;
        this.sala = sala;
        this.horarios = new ArrayList<>();
    }

    public Turma (){
        this.horarios = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public Professor getProfessor() {
        return professor;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public List<Horario> getHorarios() {
        return this.horarios;
    }

    public void addHorario(Horario horario) {
        this.horarios.add(horario);
    }

    public int getVagasOfertadas() {
        return vagasOfertadas;
    }

    public String getSala() {
        return sala;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }
    
    //verifica se turmas conflitam em seus horarios
    public boolean conflitaCom(Turma novaTurma){
        for (Horario horariosTurma : this.horarios){
            for(Horario horariosNovaTurma : novaTurma.horarios){
                if (horariosTurma.conflitaCom(horariosNovaTurma)){
                    return true;
                }
            }
        }
        return false;
    }
}
