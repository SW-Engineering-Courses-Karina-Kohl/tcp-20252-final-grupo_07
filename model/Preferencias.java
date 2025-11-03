package model;

import java.util.List;
import java.util.ArrayList;

public class Preferencias {
    private Turno turnoPreferido;
    private List<String> professoresPreferidos;

    public Preferencias() {
        this.professoresPreferidos = new ArrayList<>();
    }

    public void adicionarProfessorPreferido(String nome){
        this.professoresPreferidos.add(nome);
    }

    public void setTurnoPreferido(Turno turno){
        this.turnoPreferido = turno;
    }

    public Turno getTurnoPreferido() {
        return turnoPreferido;
    }

    public List<String> getProfessoresPreferidos() {
        return professoresPreferidos;
    }
}
