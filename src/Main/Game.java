package Main;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Game{

	public static final int WIDTH = 600;
	public static final int HEIGHT = 500;

	private Thread tickThread;
	public boolean running;
	private Player player;
	public Point mousePoint;
	
	private int cellSize = 25;
	public static int gridWidth = 10;
	public static int gridHeight = 16;
	private int[][] gamePos = new int[10][16];
	public boolean hasCreated = false;
	public Piece controlPiece;
	private Piece nextPiece;
	private ImageLoader square;
	private int speed = 400;
	public boolean hasLost = false;
	public boolean showGuide = false;
	
	public Rectangle[] buttons = {new Rectangle(WIDTH/3+135, 165, 200, 50), 
									new Rectangle(WIDTH/3+135, 220, 200, 50),
									new Rectangle(WIDTH/3+135, 275, 200, 50)};
	
	public Game() {
		init();
	
	}
	public void start() {
		clearGame();
		tickThread = new Thread(new Clock());
		tickThread.start();
		running = true;
	}
	private void init() {
		player = new Player(0, 0);
		square = new ImageLoader(ImageLoader.squarePath);
		for(int i = 0; i < gamePos.length; i++) {
			for(int j = 0; j < gamePos[0].length; j++) {
				gamePos[i][j] = 0;
			}
		}
		nextPiece = new Piece(4, 0, Shapes.randomBlock());
		mousePoint = new Point(0, 0);
	}
	
	public void tick() {
		if (!hasCreated) {
			checkRowCompletion();
			checkLoss();
			if(!hasLost) {
				controlPiece = nextPiece;
				nextPiece = new Piece(4, 0, Shapes.randomBlock());
				updateCurrentPiece();
				hasCreated = true;
			}
		}else {
			if(!hasLost) {
				updateBlockPosition(false);
			}
		}
	}

	public void render(Graphics g) {
	g.setColor(Color.black);
	g.fillRect(0, 0, WIDTH, HEIGHT);
	g.setColor(Color.white);
	g.fillRect(50, 20, 250, HEIGHT-100);
	g.setColor(new Color(255, 0, 0, 20));
	for(int i = 0; i < gridWidth; i++) {
		for(int j = 0; j < gridHeight; j++) {
			g.drawRect(i*cellSize + 50, j*cellSize+20, cellSize, cellSize);
		}
	}
	g.setColor(Color.white);
	g.drawString("Next Block", (50+gridWidth*cellSize)+52, 35);
	g.fillRect((50+gridWidth*cellSize)+50, 45, 100, 100);
	g.setColor(Color.red);
	for(int i = 0; i < 4; i++) {
		for(int j = 0; j < 4; j++) {
			g.drawRect((i*cellSize) + (50+gridWidth*cellSize)+50, j*cellSize + 45, cellSize, cellSize);
		}
	}
	for(int i = 0; i < nextPiece.getWidth(); i++) {
		for(int j = 0; j < nextPiece.getHeight(); j++) {
			if(nextPiece.getShape()[i][j] != 0) {
				g.drawImage(square.getSubImage(nextPiece.getShape()[i][j]), (i*cellSize) + (50+gridWidth*cellSize)+50+cellSize, j*cellSize + 45, cellSize, cellSize, null);
				g.setColor(Color.BLACK);
				g.drawRoundRect((i*cellSize) + (50+gridWidth*cellSize)+50+cellSize, j*cellSize + 45, cellSize, cellSize, 1, 1);
			}
		}
	}
	/**End Next Item Drawing*/
	g.setColor(new Color(100, 100, 100));
	g.fillRoundRect(Controller.WIDTH/3+125, 150, 250, 275, 25, 25);
	g.setColor(Color.white);
	for(int i = 0; i < buttons.length; i++) {
		if(buttons[i].contains(mousePoint)) {
			g.fillRoundRect(buttons[i].x, buttons[i].y, buttons[i].width, buttons[i].height, 25, 25);
		}
	}
	g.setColor(Color.red); 
	for(int i = 0; i < buttons.length; i++) {
		g.drawRoundRect(buttons[i].x, buttons[i].y, buttons[i].width, buttons[i].height, 25, 25);
	}
	g.setColor(Color.ORANGE);
	g.drawString("New Game", WIDTH/3+150, 200);
	g.drawString("Show Guide", WIDTH/3+150, 250);
	g.drawString("Main Menu", WIDTH/3+150, 300);
	g.drawString("Level: " + player.getLevel(), WIDTH/3+150, 375);
	g.drawString("Score: " + player.getScore(), WIDTH/3+150, 400);
	g.setColor(Color.white);

	for(int i = 0; i < gridWidth; i++) {
		for(int j = 0; j < gridHeight; j++) {
			g.drawImage(square.getSubImage(gamePos[i][j]), i*cellSize + 50, j*cellSize+20, cellSize, cellSize, null);
			if(gamePos[i][j] > 0) {
				g.setColor(Color.BLACK);
				g.drawRoundRect(i*cellSize + 50, j*cellSize+20, cellSize, cellSize, 1, 1);
			}
		}
	}
	g.setColor(Color.green);
	if(showGuide) {
		for(int i = controlPiece.getY()+controlPiece.getShape()[0].length-1; i < gamePos[0].length; i++) {
			for(int j = controlPiece.getX(); j < controlPiece.getX()+controlPiece.getShape().length; j++) {
				if(gamePos[j][i] == 0) {
					g.drawRoundRect(j*cellSize + 50, i*cellSize+20, cellSize, cellSize, 1, 1);
				}else {
					break;
				}
			}
		}
	}
	}

	public void updateBlockPosition(boolean update) {
		boolean canUpdate = true;
		if(controlPiece != null) {
			if(controlPiece.getY()+ controlPiece.getHeight() < gridHeight) { //if its not at bottom level
				for(int i = 0; i < controlPiece.getWidth(); i++) {
					if(canUpdate) {
						if(controlPiece.getShape()[i][controlPiece.getHeight()-1] > 0) {
							if(gamePos[controlPiece.getX() + i][controlPiece.getY()+controlPiece.getHeight()]  > 0) {
								canUpdate = false;
								hasCreated = false;
							}
						}else if(controlPiece.getShape()[i][controlPiece.getHeight()-2] > 0) {
							if(gamePos[controlPiece.getX() + i][controlPiece.getY()+controlPiece.getHeight()-1]  > 0) {
								canUpdate = false;
								hasCreated = false;							
							}
						}else if(controlPiece.getShape()[i][controlPiece.getHeight()-3] > 0) {
							if(gamePos[controlPiece.getX() + i][controlPiece.getY()+controlPiece.getHeight()-2]  > 0) {
								canUpdate = false;
								hasCreated = false;
							}
						}else if(controlPiece.getShape()[i][controlPiece.getHeight()-4] > 0) {
							if(gamePos[controlPiece.getX() + i][controlPiece.getY()+controlPiece.getHeight()-3]  > 0) {
								canUpdate = false;
								hasCreated = false;
							}
						}				
					}
				}
				if(canUpdate) {
					removeCurrentPiece();
					if(update) {
						controlPiece.updateY();
					}
					updateCurrentPiece();
				}
			}else {
				hasCreated = false;
			}
		}
		
	}
	
	public void moveLeft() {
		boolean canMove = true;
		if(controlPiece.getX() > 0) {
			for(int i = 0; i < controlPiece.getHeight(); i++) {
				if(gamePos[controlPiece.getX()-1][controlPiece.getY()+i] > 0 && controlPiece.getShape()[0][i] > 0) {
					canMove = false;
				}
			}
			if(canMove) {
				removeCurrentPiece();
				controlPiece.setX(controlPiece.getX()-1);
				updateCurrentPiece();
			}
		}
	}
	public void moveRight() {
		boolean canMove = true;
		if(controlPiece.getX()+controlPiece.getWidth() < gridWidth) {
			for(int i = 0; i < controlPiece.getHeight(); i++) {
				if(gamePos[controlPiece.getX()+controlPiece.getWidth()][controlPiece.getY()+i] > 0 && controlPiece.getShape()[controlPiece.getWidth()-1][i] > 0) {
					canMove = false;
				}
			}
			if(canMove) {
				removeCurrentPiece();
				controlPiece.setX(controlPiece.getX()+1);
				updateCurrentPiece();
			}
		}
	}
	public void removeCurrentPiece() {
		if(controlPiece.getY() >= 0) {
			for(int i = 0; i < controlPiece.getHeight(); i++) {
				for(int j = 0; j < controlPiece.getWidth(); j++) {
					if(controlPiece.getShape()[j][i] != 0){
						gamePos[controlPiece.getX()+j][controlPiece.getY()+i] = 0;
					}
				}
			}
		}
	}
	
	public void checkRowCompletion() {
		int rowCompletion = 0;
		int count = 0;
		for(int i = 0; i < gridHeight; i++) {
			count = 0;
			for(int j = 0; j < gridWidth; j++) {
				if(gamePos[j][i] > 0) {
					count++;
				}
			}
			if(count == gridWidth) {//completed one row
				for(int j = 0; j < gridWidth; j++) {
					gamePos[j][i] = 0;
				}
				player.clearedLine();
				moveDownRows(i);
				rowCompletion++;
			}
		}
		if(rowCompletion > 0) {
			player.addScore(rowCompletion);
		}
	}
	
	public void checkLoss() {
		if(gamePos[4][0] > 0 || gamePos[5][0] > 0) {
			hasLost = true;
			running = false;
			Controller.recentScore = player.getScore();
			Controller.switchClasses(Controller.STATE.ENDSCREEN);
		}
	}
	
	public void moveDownRows(int height) {
		for(int i = height; i > 1; i--) { 
			for(int j = 0; j < gridWidth; j++) {
				gamePos[j][i] = gamePos[j][i-1];
			}
		}
	}
	public void updateCurrentPiece() {
		for(int i = 0; i < controlPiece.getHeight(); i++) {
			for(int j = 0; j < controlPiece.getWidth(); j++) {
				if(controlPiece.getShape()[j][i] != 0) { 
					gamePos[controlPiece.getX()+j][controlPiece.getY()+i] = controlPiece.getShape()[j][i];
				}
			}
		}
	}
	public void rotatePiece() {
		removeCurrentPiece();
		controlPiece.rotateSelf(gamePos);
		updateCurrentPiece();
	}
	public void clearGame() {
		player = new Player(0, 0);
		hasLost = false;
		hasCreated = false;
		nextPiece = new Piece(4, 0, Shapes.randomBlock());
		for(int i = 0; i < gridHeight; i++) {
			for(int j = 0; j < gridWidth; j++) {
				gamePos[j][i] = 0;
			}
		}
}
	public void backToMenu() {
		running = false;
		Controller.switchClasses(Controller.STATE.MENU);
	}
	private class Clock implements Runnable { 
		public void run() {
			while(running == true) {
					try {
						Thread.sleep(speed-(player.getLevel()*10));
						
					}
					catch (InterruptedException e) {
						break;
					}
					if(!hasLost) {
						updateBlockPosition(true);
					}
					if (running == false) break;
			}
		}
	}
}