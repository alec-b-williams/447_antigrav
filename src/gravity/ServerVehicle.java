package gravity;

import jig.*;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.tiled.TiledMap;

import java.lang.reflect.Array;
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

        Shape boundingCircle = new ConvexPolygon(12.0f/32.0f);
        this.addShape(boundingCircle);
        this.setCoarseGrainedRadius(1);

        setRotationFrame((float)speedAngle);
    }

    public void linearMovement(int dir, float initLen, float speedLimit, TiledMap map){
        if(this.speed.length() == 0){
            this.setSpeed(Vector.getVector(this.speedAngle, initLen));
        }
        else {
            Vector oldSpeed = this.getSpeed();
            this.speed = oldSpeed.add(Vector.getVector(this.speedAngle + ((dir == -1) ? 180 : 0) , .02f));
        }

        if (this.speed.length() > 0.2f) {
            this.setSpeed(this.getSpeed().setLength(speedLimit));
        }

        move(map);
    }

    public void finishMovement(float slowdownScale, float stopThreshold, TiledMap map){
        this.setSpeed(this.speed.scale(slowdownScale));
        if(this.speed.length() < stopThreshold){
            this.setSpeed(new Vector(0,0));
            this.backUp = !this.backUp;
        }

        move(map);
    }

    public void move(TiledMap map) {
        this.setX(worldX + this.speed.getX());
        this.setY(worldY + this.speed.getY());
        int newX = (int)(this.getX() + .5);
        int newY = (int)(this.getY() + .5);
        //int newX = (int)(worldX + (this.speed.getX() + .5));
        //int newY = (int)(worldY + (this.speed.getY() + .5));

        boolean bounced = false;
        ArrayList<Vector> collisions = getWallCollisions(newX, newY, map, true);

        //bounce vehicle
        for (Vector collision : collisions) {
            if (collision.length() != 0) {
                System.out.println("Collided with adjacent");
                this.speed = this.speed.bounce((float)collision.getRotation()+90).scale(1f);
                bounced = true;
            }
        }

        if (!bounced) {
            collisions = getWallCollisions(newX, newY, map, false);

            for (Vector collision : collisions) {
                if (collision.length() != 0) {
                    System.out.println("Collided with corner");
                    this.speed = this.speed.bounce((float)collision.getRotation()+90).scale(1f);
                }
            }
        }

        worldX += this.speed.getX();
        worldY += this.speed.getY();

        //this.setX(worldX);
        //this.setY(worldY);

        setRotationFrame((float)speedAngle);
    }

    public void turn(int dir, int delta){
        float newAngle = (float)(this.speedAngle + (degPerSecond * (delta/1000.0f) * dir));
        speedAngle = Math.floorMod((int)newAngle, 360);
        setRotationFrame(newAngle);
    }

    public void setSpeed(Vector speed){
        this.speed = speed;
    }

    public Vector getSpeed(){
        return this.speed;
    }

    public void setRotationFrame(float angle) {
        int num =  (int)(angle) + 110;
        frame = Math.floorMod(((int)(num / 22.5) + 6), 16);
    }

    private ArrayList<Vector> getWallCollisions(int x, int y, TiledMap map, boolean adj) {
        ArrayList<Entity> walls = new ArrayList<>();

        if (adj) {
            if (isWall(x-1, y, map)) walls.add(newWall((x-1), y));
            if (isWall(x+1, y, map)) walls.add(newWall((x+1), y));
            if (isWall(x, y-1, map)) walls.add(newWall((x), y-1));
            if (isWall(x, y+1, map)) walls.add(newWall((x), y+1));
        } else {
            if (isWall(x-1, y-1, map)) walls.add(newWall(x-1, y-1));
            if (isWall(x+1, y-1, map)) walls.add(newWall(x+1, y-1));
            if (isWall(x-1, y+1, map)) walls.add(newWall(x-1, y+1));
            if (isWall(x+1, y+1, map)) walls.add(newWall(x+1, y+1));
        }

        ArrayList<Vector> collisions = new ArrayList<>();

        //check walls for collision
        for (Entity wall : walls) {
            Collision wallCollision = this.collides(wall);

            if (wallCollision != null && wallCollision.getMinPenetration() != null
                    && this.speed.dot(new Vector(wall.getX()+.5f - worldX, wall.getY()+.5f - worldY)) > -0.5) {
                collisions.add(wallCollision.getMinPenetration().scale(0.5f));
            }
        }

        return collisions;
    }

    private Entity newWall(int i, int j) {
        Entity wall = new Entity(i, j);
        wall.addShape(new ConvexPolygon(1, 1.0f));
        wall.setCoarseGrainedRadius(1);
        return wall;
    }

    private boolean isWall(int i, int j, TiledMap map) {
        if (i < 0 || j < 0)
            return false;

        return (map.getTileId(i, j, 0) == 2);
    }
}
