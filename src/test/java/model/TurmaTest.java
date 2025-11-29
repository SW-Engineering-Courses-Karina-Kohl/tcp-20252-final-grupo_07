package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalTime;

public class TurmaTest {

    private Professor professor = new Professor("Karina Kohl");
    private Disciplina disciplina = new Disciplina("INF01120", "TCP", 4);
    
    @Test
    @DisplayName("Adiciona horario com sucesso")
    void adicionarHorario() {
        Turma turma = new Turma("A", professor, disciplina, 30, "108");
        Horario horario = new Horario(LocalTime.of(8, 0), LocalTime.of(10, 0), DiaSemana.SEGUNDA);
        
        turma.addHorario(horario);
        
        assertEquals(1, turma.getHorarios().size(), "Deve adicionar o horário com sucesso");
        assertEquals(horario, turma.getHorarios().get(0), "O horário adicionado deve ser igual ao esperado");
    }

    @Test
    @DisplayName("Nao deve adicionar professor ou disciplina nulos")
    void validaNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Turma("A", null, disciplina, 30, "101")
        );
        assertThrows(IllegalArgumentException.class, () -> 
            new Turma("A", professor, null, 30, "101")
        );
    }

}
