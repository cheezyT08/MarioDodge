package com.github.cheezyT08.dodge;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author: CheezyT08 
 * THEO 2021-2022 Final Project
 *
*/
public class DodgeGame extends JFrame {
	
	public static void main(String[] args) {
		DodgeGame game = new DodgeGame();
		
		game.start();
	}
	
	private int width = 600, height = 400;
	
	private final static Color SKY = new Color(110, 160, 255), GRASS = new Color(20, 220, 10);
	
	private DodgePanel p = new DodgePanel(this);
		
	public boolean running = false, charloaderr = false, obloaderr = false;
	
	private static BufferedImage charimgl = null, charimgr = null, obImg = null, heartImg = null, cloudImg = null, groundImg = null;
	
	
	{
		try {
			charimgr = ImageIO.read(new File("imgs\\charimgr.png"));
			charimgl = ImageIO.read(new File("imgs\\charimgl.png"));
		} catch (Exception ex) {ex.printStackTrace(); charloaderr = true;System.err.println("Unable to load character image!");}
		
		try {
			obImg = ImageIO.read(new File("imgs\\obimg.png"));
		} catch (Exception ex) {ex.printStackTrace(); obloaderr = true; System.err.println("Unable to load obstacle image!");}
		
		try {
			heartImg = ImageIO.read(new File("imgs\\heart.png"));
		} catch (Exception ex) {ex.printStackTrace(); obloaderr = true; System.err.println("Unable to load heart image!");}
		
		try {
			cloudImg = ImageIO.read(new File("imgs\\clouds.png"));
		} catch (Exception ex) {ex.printStackTrace(); obloaderr = true; System.err.println("Unable to load cloud image!");}
		
		try {
			groundImg = ImageIO.read(new File("imgs\\ground.png"));
		} catch (Exception ex) {ex.printStackTrace(); obloaderr = true; System.err.println("Unable to load ground image!");}
	}
	
	
	public DodgeGame() {
		this.setTitle("Mario Dodge - Score: 0 - Lives: 3");
		this.setSize(width+14, height+70);
		this.setDefaultCloseOperation(3);
		this.setResizable(false);
		
		this.add(p);
		
		this.addKeyListener(p);
		
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void start() {
		p.loop();
	}
	
	public void stop() {
		p.end();
	}
	
	private class DodgePanel extends JPanel implements KeyListener {
		
		private Random rand = new Random();
		
		private int pheight = 50, pwidth = 30, minpy = height-pheight, minpx = 0, maxpx = width-pwidth, obwidth = 76, obheight = 95, minoby = height+52+obheight, score = 0, lives = 3, hsize = 30, cloudHeight = 200;
		private double gravity = -0.065, py = height-pheight, px = 100, pv = 0.00, chspeed = 1.6, obx = (double) rand.nextInt(width-obwidth)+1, oby = -obheight*2, obv = 1.00, obx2 = (double) rand.nextInt(width-obwidth)+1, oby2 =  -obheight*5, obv2 = 1.00, obtrackspeed = 0.15;
		
		private BufferedImage playerImg = charimgr;
		
		private boolean jump = false, doublej = true, pleft = false, pright = false, gameover = false, safe = false;
		
		private JFrame parent;
		
		public DodgePanel(JFrame parent) {
			this.setBackground(SKY);
			this.parent = parent;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			if(gameover) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, width+100, height+100);
				
				//Game Over
				g.setColor(Color.RED);
				g.setFont(new Font("sans-serif", 1, 51));
				g.drawString("GAME OVER", (width/2)-150, 80);
				
				//Score
				g.setFont(new Font("sans-serif", 1, 20));
				g.drawString("Score: "+(score*10), (width/2)-35, 160);
				
			} else {
			
				//Drawing
				//Grass
				g.setColor(GRASS);
				g.fillRect(0, height, width, 50);
				
				//Clouds
				g.drawImage(cloudImg, 0, 0, width, cloudHeight, rootPane);
				
				//Lives
				for(int i = 0; i < lives; i++) {
					g.drawImage(heartImg, 10+((hsize+10)*i), 10, hsize, hsize, rootPane);
				}
				
				//Character
				if(charloaderr) {
					g.setColor(Color.black);
					g.drawRect((int) px, (int) py, pwidth, pheight);
				} else {
					g.drawImage(playerImg, (int) px, (int) py, pwidth, pheight, rootPane);
				}
				
				//Obstacle
				//Ob1
				if(obloaderr) {
					g.setColor(Color.BLACK);
					g.fillRect((int) obx, (int) oby, obwidth, obheight);
				} else {
					g.drawImage(obImg, (int) obx, (int) oby, obwidth, obheight, rootPane);
				}
				
				//Ob2
				if(obloaderr) {
					g.setColor(Color.BLACK);
					g.fillRect((int) obx2, (int) oby2, obwidth, obheight);
				} else {
					g.drawImage(obImg, (int) obx2, (int) oby2, obwidth, obheight, rootPane);
				}
				
			}
		}
		
