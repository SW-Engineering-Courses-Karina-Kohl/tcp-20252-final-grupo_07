package model;

import java.time.LocalTime;

public class Horario {
    private LocalTime inicio;
    private LocalTime fim;
    private DiaSemana diaSemana;

    public Horario(LocalTime inicio, LocalTime fim, DiaSemana diaSemana) {
        if (inicio.isAfter(fim) || inicio.equals(fim)) {
            throw new IllegalArgumentException("O horário de início deve ser estritamente antes do horário de fim.");
        }
        this.inicio = inicio;
        this.fim = fim;
        this.diaSemana = diaSemana;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFim() {
        return fim;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public boolean conflitaCom(Horario outro) {
        if (outro == null) {
            throw new IllegalArgumentException("O horário para comparação não pode ser nulo.");
        }
        if (this.diaSemana != outro.diaSemana) {
            return false;
        }
        return this.inicio.isBefore(outro.fim) && outro.inicio.isBefore(this.fim);
    }
}
