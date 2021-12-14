package gravity;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

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

	public static final int VOID = 0;
	public static final int TRACK = 1;
	public static final int WALL = 2;
	public static final int DISPENSER = 3;
	public static final int JUMP = 4;
	public static final int SLOW_A = 5;
	public static final int SLOW_B = 6;
	public static final int BOOST_N = 7;
	public static final int BOOST_E = 8;
	public static final int BOOST_S = 9;
	public static final int BOOST_W = 10;
	public static final int FINISH = 11;
	public static final int CHECKPOINT = 12;

	public static final int _SCREENWIDTH = 1280;
	public static final int _SCREENHEIGHT = 1024;
	public static final int _TILEWIDTH = 64;
	public static final int _TILEHEIGHT = 32;
	public static final int _HEALTHSIZE = 116;

	public static final String POWERUP_IMG_RSC = "gravity/resource/powerup_box.png";
	public static final String ENERGY_IMG_RSC = "gravity/resource/energy.png";
	public static final String ENERGY_CONTAINER_IMG_RSC = "gravity/resource/energy_container.png";
	public static final String POWERUP_CONTAINER_IMG_RSC = "gravity/resource/powerup_container.png";
	public static final String NUM_ANIM_RSC = "gravity/resource/numAnim.png";
	public static final String LAPTIME_IMG_RSC = "gravity/resource/laptime.png";
	public static final String PLAYER_1_VEHICLE_ANIM = "gravity/resource/p1Anim.png";
	public static final String PLAYER_2_VEHICLE_ANIM = "gravity/resource/p2Anim.png";
	public static final String PLAYER_3_VEHICLE_ANIM = "gravity/resource/p3Anim.png";
	public static final String PLAYER_4_VEHICLE_ANIM = "gravity/resource/p4Anim.png";
	public static final String[] vehicleImages = {PLAYER_1_VEHICLE_ANIM, PLAYER_2_VEHICLE_ANIM,
												  PLAYER_3_VEHICLE_ANIM, PLAYER_4_VEHICLE_ANIM};
	public static final String LEVEL_1_BG_IMG_RSC = "gravity/resource/level1_bg.jpg";
	public static final String[] levelBGs = {LEVEL_1_BG_IMG_RSC};
	public static final Vector[] BGoffsets = {new Vector(1250, 500)};

	public final int ScreenWidth;
	public final int ScreenHeight;
	public float cameraXPos;
	public float cameraYPos;
	public float gameScale;
	public TiledMap map;

	public Socket socket;
	public ObjectInputStream in;
	public ObjectOutputStream out;

	public int playerID;
	public int maxPlayers;
	public ConcurrentHashMap<Integer, GameObject> gameObjects = new ConcurrentHashMap<>();

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

		Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
		Entity.setDebug(true);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new StartUpState());
		addState(new GameOverState());
		addState(new PlayingState());
		
		//ResourceManager.loadSound(BANG_EXPLOSIONSND_RSC);

		// preload all the resources to avoid warnings & minimize latency...
		// ResourceManager.setFilterMethod(ResourceManager.FILTER_LINEAR);

		ResourceManager.loadImage(ENERGY_IMG_RSC);
		ResourceManager.loadImage(ENERGY_CONTAINER_IMG_RSC);
		ResourceManager.loadImage(POWERUP_CONTAINER_IMG_RSC);
		ResourceManager.loadImage(NUM_ANIM_RSC);
		ResourceManager.loadImage(LAPTIME_IMG_RSC);
		ResourceManager.loadImage(PLAYER_1_VEHICLE_ANIM);
		ResourceManager.loadImage(PLAYER_2_VEHICLE_ANIM);
		ResourceManager.loadImage(PLAYER_3_VEHICLE_ANIM);
		ResourceManager.loadImage(PLAYER_4_VEHICLE_ANIM);
		ResourceManager.loadImage(LEVEL_1_BG_IMG_RSC);
		ResourceManager.loadImage(POWERUP_IMG_RSC);
	}

	public void startServerHandler() {
		try{
			socket = new Socket("localhost", 9158);
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());

			playerID = in.readInt();
			maxPlayers = in.readInt();
			System.out.println("You are player: " + playerID);

			String startMsg = in.readUTF();
			System.out.println("Message from server: " + startMsg);

			ServerHandler sh = new ServerHandler(socket, out, in);
			Thread shThread = new Thread(sh);
			shThread.start();
		} catch (IOException e){
			System.out.println("IOException from connectToServer()");
			e.printStackTrace();
		}
	}

	public class ServerHandler implements Runnable {

		public Socket socket;
		public ObjectOutputStream out;
		public ObjectInputStream in;

		public ServerHandler(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
			this.socket = socket;
			this.out = out;
			this.in = in;
		}

		@Override
		public void run() {
			try {
				while(true) {
					String command = in.readUTF();
					switch (command) {
						case "I" -> updateGameObjects();
						//case "R" -> removeGameObject();
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		public void updateGameObjects() throws IOException, ClassNotFoundException {
            int entityCount = in.readInt();
			ArrayList<Integer> serverKeys = new ArrayList<>();
            for (int i = 0; i < entityCount; i++) {
                EntityData entityData = (EntityData) in.readObject();
				serverKeys.add(entityData.id);
                if (entityData.entityType.equals("Player")) {
                    if (gameObjects.containsKey(entityData.id)) {
                        ((Vehicle) gameObjects.get(entityData.id)).updateData(entityData);
                    } else {
                        gameObjects.put(entityData.id, new Vehicle(entityData.xPosition,
                                entityData.yPosition, entityData.id));
                    }
                } else if (entityData.entityType.equals("Powerup")) {
                    if (gameObjects.containsKey(entityData.id)) {
                        ((Powerup) gameObjects.get(entityData.id)).updateData(entityData);
                    } else {
                        Powerup powerup = new Powerup(entityData.xPosition, entityData.yPosition, entityData.id);
                        powerup.addImage(ResourceManager.getImage(POWERUP_IMG_RSC));
                        gameObjects.put(entityData.id, powerup);
                    }
                }
            }
			Set<Integer> keys = gameObjects.keySet();
			for(Integer key: keys) {
				if(!serverKeys.contains(key)) gameObjects.remove(key);
			}
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
