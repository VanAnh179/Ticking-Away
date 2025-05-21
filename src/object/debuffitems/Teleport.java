package object.debuffitems;

import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import object.SuperObject;

public class Teleport extends SuperObject {
	
	GamePanel gp;
	
	public Teleport(GamePanel gp) {
		
		this.gp = gp;
		
		name = "Book";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/book_03c.png"));
			uTool.scaleImage(image, gp.tileSize, gp.tileSize);
		} catch(IOException e) {
			e.printStackTrace();
		}
		collision = false;
	}
}
