package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// מחלקה שמייצגת את מסך המשחק הראשי
// כאן מתנהלים השחקן, העוגות, האויבים, הפרסים, הטיימר, הניקוד והציור למסך
public class MainScenePanel extends JPanel {

    // השחקן הראשי במשחק
    private Player player;

    // מערך העוגות שמשמשות כקירות במבוך
    private Cake[] cakes;

    // מערך האויבים בשלב
    private Enemy[] enemies;

    // מערך הפרסים והסוכריות שהשחקן צריך לאסוף
    private Prize[] prizes;

    // ניקוד השחקן
    private int score;

    // מספר העוגות הפעילות במערך
    private int cakesCount;

    // השלב הנוכחי
    private int currentLevel = 1;

    // הזמן שנשאר לשלב הנוכחי
    private int timeLeft = 60;

    // סופר פריימים לצורך הורדת שנייה מהטיימר
    private int timerCounter = 0;

    // מספר השלבים המקסימלי במשחק
    private final int MAX_LEVELS = GameSettings.MAX_LEVELS;

    // מערך נתיבי התמונות של הסוכריות הרגילות
    private String[] candyImages;

    // רקע השלב
    private LevelBackground levelsBackground;

    // כפתור הפעלה וכיבוי של סאונד
    private JButton soundButton;

    // צליל טיימר שמופעל כשהזמן עומד להיגמר
    private SoundManager tickingSound;

    // מחלקה שאחראית על יצירת וניהול הפרסים
    private PrizeManager prizeManager;

    // מחלקה שאחראית על יצירת וניהול האויבים
    private EnemyManager enemyManager;

    // האם המשחק כרגע בעצירה
    private boolean isPaused = false;

    // האם השלב כרגע במסך פתיחה לפני התחלה
    private boolean isLevelStarting = true;

    // האם לולאת המשחק ממשיכה לרוץ
    private boolean isGameRunning = true;

    // בנאי ברירת מחדל שמתחיל את המשחק משלב 1
    public MainScenePanel(int x, int y, int width, int height) {
        this(x, y, width, height, 1);
    }

    // בנאי שמפעיל את המשחק משלב שנבחר במפת השלבים
    public MainScenePanel(int x, int y, int width, int height, int startLevel) {

        // שמירת השלב שממנו מתחילים
        this.currentLevel = startLevel;

        // טעינת תמונות הסוכריות
        initializeImages();

        // אתחול הגדרות בסיסיות של הפאנל
        initializePanel(x, y, width, height);

        // הוספת מאזין למקש רווח
        initializeKeyListener();

        // מפעיל ציור כפול, עוזר לציור חלק יותר ומפחית ריצודים
        this.setDoubleBuffered(true);

        // טעינת השלב הנוכחי
        loadLevel(currentLevel);

        // הוספת מאזין תנועה לשחקן
        initializeMovementListener();

        // הוספת כפתורי סאונד, חזרה ויציאה
        initializeButtons(width);

        // הפעלת לולאת המשחק
        this.gameLoop();
    }

    // מאתחל את מערך התמונות של הסוכריות הרגילות
    private void initializeImages() {
        this.candyImages = new String[]{
                "/Blue_candy.png",
                "/Orange_candy.png",
                "/Pink_candy.png",
                "/Purple_candy.png",
                "/Yellow_candy.png"
        };
    }

    // מאתחל את הפאנל, הרקע, הסאונד והגדרות הבסיס של המסך
    private void initializePanel(int x, int y, int width, int height) {

        // יצירת צליל הטיימר
        this.tickingSound = new SoundManager("/Clock_sound.wav");

        // יצירת מנהל הפרסים
        this.prizeManager = new PrizeManager();

        // קביעת מיקום וגודל הפאנל
        this.setBounds(x, y, width, height);

        // ביטול מנהל פריסה, כי הרכיבים ממוקמים ידנית
        this.setLayout(null);

        // יצירת רקע השלב
        this.levelsBackground = new LevelBackground();

        // מאפשר לפאנל לקבל לחיצות מקלדת
        this.setFocusable(true);

        // מבקש פוקוס כדי שמאזיני המקלדת יעבדו
        this.requestFocus();
    }

