import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JDialog;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.Box;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Main {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	private static int x;
	private static int y;
	private static boolean gameOver;
	private static boolean passed;
	private static boolean removingPieces;
	private static boolean resumeGame;
	private static boolean playAgain;
	private static Go go;

	public static void main(String[] args) {
		final Object click = new Object();
		JFrame frame = new JFrame("Go");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout());
		JPanel smallPanel = new JPanel();
		smallPanel.setLayout(new BoxLayout(smallPanel, BoxLayout.Y_AXIS));
		JButton start = new JButton("INSTRUCTIONS: START HERE");
		
		go = new Go();
		go.setRules("New Zealand");
		gameOver = false;
		boolean firstMove = true;
		passed = false;
		boolean blackNotPass = true;
		boolean whiteNotPass = true;

		//System.out.println("check 1");
		
		class BeginListener implements ActionListener{
		  public void actionPerformed(ActionEvent e){
		    String welcome = " READ THE ENTIRE THING THANKS \n This is a game of Go using the New Zealand rules. Details can be found here: https://senseis.xmp.net/?NewZealandRules \n\n Quick Overview: "
		    		+ "\n  -Area Scoring (captured pieces will not count for points) \n  -Superko: Applies (no position on the board can be the same as a previous one) \n  -Komi: white gets seven extra points \n  -Illegal move: pass that turn \n  -Self capture/suicide: allowed \n  -End of game: consecutive passes "
		    		+ "\n\n Click on the board to set pieces down. \n Pieces without any liberties will be considered \"captured\" and removed from board. \n As in a real game, after consecutive passes players will be given the chance to remove dead pieces before scoring. "
		    		+ "\n Area is captured when you have surrounded the area completely with your pieces (or against a border) so that it does not touch\n any pieces of the other player\'s color.";
		    JDialog intro = new JDialog(frame, "Welcome");
		    JPanel panel2 = new JPanel();
		    panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		    JTextArea text = new JTextArea(welcome);
		    JLabel label = new JLabel("Are we playing with komi?");
		    JRadioButton komi = new JRadioButton("Yes");
		    komi.setActionCommand("yes");
		    JRadioButton nokomi = new JRadioButton("No");
		    nokomi.setActionCommand("no");
		    ButtonGroup group = new ButtonGroup();
		    class komiOption implements ActionListener{
		    	public void actionPerformed(ActionEvent e) {
		    		if(e.getActionCommand().equals("yes")) {
		    			go.komi(true);
		    		} else {
		    			go.komi(false);
		    		}
		    	}
		    }
		    komi.addActionListener(new komiOption());
		    nokomi.addActionListener(new komiOption());
		    
		    JLabel label2 = new JLabel("Handicap: how many turns can black take before white can go?");
		    Integer[] option = {1, 2, 3, 4, 5, 6, 7, 8, 9};
		    JComboBox options = new JComboBox(option);
		    class Handicap implements ActionListener{
		    	public void actionPerformed(ActionEvent e) {
		    		JComboBox box = (JComboBox)e.getSource();
		    		go.handicap((int) box.getSelectedItem());
		    		//System.out.println(go.returnHandicap());
		    	}
		    }
		    options.addActionListener(new Handicap());
		    
		    JButton begin = new JButton("Begin");
		    class closeDialog implements ActionListener{
		    	public void actionPerformed(ActionEvent e) {
		    		intro.dispose();
		    		start.setVisible(false);
		    		synchronized(click) {
					    click.notify();
					}
		    	}
		    }
		    begin.addActionListener(new closeDialog());
		    group.add(komi);
		    group.add(nokomi);
		    panel2.add(text);
		    panel2.add(label);
		    label.setAlignmentX(Component.CENTER_ALIGNMENT);
		    panel2.add(komi);
		    panel2.add(nokomi);
		    panel2.add(label2);
		    label2.setAlignmentX(Component.CENTER_ALIGNMENT);
		    panel2.add(options);
		    options.setSize(200, 30);
		    panel2.add(begin);
		    intro.add(panel2);
		    intro.pack();
		    intro.setLocationRelativeTo(frame);
		    intro.setVisible(true);
		  }
		}
		start.addActionListener(new BeginListener());
		
		JLabel turn = new JLabel("Turn: ");
		
		JButton pass = new JButton("Pass");
		class PassTurn implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				go.pass();
				passed = true;
				if(go.consecPasses()) {
					synchronized(click) {
					    click.notify();
					}
				}
				
			}
		}
		pass.addActionListener(new PassTurn());
		
		Canvas canvas = new Canvas(WIDTH, HEIGHT, go.board());
		canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		class SetPiece implements MouseListener{
			public void mouseClicked(MouseEvent e) {
				    x = e.getX();
				    y = e.getY();
				if(x % 40 < 20) {
					x /= 40;
				} else {
					x /= 40;
					x += 1;
				}
				if(y % 40 < 20) {
					y /= 40;
				} else {
					y /= 40;
					y += 1;
				}


				synchronized(click) {
				    click.notify();
				}				
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		}
		canvas.addMouseListener(new SetPiece());
		
		//System.out.println("check 2");
		
		JPanel bottomPanel = new JPanel();
		JButton done = new JButton("Done");
		class Done implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				removingPieces = false;
				synchronized(click) {
					click.notify();
				}
			}
		}
		done.addActionListener(new Done());
		
		JButton resume = new JButton("Resume");
		class Resume implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				removingPieces = false;
				resumeGame = true;
				synchronized(click) {
				    click.notify();
				}
			}
		}
		resume.addActionListener(new Resume());
		
		bottomPanel.add(done);
		bottomPanel.add(resume);
		bottomPanel.setVisible(false);

		smallPanel.add(start);
		start.setAlignmentX(Component.CENTER_ALIGNMENT);
		smallPanel.add(Box.createRigidArea(new Dimension(5, 20)));
		smallPanel.add(turn);
		turn.setAlignmentX(Component.CENTER_ALIGNMENT);
		smallPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		smallPanel.add(pass);
		smallPanel.add(Box.createRigidArea(new Dimension(5, 20)));
		pass.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(smallPanel, BorderLayout.PAGE_START);
		panel.add(canvas, BorderLayout.CENTER);
		canvas.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(bottomPanel, BorderLayout.PAGE_END);
		frame.add(panel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);

		//System.out.println("check 3");
		
		while(!gameOver) {

			if(firstMove) {
				synchronized(click) {
					try {
						click.wait();
					} catch (InterruptedException e) {}
				}

				for(int i = 0; i < go.returnHandicap(); i++) {
					//System.out.println("check 4");

					turn.setText("Turn: black");

					synchronized(click) {
						try {
							click.wait();
						} catch (InterruptedException e) {}
					}			

					if (!passed) {
						go.blackTurn(x, y);
						canvas.update();

						if(go.alreadyOccupied()) {
							JDialog error = new JDialog(frame, "Objection");
							JLabel forfeit = new JLabel("You cannot put a piece there. \nYou forfeit your turn.");
							error.add(forfeit);
							error.pack();
							error.setLocationRelativeTo(frame);
							error.setVisible(true);
						}
					}


				}
				firstMove = false;
			}
			//System.out.println("check 5");

			while(!go.consecPasses()) {
				turn.setText("Turn: black");

				//need if statement around this otherwise when you pass the loop will be blocked twice
				if (whiteNotPass) {
					synchronized(click) {
						try {
							// Calling wait() will block this thread until another thread (mouseListener) calls notify() on the object.
							click.wait();
						} catch (InterruptedException e) {}
					}			
				}


				if (!passed) {
					go.blackTurn(x, y);
					blackNotPass = true;
					canvas.update();


					if(go.alreadyOccupied()) {
						JDialog error = new JDialog(frame, "Objection");
						JLabel forfeit = new JLabel("You cannot put a piece there. \nYou forfeit your turn.");
						error.add(forfeit);
						error.pack();
						error.setLocationRelativeTo(frame);
						error.setVisible(true);
					}
					if(go.ko()) {
						JDialog error = new JDialog(frame, "Objection");
						JLabel forfeit = new JLabel("This move violates ko. \nYou forfeit your turn.");
						error.add(forfeit);
						error.pack();
						error.setLocationRelativeTo(frame);
						error.setVisible(true);
					}
				} else {
					blackNotPass = false;
				}

				passed = false;

				if(go.consecPasses()) {
					//System.out.println("check 6");
					JDialog afterPasses = new JDialog(frame, "You have ended the game.");
					JTextArea update = new JTextArea("You will now have a chance to remove dead pieces. "
							+ "\n(Pieces that would have been captured) If there is "
							+ "\ndisagreement about which pieces need to be taken off"
							+ "\n press the \"resume\" button to resume the game. Once"
							+ "\n you are done removing pieces, press \"done\" to go to"
							+ "\n scoring");
					afterPasses.add(update);
					afterPasses.pack();
					afterPasses.setLocationRelativeTo(frame);
					afterPasses.setVisible(true);
					bottomPanel.setVisible(true);

					removingPieces = true;
					resumeGame = false;
					turn.setText("Turn: white");

					while(removingPieces) {
						synchronized(click) {
							try {
								click.wait();
							} catch (InterruptedException e) {}
						}

						if(!removingPieces) {
							break;
						}

						go.removePieces(x, y);
						canvas.update();

					}

					//System.out.println("check7");

					if(!resumeGame) {
						break;
					} else {
						go.clearPasses();
						blackNotPass = true;
						whiteNotPass = true;
						bottomPanel.setVisible(false);
					}
				}
				
				turn.setText("Turn: white");

				if(blackNotPass) {
					synchronized(click) {
						try {
							click.wait();
						} catch (InterruptedException e) {}
					}			
				}

				if (!passed) {
					go.whiteTurn(x, y);
					whiteNotPass = true;
					canvas.update();

					if(go.alreadyOccupied()) {
						JDialog error = new JDialog(frame, "Objection");
						JLabel forfeit = new JLabel("You cannot put a piece there. \nYou forfeit your turn.");
						error.add(forfeit);
						error.pack();
						error.setLocationRelativeTo(frame);
						error.setVisible(true);
					}
					if(go.ko()) {
						JDialog error = new JDialog(frame, "Objection");
						JLabel forfeit = new JLabel("This move violates ko. \nYou forfeit your turn.");
						error.add(forfeit);
						error.pack();
						error.setLocationRelativeTo(frame);
						error.setVisible(true);
					}
				} else {
					whiteNotPass = false;
				}

				passed = false;

				if(go.consecPasses()) {
					//System.out.println("check 6");
					JDialog afterPasses = new JDialog(frame, "You have ended the game.");
					JTextArea update = new JTextArea("You will now have a chance to remove dead pieces. "
							+ "\n(Pieces that would have been captured) If there is "
							+ "\ndisagreement about which pieces need to be taken off"
							+ "\n press the \"resume\" button to resume the game. Once"
							+ "\n you are done removing pieces, press \"done\" to go to"
							+ "\n scoring");
					afterPasses.add(update);
					afterPasses.pack();
					afterPasses.setLocationRelativeTo(frame);
					afterPasses.setVisible(true);
					bottomPanel.setVisible(true);

					removingPieces = true;
					resumeGame = false;
					turn.setText("Removing Pieces");

					while(removingPieces) {
						synchronized(click) {
							try {
								click.wait();
							} catch (InterruptedException e) {}
						}

						if(!removingPieces) {
							break;
						}
						System.out.println(removingPieces);

						go.removePieces(x, y);
						canvas.update();

					}

					//System.out.println("check7");

					if(!resumeGame) {
						break;
					} else {
						go.clearPasses();
						blackNotPass = true;
						whiteNotPass = true;
						bottomPanel.setVisible(false);
					}
				}

			}

			JDialog end = new JDialog(frame, "Congratulations!");
			JPanel panel3 = new JPanel();
			panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
			JLabel label2 = new JLabel("Here are the scores");
			go.countPoints();
			JLabel label3 = new JLabel("Black: " + go.blackPoints());
			JLabel label4 = new JLabel("White: " + go.whitePoints());
			JLabel label5 = new JLabel();
			if(go.blackPoints() > go.whitePoints()) {
				label5.setText("Black won");
			} else if (go.whitePoints() > go.blackPoints()) {
				label5.setText("White won");
			} else {
				label5.setText("It is a tie");
			}


			JButton playAgainButton = new JButton("Play again");
			class PlayGoAgain implements ActionListener{
				public void actionPerformed(ActionEvent e) {
					playAgain = true;
					end.dispose();
					synchronized(click) {
						click.notify();
					}
				}
			}
			playAgainButton.addActionListener(new PlayGoAgain());



			JButton exit = new JButton("Exit");
			class ExitGame implements ActionListener{
				public void actionPerformed(ActionEvent e) {
					gameOver = true;
					frame.dispose();
					synchronized(click) {
						click.notify();
					}
				}
			}
			exit.addActionListener(new ExitGame());

			panel3.add(label2);
			label2.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel3.add(label3);
			label3.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel3.add(label4);
			label4.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel3.add(label5);
			label5.setAlignmentX(Component.CENTER_ALIGNMENT);

			panel3.add(playAgainButton);
			playAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);

			panel3.add(exit);
			exit.setAlignmentX(Component.CENTER_ALIGNMENT);
			end.add(panel3);
			end.pack();
			end.setLocationRelativeTo(frame);
			end.setVisible(true);

			synchronized(click) {
				try {
					click.wait();
				} catch (InterruptedException e) {}
			}

			if(playAgain) {
				go = new Go();
				firstMove = true;
				bottomPanel.setVisible(false);
				start.setVisible(true);
				passed = false;
				blackNotPass = true;
				whiteNotPass = true;
				playAgain = false;
				canvas.update(go.board());
				canvas.update();
			}

		}
		
	}

}