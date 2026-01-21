package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HorarioTest {

    private static final Logger logger = LogManager.getLogger(HorarioTest.class);
    
    @Test
    @DisplayName("Nao deve permitir criacao de horario com hora de inicio igual ou superior a hora de fim")
    public void testHorarioInvalido() {
        logger.info("Teste: Verificando criacao de horario invalido");

        assertThrows(IllegalArgumentException.class, () -> {
            new Horario(LocalTime.of(10, 0), LocalTime.of(9, 0), DiaSemana.SEGUNDA);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Horario(LocalTime.of(10, 0), LocalTime.of(10, 0), DiaSemana.SEGUNDA);
        });

        logger.info("Sucesso: Horarios invalidos corretamente lancaram excecoes");
    }

    @Test
    @DisplayName("Deve detectar conflito em horarios sobrepostos no mesmo dia e nao conflitar em dias diferentes")
    void testConflitoHorario() {
        logger.info("Teste: Verificando conflito entre horarios");

        Horario h1 = new Horario(LocalTime.of(9, 0), LocalTime.of(11, 0), DiaSemana.TERCA);
        Horario h2 = new Horario(LocalTime.of(10, 0), LocalTime.of(12, 0), DiaSemana.TERCA);
        Horario h3 = new Horario(LocalTime.of(9, 0), LocalTime.of(10, 0), DiaSemana.QUARTA);

        assertTrue(h1.conflitaCom(h2), "Horarios devem conflitar");
        assertFalse(h1.conflitaCom(h3), "Horarios nao devem conflitar");

        logger.info("Sucesso: Conflitos entre horarios corretamente identificados");
    }

    @Test
    @DisplayName("Nao deve gerar erro caso um horario acabe exatamente quando outro comeca")
    void testHorarioSequencia() {
        logger.info("Teste: Verificando horarios em sequencia");    

        Horario h1 = new Horario(LocalTime.of(8, 0), LocalTime.of(10, 0), DiaSemana.TERCA);
        Horario h2 = new Horario(LocalTime.of(10, 0), LocalTime.of(12, 0), DiaSemana.TERCA);

        assertFalse(h1.conflitaCom(h2), "Horarios nao devem conflitar quando um termina exatamente quando o outro comeca");

        logger.info("Sucesso: Horarios em sequencia nao conflitam");
    }

}
