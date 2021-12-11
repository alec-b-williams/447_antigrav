package gravity;

import jig.Entity;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {

    private ServerSocket server;
    private int numPlayers;
    private int maxPlayers;
    private Socket[] playerSockets;
    private ClientHandler[] handlers;
    private volatile TiledMap currentMap;

    private ConcurrentHashMap<Integer, GameObject> gameObjects = new ConcurrentHashMap<>();
    private int entityId;

    public GameServer() throws SlickException {

        Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
        currentMap = new TiledMap("gravity/resource/track1.tmx", false);
        System.out.println("Game Server spinning up!");
        numPlayers = 0;
        maxPlayers = 1;
        handlers = new ClientHandler[maxPlayers];
        playerSockets = new  Socket[maxPlayers];

        for(int i = 0; i < maxPlayers; i++) {
            gameObjects.put(i + 1, new ServerVehicle(5f, 5f));
        }

        entityId = maxPlayers+1;

        Powerup powerup = new Powerup(7f, 7f);
        gameObjects.put(entityId++, powerup);

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
                out.writeInt(numPlayers);
                out.writeInt(maxPlayers);
                System.out.println("Player #" + numPlayers + " has connected");

                handlers[numPlayers - 1] = new ClientHandler(this.numPlayers, in, out);
                playerSockets[numPlayers - 1] = socket;
            }
            System.out.println("Max Players Reached");
            for(int i = 0; i < maxPlayers; i++ ) {
                handlers[i].sendStartMsg();
            }
            for(int i = 0; i < maxPlayers; i++ ) {
                Thread playerThread = new Thread(handlers[i]);
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
                        case "W", "S", "A", "D", "G" -> {
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
                case "W" -> player.linearMovement(1, 0.06f, 0.2f, currentMap);
                case "S" -> player.linearMovement(-1, 0.01f, 0.05f, currentMap);
                case "A" -> player.turn(-1, delta);
                case "D" -> player.turn(1, delta);
                case "G" -> player.finishMovement( 0.98f, 0.2f * 0.01f, currentMap);
            }
            updateGameObjects();
        }
        
        public void updateGameObjects() throws IOException {
            dataOut.writeUTF("I");
            dataOut.flush();
            // update player value in concurrent hashmap
            gameObjects.put(playerId, player);
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
