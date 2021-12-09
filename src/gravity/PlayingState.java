package gravity;

import java.io.IOException;
import java.text.DecimalFormat;

import jig.ResourceManager;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;


/**
 * Transitions From StartUpState
 * 
 * Transitions To GameOverState
 */
class PlayingState extends BasicGameState {
	private GravGame gg;
	private Input input;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		gg = (GravGame) game;
		input = container.getInput();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		container.setSoundOn(true);

		gg.map = new TiledMap("gravity/resource/track1.tmx", "gravity/resource");

		gg.cameraXPos = 0;
		gg.cameraYPos = 0;
		gg.gameScale = 1;

		for(int i = 0; i < gg.maxPlayers; i++) {
			gg.gameObjects[i] = new Vehicle(5.5f, 5.5f, i);
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

		Vehicle player = (Vehicle) gg.gameObjects[gg.playerID-1];

		g.drawImage(ResourceManager.getImage(GravGame.levelBGs[0]),
				(gg.BGoffsets[0].getX() * -1) - ((player.worldX - player.worldY) * 4),
				(gg.BGoffsets[0].getY() * -1) - ((player.worldX + player.worldY)) * 4);

		g.scale(gg.gameScale, gg.gameScale);

		renderEntities(player,g, true);

		gg.map.render(((GravGame._SCREENWIDTH/2) - GravGame._TILEWIDTH/2)
							+ (int)((player.worldX - player.worldY) * GravGame._TILEWIDTH/2.0f *-1),
				((GravGame._SCREENHEIGHT/2))
						- (int)((player.worldX + player.worldY) * GravGame._TILEHEIGHT/2.0f ) );

		renderEntities(player, g, false);
    
		g.scale(1, 1);

		drawUI(player, g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		try {
			if(input.isKeyDown(Input.KEY_W)) {
				gg.out.writeUTF("W");
				serverRW(delta);
			}
			if(input.isKeyDown(Input.KEY_A)){
				gg.out.writeUTF("A");
				serverRW(delta);
			}
			if(input.isKeyDown(Input.KEY_S)){
				gg.out.writeUTF("S");
				serverRW(delta);
			}
			if(input.isKeyDown(Input.KEY_D)){
				gg.out.writeUTF("D");
				serverRW(delta);
			}
			if(noMovementPressed()){
				gg.out.writeUTF("G");
				serverRW(delta);
			}
		} catch (IOException e){
			System.err.println("IOException in write: " + e);
		}
		
		if (input.isKeyDown(Input.KEY_LBRACKET))
			gg.gameScale -= (delta/2000.0f);
		if (input.isKeyDown(Input.KEY_RBRACKET))
			gg.gameScale += (delta/2000.0f);
	}

	public boolean noMovementPressed(){
		if(!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S))
			return true;

		return false;
	}

	@Override
	public int getID() {
		return GravGame.PLAYINGSTATE;
	}

	public void serverRW(int delta) {
		try {
			gg.out.writeInt(delta);
			gg.out.flush();

			int playerCount = gg.in.readInt();
			for (int i = 0; i < playerCount; i++) {
				EntityData entityData = (EntityData) gg.in.readObject();
				if (entityData.entityType.equals("Player")) {
					((Vehicle) gg.gameObjects[i]).updateData(entityData);
				}
			}
		} catch (IOException | ClassNotFoundException e){
			System.err.println("IOException in write: " + e);
		}
	}

	public void renderEntities(Vehicle player, Graphics g, boolean kill) {
		for (int i = 0; i < gg.gameObjects.length; i++) {
			if (i != (gg.playerID-1)) {
				Vehicle e = (Vehicle) gg.gameObjects[i];

				e.setX((GravGame._SCREENWIDTH/2.0f) +
						(((e.worldX-e.worldY) - (player.worldX-player.worldY))) * GravGame._TILEWIDTH/2.0f);
				e.setY((GravGame._SCREENHEIGHT/2.0f) +
						(((e.worldX+e.worldY) - (player.worldX+player.worldY))) * GravGame._TILEHEIGHT/2.0f);
			}
			if (kill) {
				if (((Vehicle)gg.gameObjects[i]).isKill) {
					gg.gameObjects[i].render(g);
				}
			} else {
				if (!((Vehicle)gg.gameObjects[i]).isKill) {
					gg.gameObjects[i].render(g);
				}
			}
		}
	}

	private void drawUI(Vehicle player, Graphics g) {
		DecimalFormat df = new DecimalFormat("####.##");

		if (player.getDebug()){
			g.drawString("Player Pos: " + df.format(player.worldX) + ", " + df.format(player.worldY), 10, 30);
			g.drawString("Player Rotation: " + df.format((float)player.speedAngle), 10, 50);
		}

		//TODO: shrink this image based on player health
		g.drawImage(ResourceManager.getImage(GravGame.ENERGY_IMG_RSC), 100, 16);
		g.drawImage(ResourceManager.getImage(GravGame.ENERGY_CONTAINER_IMG_RSC), 94, 10);

		g.drawImage(ResourceManager.getImage(GravGame.POWERUP_CONTAINER_IMG_RSC), (GravGame._SCREENWIDTH/2.0f) - 64, 10);
		g.drawImage(ResourceManager.getImage(GravGame.LAPTIME_IMG_RSC), GravGame._SCREENWIDTH - 300, 10);
	}
}