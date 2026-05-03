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
        this.setFocusable(true);//רשאי לקבל פוקוס מהמקלדת
        this.requestFocus();//מקבל פוקוס מהמקלדת- השחקן יכול להגיב ללחיצות
        this.setDoubleBuffered(true);// הפתרון לריצוד הגיף - כדי שלא נראה את המחיקה והציור בזמן אמת רק שהציור מוכן

        MovementListener movementListener = new MovementListener(player);
        this.addKeyListener(movementListener);
        JButton soundButton = Utils.createSoundButton();
        this.add(soundButton);

        this.gameLoop();


    }
    public void gameLoop () {
        new Thread(() -> {
            while (true) {
                repaint();
                Utils.sleep(16);
            }
        }).start();
    }

    public void paintComponent (Graphics graphics) {
        super.paintComponent(graphics);
        this.player.paint(graphics);
    }

}
