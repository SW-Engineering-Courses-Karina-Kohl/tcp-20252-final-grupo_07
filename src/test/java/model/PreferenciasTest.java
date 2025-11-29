package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;

public class PreferenciasTest {
    
    @Test
    @DisplayName("Inicializa com valores padrão quando nenhum parâmetro é fornecido")
    void inicializacaoPadrao() {
        Preferencias preferencias = new Preferencias();
        
        assertNotNull(preferencias.getProfessoresPreferidos());
        assertNotNull(preferencias.getProfessoresEvitados());
        assertNotNull(preferencias.getHorariosBloqueados());

        assertTrue(preferencias.getProfessoresPreferidos().isEmpty());
        assertTrue(preferencias.getProfessoresEvitados().isEmpty());
        assertTrue(preferencias.getHorariosBloqueados().isEmpty());

        assertEquals(2, preferencias.getNumeroCadeiras());
    }

    @Test
    @DisplayName("Adiciona professores preferidos corretamente")
    void adicionaProfessoresPreferidos() {
        Preferencias preferencias = new Preferencias();
        preferencias.adicionarProfessorPreferido("Karina Kohl");
        preferencias.adicionarProfessorPreferido("Bruno Grisci");

        assertEquals(2, preferencias.getProfessoresPreferidos().size());
        assertTrue(preferencias.getProfessoresPreferidos().contains("Karina Kohl"));
        assertTrue(preferencias.getProfessoresPreferidos().contains("Bruno Grisci"));
    }

    @Test
    @DisplayName("Adiciona professores evitados corretamente")
    void adicionaProfessoresEvitados() {
        Preferencias preferencias = new Preferencias();
        preferencias.adicionarProfessorEvitado("Primeiro Professor");
        preferencias.adicionarProfessorEvitado("Segundo Professor");

        assertEquals(2, preferencias.getProfessoresEvitados().size());
        assertTrue(preferencias.getProfessoresEvitados().contains("Primeiro Professor"));
        assertTrue(preferencias.getProfessoresEvitados().contains("Segundo Professor"));
    }

    @Test
    @DisplayName("Adiciona horários bloqueados corretamente")
    void adicionaHorariosBloqueados() {
        Preferencias preferencias = new Preferencias();
        Horario horario1 = new Horario(LocalTime.of(8, 0), LocalTime.of(10, 0), DiaSemana.SEGUNDA);
        Horario horario2 = new Horario(LocalTime.of(14, 0), LocalTime.of(16, 0), DiaSemana.QUARTA);

        preferencias.adicionarHorarioBloqueado(horario1);
        preferencias.adicionarHorarioBloqueado(horario2);

        assertEquals(2, preferencias.getHorariosBloqueados().size());
        assertTrue(preferencias.getHorariosBloqueados().contains(horario1));
        assertTrue(preferencias.getHorariosBloqueados().contains(horario2));
    }


}
