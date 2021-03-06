package main;

import states.*;
import input.MouseManager;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import game.Table;
import game.gfx.ImageLoader;


public class Game implements Runnable {

	private boolean running = false;
	
	private Thread thread;
	private Display display;
	
	private BufferStrategy bs;
	private Graphics g;
	
	private BufferedImage testImage;
	private State gameState;

	private Handler handler;
	
	private MouseManager mouseManager;
	
	public Game() {
		mouseManager = new MouseManager();
	}

	private void init() {
		
		display = new Display();
		display.canvas.createBufferStrategy(3);
		testImage = ImageLoader.loadImage("/texture/rink.png");
		display.getFrame().addMouseListener(mouseManager);
		display.getFrame().addMouseMotionListener(mouseManager);
		display.getCanvas().addMouseListener(mouseManager);
		display.getCanvas().addMouseMotionListener(mouseManager);
		//declared as state but initialized as a GameState
		handler = new Handler(this);
		
		gameState = new GameState(handler);
		
		State.setState(gameState);
		
		
		
	}
	
	private void tick() {
		
		mouseManager.tick();
		if(State.getState() != null){
			State.getState().tick();
		}
	}
	
	private void render() {
		bs = display.canvas.getBufferStrategy();
		g = bs.getDrawGraphics();
		// Clear Screen
		g.clearRect(0, 0, display.screenDimensions.width, display.screenDimensions.height);
		// Draw Here!
		
		g.fillRect(0, 0, display.screenDimensions.width, display.screenDimensions.height);
		
		g.drawImage(testImage, 0, 0, null);
		
		
		// End Drawing!
		bs.show();
		g.dispose();
	}
	
	@Override
	public void run() {

		init();
		
		FpsTimer timer = new FpsTimer(120);

		while(running) {
			if(timer.check()) {
				tick();
				render();
			}
		}
		
		stop();
		
	}

	public MouseManager getMouseManager(){
		return mouseManager;
	}
	
	public synchronized void start() {
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
		
	}
	
	public synchronized void stop() {
		if(!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
