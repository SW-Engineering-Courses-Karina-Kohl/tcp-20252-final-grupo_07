package app;

import javax.swing.SwingUtilities;
import app.ui.AppController;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppController());
    }
}