package app.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Disciplina;

public class SelecaoDisciplinasGUI {

    private static JPanel painelLista;
    private static Map<String, Disciplina> mapaLabelDisciplina = new LinkedHashMap<>();
    private static List<JCheckBox> checkBoxes = new ArrayList<>();

    public static JPanel criarTela(AppController controller) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Selecione as disciplinas desejadas", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        painel.add(titulo, BorderLayout.NORTH);

        //painel lista 
        painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(painelLista);
        scroll.setPreferredSize(new Dimension(500, 450));
        painel.add(scroll, BorderLayout.WEST);

        //continuar botao
        JPanel painelCentro = new JPanel(new GridBagLayout());
        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setPreferredSize(new Dimension(250, 40));
        painelCentro.add(btnContinuar);
        painel.add(painelCentro, BorderLayout.CENTER);

        //botao voltar
        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVoltar = new JButton("Voltar");
        painelSul.add(btnVoltar);
        painel.add(painelSul, BorderLayout.SOUTH);

        //acoes
        btnVoltar.addActionListener(e -> controller.mostrarMenuInicial());

        btnContinuar.addActionListener(e -> {
            List<Disciplina> selecionadas = new ArrayList<>();

            for (JCheckBox cb : checkBoxes) {
                if (cb.isSelected()) {
                    Disciplina d = mapaLabelDisciplina.get(cb.getText());
                    if (d != null) {
                        selecionadas.add(d);
                    }
                }
            }

            if (selecionadas.isEmpty()) {
                JOptionPane.showMessageDialog(painel,
                        "Selecione pelo menos uma disciplina para continuar.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.definirDisciplinasSelecionadas(selecionadas);
        });

        return painel;
    }

    public static void atualizarLista(AppController controller) {
        if (painelLista == null) return;

        painelLista.removeAll();
        mapaLabelDisciplina.clear();
        checkBoxes.clear();

        for (Disciplina d : controller.disciplinasCarregadas) {
            String label = d.getCodigo() + " - " + d.getNome();

            if (!mapaLabelDisciplina.containsKey(label)) {
                mapaLabelDisciplina.put(label, d);

                JCheckBox cb = new JCheckBox(label);
                checkBoxes.add(cb);
                painelLista.add(cb);
            }
        }

        painelLista.revalidate();
        painelLista.repaint();
    }
}