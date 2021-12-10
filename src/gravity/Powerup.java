package gravity;

import jig.Entity;
import jig.ResourceManager;

public class Powerup extends GameObject {
    public Powerup(float x, float y) {
        super(x, y);
    }

    public void updateData(EntityData data) {
        this.worldX = data.xPosition;
        this.worldY = data.yPosition;
    }
}
