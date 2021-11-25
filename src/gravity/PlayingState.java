package gravity;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

import jig.Vector;

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
			gg.players[i] = new Vehicle(5.5f, 5.5f);
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {


		DecimalFormat df = new DecimalFormat("####.##");
		g.drawString("Player Pos: " + df.format(gg.players[gg.playerID-1].worldX) + ", " + df.format(gg.players[gg.playerID-1].worldY), 10, 30);
		g.drawString("Player Rotation: " + df.format((float)gg.players[gg.playerID-1].speedAngle), 10, 50);

		g.scale(gg.gameScale, gg.gameScale);

		gg.map.render(((GravGame._SCREENWIDTH/2) - GravGame._TILEWIDTH/2)
							+ (int)((gg.players[gg.playerID-1].worldX - gg.players[gg.playerID-1].worldY) * GravGame._TILEWIDTH/2.0f *-1),
				((GravGame._SCREENHEIGHT/2))
						- (int)((gg.players[gg.playerID-1].worldX + gg.players[gg.playerID-1].worldY) * GravGame._TILEHEIGHT/2.0f ) );

		for (int i = 0; i < gg.players.length; i++) {
			if (i != (gg.playerID-1)) {
				Vehicle p = gg.players[gg.playerID-1];
				Vehicle e = gg.players[i];

				e.setX((GravGame._SCREENWIDTH/2.0f) +
						(((e.worldX-e.worldY) - (p.worldX-p.worldY))) * GravGame._TILEWIDTH/2.0f);
				e.setY((GravGame._SCREENHEIGHT/2.0f) +
						(((e.worldX+e.worldY) - (p.worldX+p.worldY))) * GravGame._TILEHEIGHT/2.0f);

			}
			gg.players[i].render(g);
		}

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
					EntityData playerData = (EntityData) gg.in.readObject();
					gg.players[i].updateData(playerData);
					System.out.println("player " + (i+1) + " position: " + gg.players[i].worldX + ", " + gg.players[i].worldY);
				}
			}
			if(input.isKeyDown(Input.KEY_A)){
				gg.out.writeUTF("A");
				gg.out.writeInt(delta);
				gg.out.flush();

				int playerCount = gg.in.readInt();
				for(int i = 0; i < playerCount; i++) {
					EntityData playerData = (EntityData) gg.in.readObject();
					gg.players[i].updateData(playerData);
					System.out.println("player " + (i+1) + " position: " + gg.players[i].worldX + ", " + gg.players[i].worldY);
				}
			}
			if(input.isKeyDown(Input.KEY_S)){
				gg.out.writeUTF("S");
				gg.out.writeInt(delta);
				gg.out.flush();

				int playerCount = gg.in.readInt();
				for(int i = 0; i < playerCount; i++) {
					EntityData playerData = (EntityData) gg.in.readObject();
					gg.players[i].updateData(playerData);
					System.out.println("player " + (i+1) + " position: " + gg.players[i].worldX + ", " + gg.players[i].worldY);
				}
			}
			if(input.isKeyDown(Input.KEY_D)){
				gg.out.writeUTF("D");
				gg.out.writeInt(delta);
				gg.out.flush();

				int playerCount = gg.in.readInt();
				for(int i = 0; i < playerCount; i++) {
					EntityData playerData = (EntityData) gg.in.readObject();
					gg.players[i].updateData(playerData);
					System.out.println("player " + (i+1) + " position: " + gg.players[i].worldX + ", " + gg.players[i].worldY);
				}
			}
			if(noMovementPressed()){
				gg.out.writeUTF("G");
				gg.out.writeInt(delta);
				gg.out.flush();

				int playerCount = gg.in.readInt();
				for(int i = 0; i < playerCount; i++) {
					EntityData playerData = (EntityData) gg.in.readObject();
					gg.players[i].updateData(playerData);
					System.out.println("player " + (i+1) + " position: " + gg.players[i].worldX + ", " + gg.players[i].worldY);
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