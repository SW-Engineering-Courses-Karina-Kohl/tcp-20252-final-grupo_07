package app.ui;

import javax.swing.*;
import java.awt.event.*;

public class TriStateActionListener implements ActionListener {

    private final String baseText;
    private int estado = 0; 
    // 0 = neutro
    // 1 = preferido
    // 2 = evitado

    public TriStateActionListener(String baseText) {
        this.baseText = baseText;
    }

    public int getEstado() {
        return estado;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCheckBox cb = (JCheckBox) e.getSource();

        estado = (estado + 1) % 3;

        switch (estado) {
            case 0:
                cb.setText(baseText);
                cb.setSelected(false);
                break;

            case 1:
                cb.setText(baseText + " (preferido)");
                cb.setSelected(true);
                break;

            case 2:
                cb.setText(baseText + " (evitar)");
                cb.setSelected(false);
                break;
        }
    }
}
