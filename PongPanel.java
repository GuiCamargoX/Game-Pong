import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.awt.event.*;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Font;

public class PongPanel extends JPanel implements KeyListener{
boolean showTitleScreen = true;
int BallX=650/2 ,BallY=480/2 ,diameter;
int player1y=250 , player2y=250;
int playerOneScore = 0;
int playerTwoScore = 0;

public boolean up=false, down=false;
	
	//dimensao
	@Override
	public Dimension getPreferredSize() {
      return new Dimension(650,480);
    }
    
    //construct a PongPanel
    public PongPanel(){
        setBackground(Color.BLACK);
        new FPS().start();
    }

    //paint a ball
    public void paintComponent(Graphics g){

        super.paintComponent(g);
        g.setColor(Color.WHITE);
        
        if (showTitleScreen) {//fazer isso enquanto showTitleSceen é verdade

            g.setFont(new Font(Font.DIALOG, Font.BOLD, 70));
            g.drawString("Pong", 225, 100);

            g.setFont(new Font(Font.DIALOG, Font.BOLD, 28));
            g.drawString("Esperando outro jogador", 145, 400);

        }else {

        //desenhar o tracejado
        for (int lineY = 0; lineY < getHeight(); lineY += 50) {
                g.drawLine(650/2, lineY, 650/2, lineY+25);
        }
        
        //desenha os placares
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
        g.drawString(String.valueOf(playerOneScore), 150, 100);
        g.drawString(String.valueOf(playerTwoScore), 450, 100);
		
		//desenha a bola
        g.fillRect(BallX, BallY, 15, 15);
        
        //parede_cima
        g.fillRect(10, 0 , 650-20 , 6);
        //parede_baixo
        g.fillRect(10, 480-6, 650-20 , 6);
        
       
        g.fillRect(30, player1y, 16, 77);//player1
        
        g.fillRect(590, player2y, 16, 77);//player2
		}
    
    }

	
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_UP && !showTitleScreen){
			up=true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_DOWN && !showTitleScreen){
			down=true;
		}
		
	}
    public void keyReleased(KeyEvent e){ 
		if(e.getKeyCode() == KeyEvent.VK_UP && !showTitleScreen){
			System.out.println("Foi pression");
			up=false;
		}

		if(e.getKeyCode() == KeyEvent.VK_DOWN && !showTitleScreen){
			System.out.println("Foi preeesi");
			down=false;
		}
	}
		
    public void keyTyped(KeyEvent e){ }
    
   	
	class FPS extends Thread{
		public void run(){
		    while(true){							
				repaint();	
		     try{
		    	 sleep(1000/60);
			     }catch(InterruptedException e){}
			  }
		}
	}
    
}
