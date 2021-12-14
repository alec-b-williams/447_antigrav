package gravity;

import jig.*;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerVehicle extends GameObject {
    public static float forwardSpeedLimit = 0.2f;
    public static float reverseSpeedLimit = 0.05f;
    public static float slowdownScale = 0.98f;
    public static float stopThreshold = 0.02f;
    public static float boostMult = 1.5f;
    public static float slowMult = 0.5f;

    public float worldX;
    public float worldY;
    private Vector speed;
    public double speedAngle;
    public float height;
    public float verticalMomentum;
    public boolean isKill;
    private Vector lastTile;
    private double lastHeading;
    public float deathCooldown;
    public float boostCooldown;
    public int lap;
    public float timer;
    public boolean checkpoint; 
    private float health;
    public int powerupTypeHeld;

    private static final float degPerSecond = 180;

    public ServerVehicle(float x, float y) {
        super(x, y);

        this.worldX = x;
        this.worldY = y;
        this.speed = new Vector(0, 0);
        this.speedAngle = 90;
        this.height = 0;
        this.isKill = false;
        this.lastTile = new Vector(x, y);
        this.deathCooldown = 0;
        this.boostCooldown = 0;
        this.lap = 1;
        this.timer = 0;
        this.checkpoint = false;
        this.health = 100;

        Shape boundingCircle = new ConvexPolygon(12.0f/32.0f);
        this.addShape(boundingCircle);
        this.setCoarseGrainedRadius(1);

        powerupTypeHeld = -1;
    }

    public void linearMovement(int dir, int delta, TiledMap map){
        float speedLimit = (dir > 0 ? forwardSpeedLimit : reverseSpeedLimit);
        this.speed = this.getSpeed().add(Vector.getVector(this.speedAngle + ((dir == -1) ? 180 : 0) , .02f));

        if (this.speed.length() > speedLimit)
            this.setSpeed(this.getSpeed().setLength(speedLimit));

        move(delta, map);
    }

    public void finishMovement(int delta, TiledMap map){
        if (height == 0) {
            this.setSpeed(this.speed.scale(slowdownScale));
            if(this.speed.length() < stopThreshold){
                this.setSpeed(new Vector(0,0));
            }
        }

        move(delta, map);
    }

    public void move(int delta, TiledMap map) {
        this.setX(worldX + this.speed.getX());
        this.setY(worldY + this.speed.getY());
        int newX = (int)(this.getX() + .5);
        int newY = (int)(this.getY() + .5);

        calcCollision(newX, newY, map);
        calcHeight(newX, newY, map);

        int tileID = safeTileID(newX, newY, map);
        boolean slow = isSlow(tileID);
        boolean boost = (boostCooldown > 0 || isBoost(tileID));

        if (tileID == GravGame.CHECKPOINT) {
            checkpoint = true;
            lastTile = new Vector(newX, newY);
            lastHeading = speedAngle;
            System.out.println("Checkpoint! lap " + (lap));
        }

        if (checkpoint && tileID == GravGame.FINISH) {
            checkpoint = false;
            lastTile = new Vector(newX, newY);
            lastHeading = speedAngle;
            lap++;
            setHealth(getHealth() + 50);
            System.out.println("Completed lap " + (lap-1) + "! Time: " + timer);
        }

        worldX += this.speed.getX() * (slow ? slowMult : 1) * (boost ? boostMult : 1) * (deathCooldown > 0 ? 0 : 1);
        worldY += this.speed.getY() * (slow ? slowMult : 1) * (boost ? boostMult : 1) * (deathCooldown > 0 ? 0 : 1);

        boostCooldown -= delta;
        deathCooldown -= delta;
        timer += delta;
    }

    private void calcCollision(int newX, int newY, TiledMap map) {
        if (height == 0) {
            boolean bounced = false;
            ArrayList<Vector> collisions = getWallCollisions(newX, newY, map, true);

            //bounce vehicle
            for (Vector collision : collisions) {
                if (collision.length() != 0) {
                    Vector oldSpeed = this.speed;
                    this.speed = this.speed.bounce((float)collision.getRotation()+90).scale(1f);
                    bounced = true;
                    damagePlayer(oldSpeed);
                }
            }

            if (!bounced) {
                collisions = getWallCollisions(newX, newY, map, false);

                for (Vector collision : collisions) {
                    if (collision.length() != 0) {
                        Vector oldSpeed = this.speed;
                        this.speed = this.speed.bounce((float)collision.getRotation()+90).scale(1f);
                        damagePlayer(oldSpeed);
                    }
                }
            }
        }
    }

    private ArrayList<Vector> getWallCollisions(int x, int y, TiledMap map, boolean adj) {
        ArrayList<Entity> walls = new ArrayList<>();
        ArrayList<Vector> collisions = new ArrayList<>();

        if (isWall(x, y, map)) {
            return collisions;
        }

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

        //check walls for collision
        for (Entity wall : walls) {
            Collision wallCollision = this.collides(wall);

            if (wallCollision != null && wallCollision.getMinPenetration() != null
                    && this.speed.unit().dot(new Vector(wall.getX() - this.getX(), wall.getY() - this.getY())) > 0) {
                collisions.add(wallCollision.getMinPenetration().scale(0.5f));
            }
        }

        return collisions;
    }

    public ArrayList<Integer> getGameObjectCollisions(ConcurrentHashMap<Integer, GameObject> gameObjects) {
        Set<Integer> keys = gameObjects.keySet();
        ArrayList<Integer> collidedObjectKeys = new ArrayList<>();
        for(Integer key: keys) {
            if(this.collides(gameObjects.get(key)) != null) collidedObjectKeys.add(key);
        }
        return collidedObjectKeys;
    }

    public void damagePlayer(Vector oldSpeed) {
        float cross = this.speed.unit().cross(oldSpeed.unit());
        cross = Math.abs(cross - 1);
        System.out.println("collision cross: " + cross + ", len: " + this.speed.length());
        setHealth(this.getHealth() - (this.speed.length() * cross * 20));
    }

    private Entity newWall(int i, int j) {
        Entity wall = new Entity(i, j);
        wall.addShape(new ConvexPolygon(1, 1.0f));
        wall.setCoarseGrainedRadius(1);
        return wall;
    }

    private boolean isWall(int i, int j, TiledMap map) {
        return (safeTileID(i, j, map) == GravGame.WALL);
    }

    private int safeTileID(int x, int y, TiledMap map) {
        if (x < 0 || y < 0)
            return -1;
        else
            return map.getTileId(x, y, 0);
    }

    private void calcHeight(int newX, int newY, TiledMap map) {
        if (height == 0 && safeTileID(newX, newY, map) == GravGame.JUMP) {
            verticalMomentum = .2f;
        }

        verticalMomentum -= .0075f;
        height += verticalMomentum;

        if (height < 0) {
            if (!isKill && (safeTileID(newX, newY, map) == GravGame.VOID || newX < 0 || newY < 0)) {
                isKill = true;
            } else if (isKill) {
                if (height < -8) {
                    setHealth(getHealth() - 10);
                    resetPlayer();
                }
            } else {
                height = 0;
                verticalMomentum = 0;
            }
        }
    }

    private void resetPlayer() {
        this.setX(lastTile.getX());
        this.setY(lastTile.getY());
        worldX = lastTile.getX();
        worldY = lastTile.getY();
        speedAngle = lastHeading;
        this.speed = new Vector(0,0);
        this.height = 0;
        this.verticalMomentum = 0;
        isKill = false;
        this.boostCooldown = 0;
        if (health <= 0) {
            this.deathCooldown = 5000;
            this.health = 100;
        } else {
            this.deathCooldown = 2000;
        }
    }

    public void boost(int delta) {
        if (speed.length() != 0 && boostCooldown < 0) {
            setHealth(getHealth() - ((delta/1000.0f) * 33) );
            boostCooldown = delta;
        }
    }

    public void turn(int dir, int delta){
        float newAngle = (float)(this.speedAngle + (degPerSecond * (delta/1000.0f) * dir));
        speedAngle = Math.floorMod((int)newAngle, 360);
    }

    private boolean isSlow(int tileID) {
        return tileID == GravGame.SLOW_A || tileID == GravGame.SLOW_B;
    }

    private boolean isBoost(int tileID) {
        if (tileID == GravGame.BOOST_N || tileID == GravGame.BOOST_E
                || tileID == GravGame.BOOST_S || tileID == GravGame.BOOST_W) {
            boostCooldown = 1000;
            return true;
        } else return false;
    }

    public void setSpeed(Vector speed){
        this.speed = speed;
    }

    public Vector getSpeed(){
        return this.speed;
    }

    public float getHealth() {
        return this.health;
    }

    public void setHealth(float _health) {
        if (_health < 0)
            health = 0;
        else if (_health > 100)
            health = 100;
        else
            health = _health;

        if (health == 0)
            resetPlayer();
    }
}
