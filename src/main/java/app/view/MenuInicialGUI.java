package app.view;

import javax.swing.*;

import app.controller.AppController;
import app.view.utils.BtnMenu;

import java.awt.*;
import java.io.File;


public class MenuInicialGUI {

    public static JPanel criarTela(AppController controller) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("AUXILIADOR DE MATRICULA INF/UFRGS", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 32f));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel botoes = new JPanel();
        botoes.setLayout(new GridLayout(4, 1, 20, 20));

        botoes.setBorder(BorderFactory.createEmptyBorder(0, 200, 0, 200));

        BtnMenu btnAlunoCIC = new BtnMenu("Aluno CIC/UFRGS");
        BtnMenu btnAlunoECP = new BtnMenu("Aluno ECP/UFRGS");
        BtnMenu btnCsv = new BtnMenu("Carregar CSV");
        BtnMenu btnManual = new BtnMenu("Inserir manualmente");

        //carregar csv aluno inf
        btnAlunoCIC.addActionListener(e -> controller.carregarTurmasDeCsv("ofertas_cic.csv"));
        btnAlunoECP.addActionListener(e -> controller.carregarTurmasDeCsv("ofertas_ecp.csv"));

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
        botoes.add(btnAlunoCIC);
        botoes.add(btnAlunoECP);
        botoes.add(btnCsv);
        botoes.add(btnManual); 

        JPanel centro = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1; gbc.gridx = 0; gbc.gridy = 0;
        centro.add(titulo);

        gbc.gridy = 1;
        centro.add(botoes, gbc);
        painel.add(centro, BorderLayout.CENTER);
        return painel;
    }
}
