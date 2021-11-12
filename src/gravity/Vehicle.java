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
    private float angleThreshold[] = new float[]{22.5f, 67.5f, 112.5f, 157.5f, -157.5f, -112.5f, -67.5f, -22.5f};
    private int thres1;
    private int thres2;
    private double currentAngle;

    public Vehicle(float x, float y) {
        super(GravGame._SCREENWIDTH/2.0f, GravGame._SCREENHEIGHT/2.0f);

        worldX = x;
        worldY = y;
        sprite = new Animation(ResourceManager.getSpriteSheet(GravGame.VEHICLE_ANIM_RSC, 64, 64),
                0, 0, 7, 0, true, 160, false);
        this.addAnimation(sprite);
        sprite.setLooping(false);
        sprite.setCurrentFrame(5);
        speed = new Vector(-.2f, 0);
        thres1 = 3;
        thres2 = 4;
        currentAngle = this.speed.getRotation();
    }

    public void update(GameContainer container, GravGame gg, int delta){

    }
    public Vector getSpeed(){
        return speed;
    }

    public void setSpeedRotation(double theta){
        this.speed = this.speed.rotate(theta);
    }

    public float getThres1Angle(){
        return angleThreshold[thres1];
    }

    public float getThres2Angle(){
        return angleThreshold[thres2];
    }

    public int thresLength(){
        return angleThreshold.length;
    }

    public int getThres1(){
        return this.thres1;
    }

    public int getThres2(){
        return this.thres2;
    }

    public void setThres1(int threshold){
        this.thres1 = threshold;
    }

    public void setThres2(int threshold){
        this.thres2 = threshold;
    }

    public void setCurrentAngle(double angleTurn){
        if(this.currentAngle + angleTurn > 180){
            this.currentAngle = angleTurn - this.currentAngle;
        }
        else if(this.currentAngle + angleTurn < -180){
            this.currentAngle = abs(this.currentAngle) + angleTurn;
        }
        else
            this.currentAngle += angleTurn;

    }

    public void matchSpeedAngle(){
        this.currentAngle = this.speed.getRotation();
    }

    public double getCurrentAngle(){
        return this.currentAngle;
    }

}
