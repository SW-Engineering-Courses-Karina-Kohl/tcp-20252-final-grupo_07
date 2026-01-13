package app.view.utils;

import javax.swing.*;
import java.awt.*;

public class BtnMenu extends JButton {
    public BtnMenu(String text){
        this.setText(text);
        this.setBackground(new Color(211,211,211));
        this.setFont(new Font("SansSerif", Font.BOLD, 24));
        this.setPreferredSize(new Dimension(500,125));
        this.setFocusPainted(true);
    }
}
