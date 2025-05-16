package object.buffitems;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;

import object.SuperObject;

public class IncreaseLight extends SuperObject {
	
	GamePanel gp;
	
	public IncreaseLight(GamePanel gp) {
		
		this.gp = gp;
		
		name = "Crystal";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/crystal_01a.png"));
			uTool.scaleImage(image, gp.tileSize, gp.tileSize);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
