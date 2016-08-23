import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.Rectangle;

class Servidor {
  public static void main(String[] args) {
    ServerSocket serverSocket=null;

    try {
      serverSocket = new ServerSocket(80);
    } catch (IOException e) {
      System.out.println("Could not listen on port: " + 80 + ", " + e);
      System.exit(1);
    }

    for (int i=0; i<3; i++) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException e) {
        System.out.println("Accept failed: " + 80 + ", " + e);
        System.exit(1);
      }

      System.out.println("Accept Funcionou!");

      new Servindo(clientSocket).start();

    }
	
	
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


class Servindo extends Thread {
  Socket clientSocket;
  static DataOutputStream os[] = new DataOutputStream[2];
  static int cont=0;
  public boolean up, down;

  static int BallX=650/2 ,BallY=480/2;//screen player1
  static int Ball2X=650/2; //screen player2
  static int player1=250 , player2=250;
  static int fator1=5, fator2=5;
  static int playerOneScore = 0, playerTwoScore = 0;
  static boolean showTitleScreen=true;
  
	Rectangle parede_cima = new Rectangle(10, 0 , 650-20 , 6);
	Rectangle parede_baixo= new Rectangle(10, 480-6, 650-20 , 6);

	Rectangle rect1 = new Rectangle(30, player1, 17, 77);
	Rectangle rect2 = new Rectangle(590, player2, 17, 77);
	
	Rectangle bola = new Rectangle(BallX, BallY, 15, 15); 
  
  Servindo(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    try {
      DataInputStream is = new DataInputStream(clientSocket.getInputStream());
      os[cont++] = new DataOutputStream(clientSocket.getOutputStream());
      
      new Out().start();
      
	 if(cont==2){
	  showTitleScreen=false;
	  new Action_ball().start();
	}
	
	char jogador=is.readChar();

      do {

				up = is.readBoolean();
				down = is.readBoolean();

					
		    if(up==true && !rect1.intersects(parede_cima) && jogador=='1' )
			player1 -=10;			 	
				
			if(down==true && !rect1.intersects(parede_baixo) && jogador=='1' )
			player1 +=10;
			
			if(up==true && !rect2.intersects(parede_cima) && jogador=='2')
			player2 -=10;			 	
				
			if(down==true && !rect2.intersects(parede_baixo) && jogador=='2')
			player2 +=10;
			
							 
			 rect1.setLocation(30,player1); 
			 rect2.setLocation(590,player2);
			
			
		} while( true );


		
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      System.out.println("Conexacao terminada pelo cliente");
    }
  }

class Action_ball extends Thread{//classe interna que movimenta a bola
	
	public void run(){
		do {
			bola.setLocation(BallX, BallY);

		if(BallX<=0 || BallX>=650){
				
				if(BallX<=0)				
				   playerTwoScore++;
	
				if(BallX>=650)
					playerOneScore++;
										
			BallX=650/2;
			Ball2X=600/2;//naosei pq esta ocorrendo este erro!
			BallY=480/2;
		 }
		
	
		if(rect1.intersects(bola) || rect2.intersects(bola))
			fator1=fator1*(-1);	
		
		if(bola.intersects(parede_cima) || bola.intersects(parede_baixo) )
			fator2=fator2*(-1);
		
		Ball2X-=fator1*(-1);
		
		BallX-=fator1;
		BallY-=fator2;
		
		
		
		
				try{
				sleep(1000/60);
				}catch(InterruptedException e){};
				
		}while(true);
	}

}


class Out extends Thread{//classe interna
	int num_cliente;
	
	public void run(){	
		    num_cliente=cont-1;  
		    
		     try{
				os[num_cliente].writeInt(num_cliente);
				
		      } catch (IOException e) {System.err.println("IOException:  " + e);}
		    
			do{
				try{    
						os[num_cliente].writeBoolean(showTitleScreen);
						os[num_cliente].writeInt(playerOneScore);
						os[num_cliente].writeInt(playerTwoScore);
						
						os[num_cliente].writeInt(player1);
						os[num_cliente].writeInt(player2);
					
						if(num_cliente==0)
							os[num_cliente].writeInt(BallX);
							
						if(num_cliente==1)
							os[num_cliente].writeInt(Ball2X);							
						
						os[num_cliente].writeInt(BallY);
						os[num_cliente].flush();	
						
				} catch (IOException e) {System.err.println("IOException:  " + e);}
		
				try{
				sleep(1000/60);//cuidado com este sleep,se for muito pequeno sobrecarrega a rede enviando muitos dados
				}catch(InterruptedException e){};
				
		
			}while(true);
		


	}

}


}
