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
		//gg.player = new Vehicle(5.5f, 5.5f, gg);

		gg.cameraXPos = 0;
		gg.cameraYPos = 0;
		gg.gameScale = 1;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {


		DecimalFormat df = new DecimalFormat("####.##");
		g.drawString("Player Pos: " + df.format(gg.player.worldX) + ", " + df.format(gg.player.worldY), 10, 30);
		g.drawString("Player Rotation: " + df.format((float)gg.player.speedAngle), 10, 50);

		g.scale(gg.gameScale, gg.gameScale);

		gg.map.render(((GravGame._SCREENWIDTH/2) - GravGame._TILEWIDTH/2)
							+ (int)((gg.player.worldX - gg.player.worldY) * GravGame._TILEWIDTH/2.0f *-1),
				((GravGame._SCREENHEIGHT/2))
						- (int)((gg.player.worldX + gg.player.worldY) * GravGame._TILEHEIGHT/2.0f ) );

		gg.player.render(g);
		g.scale(1, 1);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		try {
			if (input.isKeyDown(Input.KEY_W)) {
				//gg.out.writeInt(gg.playerID);
				System.out.println("W");
				gg.out.writeUTF("W");
				gg.out.writeFloat(gg.player.getSpeed().getX());
				gg.out.writeFloat(gg.player.getSpeed().getY());
				float newY = gg.in.readFloat();
				float newX = gg.in.readFloat();
				System.out.println("New X: " + newX);
				gg.player.worldY = newY;
				gg.player.worldX = newX;
			}
		} catch (IOException e){
			System.err.println("IOException in write: " + e);
		}
		//gg.player.update(container, delta);

		if (input.isKeyDown(Input.KEY_LBRACKET))
			gg.gameScale -= (delta/2000.0f);
		if (input.isKeyDown(Input.KEY_RBRACKET))
			gg.gameScale += (delta/2000.0f);
	}

	@Override
	public int getID() {
		return GravGame.PLAYINGSTATE;
	}
	
}