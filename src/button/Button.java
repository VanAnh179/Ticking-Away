package button;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class Button extends JButton {
    private ImageIcon normalIcon, hoverIcon, clickIcon;

    public Button(String normalImage, String hoverImage, String clickImage) {
        normalIcon = new ImageIcon(normalImage);
        hoverIcon = new ImageIcon(hoverImage);
        clickIcon = new ImageIcon(clickImage);

        setIcon(normalIcon);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);

        // Sự kiện khi di chuột vào nút
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setIcon(hoverIcon);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setIcon(normalIcon);
            }
        });

        // Sự kiện khi nút được nhấn
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setIcon(clickIcon);
                onClick();
            }
        });
    }

    protected abstract void onClick();
}

