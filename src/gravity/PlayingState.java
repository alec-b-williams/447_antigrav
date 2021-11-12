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
	private GravGame gg;
	private Input input;
	private Network network;
	private final static float speedScale = 250.0f;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		gg = (GravGame) game;
		input = container.getInput();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		container.setSoundOn(true);
		network = new Network(gg.isServer, gg, "");
		network.start();

		gg.map = new TiledMap("gravity/resource/track1.tmx", "gravity/resource");
		gg.player = new Vehicle(5.5f, 5.5f);

		gg.cameraXPos = 0;
		gg.cameraYPos = 0;
		gg.gameScale = 1;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

		DecimalFormat df = new DecimalFormat("####.##");
		g.drawString("Player Pos: " + df.format(gg.player.worldX) + ", " + df.format(gg.player.worldY), 10, 30);

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
		if(input.isKeyPressed(Input.KEY_A)) {
			if(gg.isServer) {
				network.pw.println("server says: A");
			} else {
				network.pw.println("client says: A");
			}
			network.pw.flush();
		}
		System.out.println("Length of Speed: " + gg.player.getSpeed().length());
		if (input.isKeyDown(Input.KEY_W)) {
			if(gg.player.getSpeed().length() == 0){
				gg.player.setSpeed(Vector.getVector(gg.player.getSpeedAngle(), 0.01f));
			}
			else if(gg.player.getSpeed().length() < 0.2){
				gg.player.setSpeed(gg.player.getSpeed().scale(1.1f));
			}
			System.out.println("The Angle(W): " + gg.player.getSpeed().getRotation());
			gg.player.worldY += gg.player.getSpeed().getY();
			gg.player.worldX += gg.player.getSpeed().getX();

		}
		if (input.isKeyDown(Input.KEY_S)) {
			gg.player.worldY -= gg.player.getSpeed().getY();
			gg.player.worldX -= gg.player.getSpeed().getX();
		}
		if (input.isKeyDown(Input.KEY_A)) {
			if(gg.player.getTurnCooldown() < 1000){
				gg.player.setTurnCooldown(delta);
			}
			if(gg.player.getTurnCooldown() >= 1000){
				gg.player.setSpeedRotation(-45);
				gg.player.sprite.setCurrentFrame(((gg.player.sprite.getFrame() - 1) + gg.player.sprite.getFrameCount()) % gg.player.sprite.getFrameCount());
				gg.player.resetCooldown();
			}
		}
		if (input.isKeyDown(Input.KEY_D)) {
			if(gg.player.getTurnCooldown() < 1000){
				gg.player.setTurnCooldown(delta);
			}
			if(gg.player.getTurnCooldown() >= 1000){
				gg.player.setSpeedRotation(45);
				gg.player.sprite.setCurrentFrame((gg.player.sprite.getFrame() + 1) % gg.player.sprite.getFrameCount());
				gg.player.resetCooldown();
			}
		}
		if(gg.player.getTurnCooldown() < 1000 && !input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_D)){
			gg.player.setTurnCooldown(delta*2);
		}
		if(!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S)){
			if(gg.player.getSpeed().length() > 0){
				gg.player.setSpeed(gg.player.getSpeed().scale(.8f));
			}
			if(0.2 * 0.01 > gg.player.getSpeed().length()){
				gg.player.setSpeed(new Vector(0,0));
			}
			gg.player.worldY += gg.player.getSpeed().getY();
			gg.player.worldX += gg.player.getSpeed().getX();
		}
		if(!gg.isServer){
			//gg.kart.update(container, game, delta);
		}

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