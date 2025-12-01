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
        logger.info("Iniciando setup do teste de GerenciadorDePreferencias. TempDir: {}", tempDir);
        ger = new GerenciadorDePreferencias();
        logger.info("Instância de GerenciadorDePreferencias criada com sucesso.");
    }

    @Test
    @DisplayName("Carregar arquivo inexistente retorna preferencias padrão")
    void carregarArquivoInexistente() {

        String caminho = tempDir.resolve("nao_existe.csv").toString();
        logger.info("Teste: carregarArquivoInexistente. Caminho do arquivo inexistente: {}", caminho);

        Preferencias prefs = ger.carregarPreferencias(caminho);
        logger.info("Preferencias carregadas para arquivo inexistente: {}", prefs);

        assertNotNull(prefs);
        logger.info("Assert: Preferencias não é nula (arquivo inexistente).");

        Preferencias padrao = new Preferencias();
        logger.info("Preferencias padrão criadas: {}", padrao);

        assertEquals(padrao.getTurnoPreferido(), prefs.getTurnoPreferido());
        logger.info("Assert: turnoPreferido igual ao padrão. Esperado={}, Obtido={}",
                padrao.getTurnoPreferido(), prefs.getTurnoPreferido());

        assertEquals(padrao.getNumeroDisciplinas(), prefs.getNumeroDisciplinas());
        logger.info("Assert: numeroDisciplinas igual ao padrão. Esperado={}, Obtido={}",
                padrao.getNumeroDisciplinas(), prefs.getNumeroDisciplinas());

        assertEquals(padrao.getProfessoresPreferidos(), prefs.getProfessoresPreferidos());
        logger.info("Assert: professoresPreferidos igual ao padrão. Esperado={}, Obtido={}",
                padrao.getProfessoresPreferidos(), prefs.getProfessoresPreferidos());

        assertEquals(padrao.getProfessoresEvitados(), prefs.getProfessoresEvitados());
        logger.info("Assert: professoresEvitados igual ao padrão. Esperado={}, Obtido={}",
                padrao.getProfessoresEvitados(), prefs.getProfessoresEvitados());

        assertEquals(padrao.getHorariosBloqueados(), prefs.getHorariosBloqueados());
        logger.info("Assert: horariosBloqueados igual ao padrão. Esperado={}, Obtido={}",
                padrao.getHorariosBloqueados(), prefs.getHorariosBloqueados());
    }

    @Test
    @DisplayName("Salvar e carregar preferencias")
    void salvarECarregar() throws IOException {

        String caminho = tempDir.resolve("prefs.csv").toString();
        logger.info("Teste: salvarECarregar. Caminho do arquivo de preferencias: {}", caminho);

        Preferencias p = new Preferencias();
        p.setTurnoPreferido(Turno.MANHA);
        p.adicionarProfessorPreferido("Karina");
        p.adicionarProfessorEvitado("Fulano");
        p.adicionarHorarioBloqueado(new Horario(LocalTime.of(8,30), LocalTime.of(10,10), DiaSemana.SEGUNDA));
        p.setNumeroDisciplinas(5);

        logger.info("Preferencias configuradas para salvar: turno={}, numDisciplinas={}, pref={}, evitados={}, horariosBloqueados={}",
                p.getTurnoPreferido(),
                p.getNumeroDisciplinas(),
                p.getProfessoresPreferidos(),
                p.getProfessoresEvitados(),
                p.getHorariosBloqueados());

        ger.salvarPreferencias(caminho, p);
        logger.info("Preferencias salvas em {}", caminho);

        Preferencias carregado = ger.carregarPreferencias(caminho);
        logger.info("Preferencias carregadas de {}: turno={}, numDisciplinas={}, pref={}, evitados={}, horariosBloqueados={}",
                caminho,
                carregado.getTurnoPreferido(),
                carregado.getNumeroDisciplinas(),
                carregado.getProfessoresPreferidos(),
                carregado.getProfessoresEvitados(),
                carregado.getHorariosBloqueados());

        assertEquals(Turno.MANHA, carregado.getTurnoPreferido());
        logger.info("Assert: turnoPreferido == MANHA");

        assertEquals(1, carregado.getProfessoresPreferidos().size());
        assertEquals("Karina", carregado.getProfessoresPreferidos().get(0));
        logger.info("Assert: professoresPreferidos contém apenas 'Karina'.");

        assertEquals(1, carregado.getProfessoresEvitados().size());
        assertEquals("Fulano", carregado.getProfessoresEvitados().get(0));
        logger.info("Assert: professoresEvitados contém apenas 'Fulano'.");

        assertEquals(1, carregado.getHorariosBloqueados().size());
        Horario h = carregado.getHorariosBloqueados().get(0);
        logger.info("Horario bloqueado carregado: inicio={}, fim={}, dia={}",
                h.getInicio(), h.getFim(), h.getDiaSemana());

        assertEquals(LocalTime.of(8,30), h.getInicio());
        assertEquals(LocalTime.of(10,10), h.getFim());
        assertEquals(DiaSemana.SEGUNDA, h.getDiaSemana());
        logger.info("Assert: horario bloqueado carregado bate com o salvo.");

        assertEquals(5, carregado.getNumeroDisciplinas());
        logger.info("Assert: numeroDisciplinas == 5");
    }

    @Test
    @DisplayName("Turno invalido deve ser ignorado")
    void turnoInvalido() throws IOException {

        String caminho = tempDir.resolve("turno_invalido.csv").toString();
        logger.info("Teste: turnoInvalido. Caminho do CSV: {}", caminho);

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

        logger.info("Escrevendo conteudo de preferencias com turno inválido no arquivo.");
        Files.writeString(Path.of(caminho), conteudo);

        Preferencias p = ger.carregarPreferencias(caminho);
        logger.info("Preferencias carregadas com turno inválido: turno={}, numDisciplinas={}, pref={}, evitados={}, horariosBloqueados={}",
                p.getTurnoPreferido(),
                p.getNumeroDisciplinas(),
                p.getProfessoresPreferidos(),
                p.getProfessoresEvitados(),
                p.getHorariosBloqueados());

        assertNull(p.getTurnoPreferido(), "Turno inválido deve ser ignorado");
        logger.info("Assert: turnoPreferido é nulo para turno inválido, conforme esperado.");
    }

    @Test
    @DisplayName("Horários inválidos são ignorados")
    void horariosInvalidos() throws IOException {

        String caminho = tempDir.resolve("horarios_invalidos.csv").toString();
        logger.info("Teste: horariosInvalidos. Caminho do CSV: {}", caminho);

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

        logger.info("Escrevendo conteudo de preferencias com horario inválido no arquivo.");
        Files.writeString(Path.of(caminho), conteudo);

        Preferencias p = ger.carregarPreferencias(caminho);
        logger.info("Preferencias carregadas com horario inválido: horariosBloqueados={}",
                p.getHorariosBloqueados());

        assertTrue(p.getHorariosBloqueados().isEmpty(),
                "Horários inválidos devem ser ignorados totalmente");
        logger.info("Assert: lista de horariosBloqueados está vazia para entrada inválida.");
    }
}
