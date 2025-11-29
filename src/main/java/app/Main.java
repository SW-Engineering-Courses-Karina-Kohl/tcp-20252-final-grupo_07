package app;

import model.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import app.ui.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("testeeee");

        //professores
        Professor profKarina = new Professor("Karina Kohl");
        Professor profPimenta = new Professor("Marcelo Pimenta");
        Professor profGalante = new Professor("Renata Galante");
        Professor profBridi = new Professor("Arthur Bridi");

        //disciplinas
        Disciplina d1 = new Disciplina("INF01120", "Técnicas de Construção", 4);
        Disciplina d2 = new Disciplina("INF00001", "Banco de Dados", 4);

        //turmas e horarios

        //tcp----------
        
        //tcp turma A -seg- 08:30-10:10
        Turma turmaTCP_A = new Turma("A", profKarina, d1, 30, "105");
        turmaTCP_A.addHorario(new Horario(
            LocalTime.of(8, 30), 
            LocalTime.of(10, 10), 
            DiaSemana.SEGUNDA
        ));

        turmaTCP_A.addHorario(new Horario(
            LocalTime.of(8, 30), 
            LocalTime.of(10, 10), 
            DiaSemana.QUARTA
        ));

        //tcp turma B -ter- 08:30-10:10
        Turma turmaTCP_B = new Turma("B", profPimenta, d1, 30, "106");
        turmaTCP_B.addHorario(new Horario(
            LocalTime.of(8, 30), 
            LocalTime.of(10, 10), 
            DiaSemana.TERCA
        ));

        turmaTCP_B.addHorario(new Horario(
            LocalTime.of(8, 30),    
            LocalTime.of(10, 10), 
            DiaSemana.QUINTA
        ));

        //add turmas em tcp
        d1.adicionarTurma(turmaTCP_A);
        d1.adicionarTurma(turmaTCP_B);
        //----------------

        //banco de dados-------------

        //turma C seg-08:30-10:10
        //essa turma C conflita com a turma A de tcp (mesmo dia e hora)
        Turma turmaBD_C = new Turma("C", profGalante, d2, 40, "201");
        turmaBD_C.addHorario(new Horario(
            LocalTime.of(8, 30), 
            LocalTime.of(10, 10), 
            DiaSemana.SEGUNDA
        ));

        turmaBD_C.addHorario(new Horario(
            LocalTime.of(8, 30), 
            LocalTime.of(10, 10), 
            DiaSemana.QUARTA
        ));

        // Turma D qua-10:30-12:10
        Turma turmaBD_D = new Turma("D", profBridi, d2, 40, "202");
        turmaBD_D.addHorario(new Horario(
            LocalTime.of(10, 30), 
            LocalTime.of(12, 10), 
            DiaSemana.SEGUNDA
        ));

        turmaBD_D.addHorario(new Horario(
            LocalTime.of(10, 30), 
            LocalTime.of(12, 10), 
            DiaSemana.QUARTA
        ));

        //add turmas em bd
        d2.adicionarTurma(turmaBD_C);
        d2.adicionarTurma(turmaBD_D);
        //----------------------------

        //gerador
        List<Disciplina> disciplinasDesejadas = new ArrayList<>();
        disciplinasDesejadas.add(d1);
        disciplinasDesejadas.add(d2);

        // configurando preferencias e restricoes
        Preferencias prefs = new Preferencias();

        System.out.println("\nconfig usuario");
        
        // preferencia -> soft constraint
        System.out.println("preferencia: professor marcelo pimenta (+10 pts)");
        prefs.adicionarProfessorPreferido("Marcelo Pimenta");

        // bloqueios -> hard constraints
        System.out.println("restricao: bloquear segunda das 08:30 ate 10:10");
        Horario horarioBloqueado = new Horario(
            LocalTime.of(8, 30), 
            LocalTime.of(10, 10), 
            DiaSemana.SEGUNDA
        );
        prefs.adicionarHorarioBloqueado(horarioBloqueado);


        // gerador de grades
        System.out.println("\ngerando grades");
        GeradorDeGrades gerador = new GeradorDeGrades(disciplinasDesejadas, prefs);
        gerador.gerarGrades();

        List<Grade> resultados = gerador.getGrades();

        new AppController();

        //print
        System.out.println("\nresultados:");
        
        if (resultados.isEmpty()) {
            System.out.println("nenhuma grade possivel com estas restricoes");
        } else {
            System.out.println("foram geradas " + resultados.size() + " grades possiveis.");
            System.out.println("(ordenadas da 'melhor' p pior)\n");

            int i = 1;
            for (Grade grade : resultados) {
                System.out.println("------------------------------------------------");
                System.out.println("opcao " + i + " (creditos totais: " + grade.getCreditosTotais() + ")");
                
                for (Turma t : grade.getTurmasSelecionadas()) {
                    String infoExtra = "";
                    
                    // marca professor preferido
                    if (t.getProfessor().getNome().equals("Marcelo Pimenta")) {
                        infoExtra = " [professor preferido!!!!]";
                    }


                    System.out.println("   > " + t.getDisciplina().getNome() + 
                                       " | Turma " + t.getCodigo() + 
                                       " | " + t.getProfessor().getNome() + 
                                       infoExtra);
                    
                    for(Horario h : t.getHorarios()){
                    System.out.println("      - " + h.getDiaSemana() + " " + h.getInicio() + " às " + h.getFim());
                    }
                }
                i++;
            }
        }
    }
}