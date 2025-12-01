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
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 32f));

        JPanel botoes = new JPanel();
        botoes.setLayout(new GridLayout(5, 1, 20, 20));

        botoes.setBorder(BorderFactory.createEmptyBorder(0, 200, 0, 200));


        botoes.add(titulo);
        JButton btnAlunoCIC = new JButton("Aluno CIC/UFRGS");
        JButton btnAlunoECP = new JButton("Aluno ECP/UFRGS");
        JButton btnCsv = new JButton("Carregar CSV");
        JButton btnManual = new JButton("Inserir manualmente");

        //carregar csv aluno inf
        btnAlunoCIC.addActionListener(e -> controller.iniciarFluxoAlunoUfrgs("ofertas_cic.csv"));
        btnAlunoECP.addActionListener(e -> controller.iniciarFluxoAlunoUfrgs("ofertas_ecp.csv"));

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
        
        btnCsv.setPreferredSize(new Dimension(500,125));
        btnCsv.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        btnAlunoCIC.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        btnAlunoECP.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        btnManual.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        btnCsv.setBackground(new Color(211,211,211));
        btnAlunoCIC.setBackground(new Color(211,211,211));
        btnAlunoECP.setBackground(new Color(211,211,211));
        btnManual.setBackground(new Color(211,211,211));

        botoes.add(btnAlunoCIC);
        botoes.add(btnAlunoECP);
        botoes.add(btnCsv);
        botoes.add(btnManual);

        JPanel centro = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        centro.add(botoes, gbc);

        painel.add(centro, BorderLayout.CENTER);


        painel.add(centro, BorderLayout.CENTER);

        return painel;
    }
}
