package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Random;

public class MainScenePanel extends JPanel {
    private Player player;
    private Cake[] cakes;
    private Enemy[] enemies;
    private int score;
    private Prize[] prizes;
    // מערך המחרוזות של התמונות נשאר בדיוק אותו דבר
    private String[] candyImages;

    private int cakesCount ;
    private final int CAKE_SIZE = 50;

    private LevelsBackground levelsBackground;

    public MainScenePanel(int x, int y, int width, int height) {
        this.cakes = new Cake[100];
        this.cakesCount = 0;
        this.candyImages = new String[] {
                "/Blue_candy.png",
                "/Orange_candy.png",
                "/Pink_candy.png",
                "/Purple_candy.png",
                "/Yellow_candy.png"
        };
        this.setBounds(x, y, width, height);
        this.setLayout(null);
        this.levelsBackground = new LevelsBackground();
        spawnPrizes(10); // המספר קובע כמה סוכריות ייווצרו

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
            xPositions[posCount] = gridX;
            yPositions[posCount] = midY - 4;
            posCount++;
        }
        for (int gridY = midY - 3; gridY <= midY - 2; gridY++) {
            xPositions[posCount] = midX - 6;
            yPositions[posCount] = gridY;
            posCount++;
        }

        // 2. רבע ימני עליון
        for (int gridX = midX + 2; gridX <= midX + 6; gridX++) {
            xPositions[posCount] = gridX;
            yPositions[posCount] = midY - 4;
            posCount++;
        }
        for (int gridY = midY - 3; gridY <= midY - 2; gridY++) {
            xPositions[posCount] = midX + 6;
            yPositions[posCount] = gridY;
            posCount++;
        }

        // 3. רבע שמאלי תחתון
        for (int gridX = midX - 6; gridX <= midX - 2; gridX++) {
            xPositions[posCount] = gridX;
            yPositions[posCount] = midY + 4;
            posCount++;
        }
        for (int gridY = midY + 2; gridY <= midY + 3; gridY++) {
            xPositions[posCount] = midX - 6;
            yPositions[posCount] = gridY;
            posCount++;
        }

        // 4. רבע ימני תחתון
        for (int gridX = midX + 2; gridX <= midX + 6; gridX++) {
            xPositions[posCount] = gridX;
            yPositions[posCount] = midY + 4;
            posCount++;
        }
        for (int gridY = midY + 2; gridY <= midY + 3; gridY++) {
            xPositions[posCount] = midX + 6;
            yPositions[posCount] = gridY;
            posCount++;
        }

        // 5. --- הריבוע הפנימי החדש (עם פתח אחד בימין ופתח אחד בשמאל) ---

        // קיר עליון של הריבוע הפנימי (סגור לגמרי)
        for (int gridX = midX - 1; gridX <= midX + 2; gridX++) {
            xPositions[posCount] = gridX;
            yPositions[posCount] = midY - 1;
            posCount++;
        }

        // קיר תחתון של הריבוע הפנימי (סגור לגמרי)
        for (int gridX = midX - 1; gridX <= midX + 2; gridX++) {
            xPositions[posCount] = gridX;
            yPositions[posCount] = midY + 2;
            posCount++;
        }

        // קיר שמאלי של הריבוע הפנימי - עם רווח (בעזרת תנאי שמדלג על האמצע)
        for (int gridY = midY - 1; gridY <= midY + 2; gridY++) {
            if (gridY != midY) {
                xPositions[posCount] = midX - 2;
                yPositions[posCount] = gridY;
                posCount++;
            }
        }

        // קיר ימני של הריבוע הפנימי - עם רווח (בעזרת תנאי שמדלג על האמצע)
        for (int gridY = midY - 1; gridY <= midY + 2; gridY++) {
            if (gridY != midY) {
                xPositions[posCount] = midX + 3;
                yPositions[posCount] = gridY;
                posCount++;
            }
        }

        // --- סיום וציור ---
        // לולאה שמציירת את כל העוגות מהמערכים
        for (int i = 0; i < posCount; i++) {
            if (cakesCount < cakes.length) {
                cakes[cakesCount++] = new Cake(xPositions[i] * CAKE_SIZE, yPositions[i] * CAKE_SIZE, CAKE_SIZE, CAKE_SIZE);
            }
        }
        this.player = new Player(100, 100, 60, 60);
        this.enemies = new Enemy[7];
        this.enemies[0] = new EnemyBroccoli(200, 100, 80, 80);
        this.enemies[1] = new EnemyBroccoli(400, 200, 80, 80);
        this.enemies[2] = new EnemyCorn(600, 300, 80, 80);

        this.enemies[3] = new EnemyCarrot(150, 200, 80, 80);
        this.enemies[4] = new EnemyEggplant(300, 450, 80, 80);
        this.enemies[5] = new EnemyEggplant(450, 500, 80, 80);

        this.enemies[6] = new EnemyBellPepper(600, 50, 80, 80,this.player);



        for (int i = 0; i < this.enemies.length; i++) {
            this.enemies[i].setIsMoving(true);
        }


        this.setFocusable(true);//רשאי לקבל פוקוס מהמקלדת
        this.requestFocus();//מקבל פוקוס מהמקלדת- השחקן יכול להגיב ללחיצות
        this.setDoubleBuffered(true);// הפתרון לריצוד הגיף - כדי שלא נראה את המחיקה והציור בזמן אמת רק שהציור מוכן

        MovementListener movementListener = new MovementListener(this, this.player);
        this.addKeyListener(movementListener);
        JButton soundButton = Utils.createSoundButton();
        this.add(soundButton);
        RoundedButton exitButton = new RoundedButton("Exit", 20);
        exitButton.setBounds(width - 65, 15, 60, 30);
        exitButton.setBackground(new java.awt.Color(255, 100, 100));
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
        int padTop = 22;
        int padBottom = 5;

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

