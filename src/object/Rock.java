package object;

import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.UtilityTool;

public class Rock extends SuperObject {
    GamePanel gp;
    UtilityTool uTool = new UtilityTool(); // Thêm dòng này
    
    public Rock(GamePanel gp) {
        this.gp = gp;
        name = "Rock";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/rock.png"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize); // Sửa thành uTool
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true; // Thêm thuộc tính collision
    }
}