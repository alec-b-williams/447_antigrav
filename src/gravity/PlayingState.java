package gravity;

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
	private GravGame game;
	private Input input;
	private Network network;
	private final static float speedScale = 250.0f;

	@Override
	public void init(GameContainer container, StateBasedGame stateBasedGame)
			throws SlickException {
		game = (GravGame) stateBasedGame;
		input = container.getInput();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
		container.setSoundOn(true);
		network = new Network(game.isServer, game, "");
		network.start();

		game.map = new TiledMap("gravity/resource/track1.tmx", "gravity/resource");
		game.player = new Vehicle(5.5f, 5.5f);

		game.cameraXPos = 0;
		game.cameraYPos = 0;
		game.gameScale = 1;
	}

	@Override
	public void render(GameContainer container, StateBasedGame stateBasedGame,
			Graphics g) throws SlickException {

		DecimalFormat df = new DecimalFormat("####.##");
		g.drawString("Player Pos: " + df.format(game.player.worldX) + ", " + df.format(game.player.worldY), 10, 30);

		g.scale(game.gameScale, game.gameScale);

		game.map.render(((GravGame._SCREENWIDTH/2) - GravGame._TILEWIDTH/2)
							+ (int)((game.player.worldX - game.player.worldY) * GravGame._TILEWIDTH/2.0f *-1),
				((GravGame._SCREENHEIGHT/2))
						- (int)((game.player.worldX + game.player.worldY) * GravGame._TILEHEIGHT/2.0f ) );

		game.player.render(g);

		g.scale(1, 1);
	}

	@Override
	public void update(GameContainer container, StateBasedGame stateBasedGame,
			int delta) throws SlickException {

		if (input.isKeyPressed(Input.KEY_A)) {
			if (game.isServer) {
				network.pw.println("server says: A");
			} else {
				network.pw.println("client says: A");
			}
			network.pw.flush();
		}

		if (input.isKeyDown(Input.KEY_W)) {
			game.player.worldY -= (delta / speedScale);
			game.player.worldX -= (delta / speedScale);
		}
		if (input.isKeyDown(Input.KEY_S)) {
			game.player.worldY += (delta / speedScale);
			game.player.worldX += (delta / speedScale);
		}
		if (input.isKeyDown(Input.KEY_A)) {
			game.player.worldY += (delta / speedScale);
			game.player.worldX -= (delta / speedScale);
		}
		if (input.isKeyDown(Input.KEY_D)) {
			game.player.worldY -= (delta / speedScale);
			game.player.worldX += (delta / speedScale);
		}

		if (input.isKeyDown(Input.KEY_LBRACKET))
			game.gameScale -= (delta/2000.0f);
		if (input.isKeyDown(Input.KEY_RBRACKET))
			game.gameScale += (delta/2000.0f);
	}

	@Override
	public int getID() {
		return GravGame.PLAYINGSTATE;
	}
	
}