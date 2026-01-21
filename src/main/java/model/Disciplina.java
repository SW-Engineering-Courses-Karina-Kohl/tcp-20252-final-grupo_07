package model;

import java.util.ArrayList;
import java.util.List;

public class Disciplina {
    private String codigo;
    private String nome;
    private int creditos;
    private List<Turma> turmas;

    public Disciplina(String codigo, String nome, int creditos) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código da disciplina não pode ser nulo ou vazio.");
        }

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da disciplina não pode ser nulo ou vazio.");
        }

        if (creditos <= 0) {
            throw new IllegalArgumentException("A quantidade de créditos deve ser maior que zero.");
        }
        
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
        this.turmas = new ArrayList<>();
    }

    public void adicionarTurma(Turma turma) {
        this.turmas.add(turma);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public List<Turma> getTurmas() {
        return turmas;
    }
}

