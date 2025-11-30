package app.view;

import javax.swing.*;

import app.controller.AppController;

import java.awt.*;
import java.io.File;


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
                controller.carregarTurmasDeCsv(arquivo.getAbsolutePath());
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