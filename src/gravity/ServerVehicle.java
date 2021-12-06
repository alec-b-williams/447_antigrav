package gravity;

import jig.Vector;

public class ServerVehicle {
    public float worldX;
    public float worldY;
    private Vector speed;
    public double speedAngle;
    public boolean backUp;
    public int frame;

    private static final float degPerSecond = 180;

    public ServerVehicle(float x, float y){
        this.worldX = x;
        this.worldY = y;
        this.speed = new Vector(0, 0);
        this.speedAngle = 180;
        this.backUp = false;
    }

    public void linearMovement(int dir, float initLen, float speedLimit, float speedScale){
        if(this.speed.length() == 0){
            this.setSpeed(Vector.getVector(this.speedAngle, initLen));
        }
        else if(this.speed.length() < speedLimit){
            this.setSpeed(this.speed.scale(speedScale));
        }
        if(this.speed.length() > 0.2f){
            this.setSpeed(Vector.getVector(this.speedAngle, speedLimit));
        }
        //add collision
        worldX += dir * this.speed.getX();
        worldY += dir * this.speed.getY();
        //float newX = worldX + dir * this.speed.getX();
        //float newY = worldY + dir * this.speed.getY();
    }

    public void finishMovement(int dir, float slowdownScale, float stopThreshold){
        System.out.println("Old Length: " + this.speed.length());
        this.setSpeed(this.speed.scale(slowdownScale));
        if(this.speed.length() < stopThreshold){
            this.setSpeed(new Vector(0,0));
            this.backUp = !this.backUp;
        }
        //add collision
        worldX += dir * this.speed.getX();
        worldY += dir * this.speed.getY();
    }

    public void turn(int dir, int delta){
        float newAngle = (float)(this.speedAngle + (degPerSecond * (delta/1000.0f) * dir));
        System.out.println("New Angle: " + newAngle);
        float angleDiff = newAngle - (float)this.speedAngle;
        System.out.println("angleDiff: " + angleDiff);

        this.speed = this.speed.rotate(angleDiff);
        speedAngle = newAngle % 360;

        int num =  (int)(newAngle) + 105;
        //System.out.println("num: " + num);
        //System.out.println("Frame: " + ((num / 45) + 5) % 8);
        frame = Math.floorMod(((int)(num / 22.5) + 6), 16);

    }

    public void setSpeed(Vector speed){
        this.speed = speed;
    }

    public Vector getSpeed(){
        return this.speed;
    }
}
