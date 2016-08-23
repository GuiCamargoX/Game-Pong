import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente extends Thread{
  static DataOutputStream os = null;
  static DataInputStream is = null;
  static boolean paraThread = false;
  
  int player1=0 , player2=0 , BallX=0 , BallY=0;
  int playerOneScore = 0;
  int playerTwoScore = 0;
  
  int num_cliente;
  boolean up=false ,down=false, showTitleScreen=true;
  
  Socket socket=null;
  PongPanel pongPanel=null;
  
  Cliente(PongPanel pongPanel){
	this.pongPanel=pongPanel;
	}
	
public void run(){
     try {
      socket = new Socket("localhost",80);
      os = new DataOutputStream(socket.getOutputStream());
      is = new DataInputStream(socket.getInputStream());	
      
      num_cliente=is.readInt();//iniciar
     
      new Leitura_servidor().start();
      
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host.");
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to host");
    }
   
    try {
		
		switch (num_cliente){
		case 0:
			os.writeChar('1');
			break;
		case 1:
			os.writeChar('2');
			break;
		}
	
	
		do{
	
		this.up= pongPanel.up;
		this.down= pongPanel.down;	
        
        os.writeBoolean(up);
        os.writeBoolean(down);
		os.flush();
		
		try{
		sleep(1000/60);
		}catch(InterruptedException e){};
		
		}while(true);
     
    } catch (UnknownHostException e) {
      System.err.println("Trying to connect to unknown host: " + e);
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
	

 class Leitura_servidor extends Thread{//clase interna leitura nao preciso de sleep mas na hora de enviar precisa de sleep porque ira ocorrer um atraso

	public void run(){		
	
	try{
			
		do{ 	showTitleScreen=is.readBoolean();
        
                playerOneScore= is.readInt();
				playerTwoScore= is.readInt();
				
				player1=is.readInt();
				player2=is.readInt();
				
				BallX=is.readInt();
				BallY=is.readInt();
			
			if(num_cliente==0){
				pongPanel.showTitleScreen=showTitleScreen;
				pongPanel.player1y= player1;
				pongPanel.player2y= player2;
				pongPanel.BallX=BallX;
				pongPanel.BallY=BallY;
				
				pongPanel.playerOneScore= playerOneScore;
				pongPanel.playerTwoScore= playerTwoScore;
			}
			
			if(num_cliente==1){
				pongPanel.showTitleScreen=showTitleScreen;
				pongPanel.player1y= player2;
				pongPanel.player2y= player1;
				pongPanel.BallX=BallX;
				pongPanel.BallY=BallY;
				
				pongPanel.playerOneScore= playerTwoScore;
				pongPanel.playerTwoScore= playerOneScore;
			}
				
		}while(!paraThread);
	
	} catch (IOException e) {
      System.err.println("IOException:  " + e);}

	}
  }
  
}