    public void gameLoop() {
        new Thread(() -> {
            while (true) {
                for (int i = 0; i < this.enemies.length; i++) {
                    this.enemies[i].move();
                    if (checkCollision(this.player, this.enemies[i])) {
                        System.out.println("התנגשות! השחקן נגע באויב מספר: " + i);
                        // בהמשך תוכל להוסיף כאן לוגיקה כמו הורדת פסילת חיים, סיום משחק וכו'
                    }
                    for (int j = 0; j < this.enemies.length; j++) {
                        // חשוב מאוד: לוודא שאנחנו לא בודקים התנגשות של האויב עם עצמו (i != j)
                        if (i != j) {
                            if (checkEnemyCollision(this.enemies[i], this.enemies[j])) {
                                // אם יש התנגשות, נגיד לאויב להסתובב לצד השני כדי להתרחק
                                this.enemies[i].reverseDirection();
                            }
                        }
                    }
                }
                //וספת מהירות לאויב(אוייב מתחיל במהירות 2)
                for (int i = 0; i < 3; i++) {
                    this.enemies[i].addSpeed(3);
                }
                checkPrizeCollisions();
                repaint();
                Utils.sleep(16);
            }
        }).start();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (this.levelsBackground != null) {
            this.levelsBackground.paint(graphics, this.getWidth(), this.getHeight());
        }
        for (int i = 0; i < this.enemies.length; i++) {
            this.enemies[i].paint(graphics);
        }
        if (cakes.length > 0) {
            for (int i = 0; i < cakesCount; i++) {
                if (cakes[i] != null) {
                    cakes[i].paint(graphics);
                }
            }
        }
        if (this.player != null) {
            this.player.paint(graphics);

        }

        // ציור הסוכריות
        if (prizes != null) {
            for (int i = 0; i < prizes.length; i++) {
                if (prizes[i] != null) {
                    prizes[i].draw(graphics);
                }
            }
        }
        // --- חישוב מיקום הניקוד החדש ---
        int buttonX = 20; // ה-X של הכפתור מ-Utils
        int buttonWidth = 50; // הרוחב מ-Utils
        int scoreX = buttonX + buttonWidth + 10; // יוצא 75
        int scoreY = 55; // גובה שמתיישב יפה מול מרכז הכפתור

        // הגדרת הפונט
        graphics.setFont(new Font("Arial", Font.BOLD, 30));

        // ציור הצל (שחור) - מוזז ב-2 פיקסלים מהניקוד המקורי
        graphics.setColor(Color.BLACK);
        graphics.drawString("Score: " + this.score, scoreX + 2, scoreY + 2);

        // ציור הניקוד (ורוד)
        graphics.setColor(new Color(180, 140, 207));
        graphics.drawString("Score: " + this.score, scoreX, scoreY);


    }
    private void spawnPrizes(int amount) {
        // 1. קודם כל בונים את המערך בגודל המדויק שביקשנו
        prizes = new Prize[amount];
        Random random = new Random();

        // 2. רצים על כל המשבצות במערך ומכניסים אליהן סוכריות
        for (int i = 0; i < prizes.length; i++) {
            int x = random.nextInt(Main.WINDOW_WIDTH - 50);
            int y = random.nextInt(Main.WINDOW_HEIGHT - 80);

            String randomCandy = candyImages[random.nextInt(candyImages.length)];

            // שמים את הסוכריה החדשה בדיוק במקום ה-i במערך
            prizes[i] = new Prize(x, y, 15, 36, randomCandy);
        }
    }
    // פונקציה שמקבלת שחקן ואויב ומחזירה true אם יש התנגשות
    private boolean checkCollision(Player player, Enemy enemy) {
        return (player.getX() + player.getWidth() > enemy.getX()) &&   // 1. צד ימין של השחקן עבר את שמאל של האויב?
                (player.getX() < enemy.getX() + enemy.getWidth()) &&   // 2. צד שמאל של השחקן לפני ימין של האויב?
                (player.getY() + player.getHeight() > enemy.getY()) &&  // 3. הלמטה של השחקן עבר את הלמעלה של האויב?
                (player.getY() < enemy.getY() + enemy.getHeight());    // 4. הלמעלה של השחקן לפני הלמטה של האויב?
    }

    // פונקציה שמקבלת שני אויבים ומחזירה true אם הם נוגעים אחד בשני
    private boolean checkEnemyCollision(Enemy enemy1, Enemy enemy2) {
        return (enemy1.getX() + enemy1.getWidth() > enemy2.getX()) &&
                (enemy1.getX() < enemy2.getX() + enemy2.getWidth()) &&
                (enemy1.getY() + enemy1.getHeight() > enemy2.getY()) &&
                (enemy1.getY() < enemy2.getY() + enemy2.getHeight());

    }
        public void checkPrizeCollisions() {
        // לוקחים את המלבן (קופסת הפגיעה) של השחקן
        Rectangle playerRect = player.getRect();

        if (prizes != null) {
            for (int i = 0; i < prizes.length; i++) {
                // בודקים ששלושת התנאים מתקיימים:
                // 1. יש סוכריה במשבצת הזו במערך
                // 2. עדיין לא אספו אותה
                // 3. המלבן של השחקן נוגע במלבן של הסוכריה
                if (prizes[i] != null && !prizes[i].isCollected()) {
                    if (playerRect.intersects(prizes[i].getBounds())) {

                        // בינגו! יש פגיעה. משנים לה את המצב ל"נאספה"
                        prizes[i].setCollected(true);
                        this.score += 10;
                    }
                }
            }
        }
    }
}
