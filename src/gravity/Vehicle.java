package gravity;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;

import static java.lang.Math.abs;

public class Vehicle extends Entity {
    Animation sprite;
    public float worldX;
    public float worldY;
    private Vector speed;
    private double speedAngle;
    private int turnCooldown;

    public Vehicle(float x, float y) {
        super(GravGame._SCREENWIDTH/2.0f, GravGame._SCREENHEIGHT/2.0f);

        worldX = x;
        worldY = y;
        sprite = new Animation(ResourceManager.getSpriteSheet(GravGame.VEHICLE_ANIM_RSC, 64, 64),
                0, 0, 7, 0, true, 160, false);
        this.addAnimation(sprite);
        sprite.setLooping(false);
        sprite.setCurrentFrame(5);
        speed = new Vector(0, 0);
        speedAngle = 180;
        turnCooldown = 1000;
    }

    public void update(GameContainer container, GravGame gg, int delta){

    }

    public int getTurnCooldown(){
        return this.turnCooldown;
    }

    public void setTurnCooldown(int delta){
        this.turnCooldown += delta;
    }

    public void resetCooldown(){
        this.turnCooldown = 0;
    }

    public Vector getSpeed(){
        return speed;
    }

    public void setSpeed(Vector speed){
        this.speed = speed;
    }

    public void setSpeedRotation(double theta){
        this.speed = this.speed.rotate(theta);
        this.speedAngle = this.speed.getRotation();
    }

    public void initSpeedAngle(double theta){
        this.speedAngle = theta;
    }

    public double getSpeedAngle(){
        return this.speedAngle;
    }

}
