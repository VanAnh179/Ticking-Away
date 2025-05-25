package object;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;

public class Portal extends SuperObject {
	
	GamePanel gp;
	
	public Portal(GamePanel gp) {
		
		this.gp = gp;
		collision = false;
		
		name = "Portal";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/portal.png"));
			uTool.scaleImage(image, gp.tileSize, gp.tileSize);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}