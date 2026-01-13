package model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciadorDePreferenciasTest {

    private static final Logger logger = LogManager.getLogger(GerenciadorDePreferenciasTest.class);

    @TempDir
    Path tempDir;

    GerenciadorDePreferencias ger;
    
    List<Turma> turmasDisponiveis;
    Turma turmaTeste1;
    Turma turmaTeste2;

    @BeforeEach
    void setup() {
        logger.info("Iniciando setup do teste.");
        ger = new GerenciadorDePreferencias();

        Disciplina d1 = new Disciplina("INF001", "Algoritmos", 4);
        Disciplina d2 = new Disciplina("MAT001", "Cálculo", 6);
        
        Professor p1 = new Professor("Avelino");
        Professor p2 = new Professor("Reis");

        turmaTeste1 = new Turma("A", p1, d1, 5, "201"); 
        turmaTeste2 = new Turma("B", p2, d2, 10, "102"); 

        turmasDisponiveis = new ArrayList<>();
        turmasDisponiveis.add(turmaTeste1);
        turmasDisponiveis.add(turmaTeste2);

        logger.info("Setup concluído com {} turmas simuladas.", turmasDisponiveis.size());
    }

    @Test
    @DisplayName("Carregar arquivo inexistente retorna preferencias padrão")
    void carregarArquivoInexistente() {
        String caminho = tempDir.resolve("nao_existe.csv").toString();
        
        Preferencias prefs = ger.carregarPreferencias(caminho, turmasDisponiveis);
        
        assertNotNull(prefs);
        Preferencias padrao = new Preferencias();

        assertEquals(padrao.getTurmasPreferidas(), prefs.getTurmasPreferidas());
        assertEquals(padrao.getTurmasDescartadas(), prefs.getTurmasDescartadas());
        
        assertEquals(padrao.getTurnoPreferido(), prefs.getTurnoPreferido());
        assertEquals(padrao.getNumeroDisciplinas(), prefs.getNumeroDisciplinas());
    }

    @Test
    @DisplayName("Salvar e carregar preferencias com Turmas")
    void salvarECarregar() throws IOException {
        String caminho = tempDir.resolve("prefs.csv").toString();

        Preferencias p = new Preferencias();
        p.setTurnoPreferido(Turno.MANHA);
        p.setNumeroDisciplinas(5);
        p.adicionarHorarioBloqueado(new Horario(LocalTime.of(8,30), LocalTime.of(10,10), DiaSemana.SEGUNDA));
        
        p.adicionarTurmaPreferida(turmaTeste1); 
        p.adicionarTurmaDescartada(turmaTeste2); 

        ger.salvarPreferencias(caminho, p);

        Preferencias carregado = ger.carregarPreferencias(caminho, turmasDisponiveis);

        assertEquals(Turno.MANHA, carregado.getTurnoPreferido());
        assertEquals(5, carregado.getNumeroDisciplinas());

        assertEquals(1, carregado.getTurmasPreferidas().size());
        assertEquals(turmaTeste1.getCodigo(), carregado.getTurmasPreferidas().get(0).getCodigo());
        assertEquals(turmaTeste1.getDisciplina().getNome(), carregado.getTurmasPreferidas().get(0).getDisciplina().getNome());

        assertEquals(1, carregado.getTurmasDescartadas().size());
        assertEquals(turmaTeste2.getDisciplina().getNome(), carregado.getTurmasDescartadas().get(0).getDisciplina().getNome());
    }

    @Test
    @DisplayName("Turno invalido deve ser ignorado")
    void turnoInvalido() throws IOException {
        String caminho = tempDir.resolve("turno_invalido.csv").toString();

        // Simulando um CSV escrito manualmente com erro
        String conteudo = """
                1 - Turno Preferido
                2 - Turmas Preferidas
                3 - Turmas Descartadas
                4 - Horários bloqueados
                5 - Numero de Disciplinas
                TURNO_INEXISTE


                08:30, 10:10, SEG
                3
                """;
        Files.writeString(Path.of(caminho), conteudo);

        Preferencias p = ger.carregarPreferencias(caminho, turmasDisponiveis);

        assertNull(p.getTurnoPreferido(), "Turno inválido deve ser ignorado");
    }

    @Test
    @DisplayName("Horários inválidos são ignorados")
    void horariosInvalidos() throws IOException {
        String caminho = tempDir.resolve("horarios_invalidos.csv").toString();

        // Simulando CSV com horário quebrado
        String conteudo = """
                1 - Turno Preferido
                2 - Turmas Preferidas
                3 - Turmas Descartadas
                4 - Numero de Disciplinas
                5 - Numero de Disciplinas
                MANHA
                
                
                08:30,10:10
                3
                """;
        Files.writeString(Path.of(caminho), conteudo);

        Preferencias p = ger.carregarPreferencias(caminho, turmasDisponiveis);

        assertTrue(p.getHorariosBloqueados().isEmpty());
    }
}