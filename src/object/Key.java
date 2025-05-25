package object;

import java.io.IOException;
import java.awt.Rectangle;
import javax.imageio.ImageIO;

import main.GamePanel;

public class Key extends SuperObject {
	
	GamePanel gp;
	boolean isCollected = false;
	
	public Key(GamePanel gp) {
		
		this.gp = gp;
		
		name = "Key";
		collision = false;
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/rock.png"));
			uTool.scaleImage(image, gp.tileSize, gp.tileSize);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
        for (int i = 0; i < gp.flames.size(); i++) {
            Flame flame = gp.flames.get(i);
            Rectangle flameRect = new Rectangle(flame.worldX, flame.worldY, gp.tileSize, gp.tileSize);
            Rectangle keyRect = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize);
            if (flameRect.intersects(keyRect)) {
                try {
                    image = uTool.scaleImage(
                        ImageIO.read(getClass().getResourceAsStream("/objects/key_01d.png")),
                        gp.tileSize, gp.tileSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
