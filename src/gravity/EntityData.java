package gravity;

import java.io.Serializable;

public class EntityData implements Serializable {
    public String entityType;
    public int id;
    public float xPosition;
    public float yPosition;
    public double direction;
    public float height;
    public int animationFrame;
    public boolean isKill;
    public float timer;
    public int lap;
    public int powerupType;

    public EntityData(String entityType, int id, float xPosition, float yPosition, double direction, float height, int animationFrame) {
        this.entityType = entityType;
        this.id = id;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.direction = direction;
        this.height = height;
        this.animationFrame = animationFrame;
    }

    public EntityData(ServerVehicle vehicle, int id) {
        this.entityType = "Player";
        this.id = id;
        this.xPosition = vehicle.worldX;
        this.yPosition = vehicle.worldY;
        this.direction = vehicle.speedAngle;
        this.height = vehicle.height;
        this.animationFrame = vehicle.frame;
        this.isKill = vehicle.isKill;
        this.lap = vehicle.lap;
        this.timer = vehicle.timer;
        this.powerupType = vehicle.powerupTypeHeld;
    }

    public EntityData(Powerup power, int id) {
        this.entityType = "Powerup";
        this.id = id;
        this.xPosition = power.worldX;
        this.yPosition = power.worldY;
        this.powerupType = power.type;
    }

    public EntityData(SpikeTrap spikeTrap, int id) {
        this.entityType = "SpikeTrap";
        this.id = id;
        this.xPosition = spikeTrap.worldX;
        this.yPosition = spikeTrap.worldY;
    }

    public EntityData(Rocket rocket, int id) {
        this.entityType = "Rocket";
        this.id = id;
        this.xPosition = rocket.worldX;
        this.yPosition = rocket.worldY;
    }
}
