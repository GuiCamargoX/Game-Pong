package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener {
    static final int BOARD_WIDTH = 650;
    static final int BOARD_HEIGHT = 480;

    private static final int FRAME_RATE = 60;
    private static final long FRAME_SLEEP_MILLIS = 1000L / FRAME_RATE;
    private static final int CENTER_LINE_X = BOARD_WIDTH / 2;
    private static final int CENTER_LINE_STEP = 50;
    private static final int CENTER_LINE_LENGTH = 25;
    private static final int SCORE_FONT_SIZE = 36;
    private static final int TITLE_FONT_SIZE = 70;
    private static final int WAITING_FONT_SIZE = 28;
    private static final int TITLE_X = 225;
    private static final int TITLE_Y = 100;
    private static final int WAITING_X = 145;
    private static final int WAITING_Y = 400;
    private static final int LEFT_SCORE_X = 150;
    private static final int RIGHT_SCORE_X = 450;
    private static final int SCORE_Y = 100;
    private static final int BALL_SIZE = 15;
    private static final int WALL_X = 10;
    private static final int WALL_THICKNESS = 6;
    private static final int WALL_WIDTH = BOARD_WIDTH - 20;
    private static final int LEFT_PADDLE_X = 30;
    private static final int RIGHT_PADDLE_X = 590;
    private static final int PADDLE_WIDTH = 16;
    private static final int PADDLE_HEIGHT = 77;

    boolean showTitleScreen = true;
    int ballX = BOARD_WIDTH / 2;
    int ballY = BOARD_HEIGHT / 2;
    int playerOneY = 250;
    int playerTwoY = 250;
    int playerOneScore = 0;
    int playerTwoScore = 0;

    public boolean moveUp = false;
    public boolean moveDown = false;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

    public GamePanel() {
        setBackground(Color.BLACK);
        new RepaintLoop().start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setColor(Color.WHITE);

        if (showTitleScreen) {
            graphics.setFont(new Font(Font.DIALOG, Font.BOLD, TITLE_FONT_SIZE));
            graphics.drawString("Pong", TITLE_X, TITLE_Y);

            graphics.setFont(new Font(Font.DIALOG, Font.BOLD, WAITING_FONT_SIZE));
            graphics.drawString("Esperando outro jogador", WAITING_X, WAITING_Y);
            return;
        }

        for (int lineY = 0; lineY < getHeight(); lineY += CENTER_LINE_STEP) {
            graphics.drawLine(CENTER_LINE_X, lineY, CENTER_LINE_X, lineY + CENTER_LINE_LENGTH);
        }

        graphics.setFont(new Font(Font.DIALOG, Font.BOLD, SCORE_FONT_SIZE));
        graphics.drawString(String.valueOf(playerOneScore), LEFT_SCORE_X, SCORE_Y);
        graphics.drawString(String.valueOf(playerTwoScore), RIGHT_SCORE_X, SCORE_Y);

        graphics.fillRect(ballX, ballY, BALL_SIZE, BALL_SIZE);
        graphics.fillRect(WALL_X, 0, WALL_WIDTH, WALL_THICKNESS);
        graphics.fillRect(WALL_X, BOARD_HEIGHT - WALL_THICKNESS, WALL_WIDTH, WALL_THICKNESS);

        graphics.fillRect(LEFT_PADDLE_X, playerOneY, PADDLE_WIDTH, PADDLE_HEIGHT);
        graphics.fillRect(RIGHT_PADDLE_X, playerTwoY, PADDLE_WIDTH, PADDLE_HEIGHT);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (showTitleScreen) {
            return;
        }

        if (event.getKeyCode() == KeyEvent.VK_UP) {
            moveUp = true;
        }

        if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            moveDown = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (showTitleScreen) {
            return;
        }

        if (event.getKeyCode() == KeyEvent.VK_UP) {
            moveUp = false;
        }

        if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            moveDown = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    class RepaintLoop extends Thread {
        @Override
        public void run() {
            while (true) {
                repaint();

                try {
                    sleep(FRAME_SLEEP_MILLIS);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
