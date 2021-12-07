package gravity;

import jig.*;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;

public class ServerVehicle extends Entity {
    public float worldX;
    public float worldY;
    private Vector speed;
    public double speedAngle;
    public boolean backUp;
    public int frame;

    private static final float degPerSecond = 180;

    public ServerVehicle(float x, float y) {
        super(x, y);

        this.worldX = x;
        this.worldY = y;
        this.speed = new Vector(0, 0);
        this.speedAngle = 180;
        this.backUp = false;

        Shape boundingCircle = new ConvexPolygon(24.0f/32.0f);
        this.addShape(boundingCircle);
        this.setCoarseGrainedRadius(1);

        setRotationFrame((float)speedAngle);
    }

    public void linearMovement(int dir, float initLen, float speedLimit, float speedScale, TiledMap map){
        if(this.speed.length() == 0){
            this.setSpeed(Vector.getVector(this.speedAngle, initLen));
        }
        else if(this.speed.length() < speedLimit) {
            this.setSpeed(this.speed.scale(speedScale));
        }
        if(this.speed.length() > 0.2f){
            this.setSpeed(Vector.getVector(this.speedAngle, speedLimit));
        }

        move(dir, map);
    }

    public void finishMovement(int dir, float slowdownScale, float stopThreshold, TiledMap map){
        this.setSpeed(this.speed.scale(slowdownScale));
        if(this.speed.length() < stopThreshold){
            this.setSpeed(new Vector(0,0));
            this.backUp = !this.backUp;
        }

        move(dir, map);
    }

    public void move(int dir, TiledMap map) {
        float newX = worldX + dir * this.speed.getX();
        float newY = worldY + dir * this.speed.getY();

        ArrayList<Entity> walls = new ArrayList<Entity>();
        Vector finalCollision = new Vector(0,0);

        //generate list of walls adjacent to new square
        for (int i = ((int)newX - 1); i <= ((int)newX + 1); i++) {
            for (int j = ((int)newY - 1); j <= ((int)newY + 1); j++) {
                if ((i>=0 && j>=0) && map.getTileId(i, j, 0) == 2) {
                    Entity wall = new Entity(i, j);
                    wall.setCoarseGrainedRadius(1);
                    wall.addShape(new ConvexPolygon(1, 1.0f));

                    walls.add(wall);
                }
            }
        }

        //check walls for collision
        for (Entity wall : walls) {
            Collision wallCollision = this.collides(wall);

            if (wallCollision != null && wallCollision.getMinPenetration() != null) {
                finalCollision = (wallCollision.getMinPenetration());
                System.out.println("Collision detected!");
                System.out.println(this.speed.getX() + ", " + this.speed.getY());
                System.out.println(finalCollision.getX() + ", " + finalCollision.getY() + ", " + finalCollision.getRotation());
            }
        }

        //bounce vehicle
        if (finalCollision.length() != 0) {
            this.speed = this.speed.bounce((float)finalCollision.getRotation()+90);
        }

        worldX += dir * this.speed.getX();
        worldY += dir * this.speed.getY();

        this.setX(worldX);
        this.setY(worldY);

        setRotationFrame((float)speedAngle);
    }

    public void turn(int dir, int delta){
        float newAngle = (float)(this.speedAngle + (degPerSecond * (delta/1000.0f) * dir));
        float angleDiff = newAngle - (float)this.speedAngle;

        this.speed = this.speed.rotate(angleDiff);
        speedAngle = newAngle % 360;
        setRotationFrame(newAngle);
    }

    public void setSpeed(Vector speed){
        this.speed = speed;
    }

    public Vector getSpeed(){
        return this.speed;
    }

    public void setRotationFrame(float angle) {
        int num =  (int)(angle) + 205;
        frame = ((num / 45) + 5) % 8;
    }
}
