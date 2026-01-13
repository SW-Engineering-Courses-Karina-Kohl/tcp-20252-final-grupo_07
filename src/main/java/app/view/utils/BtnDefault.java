package app.view.utils;

import javax.swing.*;
import java.awt.*;

public class BtnDefault extends JButton {
    public BtnDefault(String texto){
        this.setText(texto);
        this.setBackground(Color.WHITE);
        this.setFocusable(true);
        this.setFont(new Font("SansSerif", Font.BOLD, 14));

    }
}
