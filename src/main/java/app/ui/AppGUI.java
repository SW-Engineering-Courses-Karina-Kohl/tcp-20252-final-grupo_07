package app.ui;

import javax.swing.*;

import model.Disciplina;
import model.Grade;
import model.Horario;
import model.Professor;
import model.Turma;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter.White;

import java.awt.*;
import java.util.List;

public class AppGUI {
    private static final Logger logger = LogManager.getLogger(AppGUI.class);
    // Essas constantes precisam ficar DENTRO da classe
    public static final int DIAS_DA_SEMANA = 6;      // Segunda–Sábado
    public static final int NUMERO_DE_HORARIOS = 16; // Ex.: 7h–22h

    // Método estático para abrir a janela recebendo a lista de grades
    public static void mostrar(List<Grade> gradesSelecionadas) {
        if (gradesSelecionadas == null || gradesSelecionadas.isEmpty()) {
            //log erro fatal
            logger.fatal("Tentativa de mostrar GUI com lista de grades nula ou vazia.");
            throw new IllegalArgumentException("A lista de grades não pode ser vazia.");
        }

        // Criação do frame (janela)
        JFrame frame = new JFrame("Matriculador Master Blaster 9000");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        Grade gradeAtual = gradesSelecionadas.get(0);

        // [linha horário][coluna dia]
        Turma[][] gradeOrganizada = new Turma[NUMERO_DE_HORARIOS][DIAS_DA_SEMANA];

        // Preenche matriz gradeOrganizada
        for (Turma turma : gradeAtual.getTurmasSelecionadas()) {
            for (Horario horario : turma.getHorarios()) {
                int linha = horario.getInicio().getHour() - 7; // se começa às 7h
                int duracao = horario.getFim().getHour() - horario.getInicio().getHour();

                if (linha < 0 || linha >= NUMERO_DE_HORARIOS) {
                    // ignora horários fora do range
                    continue;
                }
                for(int k = 0; k< duracao; k++){
                    switch (horario.getDiaSemana()) {
                        case SEGUNDA:
                            gradeOrganizada[linha + k][0] = turma;
                            break;
                        case TERCA:
                            gradeOrganizada[linha + k][1] = turma;
                            break;
                        case QUARTA:
                            gradeOrganizada[linha + k][2] = turma;
                            break;
                        case QUINTA:
                            gradeOrganizada[linha + k][3] = turma;
                            break;
                        case SEXTA:
                            gradeOrganizada[linha + k][4] = turma;
                            break;
                        case SABADO:
                            gradeOrganizada[linha + k][5] = turma;
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        // Painel superior com botões das grades
        JPanel listaGrade = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        for (int i = 0; i < gradesSelecionadas.size(); i++) {
            final int indice = i;
            JButton botaoGrade = new JButton("Grade " + (i + 1));

            // Aqui você poderia trocar gradeAtual e redesenhar a grade ao clicar
            // ex.: botaoGrade.addActionListener(e -> atualizarGrade(indice));
            listaGrade.add(botaoGrade);
        }

        listaGrade.add(new JButton("Preferencias"));
        listaGrade.add(new JButton("+"));

        // Painel central com a grade de horários
        JPanel gridGrade = new JPanel(
                new GridLayout(NUMERO_DE_HORARIOS + 1, DIAS_DA_SEMANA + 1, 5, 5));

        // Cabeçalhos
        String[] labels = { "Horario", "Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado" };
        for (String label : labels) {
            gridGrade.add(new JLabel(label, JLabel.CENTER));
        }

        // Linhas da grade
        for (int i = 0; i < NUMERO_DE_HORARIOS; i++) {
            int hora = 7 + i; // aqui é só um exemplo (7h em diante)

            // primeira coluna: rótulo do horário
            gridGrade.add(new JLabel(hora + "h30", JLabel.CENTER));

            for (int j = 0; j < DIAS_DA_SEMANA; j++) {
                Turma turma = gradeOrganizada[i][j];

                if (turma == null) {
                    JButton botaoVazio = new JButton();
                    botaoVazio.setEnabled(false);
                    gridGrade.add(botaoVazio);
                } else {
                    JButton botaoTurma = new JButton(turma.getDisciplina().getNome()+" "+turma.getCodigo());
                    //----------------------------------------------------------------------------------------------------------------------------------------
                   // Gera uma cor baseada no nome da disciplina (sempre será a mesma cor para a mesma disciplina)
                    int hash = turma.getDisciplina().getNome().hashCode();
                    Color corDinamica = new Color((hash & 0xFF0000) >> 16, (hash & 0x00FF00) >> 8, hash & 0x0000FF); 
                                    
                    botaoTurma.setForeground(corDinamica); // .darker() garante que dê leitura no fundo claro
                    botaoTurma.setBackground(Color.WHITE);
                    //----------------------------------------------------------------------------------------------------------------------------------------
                    botaoTurma.addActionListener(e -> {
                        Disciplina d = turma.getDisciplina();
                        Professor p = turma.getProfessor();

                        //log acoes do usuario
                        logger.info("Usuário visualizou detalhes da disciplina: {} (Turma {})", d.getNome(), turma.getCodigo());

                        String info =

                                d.getCodigo() + "\n" +
                                d.getNome() + "\n" +
                                "Turma: " + turma.getCodigo() + "\n" +
                                "Professor: " + (p != null ? p.getNome() : "-") + "\n" +
                                "Vagas ofertadas: " + turma.getVagasOfertadas() + "\n" +
                                "Sala: " + turma.getSala() + "\n" +
                                "Creditos: " + d.getCreditos();

                        JOptionPane.showMessageDialog(
                                frame,
                                info,
                                "Informações da turma",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    });
                    gridGrade.add(botaoTurma);
                }
            }
        }

        // Adicionando painéis ao frame
        frame.add(listaGrade, BorderLayout.NORTH);
        frame.add(gridGrade, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Se você quiser que essa classe seja o ponto de entrada da aplicação,
    // dá pra usar algo assim (carregando as grades antes):
    /*
    public static void main(String[] args) {
        List<Grade> grades = carregarGradesDeAlgumLugar();
        SwingUtilities.invokeLater(() -> mostrar(grades));
    }
    */
}
