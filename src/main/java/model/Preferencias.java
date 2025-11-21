package model;

import java.util.List;
import java.util.ArrayList;

public class Preferencias {
    //preferencias
    private Turno turnoPreferido;
    private List<String> professoresPreferidos;
    //restricoes
    private List<String> professoresEvitados;
    private List<Horario> horariosEvitados;
    private int maxCreditos;

    public Preferencias() {
        this.professoresPreferidos = new ArrayList<>();
        this.professoresEvitados = new ArrayList<>();
        this.horariosEvitados = new ArrayList<>();
        this.maxCreditos = 30;
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

    public int getMaxCreditos() {
        return maxCreditos;
    }

    public void setMaxCreditos(int maxCreditos) {
        this.maxCreditos = maxCreditos;
    }
}
