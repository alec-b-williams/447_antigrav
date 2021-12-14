package gravity;

import jig.Entity;
import jig.Vector;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {

    private ServerSocket server;
    private int numPlayers;
    private int maxPlayers;
    private ArrayList<Socket> playerSockets;
    private ArrayList<ClientHandler> handlers;

    private volatile TiledMap currentMap;
    private int mapWidth;
    private int mapHeight;
    private ConcurrentHashMap<Integer, Dispenser> dispensers;
    private ArrayList<Vector> playerSpawnLocations;

    private ConcurrentHashMap<Integer, GameObject> gameObjects = new ConcurrentHashMap<>();
    private static volatile int entityId;

    public GameServer() throws SlickException {

        Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
        currentMap = new TiledMap("gravity/resource/track1.tmx", false);
        mapWidth = currentMap.getWidth();
        mapHeight = currentMap.getHeight();
        dispensers = new ConcurrentHashMap<>();
        playerSpawnLocations = new ArrayList<>();



        System.out.println("Game Server spinning up!");
        numPlayers = 0;
        maxPlayers = 1;
        handlers = new ArrayList<>();
        playerSockets = new ArrayList<>();

        entityId = maxPlayers + 1;

        int dispenserId = 0;
        for(int x = 0; x < mapWidth; x++) {
            for(int y = 0; y < mapHeight; y++) {
                if(currentMap.getTileId(x, y, 0) == GravGame.DISPENSER) {
                    Vector location = new Vector(x, y);
                    gameObjects.put(entityId, new Powerup(x, y, entityId, dispenserId));
                    dispensers.put(dispenserId, new Dispenser(location));
                    entityId++;
                    dispenserId++;
                }
                else if(currentMap.getTileId(x, y, 0) == GravGame.PLAYER_SPAWN) {
                    playerSpawnLocations.add(new Vector(x, y));
                }
            }
        }

        //Powerup powerup = new Powerup(7f, 5f, entityId++);
        //gameObjects.put(powerup.id, powerup);

        try {
            this.server = new ServerSocket(9158);
        } catch (IOException e){
            System.out.println("IOException in GS constructor");
            e.printStackTrace();
        }
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
                gameObjects.put(numPlayers, new ServerVehicle(5f, 5f));
                out.writeInt(numPlayers);
                out.writeInt(maxPlayers);
                System.out.println("Player #" + numPlayers + " has connected");

                handlers.add(new ClientHandler(this.numPlayers, in, out));
                playerSockets.add(socket);
            }
            System.out.println("Max Players Reached");
            for(ClientHandler handler: handlers) {
                handler.sendStartMsg();
            }
            for(ClientHandler handler: handlers) {
                Thread playerThread = new Thread(handler);
                playerThread.start();
            }
        } catch (IOException e){
            System.out.println("IOException in acceptConnections()");
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private int playerId;
        private ObjectInputStream dataIn;
        private ObjectOutputStream dataOut;
        private ServerVehicle player;

        public ClientHandler(int playerId, ObjectInputStream in, ObjectOutputStream out){
            this.playerId = playerId;
            this.dataIn = in;
            this.dataOut = out;
            this.player = (ServerVehicle) gameObjects.get(playerId);

        }

        @Override
        public void run() {
            try{
                while(true){
                    String command = dataIn.readUTF();
                    switch(command) {
                        case "W", "S", "A", "D", "G", " " -> {
                            handleInputs(command);
                        }
                    }
                }
            } catch(IOException e){
                System.err.println("ClientHandler IOException: " + e);
            }
        }

        public void handleInputs(String input) throws IOException {
            int delta = dataIn.readInt();

            switch (input) {
                case "W" -> player.linearMovement(1, delta, currentMap);
                case "S" -> player.linearMovement(-1, delta, currentMap);
                case "A" -> player.turn(-1, delta);
                case "D" -> player.turn(1, delta);
                case "G" -> player.finishMovement(delta, currentMap);
                case " " -> usePowerUp();
            }
            updateGameObjects();
            updateDispensers(delta);
        }
        
        public void updateGameObjects() throws IOException {
            dataOut.writeUTF("I");
            dataOut.flush();
            // update player value in concurrent hashmap
            //gameObjects.put(playerId, player);
            handlePowerups();
            // write number of players to client
            dataOut.writeInt(gameObjects.size());
            dataOut.flush();
            // write all player data to client
            Set<Integer> keys = gameObjects.keySet();
            for(Integer key : keys) {
                EntityData data;
                GameObject object = gameObjects.get(key);
                if (object instanceof ServerVehicle) {
                    data = new EntityData((ServerVehicle) object, key);
                } else if (object instanceof Powerup) {
                    data = new EntityData((Powerup) object, key);
                } else continue;
                dataOut.writeObject(data);
                dataOut.flush();
            }
        }

        public void handlePowerups() {
            if(player.powerupTypeHeld != Powerup.NONE) return;
            int powerupId = player.gotPowerup(gameObjects);
            if(powerupId != -1) {
                Powerup powerup = (Powerup) gameObjects.get(powerupId);
                player.powerupTypeHeld = powerup.type;
                Dispenser dispenser = dispensers.get(powerup.dispenserId);
                dispenser.timer = Dispenser.powerupSpawnDelay;
                dispenser.hasPowerup = false;
                gameObjects.remove(powerupId);
            }
        }

        public void updateDispensers(int delta) {
            Set<Integer> keys = dispensers.keySet();
            for(Integer key: keys) {
                Dispenser dispenser = dispensers.get(key);
                dispenser.decreaseTimer(delta);
                if(dispenser.canDispense) {
                    float x = dispenser.position.getX();
                    float y = dispenser.position.getY();
                    gameObjects.put(entityId, new Powerup(x, y, entityId, key));
                    dispenser.hasPowerup = true;
                    dispenser.canDispense = false;
                    entityId++;
                }
            }
        }

        public void usePowerUp() {
            switch (player.powerupTypeHeld) {
                case Powerup.NOS -> player.boostCooldown = 1000;
                case Powerup.SPIKE_TRAP -> {}
                case Powerup.ROCKET -> {}
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
            gs.acceptConnections();
        } catch (SlickException e){
            e.printStackTrace();
        }
    }
}
