package app.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import model.Disciplina;
import model.ExtracaoDados;

public class MenuInicialGUI {

    public static JPanel criarTela(AppController controller) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Matriculador Master Blaster 9000", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        painel.add(titulo, BorderLayout.NORTH);

        JPanel botoes = new JPanel();
        botoes.setLayout(new GridLayout(1, 3, 40, 10));

        JButton btnAluno = new JButton("Aluno INF/UFRGS");
        JButton btnCsv = new JButton("Carregar CSV");
        JButton btnManual = new JButton("Inserir manualmente");

        //carregar csv aluno inf
        btnAluno.addActionListener(e -> controller.iniciarFluxoAlunoUfrgs());

        //carregar csv generico
        btnCsv.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(painel);
            if (res == JFileChooser.APPROVE_OPTION) {
                File arquivo = chooser.getSelectedFile();

                // Usa ExtracaoDados normalmente
                ExtracaoDados extrator = new ExtracaoDados();
                java.util.List<Disciplina> disciplinas = extrator.carregarDisciplinas(arquivo.getAbsolutePath());

                if (disciplinas == null || disciplinas.isEmpty()) {
                    JOptionPane.showMessageDialog(painel,
                            "Nenhuma disciplina encontrada no arquivo selecionado.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //preenche turmasCriadas direto e vai pra Preferências
                controller.turmasCriadas.clear();
                for (Disciplina d : disciplinas) {
                    controller.turmasCriadas.addAll(d.getTurmas());
                }

                controller.mostrarPreferencias();
            }
        });

        //inserir manualmente
        btnManual.addActionListener(e -> controller.mostrarInsercao());

        //botoes menu
        botoes.add(btnAluno);
        botoes.add(btnCsv);
        botoes.add(btnManual);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.add(botoes);

        painel.add(centro, BorderLayout.CENTER);

        return painel;
    }
}