package main;

import javax.swing.*;
import java.awt.*;

class TextOutlineLabel extends JLabel {
    private Color outlineColor = new Color(255, 105, 180); // Màu hồng
    private float outlineWidth = 2.0f; // Độ dày viền

    public TextOutlineLabel(String text) {
        super(text);
        setForeground(Color.WHITE); // Màu chính
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Vẽ viền
        g2.setColor(outlineColor);
        g2.setStroke(new BasicStroke(outlineWidth * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Vẽ text nhiều lần với offset để tạo viền
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    g2.drawString(getText(), 
                        getInsets().left + i * outlineWidth, 
                        getHeight() - getInsets().bottom + j * outlineWidth - 5
                    );
                }
            }
        }

        // Vẽ text chính
        g2.setColor(getForeground());
        g2.drawString(getText(), getInsets().left, getHeight() - getInsets().bottom - 5);
        g2.dispose();
    }
}