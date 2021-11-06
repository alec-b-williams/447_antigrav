package gravity;

import java.util.Iterator;

import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 * 
 * Transitions From StartUpState
 * 
 * Transitions To GameOverState
 */
class PlayingState extends BasicGameState {
	private GravGame game;
	private Input input;
	private Network network;

	@Override
	public void init(GameContainer container, StateBasedGame stateBasedGame)
			throws SlickException {
		game = (GravGame) stateBasedGame;
		input = container.getInput();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame stateBasedGame) {
		container.setSoundOn(true);
		network = new Network(game.isServer, game, "");
		network.start();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

	}

	@Override
	public void update(GameContainer container, StateBasedGame stateBasedGame,
			int delta) throws SlickException {
		if(input.isKeyPressed(Input.KEY_A)) {
			if(game.isServer) {
				network.pw.println("server says: A");
				network.pw.flush();
			} else {
				network.pw.println("client says: A");
				network.pw.flush();
			}
		}

	}

	@Override
	public int getID() {
		return GravGame.PLAYINGSTATE;
	}
	
}