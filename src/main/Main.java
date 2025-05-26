package main;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Chạy MainFrame trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MainFrame(); // Khởi tạo MainFrame duy nhất
        });
    }
}

