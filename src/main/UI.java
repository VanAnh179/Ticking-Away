package main;

import java.awt.Graphics2D;

public class UI {
	
	GamePanel gp;
	// Font font;
	public boolean messageOn = false;
	public String message = "";
	int messageCounter = 0;
	public boolean gameFinished = false;
	
	public UI(GamePanel gp) {
		this.gp = gp;
		// font = 
	}
	
	public void showMessage(String text) {
		
		message = text;
		messageOn = true;
	}
	
	public void draw(Graphics2D g2) {
		// g2.setFont(font);
		if (gameFinished == true) {
			int x = gp.screenWidth / 2;
			int y = gp.screenHeight / 2;
			
			g2.drawString("won", x, y);
		}
		
		if (messageOn == true) {
			g2.drawString(message, gp.tileSize / 2, gp.tileSize * 5);
			
			messageCounter++;
			
			if (messageCounter > 100) {
				messageCounter = 0;
				messageOn = false;
			}
		}
	}
}
