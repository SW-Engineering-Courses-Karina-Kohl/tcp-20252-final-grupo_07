package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Logger;

public class DisciplinaTest {

    private static final Logger logger = Logger.getLogger(DisciplinaTest.class.getName());

    @Test
    @DisplayName("Cria disciplina com sucesso")
    void criarDisciplina() {
        logger.info("Iniciando teste: criarDisciplina()");

        Disciplina disciplina = new Disciplina("INF01120", "TCP", 4);
        logger.info("Disciplina criada com sucesso: " + disciplina.getCodigo() + " - " + disciplina.getNome());

        assertEquals("INF01120", disciplina.getCodigo(), "O codigo da disciplina deve ser INF01120");
        assertEquals("TCP", disciplina.getNome(), "O nome da disciplina deve ser TCP");
        assertEquals(4, disciplina.getCreditos(), "Os creditos da disciplina devem ser 4");

        assertNotNull(disciplina.getTurmas());
        logger.info("Lista de turmas iniciada corretamente.");

        assertTrue(disciplina.getTurmas().isEmpty(), "A lista de turmas deve estar vazia ao criar a disciplina");

        logger.info("Teste criarDisciplina() finalizado com sucesso.");
    }

    @Test
    @DisplayName("Lanca excecao ao criar disciplina com codigo, nome ou creditos nulos")
    void criarDisciplinaNull() {
        logger.info("Iniciando teste: criarDisciplinaNull()");

        assertThrows(IllegalArgumentException.class, () -> {
            logger.warning("Tentando criar disciplina com codigo nulo.");
            new Disciplina(null, "TCP", 4);
        }, "Deve lançar IllegalArgumentException ao criar disciplina com codigo nulo");

        assertThrows(IllegalArgumentException.class, () -> {
            logger.warning("Tentando criar disciplina com nome nulo.");
            new Disciplina("INF01120", null, 4);
        }, "Deve lançar IllegalArgumentException ao criar disciplina com nome nulo");

        assertThrows(IllegalArgumentException.class, () -> {
            logger.warning("Tentando criar disciplina com créditos 0.");
            new Disciplina("INF01120", "TCP", 0);
        }, "Deve lançar IllegalArgumentException ao criar disciplina com creditos nulos");

        assertThrows(IllegalArgumentException.class, () -> {
            logger.warning("Tentando criar disciplina com créditos negativos.");
            new Disciplina("INF01120", "TCP", -3);
        }, "Deve lançar IllegalArgumentException ao criar disciplina com creditos negativos");

        logger.info("Teste criarDisciplinaNull() finalizado com sucesso.");
    }

    @Test
    @DisplayName("Adiciona turma com sucesso")
    void adicionarTurma() {
        logger.info("Iniciando teste: adicionarTurma()");

        Disciplina disciplina = new Disciplina("INF01120", "TCP", 4);
        Professor professor = new Professor("Karina Kohl");
        Turma turma = new Turma("A", professor, disciplina, 30, "108");

        logger.info("Criando e adicionando turma: codigo = " + turma.getCodigo());

        disciplina.adicionarTurma(turma);

        assertEquals(1, disciplina.getTurmas().size(), "A lista de turmas deve conter 1 turma apos a adicao");
        assertTrue(disciplina.getTurmas().contains(turma), "A lista de turmas deve conter a turma adicionada");

        logger.info("Turma adicionada com sucesso à disciplina " + disciplina.getCodigo());
        logger.info("Teste adicionarTurma() finalizado com sucesso.");
    }
}
