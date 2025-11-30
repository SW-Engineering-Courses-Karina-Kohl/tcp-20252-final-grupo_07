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

        Map<String, Disciplina> mapaDisciplinas = new HashMap<>();

        File arquivo = new File(caminhoArquivo);

        if (!arquivo.exists()) {
            logger.fatal("Arquivo CSV não encontrado: {}", caminhoArquivo);
            return new ArrayList<>();
        }

        try (Scanner scanner = new Scanner(arquivo)) {
            // pula cabeçalho, se existir
            if (scanner.hasNextLine()) scanner.nextLine();

            int linhaAtual = 1;

            while (scanner.hasNextLine()) {
                linhaAtual++;
                String linha = scanner.nextLine();

                String[] dados = linha.split(",");

                //exigido minimamente codDisc, nomeDisc, creditos, codTurma, dia, inicio, fim
                if (dados.length < 8) {
                    logger.warn("Linha {} ignorada pois está incompleta: {}", linhaAtual, linha);
                    continue;
                }

                try {
                    String codDisc  = getCampoSeguro(dados, 0, "SEM_CODIGO");
                    String nomeDisc = getCampoSeguro(dados, 1, "Disciplina sem nome");
                    int creditos    = parseIntSeguro(dados, 2, 0);

                    String codTurma = getCampoSeguro(dados, 3, "A");
                    String nomeProf = getCampoSeguro(dados, 4, "Professor indefinido");
                    String sala     = getCampoSeguro(dados, 5, "Sala não definida");
                    int vagas       = parseIntSeguro(dados, 6, 0);

                    String diaStr    = getCampoSeguro(dados, 7, null);
                    String inicioStr = getCampoSeguro(dados, 8, null);
                    String fimStr    = getCampoSeguro(dados, 9, null);

                    if (diaStr == null || inicioStr == null || fimStr == null) {
                        logger.warn("Linha {} ignorada (sem dia/inicio/fim): {}", linhaAtual, linha);
                        continue;
                    }

                    LocalTime inicio = LocalTime.parse(inicioStr.trim());
                    LocalTime fim    = LocalTime.parse(fimStr.trim());

                    Disciplina disciplina = mapaDisciplinas.get(codDisc);
                    if (disciplina == null) {
                        disciplina = new Disciplina(codDisc, nomeDisc, creditos);
                        mapaDisciplinas.put(codDisc, disciplina);
                    }

                    // procura turma já existente
                    Turma turma = null;
                    for (Turma t : disciplina.getTurmas()) {
                        if (t.getCodigo().equalsIgnoreCase(codTurma)) {
                            turma = t;
                            break;
                        }
                    }

                    if (turma == null) {
                        Professor professor = new Professor(nomeProf);
                        turma = new Turma(codTurma, professor, disciplina, vagas, sala);
                        disciplina.adicionarTurma(turma);
                    }

                    try {
                        DiaSemana dia = DiaSemana.valueOf(diaStr.trim().toUpperCase());
                        turma.addHorario(new Horario(inicio, fim, dia));
                    } catch (IllegalArgumentException ex) {
                        logger.warn("Dia da semana inválido na linha {}: {}", linhaAtual, diaStr);
                    }

                } catch (Exception e) {
                    logger.error("Erro ao processar linha {}: {}", linhaAtual, linha, e);
                }
            }

            logger.info("Leitura concluída. {} disciplinas carregadas com sucesso.",
                    mapaDisciplinas.size());

        } catch (FileNotFoundException e) {
            logger.error("Erro ao abrir arquivo.", e);
        }

        return new ArrayList<>(mapaDisciplinas.values());
    }


    //tratamento para csv com campos faltando ou mal formatados
    private String getCampoSeguro(String[] linha, int indice, String padrao) {
        if (indice >= linha.length) return padrao;

        String valor = linha[indice].trim();
        if (valor.isEmpty()) return padrao;

        return valor;
    }

    private int parseIntSeguro(String[] linha, int indice, int padrao) {
        if (indice >= linha.length) return padrao;

        try {
            return Integer.parseInt(linha[indice].trim());
        } catch (NumberFormatException e) {
            return padrao;
        }
    }
}