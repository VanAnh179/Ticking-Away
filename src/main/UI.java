package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;


public class UI {
	
	GamePanel gp;
	BufferedImage heart;
	// Font font;
	public boolean messageOn = false;
	public String message = "";
	int messageCounter = 0;
	public boolean gameFinished = false;

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
    
    	try {
        	int startY = 20;
        	int spacing = 5;
        	int heartSize = gp.tileSize * 3 / 4;
			int startX = gp.screenWidth - 20 - (heartSize + spacing) * 4;
        
        
        	// Vẽ từng trái tim
        	for (int i = 0; i < gp.player.maxHealth; i++) {
            	if (i < gp.player.health) {
                	// Vẽ trái tim đầy
                	g2.drawImage(heart, 
                            	startX + (i * (heartSize + spacing)), 
                            	startY, 
                            	heartSize, heartSize, null);
            	}
        	}
    	} finally {
        	// Khôi phục trạng thái Graphics2D
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
		g2.setColor(Color.white);
		g2.setFont(new Font("Arial", Font.BOLD, 40));

		String text = gp.player.health <= 0 ? "Game Over" : "You Won";
		int x = getXForCenteredText(text, g2);
		g2.drawString(text, x, gp.screenHeight / 2);
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
	}

	private int getXForCenteredText(String text, Graphics2D g2) {
		int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		return gp.screenWidth / 2 - length / 2;
	}
}