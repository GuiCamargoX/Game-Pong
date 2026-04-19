import javax.swing.JFrame;
public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PongPanel gamePanel = new PongPanel();
        frame.add(gamePanel);
        frame.addKeyListener(gamePanel);

		frame.pack();
        frame.setVisible(true);

        new Cliente(gamePanel).start();
      }

}