    // מאזין ללחיצה על רווח כדי להתחיל שלב או לעצור משחק
    private void initializeKeyListener() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                // אם המשתמש לחץ על רווח, מחליפים מצב בין התחלה, משחק ועצירה
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    togglePause();
                    repaint();
                }
            }
        });
    }

    // מחליף בין מצב עצירה למצב משחק פעיל
    private void togglePause() {

        // אם השלב עדיין במסך פתיחה, רווח מתחיל את המשחק
        if (isLevelStarting) {
            isLevelStarting = false;
            isPaused = false;

            // אם המשחק כבר התחיל, רווח עוצר או ממשיך את המשחק
        } else {
            isPaused = !isPaused;
        }
    }

    // מוסיף מאזין תנועה לשחקן
    private void initializeMovementListener() {

        // MovementListener אחראי על תנועת השחקן לפי מקשי המקלדת
        MovementListener movementListener = new MovementListener(this, this.player);

        // הוספת המאזין לפאנל המשחק
        this.addKeyListener(movementListener);
    }

    // מוסיף למסך כפתור סאונד, חזרה ויציאה
    private void initializeButtons(int width) {

        // יצירת כפתור סאונד
        this.soundButton = Utils.createSoundButton();

        // הוספת כפתור הסאונד לפאנל
        this.add(this.soundButton);

        // יצירת כפתור חזרה
        RoundedButton backButton = RoundedButton.createBackButton(width, this);

        // הוספת כפתור החזרה לפאנל
        this.add(backButton);

        // יצירת כפתור יציאה
        RoundedButton exitButton = RoundedButton.createExitButton(width);

        // הוספת כפתור היציאה לפאנל
        this.add(exitButton);
    }

    // מחזירה האם המשחק כרגע בעצירה
    public boolean isPaused() {
        return isPaused;
    }

    // עוצר את לולאת המשחק ואת צליל הטיימר
    public void stopGame() {

        // מסמן ללולאת המשחק להפסיק לרוץ
        this.isGameRunning = false;

        // עצירת צליל הטיימר אם הוא קיים
        if (this.tickingSound != null) {
            this.tickingSound.stop();
        }
    }

    // טוען שלב חדש ומאתחל שחקן, מבוך, אויבים ופרסים
    private void loadLevel(int level) {

        // איפוס הטיימר לפי השלב
        resetLevelTimer(level);

        // החזרת השחקן לנקודת התחלה
        resetPlayerPosition();

        // בניית המבוך
        buildMaze(level);

        // יצירת אויבים
        createEnemies(level);

        // יצירת פרסים וסוכריות
        createPrizes(level);

        // כל שלב מתחיל במצב עצירה
        this.isPaused = true;

        // מסמן שהשחקן עוד לא התחיל את השלב
        this.isLevelStarting = true;
    }

    // מאפס את הטיימר לפי מספר השלב
    private void resetLevelTimer(int level) {

        // עצירת צליל טיימר קודם אם קיים
        if (this.tickingSound != null) {
            this.tickingSound.stop();
        }

        // בשלבים מתקדמים נותנים יותר זמן
        if (level >= 9 && level <= 15) {
            this.timeLeft = 90;
        } else {
            this.timeLeft = 60;
        }

        // איפוס מונה הפריימים של הטיימר
        this.timerCounter = 0;
    }

    // מחזיר את השחקן לנקודת ההתחלה
    private void resetPlayerPosition() {

        // אם השחקן עדיין לא נוצר, יוצרים אותו
        if (this.player == null) {
            this.player = new Player(100, 100, 60, 60);

            // אם השחקן כבר קיים, מחזירים אותו לנקודת התחלה
        } else {
            this.player.setX(100);
            this.player.setY(100);
        }
    }

    // בונה את המבוך לפי השלב ורמת הקושי
    private void buildMaze(int level) {

        // כל 3 שלבים רמת הקושי עולה
        int difficultyTier = (level - 1) / 3;

        // בחירת תבנית מבוך לפי מחזור של 3 תבניות
        int mazeTemplate = (level - 1) % 3;

        // יצירת אובייקט שבונה את המבוך
        MazeBuilder mazeBuilder = new MazeBuilder();

        // בניית מערך העוגות שמייצג את קירות המבוך
        this.cakes = mazeBuilder.buildMaze(
                mazeTemplate,
                Main.WINDOW_WIDTH,
                Main.WINDOW_HEIGHT,
                difficultyTier
        );

        // שמירת מספר העוגות שנוצרו
        this.cakesCount = mazeBuilder.getCakesCount();
    }

    // יוצר אויבים דרך מחלקת ניהול אויבים
    private void createEnemies(int level) {

        // יצירת מנהל אויבים עם השחקן והעוגות
        this.enemyManager = new EnemyManager(
                this.player,
                this.cakes,
                this.cakesCount
        );

        // יצירת מערך האויבים לשלב
        this.enemies = this.enemyManager.createEnemies(level);
    }

    // יוצר סוכריות רגילות וסוכריה מיוחדת בשלב
    private void createPrizes(int level) {

        // כל 3 שלבים כמות הסוכריות עולה
        int difficultyTier = (level - 1) / 3;

        // חישוב כמות הסוכריות לפי רמת הקושי
        int amountOfCandies = 5 + (difficultyTier * 3);

        // יצירת הפרסים במיקומים תקינים
        this.prizes = prizeManager.createPrizes(
                amountOfCandies,
                this.cakes,
                this.cakesCount,
                this.enemies,
                this.candyImages
        );
    }

    // בודק אם השחקן נוגע בעוגה כדי למנוע מעבר דרך קירות
    public boolean checkCakeCollision() {

        // גבולות השחקן המקוריים
        Rectangle characterRect = this.player.getRect();

        // יצירת מלבן פגיעה קטן יותר לשחקן
        Rectangle smallCharacterRect = new Rectangle(
                characterRect.x + 14,
                characterRect.y + 22,
                characterRect.width - 28,
                characterRect.height - 27
        );

        // מעבר על כל העוגות במבוך
        for (int i = 0; i < this.cakesCount; i++) {
            Cake currentCake = this.cakes[i];

            // בדיקה שהעוגה קיימת
            if (currentCake != null) {
                Rectangle cakeRect = currentCake.getRect();

                // יצירת מלבן פגיעה קטן יותר לעוגה
                Rectangle smallCakeRect = new Rectangle(
                        cakeRect.x + 4,
                        cakeRect.y + 4,
                        cakeRect.width - 8,
                        cakeRect.height - 8
                );

                // אם השחקן נוגע בעוגה, יש התנגשות
                if (smallCharacterRect.intersects(smallCakeRect)) {
                    return true;
                }
            }
        }

        // אם לא הייתה התנגשות עם עוגה
        return false;
    }

    // בודק איסוף סוכריות ומעבר לשלב הבא
    public void checkPrizeCollisions() {

        // יצירת מלבן פגיעה קטן יותר לשחקן עבור איסוף פרסים
        Rectangle playerHitbox = getPlayerPrizeHitbox();

        // מניחים שכל הפרסים נאספו, ואם נמצא פרס שלא נאסף נשנה ל false
        boolean allCollected = true;

        // בדיקה שהמערך קיים
        if (prizes != null) {

            // מעבר על כל הפרסים
            for (int i = 0; i < prizes.length; i++) {

                // בודקים רק פרסים קיימים שלא נאספו
                if (prizes[i] != null && !prizes[i].isCollected()) {

                    // אם השחקן נוגע בפרס, אוספים אותו
                    if (playerHitbox.intersects(prizes[i].getBounds())) {
                        collectPrize(prizes[i]);

                        // אם יש פרס שלא נאסף, השלב עדיין לא הסתיים
                    } else {
                        allCollected = false;
                    }
                }
            }
        }

        // אם כל הפרסים נאספו, עוברים לשלב הבא
        if (allCollected && prizes != null && prizes.length > 0) {
            repaint();
            Utils.sleep(100);
            goToNextLevel();
        }
    }

    // יוצר מלבן פגיעה קטן יותר לשחקן בזמן איסוף סוכריות
    private Rectangle getPlayerPrizeHitbox() {

        // הקטנת שטח הפגיעה כדי שהאיסוף ירגיש מדויק יותר
        int padding = 22;

        // החזרת מלבן הפגיעה של השחקן
        return new Rectangle(
                player.getX() + padding,
                player.getY() + padding,
                player.getWidth() - padding * 2,
                player.getHeight() - padding * 2
        );
    }

    // מסמן סוכריה כנאספה, מוסיף ניקוד ומשמיע צליל
    private void collectPrize(Prize prize) {

        // סימון הפרס כנאסף
        prize.setCollected(true);

        // הוספת הנקודות של הפרס לניקוד הכללי
        this.score += prize.getPoints();

        // השמעת צליל איסוף
        SoundEffects.play("/Sweet_Reward.wav");
    }

    // עובר לשלב הבא או מפעיל ניצחון בסיום המשחק
    private void goToNextLevel() {

        // העלאת מספר השלב
        currentLevel++;

        // פתיחת השלב הבא במפת השלבים
        if (currentLevel <= MAX_LEVELS) {
            GameProgress.unlockLevel(currentLevel);
        }

        // אם עברנו את השלב האחרון, מפעילים ניצחון
        if (currentLevel > MAX_LEVELS) {
            handleVictory();

            // אחרת טוענים את השלב הבא
        } else {
            loadLevel(currentLevel);
        }
    }

    // מציג חלון ניצחון ומסיים את המשחק
    private void handleVictory() {

        // עצירת המשחק
        stopGame();

        // עצירת מוזיקת רקע
        Utils.stopMusic();

        // השמעת צליל ניצחון
        SoundEffects.play("/Victory_sound.wav");

        // טעינת אייקון גביע
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/TrophyIcon.png"));

        // הקטנת האייקון לגודל מתאים
        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);

        // יצירת אייקון חדש מהתמונה המוקטנת
        ImageIcon trophyIcon = new ImageIcon(scaledImage);

        // קביעת צבע רקע לבן לחלון ההודעה
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);

        // יצירת תוכן הודעת הניצחון
        JOptionPane pane = new JOptionPane(
                "ניצחת במשחק כל הכבוד\nהניקוד שלך: " + this.score,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                trophyIcon
        );

        // קבלת החלון שמכיל את הפאנל
        Window parentWindow = SwingUtilities.windowForComponent(this);

        // יצירת דיאלוג מותאם להודעת ניצחון
        JDialog dialog = new JDialog(parentWindow, "Victory", Dialog.ModalityType.APPLICATION_MODAL);

        // הסרת מסגרת רגילה של הדיאלוג
        dialog.setUndecorated(true);

        // הוספת מסגרת ירוקה לדיאלוג
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(144, 238, 144), 8));

        // הכנסת הודעת הניצחון לתוך הדיאלוג
        dialog.setContentPane(pane);

        // התאמת גודל הדיאלוג לתוכן
        dialog.pack();

        // מיקום הדיאלוג במרכז החלון
        dialog.setLocationRelativeTo(parentWindow);

        // מניעת סגירה רגילה דרך X
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // מאזין לסגירת הדיאלוג אחרי פעולה של המשתמש
        pane.addPropertyChangeListener(e -> {
            if (JOptionPane.VALUE_PROPERTY.equals(e.getPropertyName())) {
                dialog.dispose();
            }
        });

        // הצגת הדיאלוג
        dialog.setVisible(true);

        // סיום התוכנית
        System.exit(0);
    }

    // מפעיל את לולאת המשחק שמעדכנת אויבים, פרסים, טיימר וציור
