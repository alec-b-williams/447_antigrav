package gravity;

import jig.ConvexPolygon;

import java.util.Random;

public class Powerup extends GameObject {
    public int id;
    public int dispenserId;
    public int type;
    public static final int numTypes = 3;
    public static final int NONE = -1;
    public static final int BOOST = 0;
    public static final int SPIKE_TRAP = 1;
    public static final int ROCKET = 2;

    public Powerup(float x, float y, int id, int dispenserId) {
        super(x, y);
        this.setX(x);
        this.setY(y);
        this.id = id;
        this.addShape(new ConvexPolygon(1, 1.0f));
        this.setCoarseGrainedRadius(1);
        this.dispenserId = dispenserId;
        Random rand = new Random();
        this.type = rand.nextInt(numTypes);
    }

    public void updateData(EntityData data) {
        this.worldX = data.xPosition;
        this.worldY = data.yPosition;
        this.type = data.powerupType;
    }
}