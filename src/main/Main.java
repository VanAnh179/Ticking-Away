package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    
    public static void main(String[] args) {
        
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Ticking Away");

        SwingUtilities.invokeLater(() -> {
            new MenuScreen().setVisible(true);
        });

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}

