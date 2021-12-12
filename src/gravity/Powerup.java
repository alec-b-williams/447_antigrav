package gravity;

import jig.ConvexPolygon;

public class Powerup extends GameObject {
    int id;

    public Powerup(float x, float y, int id) {
        super(x, y);
        this.setX(x);
        this.setY(y);
        this.id = id;
        this.addShape(new ConvexPolygon(1, 1.0f));
        this.setCoarseGrainedRadius(1);
    }

    public void updateData(EntityData data) {
        this.worldX = data.xPosition;
        this.worldY = data.yPosition;
    }
}