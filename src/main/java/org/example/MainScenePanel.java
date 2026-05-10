package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.Random;

public class MainScenePanel extends JPanel {
    private Player player;
    private Cake[] cakes;
    private Enemy[] enemies;
    private Prize[] prizes;

    private int score;
    private int cakesCount;
    private int currentLevel = 1;
    private int timeLeft = 60;
    private int timerCounter = 0;

    private final int CAKE_SIZE = 50;
    private final int ENEMY_SIZE = 46;
    private final int MAX_LEVELS = GameSettings.MAX_LEVELS;

    private String[] candyImages;
    private LevelBackground levelsBackground;
    private JButton soundButton;
    private SoundManager tickingSound;
    private PrizeManager prizeManager;

    private boolean isPaused = false;
    private boolean isLevelStarting = true;
    private boolean isGameRunning = true;

    public MainScenePanel(int x, int y, int width, int height) {
        this(x, y, width, height, 1);
    }

    public MainScenePanel(int x, int y, int width, int height, int startLevel) {
        this.currentLevel = startLevel;

        initializeImages();
        initializePanel(x, y, width, height);
        initializeKeyListener();

        this.setDoubleBuffered(true);

        loadLevel(currentLevel);
        initializeMovementListener();
        initializeButtons(width);

        this.gameLoop();
    }

    private void initializeImages() {
        this.candyImages = new String[]{
                "/Blue_candy.png",
                "/Orange_candy.png",
                "/Pink_candy.png",
                "/Purple_candy.png",
                "/Yellow_candy.png"
        };
    }

    private void initializePanel(int x, int y, int width, int height) {
        this.tickingSound = new SoundManager("/Clock_sound.wav");
        this.prizeManager = new PrizeManager();

        this.setBounds(x, y, width, height);
        this.setLayout(null);
        this.levelsBackground = new LevelBackground();

        this.setFocusable(true);
        this.requestFocus();
    }

