package model;

import java.util.List;
import java.util.ArrayList;

public class Preferencias {
    private Turno turnoPreferido;
    private List<String> professoresPreferidos;

    private List<String> professoresDescartados;
    private List<Horario> horariosDescartados;

    private List<Turma> turmasPreferidas;
    private List<Turma> turmasDescartadas;
    private int numeroDisciplinas; 

    public Preferencias() {
        this.professoresPreferidos = new ArrayList<>();
        this.professoresDescartados = new ArrayList<>();

        this.turmasPreferidas = new ArrayList<>();
        this.turmasDescartadas = new ArrayList<>();
        this.horariosDescartados = new ArrayList<>();
        this.numeroDisciplinas = 0; 
    }

    public int getNumeroDisciplinas() {
        return numeroDisciplinas;
    }

    public void setNumeroDisciplinas(int numeroDisciplina) {
        this.numeroDisciplinas = numeroDisciplina;
    }

    public Turno getTurnoPreferido() {
        return turnoPreferido;
    }

    public void setTurnoPreferido(Turno turnoPreferido) {
        this.turnoPreferido = turnoPreferido;
    }

    public List<String> getProfessoresPreferidos() {
        return professoresPreferidos;
    }

    public void adicionarProfessorPreferido(String nome) {
        this.professoresPreferidos.add(nome);
    }

    public List<String> getProfessoresDescartados() {
        return professoresDescartados;
    }

    public void adicionarProfessorDescartado(String nome) {
        this.professoresDescartados.add(nome);
    }

    public List<Horario> getHorariosBloqueados() {
        return horariosDescartados;
    }

    public void adicionarHorarioBloqueado(Horario horario) {
        this.horariosDescartados.add(horario);
    }

    public void adicionarTurmaPreferida(Turma t){
        this.turmasPreferidas.add(t);
    }

    public List<Turma> getTurmasPreferidas(){
        return turmasPreferidas;
    }

    public void adicionarTurmaDescartada(Turma t){
        this.turmasDescartadas.add(t);
    }

    public List<Turma> getTurmasDescartadas(){
        return turmasDescartadas;
    }
}