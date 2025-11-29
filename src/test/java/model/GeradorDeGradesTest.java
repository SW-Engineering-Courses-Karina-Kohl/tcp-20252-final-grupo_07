package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class GeradorDeGradesTest {

    private List<Disciplina> disciplinas;
    private Preferencias preferencias;

    @BeforeEach
    void setup() {
        disciplinas = new ArrayList<>();
        preferencias = new Preferencias();
        preferencias.setNumeroCadeiras(2);
    }

    private void adicionarDisciplinaComTurma(String codigo, String nome, String codigoTurma, String nomeProf, int inicio, int fim, DiaSemana dia) {
        Disciplina disciplina = new Disciplina(codigo, nome, 4);
        Professor professor = new Professor(nomeProf);
        Turma turma = new Turma(codigoTurma, professor, disciplina, 30, "108");
        turma.addHorario(new Horario(LocalTime.of(inicio, 0), LocalTime.of(fim, 0), dia));
        disciplina.adicionarTurma(turma);
        disciplinas.add(disciplina);
    }

    @Test
    @DisplayName("Gera grades se não houver conflitos")
    void geraGradeSemConflitos() {
        adicionarDisciplinaComTurma("INF01120", "TCP", "A", "Karina Kohl", 8, 10, DiaSemana.SEGUNDA);
        adicionarDisciplinaComTurma("INF05516", "Semantica Formal", "A", "Alvaro Moreira", 10, 12, DiaSemana.SEGUNDA);

        GeradorDeGrades gerador = new GeradorDeGrades(disciplinas, preferencias);
        gerador.gerarGrades();

        List<Grade> grades = gerador.getGrades();

        assertEquals(1, grades.size());
        assertFalse(grades.isEmpty(), "Deve gerar pelo menos uma grade válida.");
        assertEquals(2, grades.get(0).getTurmasSelecionadas().size(), "A grade deve conter 2 disciplinas.");
    }

    @Test
    @DisplayName("Não gera grades se houver conflitos")
    void BloquearGradeConflito() {
        adicionarDisciplinaComTurma("INF01120", "TCP", "A", "Karina Kohl", 8, 10, DiaSemana.SEGUNDA);
        adicionarDisciplinaComTurma("INF05516", "Semantica Formal", "A", "Alvaro Moreira", 9, 11, DiaSemana.SEGUNDA);

        GeradorDeGrades gerador = new GeradorDeGrades(disciplinas, preferencias);
        gerador.gerarGrades();

        List<Grade> grades = gerador.getGrades();

        assertEquals(0, grades.size());
        assertTrue(grades.isEmpty(), "Não deve gerar nenhuma grade válida devido a conflitos.");
    }

    @Test
    @DisplayName("Gera grades respeitando horários bloqueados")
    void respeitaHorariosBloqueados() {
        adicionarDisciplinaComTurma("INF01120", "TCP", "A", "Karina Kohl", 8, 10, DiaSemana.SEGUNDA);
        adicionarDisciplinaComTurma("INF05516", "Semantica Formal", "A", "Alvaro Moreira", 10, 12, DiaSemana.SEGUNDA);

        Horario horarioBloqueado = new Horario(LocalTime.of(8, 0), LocalTime.of(10, 0), DiaSemana.SEGUNDA);
        preferencias.adicionarHorarioBloqueado(horarioBloqueado);

        GeradorDeGrades gerador = new GeradorDeGrades(disciplinas, preferencias);
        gerador.gerarGrades();

        List<Grade> grades = gerador.getGrades();

        assertEquals(0, grades.size());
        assertTrue(grades.isEmpty(), "Não deve gerar nenhuma grade válida devido ao horário bloqueado.");
    }

    @Test
    @DisplayName("Evita professores indesejados")
    void evitaProfessoresIndesejados() {
        adicionarDisciplinaComTurma("INF01120", "TCP", "A", "Karina Kohl", 8, 10, DiaSemana.SEGUNDA);
        adicionarDisciplinaComTurma("INF05516", "Semantica Formal", "A", "Professor Ficticio", 10, 12, DiaSemana.SEGUNDA);

        preferencias.adicionarProfessorEvitado("Professor Ficticio");

        GeradorDeGrades gerador = new GeradorDeGrades(disciplinas, preferencias);
        gerador.gerarGrades();

        List<Grade> grades = gerador.getGrades();

        assertEquals(0, grades.size());
        assertTrue(grades.isEmpty(), "Não deve gerar nenhuma grade válida devido ao professor evitado.");
    }
}