    private void initializeKeyListener() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    togglePause();
                    repaint();
                }
            }
        });
    }

    private void togglePause() {
        if (isLevelStarting) {
            isLevelStarting = false;
            isPaused = false;
        } else {
            isPaused = !isPaused;
        }
    }

    private void initializeMovementListener() {
        MovementListener movementListener = new MovementListener(this, this.player);
        this.addKeyListener(movementListener);
    }

    private void initializeButtons(int width) {
        this.soundButton = Utils.createSoundButton();
        this.add(this.soundButton);

        RoundedButton backButton = RoundedButton.createBackButton(width, this);
        this.add(backButton);

        RoundedButton exitButton = RoundedButton.createExitButton(width);
        this.add(exitButton);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void stopGame() {
        this.isGameRunning = false;

        if (this.tickingSound != null) {
            this.tickingSound.stop();
        }
    }

    private void loadLevel(int level) {
        resetLevelTimer(level);
        resetPlayerPosition();
        buildMaze(level);
        createEnemies(level);
        createPrizes(level);

        this.isPaused = true;
        this.isLevelStarting = true;
    }

    private void resetLevelTimer(int level) {
        if (this.tickingSound != null) {
            this.tickingSound.stop();
        }
        if (level >= 9 && level <= 15) {
            this.timeLeft = 90;
        } else {
            this.timeLeft = 60;
        }
        this.timerCounter = 0;
    }

    private void resetPlayerPosition() {
        if (this.player == null) {
            this.player = new Player(100, 100, 60, 60);
        } else {
            this.player.setX(100);
            this.player.setY(100);
        }
    }

    private void buildMaze(int level) {
        int difficultyTier = (level - 1) / 3;
        int mazeTemplate = (level - 1) % 3;

        MazeBuilder mazeBuilder = new MazeBuilder();

        this.cakes = mazeBuilder.buildMaze(
                mazeTemplate,
                Main.WINDOW_WIDTH,
                Main.WINDOW_HEIGHT,
                difficultyTier
        );

        this.cakesCount = mazeBuilder.getCakesCount();
    }

    private void createEnemies(int level) {
        int difficultyTier = (level - 1) / 3;

        int normalEnemies = 3 + difficultyTier;
        int smartEnemies = 2;
//        int smartEnemies = Math.min(difficultyTier, 2);

        setupEnemiesForLevel(normalEnemies, smartEnemies);
        startEnemiesMovement();
    }

    private void createPrizes(int level) {
        int difficultyTier = (level - 1) / 3;
        int amountOfCandies = 5 + (difficultyTier * 3);

        this.prizes = prizeManager.createPrizes(
                amountOfCandies,
                this.cakes,
                this.cakesCount,
                this.enemies,
                this.candyImages
        );
    }

    private void startEnemiesMovement() {
        for (int i = 0; i < this.enemies.length; i++) {
            if (this.enemies[i] != null) {
                this.enemies[i].setIsMoving(true);
            }
        }
    }

    private void setupEnemiesForLevel(int normalEnemies, int smartEnemies) {
        int totalEnemies = normalEnemies + smartEnemies;
        this.enemies = new Enemy[totalEnemies];

        Random random = new Random();

        int cols = Main.WINDOW_WIDTH / CAKE_SIZE;
        int rows = Main.WINDOW_HEIGHT / CAKE_SIZE;

        for (int i = 0; i < totalEnemies; i++) {
            Point spawnPoint = findEnemySpawnPoint(random, cols, rows);

            if (i < normalEnemies) {
                createRegularEnemy(i, spawnPoint.x, spawnPoint.y);
            } else {
                this.enemies[i] = new EnemyBellPepper(
                        spawnPoint.x,
                        spawnPoint.y,
                        ENEMY_SIZE,
                        ENEMY_SIZE,
                        this.player
                );
            }
        }
    }

    private Point findEnemySpawnPoint(Random random, int cols, int rows) {
        int x;
        int y;

        do {
            int gridX = random.nextInt(cols - 2) + 1;
            int gridY = random.nextInt(rows - 2) + 1;

            x = (gridX * CAKE_SIZE) + 2;
            y = (gridY * CAKE_SIZE) + 2;

        } while (!isValidEnemyLocation(x, y));

        return new Point(x, y);
    }

    private boolean isValidEnemyLocation(int x, int y) {
        Rectangle enemyRect = new Rectangle(x, y, ENEMY_SIZE, ENEMY_SIZE);

        if (touchesCake(enemyRect)) {
            return false;
        }

        Rectangle safeZone = new Rectangle(50, 50, 200, 200);

        if (enemyRect.intersects(safeZone)) {
            return false;
        }

        return !touchesOtherEnemy(enemyRect);
    }

    private boolean touchesCake(Rectangle rect) {
        for (int i = 0; i < this.cakesCount; i++) {
            if (this.cakes[i] != null && rect.intersects(this.cakes[i].getRect())) {
                return true;
            }
        }

        return false;
    }

    private boolean touchesOtherEnemy(Rectangle enemyRect) {
        if (this.enemies == null) {
            return false;
        }

        for (int i = 0; i < this.enemies.length; i++) {
            if (this.enemies[i] != null && enemyRect.intersects(this.enemies[i].getRect())) {
                return true;
            }
        }

        return false;
    }

    private void createRegularEnemy(int index, int x, int y) {
        int type = index % 4;

        if (type == 0) {
            this.enemies[index] = new EnemyBroccoli(x, y, ENEMY_SIZE, ENEMY_SIZE);
        } else if (type == 1) {
            this.enemies[index] = new EnemyEggplant(x, y, ENEMY_SIZE, ENEMY_SIZE);
        } else if (type == 2) {
            this.enemies[index] = new EnemyGeneric(x, y, ENEMY_SIZE, ENEMY_SIZE, "Carrot");
        } else {
            this.enemies[index] = new EnemyGeneric(x, y, ENEMY_SIZE, ENEMY_SIZE, "Corn");
        }
    }

    public boolean checkCakeCollision() {
        Rectangle characterRect = this.player.getRect();

        Rectangle smallCharacterRect = new Rectangle(
                characterRect.x + 14,
                characterRect.y + 22,
                characterRect.width - 28,
                characterRect.height - 27
        );

        for (int i = 0; i < this.cakesCount; i++) {
            Cake currentCake = this.cakes[i];

            if (currentCake != null) {
                Rectangle cakeRect = currentCake.getRect();

                Rectangle smallCakeRect = new Rectangle(
                        cakeRect.x + 4,
                        cakeRect.y + 4,
                        cakeRect.width - 8,
                        cakeRect.height - 8
                );

                if (smallCharacterRect.intersects(smallCakeRect)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void checkPrizeCollisions() {
        Rectangle playerHitbox = getPlayerPrizeHitbox();

        boolean allCollected = true;

        if (prizes != null) {
            for (int i = 0; i < prizes.length; i++) {
                if (prizes[i] != null && !prizes[i].isCollected()) {
                    if (playerHitbox.intersects(prizes[i].getBounds())) {
                        collectPrize(prizes[i]);
                    } else {
                        allCollected = false;
                    }
                }
            }
        }

        if (allCollected && prizes != null && prizes.length > 0) {
            repaint();//מצייר את המסך מחדש ללא הסוכריה האחרונה
            Utils.sleep(100);//מחכה עשירית שנייה לפני שעוברים הלאה
            goToNextLevel();

        }
    }

    private Rectangle getPlayerPrizeHitbox() {
        int padding = 22;

        return new Rectangle(
                player.getX() + padding,
                player.getY() + padding,
                player.getWidth() - padding * 2,
                player.getHeight() - padding * 2
        );
    }

    private void collectPrize(Prize prize) {
        prize.setCollected(true);
        this.score += prize.getPoints();
        playSound("/Sweet_Reward.wav");
    }

    private void goToNextLevel() {
        currentLevel++;

        if (currentLevel <= MAX_LEVELS) {
            GameProgress.unlockLevel(currentLevel);
        }

        if (currentLevel > MAX_LEVELS) {
            handleVictory();
        } else {
            loadLevel(currentLevel);
        }
    }

    private void handleVictory() {
        stopGame();

        Utils.stopMusic();
        playSound("/Victory_sound.wav");

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/TrophyIcon.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        ImageIcon trophyIcon = new ImageIcon(scaledImage);

        // הגדרת הרקע ללבן נקי
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);

        JOptionPane pane = new JOptionPane(
                "ניצחת במשחק! כל הכבוד!\nהניקוד שלך: " + this.score,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                trophyIcon
        );

        Window parentWindow = SwingUtilities.windowForComponent(this);
        JDialog dialog = new JDialog(parentWindow, "Victory", Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(144, 238, 144), 8));

        dialog.setContentPane(pane);
        dialog.pack();
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // מאזין שמחכה שהשחקן ילחץ על הכפתור כדי לסגור את החלון
        pane.addPropertyChangeListener(e -> {
            if (JOptionPane.VALUE_PROPERTY.equals(e.getPropertyName())) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);

        System.exit(0);
    }

    public void gameLoop() {
        new Thread(() -> {
            while (isGameRunning) {
                if (!isPaused) {
                    if (!updateEnemies()) {
                        stopGame();
                        return;
                    }

                    checkPrizeCollisions();

                    if (!updateTimer()) {
                        stopGame();
                        return;
                    }
                }

                repaint();
                Utils.sleep(16);
            }
        }).start();
    }

    private boolean updateEnemies() {
        for (int i = 0; i < this.enemies.length; i++) {
            if (this.enemies[i] == null) {
                continue;
            }

            int oldX = this.enemies[i].getX();
            int oldY = this.enemies[i].getY();

            this.enemies[i].move();

            if (enemyHitObstacle(i)) {
                moveEnemyBack(i, oldX, oldY);
            }

            if (checkCollision(this.player, this.enemies[i])) {
                return handleGameOver("אוי לא! נתפסת על ידי הירקות!", "Game Over");
            }
        }

        return true;
    }

    private boolean enemyHitObstacle(int enemyIndex) {
        if (checkEnemyCakeCollision(this.enemies[enemyIndex])) {
            return true;
        }

        for (int i = 0; i < this.enemies.length; i++) {
            if (i != enemyIndex &&
                    this.enemies[i] != null &&
                    checkEnemyCollision(this.enemies[enemyIndex], this.enemies[i])) {
                return true;
            }
        }

        return false;
    }

    private void moveEnemyBack(int enemyIndex, int oldX, int oldY) {
        this.enemies[enemyIndex].setX(oldX);
        this.enemies[enemyIndex].setY(oldY);

        if (this.enemies[enemyIndex] instanceof EnemyBellPepper) {
            ((EnemyBellPepper) this.enemies[enemyIndex]).suspendTracking(140);
            return;
        }

        this.enemies[enemyIndex].reverseDirection();
    }

    private boolean updateTimer() {
        timerCounter++;

        if (timerCounter >= 60) {
            timeLeft--;
            timerCounter = 0;

            if (timeLeft == 10 && this.tickingSound != null) {
                this.tickingSound.playLoop();
            }

            if (timeLeft <= 0) {
                timeLeft = 0;
                repaint();

                return handleGameOver("אוי לא! הזמן אזל אנא נסה שנית.", "Time's Up");
            }
        }

        return true;
    }

    private boolean handleGameOver(String message, String title) {
        Utils.stopMusic();

        if (this.tickingSound != null) {
            this.tickingSound.stop();
        }

        playSound("/Losing_sound.wav");

        Object[] options = {"Restart Level", "Back to Menu"};

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/BellPepper_Front.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        ImageIcon pepperIcon = new ImageIcon(scaledImage);
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);

        JOptionPane pane = new JOptionPane(
                message + "\nהניקוד שלך: " + this.score,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                pepperIcon,
                options,
                options[0]
        );

        Window parentWindow = SwingUtilities.windowForComponent(this);
        JDialog dialog = new JDialog(parentWindow, title, Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.RED, 8));

        dialog.setContentPane(pane);
        dialog.pack();
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        // מאזין שמחכה שהשחקן ילחץ על אחד הכפתורים כדי לסגור את החלון
        pane.addPropertyChangeListener(e -> {
            if (JOptionPane.VALUE_PROPERTY.equals(e.getPropertyName())) {
                dialog.dispose();
            }
        });
        dialog.setVisible(true);

        Object selectedValue = pane.getValue();

        if (selectedValue != null && selectedValue.equals(options[0])) {
            this.score = 0;
            loadLevel(this.currentLevel);

            Utils.playMusic();
            Utils.syncButtonIcon(this.soundButton);

            return true;
        }else {
            Utils.playMusic();

            if (parentWindow != null) {
                parentWindow.dispose();
            }
            new MainMenu();
            return false;
        }
    }

    private void closeWindowAndOpenMenu() {
        Window parentWindow = SwingUtilities.windowForComponent(this);

        if (parentWindow != null) {
            parentWindow.dispose();
        }

        new MainMenu();
    }

    private boolean checkCollision(Player player, Enemy enemy) {
        int playerPadding = 15;

        Rectangle playerHitbox = new Rectangle(
                player.getX() + playerPadding,
                player.getY() + playerPadding,
                player.getWidth() - playerPadding * 2,
                player.getHeight() - playerPadding * 2
        );

        int enemyPadding = 10;

        Rectangle enemyHitbox = new Rectangle(
                enemy.getX() + enemyPadding,
                enemy.getY() + enemyPadding,
                enemy.getWidth() - enemyPadding * 2,
                enemy.getHeight() - enemyPadding * 2
        );

        return playerHitbox.intersects(enemyHitbox);
    }

    private boolean checkEnemyCollision(Enemy enemy1, Enemy enemy2) {
        return (enemy1.getX() + enemy1.getWidth() > enemy2.getX()) &&
                (enemy1.getX() < enemy2.getX() + enemy2.getWidth()) &&
                (enemy1.getY() + enemy1.getHeight() > enemy2.getY()) &&
                (enemy1.getY() < enemy2.getY() + enemy2.getHeight());
    }

    private boolean checkEnemyCakeCollision(Enemy enemy) {
        Rectangle enemyRect = enemy.getRect();

        for (int i = 0; i < this.cakesCount; i++) {
            if (cakes[i] != null && enemyRect.intersects(cakes[i].getRect())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        drawGameObjects(graphics);
        drawHud(graphics);
        drawPauseOverlay(graphics);
    }

    private void drawGameObjects(Graphics graphics) {
        if (this.levelsBackground != null) {
            this.levelsBackground.paint(graphics, this.getWidth(), this.getHeight());
        }

        if (this.enemies != null) {
            for (int i = 0; i < this.enemies.length; i++) {
                if (this.enemies[i] != null) {
                    this.enemies[i].paint(graphics);
                }
            }
        }

        if (this.cakes != null) {
            for (int i = 0; i < cakesCount; i++) {
                if (this.cakes[i] != null) {
                    this.cakes[i].paint(graphics);
                }
            }
        }

        if (this.player != null) {
            this.player.paint(graphics, this.isPaused);
        }

        if (this.prizes != null) {
            for (int i = 0; i < prizes.length; i++) {
                if (prizes[i] != null && !prizes[i].isCollected()) {
                    prizes[i].draw(graphics);
                }
            }
        }
    }

    private void drawHud(Graphics graphics) {
        int buttonX = 20;
        int buttonWidth = 50;
        int scoreX = buttonX + buttonWidth + 10;
        int scoreY = 55;

        graphics.setFont(new Font("Arial", Font.BOLD, 30));

        drawTextWithShadow(
                graphics,
                "Score: " + this.score,
                scoreX,
                scoreY,
                new Color(180, 140, 207)
        );

        drawTextWithShadow(
                graphics,
                "Level: " + this.currentLevel,
                scoreX + 200,
                scoreY,
                new Color(180, 244, 255)
        );

        String timeString = getTimeString();

        Color timerColor = Color.WHITE;

        if (this.timeLeft <= 10) {
            timerColor = Color.RED;
        }

        drawTextWithShadow(
                graphics,
                timeString,
                scoreX + 400,
                scoreY,
                timerColor
        );
    }

    private void drawTextWithShadow(Graphics graphics, String text, int x, int y, Color color) {
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 2, y + 2);

        graphics.setColor(color);
        graphics.drawString(text, x, y);
    }

    private String getTimeString() {
        int minutes = this.timeLeft / 60;
        int seconds = this.timeLeft % 60;

        return String.format("Time: %02d:%02d", minutes, seconds);
    }

    private void drawPauseOverlay(Graphics graphics) {
        if (!isPaused) {
            return;
        }

        graphics.setColor(new Color(0, 0, 0, 200));
        graphics.fillRect(0, 0, getWidth(), getHeight());

        graphics.setColor(Color.WHITE);

        String text;

        if (isLevelStarting) {
            graphics.setFont(new Font("Arial", Font.BOLD, 40));
            text = "PRESS SPACE TO START";
        } else {
            graphics.setFont(new Font("Arial", Font.BOLD, 60));
            text = "PAUSED";
        }

        int x = (getWidth() - graphics.getFontMetrics().stringWidth(text)) / 2;
        int y = getHeight() / 2;

        graphics.drawString(text, x, y);
    }

    private void playSound(String soundFileName) {
        try {
            URL soundURL = getClass().getResource(soundFileName);

            if (soundURL != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();

                clip.open(audioIn);
                clip.start();
            } else {
                System.out.println("שגיאה: לא מצאתי את קובץ הסאונד " + soundFileName);
            }
        } catch (Exception e) {
            System.out.println("שגיאה בניגון הסאונד:");
            e.printStackTrace();
        }
    }
}