package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
<<<<<<< HEAD

import javax.swing.SwingUtilities;
=======
>>>>>>> 1bcddee1872bded8d0eb7d3cabba04f7a19c687c

public class KeyHandler implements KeyListener {
    public boolean debugMode = false;
    public boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;
    boolean checkDrawTime;
<<<<<<< HEAD
    public boolean debugMode = false;
    private GamePanel gp;
=======
    private GamePanel gp; // Thêm tham chiếu đến GamePanel
>>>>>>> 1bcddee1872bded8d0eb7d3cabba04f7a19c687c

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }
<<<<<<< HEAD
    
=======

>>>>>>> 1bcddee1872bded8d0eb7d3cabba04f7a19c687c
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        // System.out.println("Key pressed: " + code + ", gameFinished: " + gp.ui.gameFinished);

        if (code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_SPACE) {
            spacePressed = true;
        }

<<<<<<< HEAD
         if (code == KeyEvent.VK_R && gp.ui.gameFinished) {
            SwingUtilities.invokeLater(() -> {
                gp.resetGame();
            });
            return;
        }
        
=======
>>>>>>> 1bcddee1872bded8d0eb7d3cabba04f7a19c687c
        // DEBUG
        if (code == KeyEvent.VK_T) {
            //checkDrawTime = !checkDrawTime;
            debugMode = !debugMode;
        }

        // DEBUG Xử lý phím R để khởi động lại game
        if (code == KeyEvent.VK_R && gp.ui.gameFinished) {
            gp.resetGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }
    }

<<<<<<< HEAD
     public void reset() {
=======
    public void reset() {
>>>>>>> 1bcddee1872bded8d0eb7d3cabba04f7a19c687c
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        spacePressed = false;
        checkDrawTime = false;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 1bcddee1872bded8d0eb7d3cabba04f7a19c687c
