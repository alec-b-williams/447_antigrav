package gravity;

import jig.ConvexPolygon;

public class SpikeTrap extends GameObject {
    public int id;
    public int placedById;

    public SpikeTrap(float x, float y, int id) {
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
        this.id = data.id;
    }
}
