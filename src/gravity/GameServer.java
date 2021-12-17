package gravity;

import jig.Collision;
import jig.Entity;
import jig.Vector;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer {

    private ServerSocket server;
    private int numPlayers;
    private final int maxPlayers;
    private final ArrayList<ClientHandler> handlers = new ArrayList<>();

    private TiledMap currentMap;
    private final ConcurrentHashMap<Integer, Dispenser> dispensers;

    private final ConcurrentHashMap<Integer, GameObject> gameObjects = new ConcurrentHashMap<>();
    private final AtomicInteger entityId = new AtomicInteger();

    ArrayList<Vector> playerSpawnLocations;

    public GameServer() throws SlickException {
        dispensers = new ConcurrentHashMap<>();
        playerSpawnLocations = new ArrayList<>();
        try {
            this.server = new ServerSocket(9158);
        } catch (IOException e){
            System.out.println("IOException in GS constructor");
            e.printStackTrace();
        }

        System.out.println("Game Server spinning up!");
        numPlayers = 0;
        maxPlayers = 1;
        Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);

        acceptConnections();
    }

    private void acceptConnections(){
        try{
            System.out.println("Waiting for connections...");
            while(this.numPlayers < this.maxPlayers){
                Socket socket = server.accept();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                this.numPlayers++;

                out.writeInt(numPlayers);
                out.writeInt(maxPlayers);
                out.flush();
                System.out.println("Player #" + numPlayers + " has connected");

                handlers.add(new ClientHandler(this.numPlayers, in, out));
            }
            System.out.println("Max Players Reached");
            int levelSelected = 0;
            for(ClientHandler handler: handlers) {
                if(handler.playerId == 1) {
                    levelSelected = handler.dataIn.readInt();
                }
            }
            currentMap = new TiledMap(GravGame.tileMaps[levelSelected], false);
            for (ClientHandler handler: handlers) {
                System.out.println("Writing level selected: " + levelSelected);
                handler.dataOut.writeInt(levelSelected);
                handler.sendStartMsg();
            }

            int mapWidth = currentMap.getWidth();
            int mapHeight = currentMap.getHeight();

            entityId.set(maxPlayers + 1);

            int dispenserId = 0;
            for(int x = 0; x < mapWidth; x++) {
                for(int y = 0; y < mapHeight; y++) {
                    if(currentMap.getTileId(x, y, 0) == GravGame.DISPENSER) {
                        Vector location = new Vector(x, y);
                        gameObjects.put(entityId.get(), new Powerup(x, y, entityId.get(), dispenserId));
                        dispensers.put(dispenserId, new Dispenser(location));
                        entityId.incrementAndGet();
                        dispenserId++;
                    }
                    else if(currentMap.getTileId(x, y, 0) == GravGame.PLAYER_SPAWN) {
                        System.out.println("Added spawn location at " + x + ", " + y);
                        playerSpawnLocations.add(new Vector(x, y));
                    }
                }
            }
            for(ClientHandler handler: handlers) {
                Vector spawnLocation = playerSpawnLocations.remove(0);
                System.out.println("Spawn location for player " + handler.playerId + " created at " + spawnLocation.getX() + ", " + spawnLocation.getY());
                gameObjects.put(handler.playerId, new ServerVehicle(spawnLocation.getX(), spawnLocation.getY(), levelSelected));
                handler.player = (ServerVehicle) gameObjects.get(handler.playerId);
                System.out.println("Player: " + handler.player);
                Thread playerThread = new Thread(handler);
                playerThread.start();
            }

        } catch (IOException | SlickException e){
            System.out.println("IOException in acceptConnections()");
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private final int playerId;
        private final ObjectInputStream dataIn;
        private final ObjectOutputStream dataOut;
        public ServerVehicle player;

        public ClientHandler(int playerId, ObjectInputStream in, ObjectOutputStream out){
            this.playerId = playerId;
            this.dataIn = in;
            this.dataOut = out;
        }

        @Override
        public void run() {
            try{
                while(true){
                    String command = dataIn.readUTF();
                    if ("W".equals(command) || "S".equals(command) || "A".equals(command) ||
                            "D".equals(command) || "G".equals(command) || " ".equals(command) || "^".equals(command)) {
                        handleInputs(command);
                    }
                }
            } catch(IOException e){
                System.err.println("ClientHandler IOException: " + e);
            }
        }

        public void handleInputs(String input) throws IOException {
          
            int delta = dataIn.readInt();

            if ("W".equals(input)) player.linearMovement(1, delta, currentMap);
            else if ("S".equals(input)) player.linearMovement(-1, delta, currentMap);
            else if ("A".equals(input)) player.turn(-1, delta);
            else if ("D".equals(input)) player.turn(1, delta);
            else if ("G".equals(input)) player.finishMovement(delta, currentMap);
            else if (" ".equals(input)) usePowerUp();
            else if ("^".equals(input)) player.boost(delta);

            synchronized (gameObjects) {
                updateRockets(delta);
                updateGameObjects();
            }
            updateDispensers(delta);
        }
        
        public void updateGameObjects() throws IOException {
            //dataOut.writeUTF("I");
            //dataOut.flush();
            // update player value in concurrent hashmap
            //gameObjects.put(playerId, player);
            handleGameObjectCollisions();
            // write number of players to client
            Set<Map.Entry<Integer, GameObject>> entries = gameObjects.entrySet();
            dataOut.writeObject(entries.size());
            dataOut.flush();
            // write all player data to client
            for(Map.Entry<Integer, GameObject> entry : entries) {
                Integer key = entry.getKey();
                GameObject object = entry.getValue();
                EntityData data;
                if (object instanceof ServerVehicle) {
                    data = new EntityData((ServerVehicle) object, key);
                } else if (object instanceof Powerup) {
                    data = new EntityData((Powerup) object, key);
                } else if (object instanceof SpikeTrap) {
                    data = new EntityData((SpikeTrap) object, key);
                } else if (object instanceof Rocket) {
                    data = new EntityData((Rocket) object, key);
                } else {
                    System.out.println("Weird object: " + object);
                    continue;
                }
                dataOut.writeObject(data);
                dataOut.flush();
            }
        }

        public void updateRockets(int delta) {
            Set<Integer> keys = gameObjects.keySet();
            for(Integer key: keys) {
                GameObject object = gameObjects.get(key);
                if(object instanceof Rocket) {
                    ((Rocket) object).move(delta, currentMap);
                    if(((Rocket) object).bounces <= 0) gameObjects.remove(key);
                }
            }
        }

        public void handleGameObjectCollisions() {
            ArrayList<Integer> collidedObjectIds = player.getGameObjectCollisions(gameObjects);
            for(int key: collidedObjectIds) {
                GameObject object = gameObjects.get(key);
                if(object instanceof Powerup) handlePowerup(key);
                else if(object instanceof SpikeTrap) handleSpikeTrap(key);
                else if(object instanceof Rocket) handleRocket(key);
                else if(object instanceof ServerVehicle && key != playerId) handleVehicle(key);
            }
        }

        public void handlePowerup(int id) {
            if(player.powerupTypeHeld != Powerup.NONE) return;
            Powerup powerup = (Powerup) gameObjects.get(id);
            player.powerupTypeHeld = powerup.type;
            Dispenser dispenser = dispensers.get(powerup.dispenserId);
            dispenser.timer = Dispenser.powerupSpawnDelay;
            dispenser.hasPowerup = false;
            gameObjects.remove(id);
        }

        public void handleSpikeTrap(int id) {
            SpikeTrap spikeTrap = (SpikeTrap) gameObjects.get(id);
            ServerVehicle player = ((ServerVehicle)gameObjects.get(playerId));
            if(spikeTrap.placedById != playerId && player.height == 0) {
                player.setHealth(player.getHealth() - 10);
                player.slowCooldown = 1500;
                gameObjects.remove(id);
            }
        }

        public void handleRocket(int id) {
            Rocket rocket = (Rocket) gameObjects.get(id);
            ServerVehicle player = ((ServerVehicle)gameObjects.get(playerId));
            if(rocket.placedById != playerId && player.height == 0) {
                player.setHealth(player.getHealth() - 10);
                player.slowCooldown = 1500;
                gameObjects.remove(id);
            }
        }

        public void handleVehicle(int id) {
            ServerVehicle player = ((ServerVehicle)gameObjects.get(playerId));
            ServerVehicle other = ((ServerVehicle)gameObjects.get(id));

            Collision vehicleCollision = player.collides(other);
            if (!other.recentCollisions.contains(playerId) && vehicleCollision != null && vehicleCollision.getMinPenetration() != null) {
                System.out.println("Colliding player " + playerId + " with player " + id);
                Vector playerSpeed = player.getSpeed();
                Vector otherSpeed = other.getSpeed();

                Vector bounce = vehicleCollision.getMinPenetration();
                player.setSpeed(player.getSpeed().bounce((float)bounce.getRotation()+90));
                other.setSpeed(other.getSpeed().bounce((float)bounce.getRotation()+90));

                player.setSpeed(player.getSpeed().add(otherSpeed));
                other.setSpeed(other.getSpeed().add(playerSpeed));

                System.out.println("Player speed: " + player.getSpeed().length()+ ", other speed: " + other.getSpeed().length());
            }
        }

        public void updateDispensers(int delta) {
            synchronized (dispensers) {
                Set<Integer> keys = dispensers.keySet();
                for (Integer key : keys) {
                    Dispenser dispenser = dispensers.get(key);
                    dispenser.decreaseTimer(delta);
                    if (dispenser.canDispense) {
                        float x = dispenser.position.getX();
                        float y = dispenser.position.getY();
                        int id = entityId.incrementAndGet();
                        gameObjects.put(id, new Powerup(x, y, id, key));
                        dispenser.hasPowerup = true;
                        dispenser.canDispense = false;
                    }
                }
            }
        }

        public void usePowerUp() {
            if (player.powerupTypeHeld == Powerup.BOOST) {
                player.boostCooldown = 1000;
            } else if (player.powerupTypeHeld == Powerup.SPIKE_TRAP) {
                int id = entityId.incrementAndGet();
                SpikeTrap spikeTrap = new SpikeTrap(player.worldX, player.worldY, id);
                spikeTrap.placedById = playerId;
                gameObjects.put(id, spikeTrap);
            } else if (player.powerupTypeHeld == Powerup.ROCKET) {
                int id = entityId.incrementAndGet();
                Rocket rocket = new Rocket(player.worldX, player.worldY, id);
                rocket.placedById = playerId;
                rocket.speed = Vector.getVector(player.speedAngle, ServerVehicle.forwardSpeedLimit);
                gameObjects.put(id, rocket);
            }
            player.powerupTypeHeld = Powerup.NONE;
        }

        public void sendStartMsg(){
            try{
                dataOut.writeUTF("All players have connected");
                dataOut.flush();
                System.out.println("Sending start message...");
            } catch (IOException e){
                System.out.println("IOException form sendStartMsg()");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            GameServer gs = new GameServer();
            //gs.acceptConnections();
        } catch (SlickException e){
            e.printStackTrace();
        }
    }
}
