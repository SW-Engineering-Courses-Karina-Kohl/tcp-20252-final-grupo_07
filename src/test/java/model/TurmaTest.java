package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TurmaTest {

    private static final Logger logger = LogManager.getLogger(TurmaTest.class);

    private Professor professor = new Professor("Karina Kohl");
    private Disciplina disciplina = new Disciplina("INF01120", "TCP", 4);
    
    @Test
    @DisplayName("Adiciona horario com sucesso")
    void adicionarHorario() {
        logger.info("Teste: Adiciona horario com sucesso iniciado");
        Turma turma = new Turma("A", professor, disciplina, 30, "108");
        Horario horario = new Horario(LocalTime.of(8, 0), LocalTime.of(10, 0), DiaSemana.SEGUNDA);
        
        turma.addHorario(horario);
        
        assertEquals(1, turma.getHorarios().size(), "Deve adicionar o horário com sucesso");
        assertEquals(horario, turma.getHorarios().get(0), "O horário adicionado deve ser igual ao esperado");

        logger.info("Sucesso: Horario adicionado corretamente");
    }

    @Test
    @DisplayName("Nao deve adicionar professor ou disciplina nulos")
    void validaNull() {
        logger.info("Teste: Nao deve adicionar professor ou disciplina nulos iniciado");

        assertThrows(IllegalArgumentException.class, () -> 
            new Turma("A", null, disciplina, 30, "101")
        );
        assertThrows(IllegalArgumentException.class, () -> 
            new Turma("A", professor, null, 30, "101")
        );
        
        logger.info("Sucesso: Excecoes lancadas corretamente para professor ou disciplina nulos");
    }

}
