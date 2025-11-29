package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class ProfessorTest {
    private Professor professor;

    @BeforeEach
    void setup() {
        professor = new Professor("Karina Kohl");
    }

    @Test
    @DisplayName("Deve criar um professor com nome correto")
    void criaProfessor() {
        assertEquals("Karina Kohl", professor.getNome(), "O nome do professor deve ser 'Karina Kohl'");
    }

    @Test
    @DisplayName("Deve lançar exceção para nome nulo ou vazio")
    void nomeInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Professor(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Professor("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Professor("   ");
        });
    }
}
