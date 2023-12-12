package snake;
import java.awt.*;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

import javax.swing.JPanel;

/**
 * 
 * This class executes the actual picture within the window and allows for manipulation of the frame which allows for movement of the snake and all functionalities of the game
 *
 */

public class GamePanel extends JPanel implements ActionListener {
		static final int SCREEN_WIDTH = 600;
		static final int SCREEN_HEIGHT = 600;
		static final int UNIT_SIZE = 25;
		static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*2);
		static final int DELAY = 75; //Milliseconds
		final int x[] = new int[GAME_UNITS];
		final int y[] = new int[GAME_UNITS];
		int bodyParts = 6;
		int applesEaten;
		int appleX;
		int appleY;
		char direction = 'R';
		boolean running = false;
		Timer timer;
		Random random;
		
		/**
		 * creates the window for the game and sets the size, background color, makes it visible, and creates functionality for keyboard keys when pressed
		 */
		GamePanel() {
			random = new Random();
			this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
			this.setBackground(Color.black);
			this.setFocusable(true);
			this.addKeyListener(new MyKeyAdapter());
			startGame();
			
		}
		
		/**
		 * Starts the game setting up the board for the snake to run in. 
		 */
		public void startGame() {
			newApple();
			running = true;
			timer = new Timer(DELAY, this);
			timer.start();
		}
		//this method is not called in this program, it is called under the hood somewhere where I'm not sure exactly
		public void paintComponent(Graphics g) { //this is where g is derived from I am guessing. 
			super.paintComponent(g);
			draw(g);
		}
		
		/**
		 * draws the grid, position of the snake, apple at every repaint. 
		 * @param g 
		 */
		public void draw(Graphics g) { 
			//drawing the grid
			if(running) {
				for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
					g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				}
				for (int i = 0; i < SCREEN_WIDTH/UNIT_SIZE; i++) {
					g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
				}
				//drawing apple
				g.setColor(Color.red);
				g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
				
				//drawing snake
				for(int i = 0; i < bodyParts; i++) {
					if (i == 0) { //for the head of the snake
						g.setColor(Color.green);
						g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE); //what is at x[i]??? and how is it changing in the program
					}
					else {
						g.setColor(new Color(45,180,0));
						//g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
						g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
	
					}
				}
				g.setColor(Color.red);
				g.setFont( new Font("Ink Free", Font.BOLD, 40));
				FontMetrics metrics = getFontMetrics(g.getFont()); //Not sure what this is used for
				g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
			}
			else {
				gameOver(g);
			}
		}
		
		/**
		 * Creates a new apple in a random location
		 */
		public void newApple() {
			appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE; //divide by unit size to keep apple within the grid
			appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
			
		}
		
		/**
		 * moves snakes body parts to correct location depending on direction
		 */
		public void move() {
			for (int i = bodyParts; i > 0; i--) {
				x[i] = x[i-1];
				y[i] = y[i-1];
			}
			
			switch(direction) {
			case 'U':
				y[0] = y[0] - UNIT_SIZE;
				break;
			case 'D':
				y[0] = y[0] + UNIT_SIZE; 
				break;
			case 'L':
				x[0] = x[0] - UNIT_SIZE; 
				break;
			case 'R':
				x[0] = x[0] + UNIT_SIZE; //since it starts by moving in the right direction the program adds 25 to x[0]
				break;
			}
		}
		
		/**
		 * checks if snakes head is in the same spot as apple
		 */
		public void checkApple() {
			if((x[0] == appleX) && (y[0] == appleY)) {
				bodyParts++;
				applesEaten++;
				newApple();
			}
		}
		
		/**
		 * checks if snake has hit perimeter or body
		 */
		public void checkCollisions() {
			//checks if head collides with body
			for(int i = bodyParts; i > 0; i--) {
				if((x[0] == x[i]) && (y[0] == y[i])) {
					running = false;
				}
			}
			//check if head touches left border
			if(x[0] < 0) {
				running = false;
			}
			//check if head touches right border
			if(x[0] > SCREEN_WIDTH) {
				running = false;
			}
			//Ccheck if head touches top border
			if(y[0] < 0) {
				running = false;
			}
			//CHECK IF HEAD TOUCHES bottom border
			if(y[0] > SCREEN_HEIGHT) {
				running = false;
			}
			
			if(!running) {
				timer.stop();
			}
			
		}
		
		/**
		 * displays game over screen with total score
		 * @param g
		 */
		public void gameOver(Graphics g) {
			//Score
			g.setColor(Color.red);
			g.setFont( new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics1 = getFontMetrics(g.getFont()); //Not sure what this is used for
			g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
			//Game Over text
			g.setColor(Color.red);
			g.setFont( new Font("Ink Free", Font.BOLD, 75));
			FontMetrics metrics2 = getFontMetrics(g.getFont()); //Not sure what this is used for
			g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
		}
		
		/**
		 * calls the three methods needed to update the games frame and data every time an action is performed
		 */
		@Override
		public void actionPerformed(ActionEvent e) { //the timer class will repeatedly call this after a delay 
			if(running) {
				move();
				checkApple();
				checkCollisions();
				
			}
			repaint();
			
		}
		
		/**
		 * changes the direction of the snake depending on which direction is was previously going and what key has been pressed by player
		 */
		public class MyKeyAdapter extends KeyAdapter {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					if(direction != 'R') {
						direction = 'L';
					}
					break;
				case KeyEvent.VK_RIGHT:
					if(direction != 'L') {
						direction = 'R';
					}
					break;
				case KeyEvent.VK_UP:
					if(direction != 'D') {
						direction = 'U';
					}
					break;
				case KeyEvent.VK_DOWN:
					if(direction != 'U') {
						direction = 'D';
					}
					break;
				}
			}
		}
		
}
