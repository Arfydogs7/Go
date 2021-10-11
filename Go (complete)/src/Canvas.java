import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;

import javax.swing.JComponent;

public class Canvas extends JComponent{
	private final int WIDTH;
	private final int HEIGHT;
	private String[][] board;
	
	public Canvas(int width, int height, String[][] board) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.board = board;
	}
	
	public void paintComponent(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		g.setStroke(new BasicStroke(10));
		g.setColor(new Color(0, 0, 0));
		g.drawRect(0, 0, WIDTH, HEIGHT);
		g.setStroke(new BasicStroke(2));
		g.drawRect(40, 40, 720, 720);
		
		for(int x = 80; x < 760; x+=40) {
			g.drawLine(x, 40, x, 760);
			g.drawLine(40, x, 760, x);
		}
		
		for(int row = 1; row < 20; row++) {
			for(int col = 1; col < 20; col++) {
				if(board[row][col].equals("black")) {
					g.setColor(new Color(0, 0, 0));
					g.fillOval(col * 40 - 17, row * 40 - 17, 34, 34);
				} else if (board[row][col].equals("white")) {
					g.setColor(new Color(255, 255, 255));
					g.fillOval(col * 40 - 17, row * 40 - 17, 34, 34);
				}
			}
		}
	}
	
	public void update() {
		repaint();
	}
	
	public void update(String[][] board) {
		this.board = board;
	}

}