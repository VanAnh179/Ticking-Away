package object.debuffitems;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;

import object.*;

public class DecreaseLight extends SuperObject {
	
	GamePanel gp;
	
	public DecreaseLight(GamePanel gp) {
		
		this.gp = gp;
		
		name = "Scroll";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/scroll_01g.png"));
			uTool.scaleImage(image, gp.tileSize, gp.tileSize);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
