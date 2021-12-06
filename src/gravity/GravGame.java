package gravity;

import java.io.*;
import java.net.Socket;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

/**
 * Antigravity Racing Game
 *
 * 
 * @author TODO: add our names
 * 
 */
public class GravGame extends StateBasedGame {
	
	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int GAMEOVERSTATE = 2;

	public static final int _SCREENWIDTH = 1280;
	public static final int _SCREENHEIGHT = 1024;
	public static final int _TILEWIDTH = 64;
	public static final int _TILEHEIGHT = 32;
	
	public static final String VEHICLE_ANIM_RSC = "gravity/resource/vehicleAnim.png";
	public static final String PLAYER_1_VEHICLE_ANIM = "gravity/resource/p1Anim.png";

	public final int ScreenWidth;
	public final int ScreenHeight;
	public float cameraXPos;
	public float cameraYPos;
	public float gameScale;
	public TiledMap map;

	public int playerID;
	public int maxPlayers;
	public Entity[] gameObjects;

	public Socket socket;
	public ObjectInputStream in;
	public ObjectOutputStream out;

	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public GravGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;

		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new StartUpState());
		addState(new GameOverState());
		addState(new PlayingState());
		
		//ResourceManager.loadSound(BANG_EXPLOSIONSND_RSC);

		// preload all the resources to avoid warnings & minimize latency...
		ResourceManager.loadImage(VEHICLE_ANIM_RSC);
		ResourceManager.loadImage(PLAYER_1_VEHICLE_ANIM);
	}

	public void connectToServer(){
		try{
			socket = new Socket("localhost", 9158);
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());

			playerID = in.readInt();
			maxPlayers = in.readInt();
			gameObjects = new Entity[maxPlayers];
			System.out.println("You are player: " + this.playerID);
		} catch (IOException e){
			System.out.println("IOException from connectToServer()");
			e.printStackTrace();
		}
	}

	public void waitForStartMsg(){
		try{
			String startMsg = in.readUTF();
			System.out.println("Message from server: " + startMsg);
		} catch (IOException e){
			System.err.println("Wait Start IOException error: " + e);
		}
	}

	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new gravity.GravGame("Antigravity", _SCREENWIDTH, _SCREENHEIGHT));
			app.setDisplayMode(_SCREENWIDTH, _SCREENHEIGHT, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
