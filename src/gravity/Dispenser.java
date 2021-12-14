package gravity;

import jig.Vector;

public class Dispenser {
    public Vector position;
    public int timer;
    public boolean canDispense;
    public boolean hasPowerup;
    public static final int powerupSpawnDelay = 2000;

    public Dispenser(Vector position) {
        this.position = position;
        this.hasPowerup = true;
    }

    public void decreaseTimer(int delta) {
        if(!hasPowerup) {
            timer -= delta;
            canDispense = timer <= 0;
        }
    }
}
