package gravity.resource;

import jig.Vector;

public class ServerVehicle {
    public float worldX;
    public float worldY;
    private Vector speed;
    public double speedAngle;
    private boolean backUp;

    private static final float degPerSecond = 180;

    public ServerVehicle(float x, float y){
        this.worldX = x;
        this.worldY = y;
        this.speed = new Vector(1, 0);
        this.speedAngle = 180;
        this.backUp = false;
    }

    public void serverForward(float speedX, float speedY){
        this.worldY += speedY;
        this.worldX += speedX;
    }
}
