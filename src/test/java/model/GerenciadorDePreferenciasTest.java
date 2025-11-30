package model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciadorDePreferenciasTest {

    private static final Logger logger = LogManager.getLogger(GerenciadorDePreferenciasTest.class);

    @TempDir
    Path tempDir;

    GerenciadorDePreferencias ger;

    @BeforeEach
    void setup() {
        ger = new GerenciadorDePreferencias();
    }

    @Test
    @DisplayName("Carregar arquivo inexistente retorna preferencias padrão")
    void carregarArquivoInexistente() {

        String caminho = tempDir.resolve("nao_existe.csv").toString();

        Preferencias prefs = ger.carregarPreferencias(caminho);

        assertNotNull(prefs);

        Preferencias padrao = new Preferencias();

        assertEquals(padrao.getTurnoPreferido(), prefs.getTurnoPreferido());
        assertEquals(padrao.getNumeroCadeiras(), prefs.getNumeroCadeiras());
        assertEquals(padrao.getProfessoresPreferidos(), prefs.getProfessoresPreferidos());
        assertEquals(padrao.getProfessoresEvitados(), prefs.getProfessoresEvitados());
        assertEquals(padrao.getHorariosBloqueados(), prefs.getHorariosBloqueados());
    }

    @Test
    @DisplayName("Salvar e carregar preferencias")
    void salvarECarregar() throws IOException {

        String caminho = tempDir.resolve("prefs.csv").toString();

        Preferencias p = new Preferencias();
        p.setTurnoPreferido(Turno.MANHA);
        p.adicionarProfessorPreferido("Karina");
        p.adicionarProfessorEvitado("Fulano");
        p.adicionarHorarioBloqueado(new Horario(LocalTime.of(8,30), LocalTime.of(10,10), DiaSemana.SEGUNDA));
        p.setNumeroCadeiras(5);

        ger.salvarPreferencias(caminho, p);

        Preferencias carregado = ger.carregarPreferencias(caminho);

        assertEquals(Turno.MANHA, carregado.getTurnoPreferido());
        assertEquals(1, carregado.getProfessoresPreferidos().size());
        assertEquals("Karina", carregado.getProfessoresPreferidos().get(0));

        assertEquals(1, carregado.getProfessoresEvitados().size());
        assertEquals("Fulano", carregado.getProfessoresEvitados().get(0));

        assertEquals(1, carregado.getHorariosBloqueados().size());
        Horario h = carregado.getHorariosBloqueados().get(0);
        assertEquals(LocalTime.of(8,30), h.getInicio());
        assertEquals(LocalTime.of(10,10), h.getFim());
        assertEquals(DiaSemana.SEGUNDA, h.getDiaSemana());

        assertEquals(5, carregado.getNumeroCadeiras());
    }

    @Test
    @DisplayName("Turno invalido deve ser ignorado")
    void turnoInvalido() throws IOException {

        String caminho = tempDir.resolve("turno_invalido.csv").toString();

        String conteudo = """
                1 - Turno Preferido
                2 - Professores Preferidos
                3 - Professores Evitados
                4 - Numero de Disciplinas
                TURNO_INEXISTE
                Karina
                Fulano
                08:30, 10:10, SEG
                3
                """;

        Files.writeString(Path.of(caminho), conteudo);

        Preferencias p = ger.carregarPreferencias(caminho);

        assertNull(p.getTurnoPreferido(), "Turno inválido deve ser ignorado");
    }

    @Test
    @DisplayName("Horários inválidos são ignorados")
    void horariosInvalidos() throws IOException {

        String caminho = tempDir.resolve("horarios_invalidos.csv").toString();

        String conteudo = """
                1 - Turno Preferido
                2 - Professores Preferidos
                3 - Professores Evitados
                4 - Numero de Disciplinas
                MANHA
                Karina
                Fulano
                08:30,10:10   << faltando o dia
                3
                """;

        Files.writeString(Path.of(caminho), conteudo);

        Preferencias p = ger.carregarPreferencias(caminho);

        assertTrue(p.getHorariosBloqueados().isEmpty(),
                "Horários inválidos devem ser ignorados totalmente");
    }
}
