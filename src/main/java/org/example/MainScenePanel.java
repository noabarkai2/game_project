package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Random;

public class MainScenePanel extends JPanel {
    private Player player;
    private Cake [] cakes = new Cake[100];
    private int cakesCount = 0;
    private final int CAKE_SIZE = 50;

    private LevelsBackground levelsBackground;

    public MainScenePanel (int x, int y, int width, int height) {
        this.setBounds(x, y, width, height);
        this.setLayout(null);
        this.levelsBackground = new LevelsBackground();
        // --- בניית המבוך (לולאות ומערכים) ---
        cakesCount = 0;

        int cols = width / CAKE_SIZE;
        int rows = height / CAKE_SIZE;
        int midX = cols / 2;
        int midY = rows / 2;

        // הגדרת מערכים חד-מימדיים
        int[] xPositions = new int[200];
        int[] yPositions = new int[200];
        int posCount = 0;

        // 1. רבע שמאלי עליון
        for (int gridX = midX - 6; gridX <= midX - 2; gridX++) {
            xPositions[posCount] = gridX; yPositions[posCount] = midY - 4; posCount++;
        }
        for (int gridY = midY - 3; gridY <= midY - 2; gridY++) {
            xPositions[posCount] = midX - 6; yPositions[posCount] = gridY; posCount++;
        }

        // 2. רבע ימני עליון
        for (int gridX = midX + 2; gridX <= midX + 6; gridX++) {
            xPositions[posCount] = gridX; yPositions[posCount] = midY - 4; posCount++;
        }
        for (int gridY = midY - 3; gridY <= midY - 2; gridY++) {
            xPositions[posCount] = midX + 6; yPositions[posCount] = gridY; posCount++;
        }

        // 3. רבע שמאלי תחתון
        for (int gridX = midX - 6; gridX <= midX - 2; gridX++) {
            xPositions[posCount] = gridX; yPositions[posCount] = midY + 4; posCount++;
        }
        for (int gridY = midY + 2; gridY <= midY + 3; gridY++) {
            xPositions[posCount] = midX - 6; yPositions[posCount] = gridY; posCount++;
        }

        // 4. רבע ימני תחתון
        for (int gridX = midX + 2; gridX <= midX + 6; gridX++) {
            xPositions[posCount] = gridX; yPositions[posCount] = midY + 4; posCount++;
        }
        for (int gridY = midY + 2; gridY <= midY + 3; gridY++) {
            xPositions[posCount] = midX + 6; yPositions[posCount] = gridY; posCount++;
        }

        // 5. --- הריבוע הפנימי החדש (עם פתח אחד בימין ופתח אחד בשמאל) ---

        // קיר עליון של הריבוע הפנימי (סגור לגמרי)
        for (int gridX = midX - 1; gridX <= midX + 2; gridX++) {
            xPositions[posCount] = gridX; yPositions[posCount] = midY - 1; posCount++;
        }

        // קיר תחתון של הריבוע הפנימי (סגור לגמרי)
        for (int gridX = midX - 1; gridX <= midX + 2; gridX++) {
            xPositions[posCount] = gridX; yPositions[posCount] = midY + 2; posCount++;
        }

        // קיר שמאלי של הריבוע הפנימי - עם רווח (בעזרת תנאי שמדלג על האמצע)
        for (int gridY = midY - 1; gridY <= midY + 2; gridY++) {
            if (gridY != midY) {
                xPositions[posCount] = midX - 2; yPositions[posCount] = gridY; posCount++;
            }
        }

        // קיר ימני של הריבוע הפנימי - עם רווח (בעזרת תנאי שמדלג על האמצע)
        for (int gridY = midY - 1; gridY <= midY + 2; gridY++) {
            if (gridY != midY) {
                xPositions[posCount] = midX + 3; yPositions[posCount] = gridY; posCount++;
            }
        }

        // --- סיום וציור ---
        // לולאה שמציירת את כל העוגות מהמערכים
        for (int i = 0; i < posCount; i++) {
            if (cakesCount < cakes.length) {
                cakes[cakesCount++] = new Cake(xPositions[i] * CAKE_SIZE, yPositions[i] * CAKE_SIZE, CAKE_SIZE, CAKE_SIZE);
            }
        }
        this.player = new Player(100, 100, 60,60);


        this.setFocusable(true);//רשאי לקבל פוקוס מהמקלדת
        this.requestFocus();//מקבל פוקוס מהמקלדת- השחקן יכול להגיב ללחיצות
        this.setDoubleBuffered(true);// הפתרון לריצוד הגיף - כדי שלא נראה את המחיקה והציור בזמן אמת רק שהציור מוכן

        MovementListener movementListener = new MovementListener(this, this.player);
        this.addKeyListener(movementListener);
        JButton soundButton = Utils.createSoundButton();
        this.add(soundButton);
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(width-65,15,60,30);
        exitButton.setBackground(new java.awt.Color(255,100,100));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new java.awt.Font("Arial", Font.BOLD, 12));
        exitButton.setBorderPainted(false);
        exitButton.setFocusable(false);
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        this.add(exitButton);
        this.gameLoop();


    }
    public boolean checkCakeCollision() {
        Rectangle characterRect = this.player.getRect();
// עכשיו אנחנו שולטים בכל כיוון בנפרד!
        int padLeft = 14;   // חותכים מהצדדים כדי שייכנס למעברים
        int padRight = 14;
        int padTop = 22;    // חותכים הרבה מלמעלה בגלל הדובדבן והשטח הריק
        int padBottom = 5;  // חותכים ממש מעט מלמטה, כדי שלא יעלה על העוגות!

        Rectangle smallCharacterRect = new Rectangle(
                characterRect.x + padLeft,
                characterRect.y + padTop,
                characterRect.width - (padLeft + padRight),
                characterRect.height - (padTop + padBottom)
        );

        for (int i = 0; i < this.cakesCount; i++) {
            Cake currentCake = this.cakes[i];
            if (currentCake != null) {
                Rectangle cakeRect = currentCake.getRect();

                int cakePadding = 4;
                Rectangle smallCakeRect = new Rectangle(
                        cakeRect.x + cakePadding,
                        cakeRect.y + cakePadding,
                        cakeRect.width - (cakePadding * 2),
                        cakeRect.height - (cakePadding * 2)
                );

                if (smallCharacterRect.intersects(smallCakeRect)) {
                    return true;
                }
            }
        }
        return false;
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
        if(this.levelsBackground != null){
            this.levelsBackground.paint(graphics, this.getWidth(), this.getHeight());
        }
        if(cakes.length>0){
            for (int i = 0; i < cakesCount; i++) {
                if(cakes[i]!= null){
                    cakes[i].paint(graphics);
                }
            }
        }


        if(this.player != null){
            this.player.paint(graphics);

        }
    }

}
