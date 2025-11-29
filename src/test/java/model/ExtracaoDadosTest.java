package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class ExtracaoDadosTest {

    @TempDir
    Path diretorioTemporario;

    @Test
    @DisplayName("Deve ler arquivo CSV corretamente e agrupar as turmas na mesma disciplina")
    void lerArquivoCSVSucesso() throws IOException {

        File arquivoCsv = diretorioTemporario.resolve("teste.csv").toFile();
        
        String conteudo = 
            "COD,NOME,CREDITOS,TURMA,PROF,SALA,VAGAS,DIA,INICIO,FIM\n" +
            "INF01120,TCP,4,A,Karina Kohl,108,30,SEGUNDA,08:30,10:10\n" +
            "INF01120,TCP,4,A,Karina Kohl,108,30,QUARTA,08:30,10:10";
        
        Files.writeString(arquivoCsv.toPath(), conteudo);

        ExtracaoDados extrator = new ExtracaoDados();
        List<Disciplina> disciplinas = extrator.carregarDisciplinas(arquivoCsv.getAbsolutePath());

        assertFalse(disciplinas.isEmpty());
        assertEquals(1, disciplinas.size(), "Deve agrupar as 2 linhas em 1 disciplina");
        
        Disciplina disciplina = disciplinas.get(0);
        assertEquals("INF01120", disciplina.getCodigo());
        assertEquals("TCP", disciplina.getNome());
        
        assertEquals(1, disciplina.getTurmas().size(), "Deve ser 1 turma com 2 horarios");
        assertEquals(2, disciplina.getTurmas().get(0).getHorarios().size(), "A turma deve ter 2 horarios");
    }

    @Test
    @DisplayName("Deve retornar lista vazia se arquivo nao existe")
    void arquivoInexistente() {
        ExtracaoDados extrator = new ExtracaoDados();
        List<Disciplina> resultado = extrator.carregarDisciplinas("caminho/que/nao/existe.csv");
        
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}