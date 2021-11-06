package gravity;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Network extends Thread{

    public ServerSocket server;
    public Socket client;
    public PrintWriter pw;
    public Scanner sc;
    public boolean isServer;

    public static final Integer port = 9180;

    private GravGame game;

    public Network(boolean isServer, GravGame game, String hostID) {
        this.game = game;
        this.isServer = isServer;

        if(hostID == null || hostID.length() == 0) hostID = "localhost"; // localhost by default

        // server
        if(isServer) {
            // create server socket on port 9180
            try {
                server = new ServerSocket(port);
            } catch (Exception e) {
                System.out.println("Error constructing server socket: " + e);
                System.exit(0);
            }

            // create socket when signal is received from server socket
            try {
                client = server.accept();
            } catch (Exception e) {
                System.out.println("Error accepting from server socket: " + e);
                System.exit(0);
            }
        }
        // client
        else {
            try {
                client = new Socket(hostID, port);
            } catch (Exception e) {
                System.out.println("Error constructing client socket: " + e);
                System.exit(0);
            }
        }

        //open scanner to scan input stream from socket
        try {
            sc = new Scanner(client.getInputStream());
        } catch (Exception e) {
            System.out.println("Error constructing scanner: " + e);
            System.exit(0);
        }

        //open print writer to send output through socket
        try {
            pw = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error constructing printwriter: " + e);
            System.exit(0);
        }
    }

    public void run() {
        String[] data;
        String temp;

        // server
        if(isServer) {
            // read outputs from the client
            while (sc.hasNextLine()) {
                temp = sc.nextLine();
                data = temp.split(" ");
                System.out.println(temp);
                // do something with the data here, probably pass it as an argument to some helper method
                // this helper method will likely just be sending updates to the game state to the client
            }
        }
        // client
        else {
            // read outputs from the client
            while (sc.hasNextLine()) {
                temp = sc.nextLine();
                data = temp.split(" ");
                System.out.println(temp);
                // pretty similar to above, you will probably want a separate method for the client
            }
        }
    }
}