// מפעיל את לולאת המשחק שמעדכנת אויבים פרסים טיימר וציור
    public void gameLoop() {
        new Thread(() -> {
            while (isGameRunning) {
                if (!isPaused) {

                    if (enemyManager.updateEnemies()) {
                        // שומרים את התשובה של חלון הפסילה (האם להתחיל מחדש או לא)
                        boolean shouldRestart = handleGameOver("אוי לא נתפסת על ידי הירקות", "Game Over");

                        // אם בחרנו לחזור לתפריט, נעצור את המשחק ונצא מהלולאה
                        if (!shouldRestart) {
                            stopGame();
                            return;
                        }

                        // אם בחרנו ב-Restart, אנחנו ממשיכים את הלולאה (מדלגים להמשך)
                        continue;
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
    // מעדכן את הטיימר ובודק אם הזמן נגמר
    private boolean updateTimer() {

        // העלאת מונה הפריימים
        timerCounter++;

        // אחרי בערך 60 פריימים מורידים שנייה אחת
        if (timerCounter >= 60) {
            timeLeft--;
            timerCounter = 0;

            // כאשר נשארות 10 שניות, מתחיל צליל טיימר
            if (timeLeft == 10 && this.tickingSound != null) {
                this.tickingSound.playLoop();
            }

            // אם הזמן נגמר, מפעילים הפסד
            if (timeLeft <= 0) {
                timeLeft = 0;
                repaint();

                return handleGameOver("אוי לא הזמן אזל אנא נסה שנית", "Time's Up");
            }
        }

        // מחזיר true אם המשחק ממשיך
        return true;
    }

    // מציג חלון הפסד ומטפל בבחירה של הפעלה מחדש או חזרה לתפריט
    private boolean handleGameOver(String message, String title) {

        // עצירת מוזיקת הרקע
        Utils.stopMusic();

        // עצירת צליל הטיימר
        if (this.tickingSound != null) {
            this.tickingSound.stop();
        }

        // השמעת צליל הפסד
        SoundEffects.play("/Losing_sound.wav");

        // אפשרויות שיופיעו למשתמש
        Object[] options = {"Restart Level", "Back to Menu"};

        // טעינת אייקון פלפל להודעת הפסד
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/BellPepper_Front.png"));

        // הקטנת האייקון
        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);

        // יצירת אייקון מהתמונה המוקטנת
        ImageIcon pepperIcon = new ImageIcon(scaledImage);

        // קביעת רקע לבן להודעה
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);

        // יצירת תוכן הודעת ההפסד
        JOptionPane pane = new JOptionPane(
                message + "\nהניקוד שלך: " + this.score,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                pepperIcon,
                options,
                options[0]
        );

        // קבלת החלון שמכיל את הפאנל
        Window parentWindow = SwingUtilities.windowForComponent(this);

        // יצירת דיאלוג מותאם להפסד
        JDialog dialog = new JDialog(parentWindow, title, Dialog.ModalityType.APPLICATION_MODAL);

        // הסרת מסגרת רגילה
        dialog.setUndecorated(true);

        // הוספת מסגרת אדומה
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.RED, 8));

        // הכנסת תוכן ההודעה לדיאלוג
        dialog.setContentPane(pane);

        // התאמת גודל הדיאלוג לתוכן
        dialog.pack();

        // מיקום במרכז החלון
        dialog.setLocationRelativeTo(parentWindow);

        // מניעת סגירה רגילה
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // מאזין לבחירת המשתמש וסגירת הדיאלוג
        pane.addPropertyChangeListener(e -> {
            if (JOptionPane.VALUE_PROPERTY.equals(e.getPropertyName())) {
                dialog.dispose();
            }
        });

        // הצגת הדיאלוג
        dialog.setVisible(true);

        // קבלת הבחירה של המשתמש
        Object selectedValue = pane.getValue();

        // אם המשתמש בחר להתחיל את השלב מחדש
        if (selectedValue != null && selectedValue.equals(options[0])) {
            this.score = 0;
            loadLevel(this.currentLevel);

            Utils.playMusic();
            Utils.syncButtonIcon(this.soundButton);

            return true;

            // אם המשתמש בחר לחזור לתפריט
        } else {
            Utils.playMusic();

            if (parentWindow != null) {
                parentWindow.dispose();
            }

            new MainMenu();
            return false;
        }
    }

    // סוגר את חלון המשחק ופותח את התפריט הראשי
    private void closeWindowAndOpenMenu() {

        // קבלת החלון שמכיל את הפאנל
        Window parentWindow = SwingUtilities.windowForComponent(this);

        // סגירת החלון אם הוא קיים
        if (parentWindow != null) {
            parentWindow.dispose();
        }

        // פתיחת תפריט ראשי חדש
        new MainMenu();
    }

    // מצייר את כל מסך המשחק
    @Override
    public void paintComponent(Graphics graphics) {

        // ניקוי וציור בסיסי של JPanel
        super.paintComponent(graphics);

        // ציור אובייקטים במשחק
        drawGameObjects(graphics);

        // ציור ניקוד, שלב וטיימר
        drawHud(graphics);

        // ציור שכבת עצירה אם המשחק בעצירה
        drawPauseOverlay(graphics);
    }

    // מצייר רקע, אויבים, עוגות, שחקן וסוכריות
    private void drawGameObjects(Graphics graphics) {

        // ציור רקע השלב
        if (this.levelsBackground != null) {
            this.levelsBackground.paint(graphics, this.getWidth(), this.getHeight());
        }

        // ציור האויבים
        if (this.enemies != null) {
            for (int i = 0; i < this.enemies.length; i++) {
                if (this.enemies[i] != null) {
                    this.enemies[i].paint(graphics);
                }
            }
        }

        // ציור העוגות
        if (this.cakes != null) {
            for (int i = 0; i < cakesCount; i++) {
                if (this.cakes[i] != null) {
                    this.cakes[i].paint(graphics);
                }
            }
        }

        // ציור השחקן
        if (this.player != null) {
            this.player.paint(graphics, this.isPaused);
        }

        // ציור הפרסים שלא נאספו
        if (this.prizes != null) {
            for (int i = 0; i < prizes.length; i++) {
                if (prizes[i] != null && !prizes[i].isCollected()) {
                    prizes[i].draw(graphics);
                }
            }
        }
    }

    // מצייר ניקוד, שלב וטיימר
    private void drawHud(Graphics graphics) {

        // מיקום בסיסי לפי כפתור הסאונד
        int buttonX = 20;
        int buttonWidth = 50;
        int scoreX = buttonX + buttonWidth + 10;
        int scoreY = 55;

        // קביעת פונט ל HUD
        graphics.setFont(new Font("Arial", Font.BOLD, 30));

        // ציור ניקוד
        drawTextWithShadow(
                graphics,
                "Score: " + this.score,
                scoreX,
                scoreY,
                new Color(180, 140, 207)
        );

        // ציור שלב נוכחי
        drawTextWithShadow(
                graphics,
                "Level: " + this.currentLevel,
                scoreX + 200,
                scoreY,
                new Color(180, 244, 255)
        );

        // קבלת זמן בתור טקסט
        String timeString = getTimeString();

        // צבע ברירת מחדל לטיימר
        Color timerColor = Color.WHITE;

        // אם נשאר מעט זמן, הטיימר הופך לאדום
        if (this.timeLeft <= 10) {
            timerColor = Color.RED;
        }

        // ציור הטיימר
        drawTextWithShadow(
                graphics,
                timeString,
                scoreX + 400,
                scoreY,
                timerColor
        );
    }

    // מצייר טקסט עם צל כדי שיהיה קריא יותר
    private void drawTextWithShadow(Graphics graphics, String text, int x, int y, Color color) {

        // ציור צל שחור
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 2, y + 2);

        // ציור הטקסט בצבע המבוקש
        graphics.setColor(color);
        graphics.drawString(text, x, y);
    }

    // מחזיר את הזמן בפורמט דקות ושניות
    private String getTimeString() {

        // חישוב דקות
        int minutes = this.timeLeft / 60;

        // חישוב שניות
        int seconds = this.timeLeft % 60;

        // החזרת טקסט בפורמט 00:00
        return String.format("Time: %02d:%02d", minutes, seconds);
    }

    // מצייר שכבת עצירה או פתיחת שלב מעל המשחק
    private void drawPauseOverlay(Graphics graphics) {

        // אם המשחק לא בעצירה, אין שכבה לצייר
        if (!isPaused) {
            return;
        }

        // ציור שכבה שחורה שקופה מעל המשחק
        graphics.setColor(new Color(0, 0, 0, 200)); //a-מסמל את רמת השקיפות 0-255 (0 הכי שקוף ו255 הכי שחור אטום)
        graphics.fillRect(0, 0, getWidth(), getHeight());

        // צבע הטקסט על השכבה
        graphics.setColor(Color.WHITE);

        String text;

        // אם השלב עדיין לא התחיל, מציגים הודעת התחלה
        if (isLevelStarting) {
            graphics.setFont(new Font("Arial", Font.BOLD, 40));
            text = "PRESS SPACE TO START";

            // אם המשחק באמצע ונעצר, מציגים PAUSED
        } else {
            graphics.setFont(new Font("Arial", Font.BOLD, 60));
            text = "PAUSED";
        }

        // חישוב X כדי למרכז את הטקסט
        int x = (getWidth() - graphics.getFontMetrics().stringWidth(text)) / 2;

        // מיקום Y במרכז המסך
        int y = getHeight() / 2;

        // ציור הטקסט
        graphics.drawString(text, x, y);
    }
}