package app;

import javax.swing.SwingUtilities;

import app.controller.AppController;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppController());
    }
}