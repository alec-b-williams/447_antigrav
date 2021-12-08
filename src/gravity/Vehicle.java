package gravity;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Shape;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Transform;

public class Vehicle extends Entity {
    Animation sprite;
    public float worldX;
    public float worldY;
    public double speedAngle;

    public Vehicle(float x, float y) {
        super(GravGame._SCREENWIDTH/2.0f, GravGame._SCREENHEIGHT/2.0f);

        worldX = x;
        worldY = y;

        sprite = new Animation(ResourceManager.getSpriteSheet(GravGame.VEHICLE_ANIM_RSC, 64, 64),
                0, 0, 7, 0, true, 160, false);
        this.addAnimation(sprite);
        sprite.setLooping(false);
        sprite.setCurrentFrame(5);
        speedAngle = 180;
    }

    public void updateData(EntityData data) {
        this.worldX = data.xPosition;
        this.worldY = data.yPosition;
        this.speedAngle = data.direction;
        this.sprite.setCurrentFrame(data.animationFrame);
    }
}
