package gravity;

import jig.Entity;
import jig.ResourceManager;
import org.newdawn.slick.Animation;

public class Vehicle extends Entity {
    Animation sprite;

    public Vehicle(float x, float y) {
        super(x, y);

        sprite = new Animation(ResourceManager.getSpriteSheet(GravGame.VEHICLE_ANIM_RSC, 64, 64),
                0, 0, 7, 0, true, 160, true);
        this.addAnimation(sprite);
        sprite.setLooping(false);
        sprite.setCurrentFrame(0);
    }
}
