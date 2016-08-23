import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.*;

public class Main{

    public static void main(String[] args) {

        JFrame frame = new JFrame("Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PongPanel pongPanel = new PongPanel();
		
        frame.add(pongPanel);
        frame.addKeyListener(pongPanel);
        
        
		frame.pack();
        frame.setVisible(true);
        
        new Cliente(pongPanel).start();
     }

}
