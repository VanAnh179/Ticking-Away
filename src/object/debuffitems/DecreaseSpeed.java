package object.debuffitems;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;

import object.*;

public class DecreaseSpeed extends SuperObject {
	
	GamePanel gp;
	
	public DecreaseSpeed(GamePanel gp) {
		
		this.gp = gp;
		
		name = "Candy";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/candy_02f.png"));
			uTool.scaleImage(image, gp.tileSize, gp.tileSize);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
