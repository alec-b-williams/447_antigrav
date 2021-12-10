package gravity;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Shape;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Transform;

public class Vehicle extends GameObject {
    Animation sprite;
    public double speedAngle;
    public int playerNumber;
    public boolean isKill;

    public Vehicle(float x, float y, int _playerNumber) {
        super(x, y);

        playerNumber = _playerNumber;
        sprite = new Animation(ResourceManager.getSpriteSheet(GravGame.vehicleImages[_playerNumber], 64, 64),
                0, 0, 15, 0, true, 160, false);

        this.addAnimation(sprite);
        sprite.setLooping(false);
        sprite.setCurrentFrame(5);
        speedAngle = 180;
        isKill = false;
    }

    public void updateData(EntityData data) {
        this.worldX = data.xPosition;
        this.worldY = data.yPosition;
        this.speedAngle = data.direction;
        this.height = data.height;
        this.setY((GravGame._SCREENHEIGHT/2.0f) - (height * 32));
        this.isKill = data.isKill;
        this.sprite.setCurrentFrame(data.animationFrame);
    }
}
