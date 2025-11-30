package model;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GerenciadorDePreferencias {

    private static final Logger logger = LogManager.getLogger(GerenciadorDePreferencias.class);

    //numero de linhas do cabeçalho
    private static final int TAM_HEADER = 4;

    public Preferencias carregarPreferencias(String caminhoArquivo) {
        File arquivo = new File(caminhoArquivo);
        Preferencias preferencias = new Preferencias();

        if (!arquivo.exists()) {
            logger.info("Arquivo de preferências '{}' não encontrado. Usando valores padrão.", caminhoArquivo);
            return preferencias;
        }

        logger.info("Carregando preferências a partir do arquivo '{}'.", caminhoArquivo);

        try (Scanner scanner = new Scanner(arquivo)) {

            //cabeçalho
            for (int i = 0; i < TAM_HEADER && scanner.hasNextLine(); i++) {
                scanner.nextLine();
            }

            //linha 5 - turno preferido
            if (scanner.hasNextLine()) {
                String linhaTurno = scanner.nextLine().trim();
                if (!linhaTurno.isBlank()) {
                    try {
                        preferencias.setTurnoPreferido(Turno.valueOf(linhaTurno.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        logger.warn("Turno inválido no arquivo de preferências: '{}'. Ignorando.", linhaTurno);
                    }
                }
            }

            //linha 6 - professores preferidos
            if (scanner.hasNextLine()) {
                String linhaProfsPref = scanner.nextLine().trim();
                if (!linhaProfsPref.isBlank()) {
                    String[] nomes = linhaProfsPref.split(",");
                    for (String nome : nomes) {
                        String limpo = nome.trim();
                        if (!limpo.isEmpty()) {
                            preferencias.adicionarProfessorPreferido(limpo);
                        }
                    }
                }
            }

            //linha 7 - professores evitados
            if (scanner.hasNextLine()) {
                String linhaProfsEv = scanner.nextLine().trim();
                if (!linhaProfsEv.isBlank()) {
                    String[] nomes = linhaProfsEv.split(",");
                    for (String nome : nomes) {
                        String limpo = nome.trim();
                        if (!limpo.isEmpty()) {
                            preferencias.adicionarProfessorEvitado(limpo);
                        }
                    }
                }
            }

            // linha 8 - horarios bloqueados
            if (scanner.hasNextLine()) {
                String linhaHorarios = scanner.nextLine().trim();
                if (!linhaHorarios.isBlank()) {
                    String[] dados = linhaHorarios.split(",");

                    if (dados.length % 3 == 0) {
                        for (int i = 0; i < dados.length; i += 3) {
                            String strInicio = dados[i].trim();
                            String strFim = dados[i + 1].trim();
                            String strDia = dados[i + 2].trim().toUpperCase();

                            try {
                                LocalTime inicio = LocalTime.parse(strInicio);
                                LocalTime fim = LocalTime.parse(strFim);
                                DiaSemana dia = DiaSemana.valueOf(strDia);
                                preferencias.adicionarHorarioBloqueado(
                                        new Horario(inicio, fim, dia)
                                );
                            } catch (Exception e) {
                                logger.warn("Horário inválido nas preferências: '{}', '{}', '{}'. Ignorando.",
                                        strInicio, strFim, strDia);
                            }
                        }
                    } else {
                        logger.warn("Linha de horários mal formatada no arquivo de preferências: '{}'.", linhaHorarios);
                    }
                }
            }

            // linha 9 - numero de disciplinas
            if (scanner.hasNextLine()) {
                String linhaNum = scanner.nextLine().trim();
                if (!linhaNum.isBlank()) {
                    try {
                        int num = Integer.parseInt(linhaNum);
                        preferencias.setNumeroCadeiras(num);
                    } catch (NumberFormatException e) {
                        logger.warn("Número de disciplinas inválido nas preferências: '{}'. Mantendo valor padrão.",
                                linhaNum);
                    }
                }
            }

            logger.info("Preferências carregadas com sucesso do arquivo '{}'.", caminhoArquivo);

        } catch (FileNotFoundException e) {
            logger.error("Erro ao abrir arquivo de preferências '{}'.", caminhoArquivo, e);
        }

        return preferencias;
    }

    public void salvarPreferencias(String caminhoArquivo, Preferencias preferencias) {
        logger.info("Salvando preferências no arquivo '{}'.", caminhoArquivo);

        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {

            //cabeçalho
            writer.println("1 - Turno Preferido");
            writer.println("2 - Professores Preferidos");
            writer.println("3 - Professores Evitados");
            writer.println("4 - Numero de Disciplinas");

            if (preferencias.getTurnoPreferido() != null) {
                writer.println(preferencias.getTurnoPreferido().name());
            } else {
                writer.println("");
            }

            writer.println(String.join(", ", preferencias.getProfessoresPreferidos()));

            writer.println(String.join(", ", preferencias.getProfessoresEvitados()));

            List<Horario> horarios = preferencias.getHorariosBloqueados();
            if (horarios.isEmpty()) {
                writer.println("");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < horarios.size(); i++) {
                    Horario h = horarios.get(i);
                    sb.append(h.getInicio())
                      .append(", ")
                      .append(h.getFim())
                      .append(", ")
                      .append(h.getDiaSemana().name());
                    if (i < horarios.size() - 1) {
                        sb.append(", ");
                    }
                }
                writer.println(sb.toString());
            }

            writer.println(preferencias.getNumeroCadeiras());

            logger.info("Preferências salvas com sucesso no arquivo '{}'.", caminhoArquivo);

        } catch (IOException e) {
            logger.error("Erro ao salvar arquivo de preferências '{}'.", caminhoArquivo, e);
        }
    }
}
