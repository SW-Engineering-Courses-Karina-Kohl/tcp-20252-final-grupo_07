package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class DisciplinaTest {
    
    @Test
    @DisplayName("Cria disciplina com sucesso")
    void criarDisciplina() {
        Disciplina disciplina = new Disciplina("INF01120", "TCP", 4);
        assertEquals("INF01120", disciplina.getCodigo(), "O codigo da disciplina deve ser INF01120");
        assertEquals("TCP", disciplina.getNome(), "O nome da disciplina deve ser TCP");
        assertEquals(4, disciplina.getCreditos(), "Os creditos da disciplina devem ser 4");
        assertNotNull(disciplina.getTurmas());
        assertTrue(disciplina.getTurmas().isEmpty(), "A lista de turmas deve estar vazia ao criar a disciplina");
    }

    @Test
    @DisplayName("Lanca excecao ao criar disciplina com codigo, nome ou creditos nulos")
    void criarDisciplinaNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Disciplina(null, "TCP", 4);
        }, "Deve lançar IllegalArgumentException ao criar disciplina com codigo nulo");
        assertThrows(IllegalArgumentException.class, () -> {
            new Disciplina("INF01120", null, 4);
        }, "Deve lançar IllegalArgumentException ao criar disciplina com nome nulo");
        assertThrows(IllegalArgumentException.class, () -> {
            new Disciplina("INF01120", "TCP", 0);
        }, "Deve lançar IllegalArgumentException ao criar disciplina com creditos nulos");
        assertThrows(IllegalArgumentException.class, () -> {
            new Disciplina("INF01120", "TCP", -3);
        }, "Deve lançar IllegalArgumentException ao criar disciplina com creditos negativos");
    }

    @Test
    @DisplayName("Adiciona turma com sucesso")
    void adicionarTurma() { 
        Disciplina disciplina = new Disciplina("INF01120", "TCP", 4);
        Professor professor = new Professor("Karina Kohl");
        Turma turma = new Turma("A", professor, disciplina, 30, "108");
        disciplina.adicionarTurma(turma);
        assertEquals(1, disciplina.getTurmas().size(), "A lista de turmas deve conter 1 turma apos a adicao");
        assertTrue(disciplina.getTurmas().contains(turma), "A lista de turmas deve conter a turma adicionada");
    }
}
