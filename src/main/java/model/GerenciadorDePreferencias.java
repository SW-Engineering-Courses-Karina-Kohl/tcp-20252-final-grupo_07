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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Le o arquivo preferencias.csv e armazena os dados lidos OU escreve preferencias no arquivo
public class GerenciadorDePreferencias {
    private static final int LINHA_TURNO = 6;
    private static final int LINHA_TURMAS_PREF = 7;
    private static final int LINHA_TURMAS_DESC = 8;
    private static final int LINHA_HORARIOS = 9;
    private static final int LINHA_NUM_DISC = 10;

    private static final Logger logger = LogManager.getLogger(GerenciadorDePreferencias.class);

    //numero de linhas do cabeçalho
    private static final int TAM_HEADER = 6;


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
                        stringTurma = stringTurma.trim();
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
                        stringTurma = stringTurma.trim();
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

    
    private void atualizarArquivo(String caminhoArquivo, int linhaAlvo, String novoConteudo) {
        try {
            List<String> linhas;
            
            // 1. Tenta ler o arquivo existente
            if (Files.exists(Paths.get(caminhoArquivo))) {
                linhas = Files.readAllLines(Paths.get(caminhoArquivo), StandardCharsets.UTF_8);
            } else {
                linhas = new ArrayList<>();
            }

            // 2. Garante que o arquivo tenha linhas suficientes (preenche com vazio se for curto)
            // Precisamos garantir que exista até a linhaAlvo + cabeçalho
            while (linhas.size() <= linhaAlvo) {
                linhas.add(""); 
            }

            // 3. Reconstrói o cabeçalho se o arquivo for novo ou estiver vazio
            if (linhas.size() < TAM_HEADER || linhas.get(0).isEmpty()) {
                linhas.set(0, "1 - Turno Preferido");
                linhas.set(1, "2 - Turmas Preferidas");
                linhas.set(2, "3 - Turmas Descartadas");
                linhas.set(3, "4 - Horários bloqueados");
                linhas.set(4, "5 - Numero de Disciplinas");
                linhas.set(5, "6 - Nomes Disciplinas");
            }

            // 4. Atualiza APENAS a linha desejada
            linhas.set(linhaAlvo, novoConteudo);

            // 5. Reescreve o arquivo todo com a lista atualizada
            Files.write(Paths.get(caminhoArquivo), linhas, StandardCharsets.UTF_8);

        } catch (IOException e) {
            logger.error("Erro ao atualizar arquivo '{}'.", caminhoArquivo, e);
        }
    }
    
    //chamada na preferenciasGUI
    public void salvarPreferencias(String caminhoArquivo, Preferencias preferencias) {
        logger.info("Salvando preferências...");

        // Prepara turno preferido
        String strTurno;
        if (preferencias.getTurnoPreferido() != null) 
            strTurno = preferencias.getTurnoPreferido().name();
        
        else 
            strTurno = "";

        //prepara turmas preferidas
        String strTurmasPref;
        if (preferencias.getTurmasPreferidas() != null){
            List <String> listaTurmasPref = new ArrayList<>();
            for (Turma t : preferencias.getTurmasPreferidas()){
                String labelTurmaPref = t.getDisciplina().getNome() + ": " + t.getCodigo() + " - " + t.getProfessor();
                listaTurmasPref.add(labelTurmaPref);
            }
            strTurmasPref = String.join(", ", listaTurmasPref);
        }
        else 
            strTurmasPref = "";

        //prepara turmas descartadas;
        String strTurmasDesc;
        if (preferencias.getTurmasDescartadas() != null){
            List <String> listaTurmasDesc = new ArrayList<>();
            for (Turma t : preferencias.getTurmasDescartadas()){
                String labelTurmaDesc = t.getDisciplina().getNome() + ": " + t.getCodigo() + " - " + t.getProfessor();
                listaTurmasDesc.add(labelTurmaDesc);
            }
            strTurmasDesc = String.join(", ", listaTurmasDesc);
        } 
        else 
            strTurmasDesc = "";

        //prepara horarios bloqueados
        String strHorariosBloq; 
        if(preferencias.getHorariosBloqueados() != null){
            List<Horario> listHorariosBloq = preferencias.getHorariosBloqueados();
            if (listHorariosBloq.isEmpty()) {
                strHorariosBloq = "";
            } 
            else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < listHorariosBloq.size(); i++) {
                    Horario h = listHorariosBloq.get(i);
                    sb.append(h.getInicio())
                        .append(", ")
                        .append(h.getFim())
                        .append(", ")
                        .append(h.getDiaSemana().name());
                    if (i < listHorariosBloq.size() - 1) {
                        sb.append(", ");
                    }
                }
                strHorariosBloq = sb.toString();
            }
        }
        else 
            strHorariosBloq = "";
        
                                    atualizarArquivo(caminhoArquivo, LINHA_TURNO, strTurno);
                                    atualizarArquivo(caminhoArquivo, LINHA_TURMAS_PREF, strTurmasPref);
                                    atualizarArquivo(caminhoArquivo, LINHA_TURMAS_DESC, strTurmasDesc);
                                    atualizarArquivo(caminhoArquivo, LINHA_HORARIOS, strHorariosBloq);
        if (strHorariosBloq != "")  atualizarArquivo(caminhoArquivo, LINHA_NUM_DISC, String.valueOf(preferencias.getNumeroDisciplinas()));        
    }

    //chamada no actionlistener do btn continuar na tela selecao disciplinas
    public void salvarDisciplinas(String caminhoArquivo, List<Disciplina> disciplinas){
        logger.info("Salvando preferências no arquivo '{}'.", caminhoArquivo);

        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {

            //cabeçalho
            writer.println("1 - Turno Preferido");
            writer.println("2 - Turmas Preferidas");
            writer.println("3 - Turmas Descartadas");
            writer.println("4 - Horários bloqueados");
            writer.println("5 - Numero de Disciplinas");
            writer.println("6 - Nomes Disciplinas");

            for (int i = 0; i < TAM_HEADER-1; i++){
                writer.println("");
            }

            List <String> strDisciplinas = new ArrayList<>();
            for (Disciplina d : disciplinas){
                strDisciplinas.add((d.getCodigo() + " - " + d.getNome()).trim());
            }
            if(strDisciplinas.isEmpty())
                writer.println("");
            else
                writer.println(String.join(", ", strDisciplinas));

        } catch (IOException e) {
            logger.error("Erro ao salvar disciplinas no arquivo '{}'.", caminhoArquivo, e);
        }
    }


    public List<String> carregarStrDisciplinas(String caminhoArquivo){
        File arquivo = new File(caminhoArquivo);
        List<String> listDisciplinas = new ArrayList<>();

        if (!arquivo.exists()) {
            logger.info("Arquivo de preferências '{}' não encontrado.", caminhoArquivo);
            return listDisciplinas;
        }

        try (Scanner scanner = new Scanner(arquivo)) {
            //cabeçalho
            for (int i = 0; i < (TAM_HEADER*2)-1 && scanner.hasNextLine(); i++) {
                scanner.nextLine();
            }
            
            if(scanner.hasNextLine()){
                String linhaDisciplinas = scanner.nextLine().trim();
                if(!linhaDisciplinas.isBlank()){
                    String[] stringsDisciplinas = linhaDisciplinas.split(",");   
                    
                    for (String s : stringsDisciplinas){
                        listDisciplinas.add(s.trim());
                    }
                }              
            }
        } catch (IOException e) {
            logger.error("Erro ao ler strings das disciplinas no arquivo '{}'.", caminhoArquivo, e);
        }
        return listDisciplinas;
    }

}
