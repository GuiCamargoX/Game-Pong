package client;

import javax.swing.JFrame;

public class ClientMain {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.addKeyListener(gamePanel);

        frame.pack();
        frame.setVisible(true);

        new GameClient(gamePanel).start();
    }
}
