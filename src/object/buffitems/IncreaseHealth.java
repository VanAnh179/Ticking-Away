package object.buffitems;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;

import object.*;

public class IncreaseHealth extends SuperObject {
	
	GamePanel gp;
	
	public IncreaseHealth(GamePanel gp) {
		
		this.gp = gp;
		
		name = "Helmet";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/helmet_02a.png"));
			uTool.scaleImage(image, gp.tileSize, gp.tileSize);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
