package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;

class GradeTest {

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
    }

    @Test
    @DisplayName("Adiciona uma turma se nao houver conflito")
    void adicionarSucesso() {
        Turma t1 = new Turma("A", karina, tcp, 30, "108");

        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));

        boolean adicionou = grade.adicionarTurma(t1);
        
        assertTrue(adicionou, "Deve adicionar a turma sem conflitos");
        assertEquals(4, grade.getCreditosTotais(), "Os creditos totais devem ser 4 apos adicionar a turma");
    }

    @Test
    @DisplayName("Nao deve adicionar turma se houver conflito de horario")
    void bloquearConflitoHorario() {
        Turma t1 = new Turma("A", karina, tcp, 30, "108");
        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));
        grade.adicionarTurma(t1);

        Turma t2 = new Turma("A", alvaro, semantica, 30, "109");
        t2.addHorario(new Horario(LocalTime.of(9,0), LocalTime.of(11,0), DiaSemana.SEGUNDA));

        boolean adicionou = grade.adicionarTurma(t2);

        assertFalse(adicionou, "Nao deveria adicionar turma com conflito de horario");
        assertEquals(1, grade.getTurmasSelecionadas().size());
    }

    @Test
    @DisplayName("Nao deve adicionar uma mesma turma duas vezes")
    void bloquearDuplicidadeTurma() {
        Turma t1 = new Turma("A", karina, tcp, 30, "108");
        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));
        grade.adicionarTurma(t1);

        boolean adicionouNovamente = grade.adicionarTurma(t1);
        
        assertFalse(adicionouNovamente, "Nao deveria permitir adicionar a mesma turma duas vezes");
        assertEquals(1, grade.getTurmasSelecionadas().size());
    }

    @Test
    @DisplayName("Nao deve adicionar duas turmas da mesma disciplina")
    void bloquearDuplicidadeDisciplina() {
        Turma t1 = new Turma("A", karina, tcp, 30, "108");
        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));
        grade.adicionarTurma(t1);

        Turma t2 = new Turma("B", karina, tcp, 30, "102");
        t2.addHorario(new Horario(LocalTime.of(14,0), LocalTime.of(16,0), DiaSemana.SEGUNDA));

        boolean adicionou = grade.adicionarTurma(t2);
        
        assertFalse(adicionou, "Nao deveria permitir a mesma disciplina duas vezes");
    }

    @Test
    @DisplayName("Remove uma turma corretamente")
    void removerTurma() {
        Turma t1 = new Turma("A", karina, tcp, 30, "108");
        t1.addHorario(new Horario(LocalTime.of(8,0), LocalTime.of(10,0), DiaSemana.SEGUNDA));
        grade.adicionarTurma(t1);

        grade.removerTurma(t1);

        assertEquals(0, grade.getTurmasSelecionadas().size(), "A grade deve estar vazia apos remover a turma");
        assertEquals(0, grade.getCreditosTotais(), "Os creditos totais devem ser 0 apos remover a turma");
    }
}