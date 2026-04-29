package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class MainScenePanel extends JPanel {
    private Player player;

    public MainScenePanel (int x, int y, int width, int height) {
        this.setBounds(x, y, width, height);
        this.setLayout(null);
        this.player = new Player(100, 100, 60,60);
        this.setFocusable(true);
        this.requestFocus();
        this.gameLoop();
        this.setDoubleBuffered(true);
        MovementListener movementListener = new MovementListener(player);
        this.addKeyListener(movementListener);

    }
    public void gameLoop () {
        new Thread(() -> {
            while (true) {
                repaint();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void paintComponent (Graphics graphics) {
        super.paintComponent(graphics);
        this.player.paint(graphics);
    }

}
