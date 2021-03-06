package gravity;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

public class Vehicle extends Entity {
    Animation sprite;
    public float worldX;
    public float worldY;
    private Vector speed;
    public double speedAngle;
    private boolean backUp;
    private GravGame gg;

    private static final float degPerSecond = 180;

    public Vehicle(float x, float y, GravGame _gg) {
        super(GravGame._SCREENWIDTH/2.0f, GravGame._SCREENHEIGHT/2.0f);

        worldX = x;
        worldY = y;
        gg = _gg;

        sprite = new Animation(ResourceManager.getSpriteSheet(GravGame.VEHICLE_ANIM_RSC, 64, 64),
                0, 0, 7, 0, true, 160, false);
        this.addAnimation(sprite);
        sprite.setLooping(false);
        sprite.setCurrentFrame(5);
        speed = new Vector(0, 0);
        speedAngle = 180;
        backUp = false;
    }

    public void update(GameContainer container, int delta){
        Input input = container.getInput();
        if(input.isKeyDown(Input.KEY_W)){
            if(this.backUp && this.speed.length() > 0){
                this.finishMovement(-1, 0.6f, 0.2f * 0.01f);
            }
            this.linearMovement(1, 0.06f, 0.2f, 1.1f);
        }
        if(input.isKeyDown(Input.KEY_S)){
            if(!this.backUp && this.speed.length() > 0){
                this.finishMovement(1, 0.6f, 0.2f * 0.01f);
            }
            this.linearMovement(-1, 0.01f, 0.05f, 1.05f);
        }
        if(input.isKeyDown(Input.KEY_A)){
            this.turn(-1, delta);
        }
        if(input.isKeyDown(Input.KEY_D)){
            this.turn(1, delta);
        }
        if(!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S)){
            if(this.backUp && this.speed.length() > 0){
                this.finishMovement(-1, 0.99f, 0.05f * 0.05f);
            }
            else if(!this.backUp && this.speed.length() > 0){
                this.finishMovement(1, 0.98f, 0.2f * 0.01f);
            }
        }
    }

    public void finishMovement(int dir, float slowdownScale, float stopThreshold){
        this.setSpeed(this.speed.scale(slowdownScale));
        if(this.speed.length() < stopThreshold){
            this.setSpeed(new Vector(0, 0));
            this.backUp = !this.backUp;
        }
        /*this.worldY += dir * this.speed.getY();
        this.worldX += dir * this.speed.getX();*/

        float newX = worldX + dir * this.speed.getX();
        float newY = worldY + dir * this.speed.getY();
        int newTileID = gg.map.getTileId((int)newX, (int)newY, 0);

        //System.out.println(newTileID);
        //System.out.println(gg.map.getTileProperty(newTileID, "traversable", "false"));

        if ((newTileID >= 0) && (gg.map.getTileProperty(newTileID, "traversable", "false").equals("true"))) {
            this.worldY += dir * this.speed.getY();
            this.worldX += dir * this.speed.getX();
        }
    }

    public void linearMovement(int dir, float initLen, float speedLimit, float speedScale){
        if(this.speed.length() == 0){
            this.setSpeed(Vector.getVector(this.speedAngle, initLen));
        }
        else if(this.speed.length() < speedLimit){
            this.setSpeed(this.speed.scale(speedScale));
        }
        if(this.speed.length() > speedLimit){
            this.setSpeed(Vector.getVector(this.speedAngle, speedLimit));
        }
        float newX = worldX + dir * this.speed.getX();
        float newY = worldY + dir * this.speed.getY();
        int newTileID = gg.map.getTileId((int)newX, (int)newY, 0);

        //System.out.println(newTileID);
        //System.out.println(gg.map.getTileProperty(newTileID, "traversable", "false"));

        if ((newTileID >= 0) && (gg.map.getTileProperty(newTileID, "traversable", "false").equals("true"))) {
            this.worldY += dir * this.speed.getY();
            this.worldX += dir * this.speed.getX();
        }
    }

    public void setSpeed(Vector speed){
        this.speed = speed;
    }

    public void turn(int dir, int delta) {
        float newAngle = (float)(speedAngle + (degPerSecond * (delta/1000.0f) * dir));
        float angleDiff = newAngle - (float)speedAngle;

        this.speed = this.speed.rotate(angleDiff);
        speedAngle = newAngle % 360;

        int num = (int)(newAngle) + 205;
        int index = ((num / 45) + 5) % 8;
        //System.out.println("Angle: " + newAngle + ", index: " + index);
        this.sprite.setCurrentFrame(index);
    }
}
