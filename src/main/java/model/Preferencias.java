package model;

import java.util.List;
import java.util.ArrayList;

public class Preferencias {
    private Turno turnoPreferido;
    private List<String> professoresPreferidos;

    private List<String> professoresEvitados;
    private List<Horario> horariosEvitados;
    private int numeroCadeiras; 

    public Preferencias() {
        this.professoresPreferidos = new ArrayList<>();
        this.professoresEvitados = new ArrayList<>();
        this.horariosEvitados = new ArrayList<>();
        this.numeroCadeiras = 0; 
    }

    public int getNumeroCadeiras() {
        return numeroCadeiras;
    }

    public void setNumeroCadeiras(int numeroCadeiras) {
        this.numeroCadeiras = numeroCadeiras;
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

    public List<String> getProfessoresEvitados() {
        return professoresEvitados;
    }

    public void adicionarProfessorEvitado(String nome) {
        this.professoresEvitados.add(nome);
    }

    public List<Horario> getHorariosBloqueados() {
        return horariosEvitados;
    }

    public void adicionarHorarioBloqueado(Horario horario) {
        this.horariosEvitados.add(horario);
    }
}