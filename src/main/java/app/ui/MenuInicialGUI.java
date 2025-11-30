package app.ui;

import model.Disciplina;
import model.ExtracaoDados;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MenuInicialGUI {

    public static JPanel criarTela(AppController controller) {
        JPanel painel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Matriculador Master Blaster 9000", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painel.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 40, 0, 40);

        JButton btnCsv = new JButton("Carregar CSV");
        JButton btnManual = new JButton("Inserir manualmente");

        // --- BOTÃO CARREGAR CSV ---
        btnCsv.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int resultado = chooser.showOpenDialog(painel);

            if (resultado == JFileChooser.APPROVE_OPTION) {
                File arquivo = chooser.getSelectedFile();

                try {
                    // Ajusta conforme a assinatura da tua ExtracaoDados
                    ExtracaoDados extrator = new ExtracaoDados();
                    // Se teu método for sem parâmetro, usa: extrator.carregarDisciplinas();
                    List<Disciplina> disciplinas =
                            extrator.carregarDisciplinas(arquivo.getAbsolutePath());

                    controller.turmasCriadas.clear();

                    for (Disciplina d : disciplinas) {
                        controller.turmasCriadas.addAll(d.getTurmas());
                    }

                    if (controller.turmasCriadas.isEmpty()) {
                        JOptionPane.showMessageDialog(painel,
                                "Nenhuma turma encontrada no CSV.",
                                "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    JOptionPane.showMessageDialog(painel,
                            "CSV carregado com sucesso! Foram lidas "
                                    + controller.turmasCriadas.size() + " turmas.",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // Vai direto para tela de preferências
                    controller.mostrarPreferencias();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(painel,
                            "Erro ao carregar CSV:\n" + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- BOTÃO INSERIR MANUALMENTE ---
        btnManual.addActionListener(e -> controller.mostrarInsercao());

        gbc.gridx = 0;
        centro.add(btnCsv, gbc);
        gbc.gridx = 1;
        centro.add(btnManual, gbc);

        painel.add(centro, BorderLayout.CENTER);

        return painel;
    }
}
