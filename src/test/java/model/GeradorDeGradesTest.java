package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeradorDeGradesTest {

    private static final Logger logger = LogManager.getLogger(GeradorDeGradesTest.class);

    private List<Disciplina> disciplinas;
    private Preferencias preferencias;

    @BeforeEach
    void setup() {
        disciplinas = new ArrayList<>();
        preferencias = new Preferencias();
        preferencias.setNumeroCadeiras(2);

        logger.info("Setup inicial feito para o teste.");
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
        logger.info("Teste: Gerar grade sem conflitos.");

        adicionarDisciplinaComTurma("INF01120", "TCP", "A", "Karina Kohl", 8, 10, DiaSemana.SEGUNDA);
        adicionarDisciplinaComTurma("INF05516", "Semantica Formal", "A", "Alvaro Moreira", 10, 12, DiaSemana.SEGUNDA);

        GeradorDeGrades gerador = new GeradorDeGrades(disciplinas, preferencias);
        gerador.gerarGrades();

        List<Grade> grades = gerador.getGrades();

        assertEquals(1, grades.size());
        assertFalse(grades.isEmpty(), "Deve gerar pelo menos uma grade válida.");
        assertEquals(2, grades.get(0).getTurmasSelecionadas().size(), "A grade deve conter 2 disciplinas.");

        logger.info("Sucesso: Grade gerada com 2 turmas.");
    }

    @Test
    @DisplayName("Não gera grades se houver conflitos")
    void BloquearGradeConflito() {
        logger.info("Teste: Bloquear grade com conflitos de horário.");

        adicionarDisciplinaComTurma("INF01120", "TCP", "A", "Karina Kohl", 8, 10, DiaSemana.SEGUNDA);
        adicionarDisciplinaComTurma("INF05516", "Semantica Formal", "A", "Alvaro Moreira", 9, 11, DiaSemana.SEGUNDA);

        GeradorDeGrades gerador = new GeradorDeGrades(disciplinas, preferencias);
        gerador.gerarGrades();

        List<Grade> grades = gerador.getGrades();

        assertEquals(0, grades.size());
        assertTrue(grades.isEmpty(), "Não deve gerar nenhuma grade válida devido a conflitos.");

        logger.info("Sucesso: Nenhuma grade gerada pois houve conflito.");
    }

    @Test
    @DisplayName("Gera grades respeitando horários bloqueados")
    void respeitaHorariosBloqueados() {
        logger.info("Teste: Bloqueio de horario definido pelo usuario.");

        adicionarDisciplinaComTurma("INF01120", "TCP", "A", "Karina Kohl", 8, 10, DiaSemana.SEGUNDA);
        adicionarDisciplinaComTurma("INF05516", "Semantica Formal", "A", "Alvaro Moreira", 10, 12, DiaSemana.SEGUNDA);

        Horario horarioBloqueado = new Horario(LocalTime.of(8, 0), LocalTime.of(10, 0), DiaSemana.SEGUNDA);
        preferencias.adicionarHorarioBloqueado(horarioBloqueado);

        GeradorDeGrades gerador = new GeradorDeGrades(disciplinas, preferencias);
        gerador.gerarGrades();

        List<Grade> grades = gerador.getGrades();

        assertEquals(0, grades.size());
        assertTrue(grades.isEmpty(), "Não deve gerar nenhuma grade válida devido ao horário bloqueado.");

        logger.info("Sucesso: Grade bloqueada pela restricao de horario do usuario.");
    }

    @Test
    @DisplayName("Evita professores indesejados")
    void evitaProfessoresIndesejados() {
        logger.info("Teste: Restricao de professor evitado.");

        adicionarDisciplinaComTurma("INF01120", "TCP", "A", "Karina Kohl", 8, 10, DiaSemana.SEGUNDA);
        adicionarDisciplinaComTurma("INF05516", "Semantica Formal", "A", "Professor Ficticio", 10, 12, DiaSemana.SEGUNDA);

        preferencias.adicionarProfessorEvitado("Professor Ficticio");

        GeradorDeGrades gerador = new GeradorDeGrades(disciplinas, preferencias);
        gerador.gerarGrades();

        List<Grade> grades = gerador.getGrades();

        assertEquals(0, grades.size());
        assertTrue(grades.isEmpty(), "Não deve gerar nenhuma grade válida devido ao professor evitado.");

        logger.info("Sucesso: Grade bloqueada por conter professor evitado.");
    }
}
