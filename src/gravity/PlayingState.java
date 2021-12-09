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
		//gg.players[gg.playerID-1] = new Vehicle(5.5f, 5.5f, gg);

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

		DecimalFormat df = new DecimalFormat("####.##");

		g.scale(gg.gameScale, gg.gameScale);

		gg.map.render(((GravGame._SCREENWIDTH/2) - GravGame._TILEWIDTH/2)
							+ (int)((player.worldX - player.worldY) * GravGame._TILEWIDTH/2.0f *-1),
				((GravGame._SCREENHEIGHT/2))
						- (int)((player.worldX + player.worldY) * GravGame._TILEHEIGHT/2.0f ) );

		for (int i = 0; i < gg.gameObjects.length; i++) {
			if (i != (gg.playerID-1)) {
				Vehicle e = (Vehicle) gg.gameObjects[i];

				e.setX((GravGame._SCREENWIDTH/2.0f) +
						(((e.worldX-e.worldY) - (player.worldX-player.worldY))) * GravGame._TILEWIDTH/2.0f);
				e.setY((GravGame._SCREENHEIGHT/2.0f) +
						(((e.worldX+e.worldY) - (player.worldX+player.worldY))) * GravGame._TILEHEIGHT/2.0f);
			}
			gg.gameObjects[i].render(g);
		}

		g.drawString("Player Pos: " + df.format(player.worldX) + ", " + df.format(player.worldY), 10, 30);
		g.drawString("Player Rotation: " + df.format((float)player.speedAngle), 10, 50);
		g.drawString("Player Height: " + df.format((float)player.height), 10, 70);

		g.scale(1, 1);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		try {
			if(input.isKeyDown(Input.KEY_W)) {
				gg.out.writeUTF("W");
				gg.out.writeInt(delta);
				gg.out.flush();

				int playerCount = gg.in.readInt();
				for(int i = 0; i < playerCount; i++) {
					EntityData entityData = (EntityData) gg.in.readObject();
					if(entityData.entityType.equals("Player")) {
						((Vehicle) gg.gameObjects[i]).updateData(entityData);
					}
				}
			}
			if(input.isKeyDown(Input.KEY_A)){
				gg.out.writeUTF("A");
				gg.out.writeInt(delta);
				gg.out.flush();

				int playerCount = gg.in.readInt();
				for(int i = 0; i < playerCount; i++) {
					EntityData entityData = (EntityData) gg.in.readObject();
					if(entityData.entityType.equals("Player")) {
						((Vehicle) gg.gameObjects[i]).updateData(entityData);
					}
				}
			}
			if(input.isKeyDown(Input.KEY_S)){
				gg.out.writeUTF("S");
				gg.out.writeInt(delta);
				gg.out.flush();

				int playerCount = gg.in.readInt();
				for(int i = 0; i < playerCount; i++) {
					EntityData entityData = (EntityData) gg.in.readObject();
					if(entityData.entityType.equals("Player")) {
						((Vehicle) gg.gameObjects[i]).updateData(entityData);
					}
				}
			}
			if(input.isKeyDown(Input.KEY_D)){
				gg.out.writeUTF("D");
				gg.out.writeInt(delta);
				gg.out.flush();

				int playerCount = gg.in.readInt();
				for(int i = 0; i < playerCount; i++) {
					EntityData entityData = (EntityData) gg.in.readObject();
					if(entityData.entityType.equals("Player")) {
						((Vehicle) gg.gameObjects[i]).updateData(entityData);
					}
				}
			}
			if(noMovementPressed()){
				gg.out.writeUTF("G");
				gg.out.writeInt(delta);
				gg.out.flush();

				int playerCount = gg.in.readInt();
				for(int i = 0; i < playerCount; i++) {
					EntityData entityData = (EntityData) gg.in.readObject();
					if(entityData.entityType.equals("Player")) {
						((Vehicle) gg.gameObjects[i]).updateData(entityData);
					}
				}
			}
		} catch (IOException | ClassNotFoundException e){
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
	
}