package Main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class EndScreen {

	public Rectangle menu = new Rectangle(200, 300, 200, 50);
	public Point mousePoint;
	
	public EndScreen() {
		mousePoint = new Point(0, 0);
	}
	public void init() {
		checkScore();
	}
	public void checkScore() {
		int[] score = Scoring.readFile(Scoring.HIGHSCOREPATH);
 		for(int i = 0; i < score.length; i++) {
			if(score[i] < Controller.recentScore) {
				
	 			for(int j = score.length-1; j > i; j--) {
	 				score[j] = score[j-1];
	 			}
				score[i] = Controller.recentScore;
				Scoring.SaveScores(score);
				break;
			}
		}
	}
	public void tick() {
		
	}
	public void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Controller.WIDTH, Controller.HEIGHT);
		g.setColor(Color.WHITE);
		g.drawString("You Lost..", Controller.WIDTH/2-g.getFontMetrics().stringWidth("You Lost..")/2, 175);
		g.drawString("Score:  " + Controller.recentScore, Controller.WIDTH/2-g.getFontMetrics().stringWidth("Score:  " + Controller.recentScore)/2, 200);
		if(menu.contains(mousePoint)) {
			g.fillRoundRect(menu.x, menu.y, menu.width, menu.height, 25, 25);
		}
		g.setColor(Color.red);
		g.drawRoundRect(menu.x, menu.y, menu.width, menu.height, 25, 25);
		g.setColor(Color.gray);
		g.drawString("Main Menu", Controller.WIDTH/2-g.getFontMetrics().stringWidth("Main Menu")/2, menu.y+30);
	}
}
