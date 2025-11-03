package model;

import java.util.List;

public class Turma {
    private String codigo;
    private Professor professor;
    private Disciplina disciplina;
    private Horario horario;
    private int vagasOfertadas;
    private String sala;
    private String cor; //interface 

    public Turma(String codigo, Professor professor, Disciplina disciplina,int vagasOfertadas, String sala) {
        this.codigo = codigo;
        this.professor = professor;
        this.disciplina = disciplina;
        this.vagasOfertadas = vagasOfertadas;
        this.sala = sala;
    }

    public boolean conflitaCom(List<Turma> outrasTurmas) {
        for (Turma outra : outrasTurmas) {
            if (this.horario.conflitaCom(outra.getHorario())) {
                return true;
            }
        }
        return false;
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

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
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
}
