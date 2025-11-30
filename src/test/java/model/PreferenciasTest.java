package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreferenciasTest {
    
    private static final Logger logger = LogManager.getLogger(PreferenciasTest.class);

    @Test
    @DisplayName("Inicializa com valores padrao quando nao ha parametros")
    void inicializacaoPadrao() {
        logger.info("Teste: Inicializacao padrao das Preferencias");

        Preferencias preferencias = new Preferencias();
        
        assertNotNull(preferencias.getProfessoresPreferidos());
        assertNotNull(preferencias.getProfessoresEvitados());
        assertNotNull(preferencias.getHorariosBloqueados());

        assertTrue(preferencias.getProfessoresPreferidos().isEmpty());
        assertTrue(preferencias.getProfessoresEvitados().isEmpty());
        assertTrue(preferencias.getHorariosBloqueados().isEmpty());

        assertEquals(0, preferencias.getNumeroCadeiras());

        logger.info("Sucesso: Inicializacao padrao das Preferencias verificada.");
    }

    @Test
    @DisplayName("Adiciona professores preferidos corretamente")
    void adicionaProfessoresPreferidos() {
        logger.info("Teste: Adiciona professores preferidos nas Preferencias");

        Preferencias preferencias = new Preferencias();
        preferencias.adicionarProfessorPreferido("Karina Kohl");
        preferencias.adicionarProfessorPreferido("Bruno Grisci");

        assertEquals(2, preferencias.getProfessoresPreferidos().size());
        assertTrue(preferencias.getProfessoresPreferidos().contains("Karina Kohl"));
        assertTrue(preferencias.getProfessoresPreferidos().contains("Bruno Grisci"));

        logger.info("Sucesso: Adicao de professores preferidos verificada.");
    }

    @Test
    @DisplayName("Adiciona professores evitados corretamente")
    void adicionaProfessoresEvitados() {
        logger.info("Teste: Adiciona professores evitados nas Preferencias");
        
        Preferencias preferencias = new Preferencias();
        preferencias.adicionarProfessorEvitado("Primeiro Professor");
        preferencias.adicionarProfessorEvitado("Segundo Professor");

        assertEquals(2, preferencias.getProfessoresEvitados().size());
        assertTrue(preferencias.getProfessoresEvitados().contains("Primeiro Professor"));
        assertTrue(preferencias.getProfessoresEvitados().contains("Segundo Professor"));

        logger.info("Sucesso: Adicao de professores evitados verificada.");
    }

    @Test
    @DisplayName("Adiciona horários bloqueados corretamente")
    void adicionaHorariosBloqueados() {
        logger.info("Teste: Adiciona horarios bloqueados nas Preferencias");

        Preferencias preferencias = new Preferencias();
        Horario horario1 = new Horario(LocalTime.of(8, 0), LocalTime.of(10, 0), DiaSemana.SEGUNDA);
        Horario horario2 = new Horario(LocalTime.of(14, 0), LocalTime.of(16, 0), DiaSemana.QUARTA);

        preferencias.adicionarHorarioBloqueado(horario1);
        preferencias.adicionarHorarioBloqueado(horario2);

        assertEquals(2, preferencias.getHorariosBloqueados().size());
        assertTrue(preferencias.getHorariosBloqueados().contains(horario1));
        assertTrue(preferencias.getHorariosBloqueados().contains(horario2));

        logger.info("Sucesso: Adicao de horarios bloqueados verificada.");
    }


}
