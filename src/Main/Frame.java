package Main;

import javax.swing.JFrame;


public class Frame{

	static JFrame window = new JFrame();
	public static void main(String[] args) {	
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(new Controller());
		window.pack();	
		window.setSize(StartingMenu.WIDTH, StartingMenu.HEIGHT);
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		window.setTitle("Tetris");
		window.setVisible(true);	
	}
}