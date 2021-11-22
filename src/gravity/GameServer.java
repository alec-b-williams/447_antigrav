package gravity;

import gravity.resource.ServerVehicle;
import jig.Entity;
import org.newdawn.slick.Input;

import java.io.*;
import java.net.*;

public class GameServer {

    private ServerSocket server;
    private int numPlayers;
    private int maxPlayers;
    private ServerVehicle player;
    //private Vehicle players[];
    private Socket playerSocket;
    private ClientHandler handler;

    public GameServer(){
        Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
        System.out.println("Game Server spinning up!");
        numPlayers = 0;
        maxPlayers = 1;
        player = new ServerVehicle(5.5f, 5.5f);
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
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                this.numPlayers++;
                out.writeInt(numPlayers);
                out.writeInt(maxPlayers);
                System.out.println("Player #" + numPlayers + " has connected");

                handler = new ClientHandler(this.numPlayers, in, out);
                this.playerSocket = socket;
            }
            System.out.println("Max Players");
            handler.sendStartMsg();
            Thread playerThread = new Thread(handler);
            playerThread.start();
        } catch (IOException e){
            System.out.println("IOException in acceptConnections()");
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable{
        private int playerID;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientHandler(int pID, DataInputStream in, DataOutputStream out){
            this.playerID = pID;
            this.dataIn = in;
            this.dataOut = out;
        }

        @Override
        public void run() {
            try{
                while(true){
                    String command = dataIn.readUTF();
                    if(command.equals("W")){
                        float x = dataIn.readFloat();
                        System.out.println("X got here: " + x);
                        float y = dataIn.readFloat();
                        player.serverForward(x, y);
                        dataOut.writeFloat(player.worldY);
                        dataOut.writeFloat(player.worldX);
                    }
                }
            } catch(IOException e){
                System.err.println("ClientHandler IOException: " + e);
            }
        }

        public void sendStartMsg(){
            try{
                dataOut.writeUTF("All players have connected");
            } catch (IOException e){
                System.out.println("IOException form sendStartMsg()");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
