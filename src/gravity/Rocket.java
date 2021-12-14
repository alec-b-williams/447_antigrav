package gravity;

import jig.Collision;
import jig.ConvexPolygon;
import jig.Entity;
import jig.Vector;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;

public class Rocket extends GameObject {
    public int id;
    public int placedById;
    public Vector speed;

    public Rocket(float x, float y, int id) {
        super(x, y);
        this.setX(x);
        this.setY(y);
        this.id = id;
        this.addShape(new ConvexPolygon(1, 1.0f));
        this.setCoarseGrainedRadius(1);
    }

    public void updateData(EntityData data) {
        this.worldX = data.xPosition;
        this.worldY = data.yPosition;
        this.id = data.id;
    }

    public void move(int delta, TiledMap map) {
        this.setX(worldX + this.speed.getX());
        this.setY(worldY + this.speed.getY());
        int newX = (int) (this.getX() + .5);
        int newY = (int) (this.getY() + .5);

        calcCollision(newX, newY, map);
        worldX += this.speed.getX();
        worldY += this.speed.getY();
    }

    private void calcCollision(int newX, int newY, TiledMap map) {
        if (height == 0) {
            boolean bounced = false;
            ArrayList<Vector> collisions = getWallCollisions(newX, newY, map, true);

            //bounce vehicle
            for (Vector collision : collisions) {
                if (collision.length() != 0) {
                    //System.out.println("Collided with adjacent");
                    this.speed = this.speed.bounce((float)collision.getRotation()+90).scale(1f);
                    bounced = true;
                }
            }

            if (!bounced) {
                collisions = getWallCollisions(newX, newY, map, false);

                for (Vector collision : collisions) {
                    if (collision.length() != 0) {
                        //System.out.println("Collided with corner");
                        this.speed = this.speed.bounce((float)collision.getRotation()+90).scale(1f);
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

    private boolean isWall(int i, int j, TiledMap map) {
        return (safeTileID(i, j, map) == GravGame.WALL);
    }

    private int safeTileID(int x, int y, TiledMap map) {
        if (x < 0 || y < 0)
            return -1;
        else
            return map.getTileId(x, y, 0);
    }

    private Entity newWall(int i, int j) {
        Entity wall = new Entity(i, j);
        wall.addShape(new ConvexPolygon(1, 1.0f));
        wall.setCoarseGrainedRadius(1);
        return wall;
    }
}
