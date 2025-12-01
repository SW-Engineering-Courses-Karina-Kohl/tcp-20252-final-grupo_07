package app.view;

import javax.swing.*;

import app.controller.AppController;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.Collator;
import java.util.Locale;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Disciplina;

public class SelecaoDisciplinasGUI {

    private static Map<String, Disciplina> mapaLabelDisciplina = new LinkedHashMap<>();

    private static DefaultListModel<String> modelDisponiveis = new DefaultListModel<>();
    private static DefaultListModel<String> modelSelecionadas = new DefaultListModel<>();
    private static JList<String> listDisponiveis = new JList<>(modelDisponiveis);
    private static JList<String> listSelecionadas = new JList<>(modelSelecionadas);
    private static final Collator COLL = Collator.getInstance(Locale.getDefault());

    public static JPanel criarTela(AppController controller) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Selecione as disciplinas desejadas", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        painel.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        listDisponiveis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listSelecionadas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // clear focus when switching list
        listDisponiveis.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                listSelecionadas.clearSelection();
            }
        });

        listSelecionadas.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                listDisponiveis.clearSelection();
            }
        });

        JScrollPane scrollLeft = new JScrollPane(listDisponiveis);
        scrollLeft.setPreferredSize(new Dimension(350, 420));
        c.gridx = 0; c.gridy = 0; c.fill = GridBagConstraints.BOTH; c.weightx = 0.45; c.weighty = 1.0;
        centro.add(scrollLeft, c);

        // setinhas
        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        JButton btnRight = new JButton(" > ");
        btnRight.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton btnLeft = new JButton(" < ");
        btnLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
        middle.add(Box.createVerticalGlue());
        middle.add(btnRight);
        middle.add(Box.createRigidArea(new Dimension(0,10)));
        middle.add(btnLeft);
        middle.add(Box.createVerticalGlue());

        c.gridx = 1; c.gridy = 0; c.fill = GridBagConstraints.NONE; c.weightx = 0.1;
        centro.add(middle, c);

        JScrollPane scrollRight = new JScrollPane(listSelecionadas);
        scrollRight.setPreferredSize(new Dimension(350, 420));
        c.gridx = 2; c.gridy = 0; c.fill = GridBagConstraints.BOTH; c.weightx = 0.45;
        centro.add(scrollRight, c);

        painel.add(centro, BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVoltar = new JButton("Voltar");
        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setPreferredSize(new Dimension(120, 36));
        painelSul.add(btnVoltar);
        painelSul.add(Box.createRigidArea(new Dimension(8,0)));
        painelSul.add(btnContinuar);
        painel.add(painelSul, BorderLayout.SOUTH);

        //acoes
        btnVoltar.addActionListener(e -> controller.mostrarMenuInicial());

        btnRight.addActionListener(e -> {
            List<String> selecionados = listDisponiveis.getSelectedValuesList();
            moveItems(modelDisponiveis, modelSelecionadas, selecionados);
        });

        btnLeft.addActionListener(e -> {
            List<String> selecionados = listSelecionadas.getSelectedValuesList();
            moveItems(modelSelecionadas, modelDisponiveis, selecionados);
        });

        // double-click move
        listDisponiveis.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    List<String> selecionados = listDisponiveis.getSelectedValuesList();
                    moveItems(modelDisponiveis, modelSelecionadas, selecionados);
                }
            }
        });

        // double-click move
        listSelecionadas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    List<String> selecionados = listSelecionadas.getSelectedValuesList();
                    moveItems(modelSelecionadas, modelDisponiveis, selecionados);
                }
            }
        });

        btnContinuar.addActionListener(e -> {
            List<Disciplina> selecionadas = new ArrayList<>();

            for (int i = 0; i < modelSelecionadas.size(); i++) {
                String label = modelSelecionadas.get(i);
                Disciplina d = mapaLabelDisciplina.get(label);
                if (d != null) selecionadas.add(d);
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
        mapaLabelDisciplina.clear();
        modelDisponiveis.clear();
        modelSelecionadas.clear();

        for (Disciplina d : controller.getDisciplinasCarregadas()) {
            String label = d.getCodigo() + " - " + d.getNome();
            if (!mapaLabelDisciplina.containsKey(label)) {
                mapaLabelDisciplina.put(label, d);
                insertSorted(modelDisponiveis, label);
            }
        }
    }

    private static void moveItems(DefaultListModel<String> from, DefaultListModel<String> to, List<String> items) {
        if (items == null || items.isEmpty()) return;

        
        List<Integer> indices = new ArrayList<>();
        int minIdx = Integer.MAX_VALUE;
        for (String s : items) {
            for (int i = 0; i < from.getSize(); i++) {
                if (from.getElementAt(i).equals(s)) {
                    indices.add(i);
                    if (i < minIdx) minIdx = i;
                    break;
                }
            }
        }

        indices.sort((a, b) -> b - a);
        for (int idx : indices) {
            String val = from.getElementAt(idx);
            if (!to.contains(val)) {
                insertSorted(to, val);
            }
            from.remove(idx);
        }

        // manter seletor apos mover
        JList<String> fromList = (from == modelDisponiveis) ? listDisponiveis : listSelecionadas;
        int newSel = -1;
        if (from.getSize() > 0 && minIdx != Integer.MAX_VALUE) {
            if (minIdx < from.getSize()) newSel = minIdx;
            else newSel = from.getSize() - 1;
        }
        if (newSel >= 0) {
            fromList.setSelectedIndex(newSel);
            fromList.ensureIndexIsVisible(newSel);
        } else {
            fromList.clearSelection();
        }

        listDisponiveis.revalidate();
        listDisponiveis.repaint();
        listSelecionadas.revalidate();
        listSelecionadas.repaint();
    }

    private static void insertSorted(DefaultListModel<String> model, String value) {
        if (value == null) return;
        int size = model.getSize();
        int insertAt = 0;
        while (insertAt < size) {
            String cur = model.getElementAt(insertAt);
            // pegar o nome da disciplina
            String nameVal = value;
            String nameCur = cur;
            int sepVal = value.indexOf(" - ");
            if (sepVal >= 0 && sepVal + 3 < value.length()) nameVal = value.substring(sepVal + 3);
            int sepCur = cur.indexOf(" - ");
            if (sepCur >= 0 && sepCur + 3 < cur.length()) nameCur = cur.substring(sepCur + 3);

            // collator para comparar com acentos
            int cmp = COLL.compare(nameVal, nameCur);
            if (cmp == 0) cmp = COLL.compare(value, cur);
            if (cmp <= 0) break;
            insertAt++;
        }
        model.add(insertAt, value);
    }
}