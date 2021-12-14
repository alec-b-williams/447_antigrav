package gravity;

import jig.Entity;

public class GameObject extends Entity {
    public float worldX;
    public float worldY;
    public float height;
    public boolean destroy;

    public GameObject(float x, float y) {
        super(GravGame._SCREENWIDTH/2.0f, GravGame._SCREENHEIGHT/2.0f);
        this.worldX = x;
        this.worldY = y;
        this.height = 0;
        this.destroy = false;
    }
}
