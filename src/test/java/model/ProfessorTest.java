package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class ProfessorTest {
    private Professor professor;
    private static final Logger logger = LogManager.getLogger(ProfessorTest.class);
    @BeforeEach
    void setup() {
        professor = new Professor("Karina Kohl");
    }

    @Test
    @DisplayName("Deve criar um professor com nome correto")
    void criaProfessor() {
        logger.info("Teste: Criar professor com nome valido");

        assertEquals("Karina Kohl", professor.getNome(), "O nome do professor deve ser 'Karina Kohl'");

        logger.info("Sucesso: Professor criado com nome valido");
    }

    @Test
    @DisplayName("Deve lançar exceção para nome nulo ou vazio")
    void nomeInvalido() {
        logger.info("Teste: Criar professor com nome nulo ou vazio");

        assertThrows(IllegalArgumentException.class, () -> {
            new Professor(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Professor("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Professor("   ");
        });

        logger.info("Sucesso: Excecao lancada para nome nulo ou vazio");
    }
}