		public void loop() {
			running = true;
			
			while(running) {
				
				//Add forces
				if(jump) {
					pv = 4;
					jump = false;
				}
				
				if(pleft) {
					px -= chspeed;
				} else if(pright) {
					px += chspeed;
				}
				
				if(py > minpy-1) {
					doublej = true;
				}
				
				//Update velocity & acceleration
				
					//Character
				pv += gravity;
				py -= pv;
				
					//Obstacle
						
						//Ob1
							//y
				oby += obv+(Math.sqrt(score/3))/5;
							
							//x
				if(px < obx) {
					obx -= (obtrackspeed+(score/20));
				} else {
					obx += (obtrackspeed+(score/20));
				}
							
						//Ob2
							//y
				if(score > 10) {
					oby2 += obv2+(Math.sqrt(score/3))/5;
				}
				
							//x
				if(px < obx2) {
					obx2 -= (obtrackspeed+(score/40));
				} else {
					obx2 += (obtrackspeed+(score/40));
				}
				
				//Collision Check
				
					//Player & Obstacle
				
						//Ob1
				if(px > obx+(obwidth/5) && px < obx+obwidth-(obwidth/5) && py < oby+obheight-1 && py+pheight > oby+(obheight/3)) {
					if(!safe) {
						safe = true;
						
						lives--;
						
						setTitle();
					}
					
					if(lives <= 0) {
						running = false;
						
						lose();
					}
				}
				
						//Ob2
				if(px > obx2+(obwidth/5) && px < obx2+obwidth-(obwidth/5) && py < oby2+obheight-1 && py+pheight > oby2+(obheight/3)) {
					if(!safe) {
						safe = true;
						
						lives--;
						
						setTitle();
					}
					
					if(lives <= 0) {
						running = false;
						
						lose();
					}
				}
				
				//Check Bounds
				if(py > minpy) {
					py = minpy;
					pv = 0.00;
				}
				
				if(px < minpx) {
					px = minpx;
				} else if(px > maxpx) {
					px = maxpx;
				}
				
				//Reset positions
				
					//Obstacle
				
						//Ob1
				if(oby > minoby) {
					score ++;
					obx = (double) rand.nextInt(width-obwidth)+1;
					oby = -obheight;
					
					safe = false;
					
					setTitle();
				}
				
						//Ob2
				if(oby2 > minoby) {
					score ++;
					obx2 = (double) rand.nextInt(width-obwidth)+1;
					oby2 = -obheight;
					
					safe = false;
					
					setTitle();
				}
				
				repaint();
				
				try {
					Thread.sleep(5);
				} catch (InterruptedException ex) {}
			}
		}
		
		public void end() {
			running = false;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
				if(py > minpy-1 || doublej) {
					jump = true;
					doublej = false;
				}
			} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				playerImg = charimgl;
				pleft = true;
			} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
				playerImg = charimgr;
				pright = true;
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				pleft = false;
			} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
				pright = false;
			}
		}
		
		@Override
		public void keyTyped(KeyEvent e) {}
		
		public void lose() {
			pv = 3.5;
			
			while(py < 1000) {
				px+=1.1;
				pv+=gravity;
				py-=pv;
				
				oby += obv+(Math.sqrt(score/3))/5;
				obx -= 0.3;
				
				if(score > 10) {
					oby2 += obv2+(Math.sqrt(score/3))/5;
				} obx2 -= 0.3;
				
				repaint();
				
				try {
					Thread.sleep(5);
				} catch(Exception ex) {}
			}
			
			
			//String gameovertag = "<html><span style=\"color: rgb(200 0 0); font-weight: 700;\">&nbsp;&nbsp;GAME OVER</span></html>";
			//JOptionPane.showMessageDialog(null, gameovertag+"\n Final Score: "+score*10);
			gameover = true;
			repaint();
			
			try {
				Thread.sleep(5000);
			} catch(Exception ex) {}
			
			System.exit(0);
		}
		
		private void setTitle() {
			parent.setTitle("Mario Dodge - Score: "+score*10+" Lives: "+lives);
		}
	}

}
