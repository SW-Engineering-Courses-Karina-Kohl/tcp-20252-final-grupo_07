package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtracaoDados {
    private static final Logger logger = LogManager.getLogger(ExtracaoDados.class);

    public List<Disciplina> carregarDisciplinas(String caminhoArquivo) {
        logger.info("Iniciando leitura do arquivo CSV: {}", caminhoArquivo);
        
        // mapa para agrupar linhas do mesmo código na mesma Disciplina
        Map<String, Disciplina> mapaDisciplinas = new HashMap<>();

        File arquivo = new File(caminhoArquivo);
        
        if (!arquivo.exists()) {
            logger.fatal("Arquivo CSV não encontrado: {}", caminhoArquivo);
            return new ArrayList<>(); //retorna lista vazia
        }

        try (Scanner scanner = new Scanner(arquivo)) {
           //pula o cabeçalho
            if (scanner.hasNextLine()) scanner.nextLine();

            int linhaAtual = 1;

            while (scanner.hasNextLine()) {
                linhaAtual++;
                String linha = scanner.nextLine();
                
                //divide linha por virgula
                String[] dados = linha.split(",");

                //validacao
                if (dados.length < 10) {
                    logger.warn("Linha {} ignorada pois está incompleta: {}", linhaAtual, linha);
                    continue;
                }

                try {
                    String codDisc = dados[0].trim();
                    String nomeDisc = dados[1].trim();
                    int creditos = Integer.parseInt(dados[2].trim());
                    
                    String codTurma = dados[3].trim();
                    String nomeProf = dados[4].trim();
                    String sala = dados[5].trim();
                    int vagas = Integer.parseInt(dados[6].trim());
                    
                    String diaStr = dados[7].trim().toUpperCase();
                    LocalTime inicio = LocalTime.parse(dados[8].trim());
                    LocalTime fim = LocalTime.parse(dados[9].trim());
                    
                    Disciplina disciplina = mapaDisciplinas.get(codDisc);
                    if (disciplina == null) {
                        disciplina = new Disciplina(codDisc, nomeDisc, creditos);
                        mapaDisciplinas.put(codDisc, disciplina);
                    }
                    //verifica se a turma já existe dentro dessa disciplina
                    Turma turma = null;
                    for (Turma t : disciplina.getTurmas()) {
                        if (t.getCodigo().equalsIgnoreCase(codTurma)) {
                            turma = t;
                            break;
                        }
                    }

                    // se a turma não existe, cria. se existe adiciona horario novo
                    if (turma == null) {
                        Professor professor = new Professor(nomeProf);
                        turma = new Turma(codTurma, professor, disciplina, vagas, sala);
                        disciplina.adicionarTurma(turma);
                    }

                    // adiciona o horário
                    DiaSemana dia = DiaSemana.valueOf(diaStr);
                    turma.addHorario(new Horario(inicio, fim, dia));

                } catch (Exception e) {
                    logger.error("Erro ao processar linha {}: {}", linhaAtual, linha, e);
                }
            }
            
            logger.info("Leitura concluída. {} disciplinas carregadas com sucesso.", mapaDisciplinas.size());

        } catch (FileNotFoundException e) {
            logger.error("Erro ao abrir arquivo.", e);
        }

        return new ArrayList<>(mapaDisciplinas.values());
    }
}