package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UI {
<<<<<<< HEAD
	
	GamePanel gp;
	BufferedImage heart;
	// Font font;
	public boolean messageOn = false;
	public String message = "";
	int messageCounter = 0;
	public boolean gameFinished = false;
	public boolean gameWon = false; // Thêm biến gameWon

	//biến time
	private long startTime;
	private long lastBackgroundScoreUpdate;
	private boolean isRunning;
	
	//biến score
	public int visibleScore = 0;
	public int backgroundScore = 50000;
	public final int BG_SCORE_DECREASE = 13;


	public UI(GamePanel gp) {
		this.gp = gp;
		// font = 
		try {
			heart = ImageIO.read(getClass().getResourceAsStream("/objects/health.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		//initialize time
		resetTimer();
	}
	
	private BufferedImage scaleImage(BufferedImage original, int width, int height) {
		BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.drawImage(original, 0, 0, width, height, null);
		g2d.dispose();
		return scaledImage;
		
	}



	public void resetTimer() {
		startTime = System.currentTimeMillis();
		lastBackgroundScoreUpdate = startTime;
		isRunning = true;
		visibleScore = 0;
		backgroundScore = 50000;
	}

	public void update() {
		if(isRunning) {
			long currentTime = System.currentTimeMillis();
			if(currentTime - lastBackgroundScoreUpdate >= 1000) {
				backgroundScore -= BG_SCORE_DECREASE;
				if(backgroundScore < 0) {
					backgroundScore = 0;
				}
				lastBackgroundScoreUpdate = currentTime;
			}
		}
	}

	public void addScore(int points) {
		visibleScore += points;
	}

	public int getFinalScore() {
		if (gameFinished && !gameWon) {
            return visibleScore;
        }
        return visibleScore + backgroundScore;
	}

	private String getFormattedTime() {
		long currentTime = isRunning ? System.currentTimeMillis() - startTime : 0;
		currentTime /= 1000;

		int minutes = (int) (currentTime / 60);
		int seconds = (int) (currentTime % 60);
		
		return String.format("%02d:%02d", minutes, seconds);
	}

	private String formatScore(int score) {
		return String.format("%,d", score);
	}

	public void showMessage(String text) {
		
		message = text;
		messageOn = true;
	}
	
	public void draw(Graphics2D g2) {
		drawHealthBar(g2);
		drawClock(g2);
		drawScore(g2);

		if(gameFinished) {
			drawGameFinishedScreen(g2);
		}

		if(messageOn) {
			drawMessage(g2);
		}
	}

	private void drawHealthBar(Graphics2D g2) {
    	// Lưu trạng thái Graphics2D ban đầu
    	Composite originalComposite = g2.getComposite();
=======
    GamePanel gp;
    BufferedImage heart;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    public boolean gameWon = false; // Thêm biến gameWon

    private long startTime;
    private long lastBackgroundScoreUpdate;
    private boolean isRunning;
>>>>>>> 1bcddee1872bded8d0eb7d3cabba04f7a19c687c
    
    public int visibleScore = 0;
    public int backgroundScore = 50000;
    public final int BG_SCORE_DECREASE = 13;

    public UI(GamePanel gp) {
        this.gp = gp;
        try {
            heart = ImageIO.read(getClass().getResourceAsStream("/objects/health.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        resetTimer();
    }
    
    private BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    public void resetTimer() {
        startTime = System.currentTimeMillis();
        lastBackgroundScoreUpdate = startTime;
        isRunning = true;
        visibleScore = 0;
        backgroundScore = 50000;
    }

    public void update() {
        if(isRunning) {
            long currentTime = System.currentTimeMillis();
            if(currentTime - lastBackgroundScoreUpdate >= 1000) {
                backgroundScore -= BG_SCORE_DECREASE;
                if(backgroundScore < 0) {
                    backgroundScore = 0;
                }
                lastBackgroundScoreUpdate = currentTime;
            }
        }
    }

    public void addScore(int points) {
        visibleScore += points;
    }

    public int getFinalScore() {
        if (gameFinished && !gameWon) {
            return visibleScore;
        }
        return visibleScore + backgroundScore;
    }

    private String getFormattedTime() {
        long currentTime = isRunning ? System.currentTimeMillis() - startTime : 0;
        currentTime /= 1000;
        int minutes = (int) (currentTime / 60);
        int seconds = (int) (currentTime % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String formatScore(int score) {
        return String.format("%,d", score);
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }
    
    public void draw(Graphics2D g2) {
        drawHealthBar(g2);
        drawClock(g2);
        drawScore(g2);

        if(gameFinished) {
            drawGameFinishedScreen(g2);
        }

        if(messageOn) {
            drawMessage(g2);
        }
    }

    private void drawHealthBar(Graphics2D g2) {
        Composite originalComposite = g2.getComposite();
        try {
            int startY = 20;
            int spacing = 5;
            int heartSize = gp.tileSize * 3 / 4;
            int startX = gp.screenWidth - 20 - (heartSize + spacing) * 4;
        
            for (int i = 0; i < gp.player.maxHealth; i++) {
                if (i < gp.player.health) {
                    g2.drawImage(heart, 
                            startX + (i * (heartSize + spacing)), 
                            startY, 
                            heartSize, heartSize, null);
                }
            }
        } finally {
            g2.setComposite(originalComposite);
        }
    }

    private void drawClock(Graphics2D g2) {
        Font originalFont = g2.getFont();
        Color originalColor = g2.getColor();
        Font timeFont = new Font("Arial", Font.BOLD, 24);
        g2.setFont(timeFont);
        g2.setColor(Color.white);
        String timeText = getFormattedTime();
        FontMetrics fm = g2.getFontMetrics();
        int x = (gp.screenWidth - fm.stringWidth(timeText)) / 2;
        int y = 42;
        g2.setColor(Color.white);
        g2.drawString(timeText, x, y);
        g2.setFont(originalFont);
        g2.setColor(originalColor);
    }

    private void drawScore(Graphics2D g2) {
        Font originalFont = g2.getFont();
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String scoreText = "Score: " + formatScore(visibleScore);
        int x = 20;
        int y = 48;
        g2.setColor(Color.white);
        g2.drawString(scoreText, x, y);
        g2.setFont(originalFont);
    }

    private void drawGameFinishedScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int boxWidth = gp.screenWidth * 3/4;
        int boxHeight = gp.screenHeight / 2;
        int boxX = (gp.screenWidth - boxWidth) / 2;
        int boxY = (gp.screenHeight - boxHeight) / 2;

        g2.setColor(new Color(30, 30, 30));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        g2.setColor(gameWon ? new Color(50, 200, 50) : new Color(200, 50, 50));
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        String text = gameWon ? "You Won!" : "Game Over";
        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.white);
        int x = getXForCenteredText(text, g2);
        int y = boxY + boxHeight/2 - 20;
        g2.drawString(text, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        String scoreText = "Final Score: " + formatScore(getFinalScore());
        x = getXForCenteredText(scoreText, g2);
        y += 60;
        g2.drawString(scoreText, x, y);

<<<<<<< HEAD
	private void drawGameFinishedScreen(Graphics2D g2) {
	
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int boxWidth = gp.screenWidth * 3/4;
        int boxHeight = gp.screenHeight / 2;
        int boxX = (gp.screenWidth - boxWidth) / 2;
        int boxY = (gp.screenHeight - boxHeight) / 2;

        g2.setColor(new Color(30, 30, 30));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        g2.setColor(gameWon ? new Color(50, 200, 50) : new Color(200, 50, 50));
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        String text = gameWon ? "You Won!" : "Game Over";
        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.white);
        int x = getXForCenteredText(text, g2);
        int y = boxY + boxHeight/2 - 20;
        g2.drawString(text, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        String scoreText = "Final Score: " + formatScore(getFinalScore());
        x = getXForCenteredText(scoreText, g2);
        y += 60;
        g2.drawString(scoreText, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 25));
        String restartText = "Press R to restart";
        x = getXForCenteredText(restartText, g2);
        y += 40;
        g2.drawString(restartText, x, y);

=======
        g2.setFont(new Font("Arial", Font.PLAIN, 25));
        String restartText = "Press R to restart";
        x = getXForCenteredText(restartText, g2);
        y += 40;
        g2.drawString(restartText, x, y);
    }

    private void drawMessage(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString(message, 20, gp.screenHeight - 40);
        messageCounter++;
        if(messageCounter > 120) {
            messageCounter = 0;
            messageOn = false;
        }
>>>>>>> 1bcddee1872bded8d0eb7d3cabba04f7a19c687c
    }

    private int getXForCenteredText(String text, Graphics2D g2) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }
}