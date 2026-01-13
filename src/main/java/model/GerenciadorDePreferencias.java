package model;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Le o arquivo preferencias.csv e armazena os dados lidos OU escreve preferencias no arquivo
public class GerenciadorDePreferencias {

    private static final Logger logger = LogManager.getLogger(GerenciadorDePreferencias.class);

    //numero de linhas do cabeçalho
    private static final int TAM_HEADER = 5;


    public Preferencias carregarPreferencias(String caminhoArquivo, List<Turma> turmas) {
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

            //turno preferido
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

            //turmas preferidas
            if (scanner.hasNextLine()) { //esta pegando todas as turmas em vez das turmas 
                String linhaProfsPref = scanner.nextLine().trim();
                if (!linhaProfsPref.isBlank()) {
                    String[] stringsTurmas = linhaProfsPref.split(",");
                    for (String stringTurma : stringsTurmas) {
                        for (Turma t : turmas){
                            String labelTurma = t.getDisciplina().getNome() + ": " + t.getCodigo() + " - " + t.getProfessor();

                            if (stringTurma.equals(labelTurma)) {
                                preferencias.adicionarTurmaPreferida(t);
                            }
                        }
                    }
                }
            }

            //turmas descartadas
            if (scanner.hasNextLine()) {
                String linhaProfsDesc = scanner.nextLine().trim();
                if (!linhaProfsDesc.isBlank()) {
                    String[] stringsTurmas = linhaProfsDesc.split(",");
                    for (String stringTurma : stringsTurmas) {
                        for (Turma t : turmas){
                            String labelTurma = t.getDisciplina().getNome() + ": " + t.getCodigo() + " - " + t.getProfessor();
                            
                            if (stringTurma.equals(labelTurma)) {
                                preferencias.adicionarTurmaDescartada(t);
                            }
                        }
                    }
                }
            }

            //horarios bloqueados
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

            //numero de disciplinas
            if (scanner.hasNextLine()) {
                String linhaNum = scanner.nextLine().trim();
                if (!linhaNum.isBlank()) {
                    try {
                        int num = Integer.parseInt(linhaNum);
                        preferencias.setNumeroDisciplinas(num);
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
            writer.println("2 - Turmas Preferidas");
            writer.println("3 - Turmas Descartadas");
            writer.println("4 - Horários bloqueados");
            writer.println("5 - Numero de Disciplinas");

            if (preferencias.getTurnoPreferido() != null) {
                writer.println(preferencias.getTurnoPreferido().name());
            } else {
                writer.println("");
            }

            //escreve turmas preferidas
            List <String> strTurmasPref = new ArrayList<>();
            for (Turma t : preferencias.getTurmasPreferidas()){
                String labelTurmaPref = t.getDisciplina().getNome() + ": " + t.getCodigo() + " - " + t.getProfessor();
                strTurmasPref.add(labelTurmaPref);
            }
            if(strTurmasPref.isEmpty())
                writer.println("");
            else
                writer.println(String.join(", ", strTurmasPref));


            //escreve turmas descartadas
            List <String> strTurmasDesc = new ArrayList<>();
            for (Turma t : preferencias.getTurmasDescartadas()){
                String labelTurmaDesc = t.getDisciplina().getNome() + ": " + t.getCodigo() + " - " + t.getProfessor();
                strTurmasDesc.add(labelTurmaDesc);
            }
            if(strTurmasDesc.isEmpty())
                writer.println("");
            else
                writer.println(String.join(", ", strTurmasDesc));

            //escreve horarios bloqueados
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

            writer.println(preferencias.getNumeroDisciplinas());

            logger.info("Preferências salvas com sucesso no arquivo '{}'.", caminhoArquivo);

        } catch (IOException e) {
            logger.error("Erro ao salvar arquivo de preferências '{}'.", caminhoArquivo, e);
        }
    }
}
