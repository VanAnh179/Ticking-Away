package object.buffitems;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;

import object.SuperObject;

public class IncreaseDamage extends SuperObject {
	
	GamePanel gp;
	
	public IncreaseDamage(GamePanel gp) {
		
		this.gp = gp;
		
		name = "Potion";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/potion_02e.png"));
			uTool.scaleImage(image, gp.tileSize, gp.tileSize);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
