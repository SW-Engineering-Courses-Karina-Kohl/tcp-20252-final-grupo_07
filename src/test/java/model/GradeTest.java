package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class GradeTest {
    
    private static final Logger logger = LogManager.getLogger(GradeTest.class);

    private Grade grade;
    private Disciplina tcp;
    private Disciplina semantica;
    private Professor karina;
    private Professor alvaro;

    @BeforeEach
    void setup() {
        grade = new Grade();
        karina = new Professor("Karina Kohl");
        alvaro = new Professor("Alvaro Moreira");
        tcp = new Disciplina("INF01120", "TCP", 4);
        semantica = new Disciplina("INF05516", "Semantica Formal", 4);

        logger.info("Setup inicial feito para o teste.");
    }

    @Test
    @DisplayName("Adiciona uma turma se nao houver conflito")
    void adicionarSucesso() {
        logger.info("Teste: Adicionar turma valida a grade.");

        Turma t1 = new Turma("A", karina, tcp, 30, "108");

        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));

        boolean adicionou = grade.adicionarTurma(t1);
        
        assertTrue(adicionou, "Deve adicionar a turma sem conflitos");
        assertEquals(4, grade.getCreditosTotais(), "Os creditos totais devem ser 4 apos adicionar a turma");

        logger.info("Sucesso: Turma adicionada e creditos atualizados.");
    }

    @Test
    @DisplayName("Nao deve adicionar turma se houver conflito de horario")
    void bloquearConflitoHorario() {
        logger.info("Teste: Bloquear adicao de turma com conflito de horario.");

        Turma t1 = new Turma("A", karina, tcp, 30, "108");
        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));
        grade.adicionarTurma(t1);

        Turma t2 = new Turma("A", alvaro, semantica, 30, "109");
        t2.addHorario(new Horario(LocalTime.of(9,0), LocalTime.of(11,0), DiaSemana.SEGUNDA));

        boolean adicionou = grade.adicionarTurma(t2);

        assertFalse(adicionou, "Nao deveria adicionar turma com conflito de horario");
        assertEquals(1, grade.getTurmasSelecionadas().size());

        logger.info("Sucesso: Adicao de turma bloqueada devido a conflito de horario.");
    }

    @Test
    @DisplayName("Nao deve adicionar uma mesma turma duas vezes")
    void bloquearDuplicidadeTurma() {
        logger.info("Teste: Bloquear adicao duplicada de turma.");

        Turma t1 = new Turma("A", karina, tcp, 30, "108");
        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));
        grade.adicionarTurma(t1);

        boolean adicionouNovamente = grade.adicionarTurma(t1);
        
        assertFalse(adicionouNovamente, "Nao deveria permitir adicionar a mesma turma duas vezes");
        assertEquals(1, grade.getTurmasSelecionadas().size());

        logger.info("Sucesso: Adicao duplicada de turma bloqueada.");
    }

    @Test
    @DisplayName("Nao deve adicionar duas turmas da mesma disciplina")
    void bloquearDuplicidadeDisciplina() {
        logger.info("Teste: Bloquear adicao de segunda turma da mesma disciplina.");

        Turma t1 = new Turma("A", karina, tcp, 30, "108");
        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));
        grade.adicionarTurma(t1);

        Turma t2 = new Turma("B", karina, tcp, 30, "102");
        t2.addHorario(new Horario(LocalTime.of(14,0), LocalTime.of(16,0), DiaSemana.SEGUNDA));

        boolean adicionou = grade.adicionarTurma(t2);
        
        assertFalse(adicionou, "Nao deveria permitir a mesma disciplina duas vezes");

        logger.info("Sucesso: Adicao de segunda turma da mesma disciplina bloqueada.");
    }

    @Test
    @DisplayName("Remove uma turma corretamente")
    void removerTurma() {
        logger.info("Teste: Remover turma da grade.");

        Turma t1 = new Turma("A", karina, tcp, 30, "108");
        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));
        grade.adicionarTurma(t1);

        grade.removerTurma(t1);

        assertEquals(0, grade.getTurmasSelecionadas().size(), "A grade deve estar vazia apos remover a turma");
        assertEquals(0, grade.getCreditosTotais(), "Os creditos totais devem ser 0 apos remover a turma");

        logger.info("Sucesso: Turma removida e creditos atualizados.");
    }
}