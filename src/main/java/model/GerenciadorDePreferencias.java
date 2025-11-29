package model;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.List;

public class GerenciadorDePreferencias {
    private final int TAM_HEADER = 4;

   public Preferencias carregarPreferencias(String caminhoArquivo){
        File arquivo = new File(caminhoArquivo);

        Preferencias preferencias = new Preferencias();
        if (!arquivo.exists()){
            System.out.println("Arquivo não encontrado. Retornando preferências padrão.");
            return new Preferencias();
        }

        try(Scanner scanner = new Scanner(arquivo)){
            int indexLinha = 0;

            //pula o header do arquivo
            while (scanner.hasNextLine() && indexLinha < TAM_HEADER){ 
                scanner.nextLine();
                indexLinha++;
            }
            
            //reseta indice
            indexLinha = 0;
            while(scanner.hasNextLine()){
                String linha = scanner.nextLine();

                // Se a linha estiver vazia, pula para evitar erros
                if (linha.trim().isEmpty()) continue;

                //divide linha por virgula
                String[] dados = linha.split(",");

                switch (indexLinha){
                    case 0: //Turno preferido
                        String strTurno = dados[0].trim();
                        preferencias.setTurnoPreferido(Turno.valueOf((strTurno)));
                    break;

                    case 1: //Professores Preferidos
                        for(String nome : dados){
                            preferencias.adicionarProfessorPreferido(nome);
                        }
                    break;

                    case 2: //Professores Evitados
                        for(String nome : dados){
                            preferencias.adicionarProfessorEvitado(nome);
                        }
                    break;

                    case 3: //Horarios evitados
                        if (dados.length % 3 == 0){ //verifica se linha de horarios esta bem formatada
                            for (int i = 0; i < (dados.length/3); i++){
                                String strHoraInicio = dados[i].trim();
                                String strHoraFim = dados[i+1].trim();
                                String strDiaSemana = dados[i+2].trim();

                                LocalTime inicio = LocalTime.parse(strHoraInicio);
                                LocalTime fim = LocalTime.parse(strHoraFim);
                                DiaSemana diaSemana = DiaSemana.valueOf(strDiaSemana);

                                Horario horario = new Horario(inicio, fim, diaSemana);
                                preferencias.adicionarHorarioBloqueado(horario);
                            }
                        }
                    break;

                    case 4: //Numero de cadeiras
                        String strNumCadeiras = dados[0].trim();
                        preferencias.setnumeroDisciplinas(Integer.parseInt(strNumCadeiras));
                    break;
                }
                indexLinha++;
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return preferencias;
    }

    public void salvarPreferencias(String caminhoArquivo, Preferencias preferencias){
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
            writer.println("1 - Turno Preferido");
            writer.println("2 - Professores Preferidos");
            writer.println("3 - Professores Evitados");
            writer.println("4 - Numero de Disciplinas");
            writer.println(preferencias.getTurnoPreferido());

            String linhaProfsPreferidos = String.join(",", preferencias.getProfessoresPreferidos());
            writer.println(linhaProfsPreferidos);

            String linhaProfsEvitados = String.join(",", preferencias.getProfessoresEvitados());
            writer.println(linhaProfsEvitados);

            StringBuilder sbHorarios = new StringBuilder();
            List<Horario> horarios = preferencias.getHorariosBloqueados();

            for(int i = 0; i < horarios.size(); i++){
                Horario horario = horarios.get(i);
                sbHorarios.append(horario.getInicio()).append(",");
                sbHorarios.append(horario.getFim()).append(",");
                sbHorarios.append(horario.getDiaSemana());

                if(i < horarios.size() - 1){
                    sbHorarios.append(",");
                }
                writer.println(sbHorarios);

                String linhaNumDisciplinas = String.valueOf(preferencias.getnumeroDisciplinas());
                writer.println(linhaNumDisciplinas);
            }


        }catch(IOException e){
            System.err.println("Erro ao salvar arquivo CSV: " + e.getMessage());
        }
    }
}
