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
    public float health;
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
        if(!vehicle.destroy) this.entityType = "Player";
        else this.entityType = "null";
        this.id = id;
        this.xPosition = vehicle.worldX;
        this.yPosition = vehicle.worldY;
        this.direction = vehicle.speedAngle;
        this.height = vehicle.height;
        this.animationFrame = vehicle.frame;
        this.isKill = vehicle.isKill;
        this.lap = vehicle.lap;
        this.timer = vehicle.timer;
        this.health = vehicle.getHealth();
        this.powerupType = vehicle.powerupTypeHeld;
    }

    public EntityData(Powerup power, int id) {
        if(!power.destroy) this.entityType = "Powerup";
        else this.entityType = "null";
        this.id = id;
        this.xPosition = power.worldX;
        this.yPosition = power.worldY;
        this.powerupType = power.type;
    }

    public EntityData(SpikeTrap spikeTrap, int id) {
        if(!spikeTrap.destroy) this.entityType = "SpikeTrap";
        else this.entityType = "null";
        this.id = id;
        this.xPosition = spikeTrap.worldX;
        this.yPosition = spikeTrap.worldY;
    }

    public EntityData(Rocket rocket, int id) {
        if(!rocket.destroy) this.entityType = "Rocket";
        else this.entityType = "null";
        this.id = id;
        this.xPosition = rocket.worldX;
        this.yPosition = rocket.worldY;
    }
}
