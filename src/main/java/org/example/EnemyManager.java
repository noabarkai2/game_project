package org.example;

import java.awt.*;
import java.util.Random;

// מחלקה שאחראית על יצירה, מיקום, עדכון וניהול של כל האויבים במשחק
public class EnemyManager {

    // גודל תא בסיסי במפת המשחק, לפי גודל העוגות
    private static final int CAKE_SIZE = 50;

    // גודל קבוע לכל אויב
    private static final int ENEMY_SIZE = 46;

    // השחקן הראשי, נדרש לבדיקת התנגשות ולרדיפה של אויב חכם
    private Player player;

    // מערך העוגות במשחק
    private Cake[] cakes;

    // מספר העוגות הפעילות במערך
    private int cakesCount;

    // מערך האויבים במשחק
    private Enemy[] enemies;

    // בנאי שמקבל את השחקן, מערך העוגות ומספר העוגות
    public EnemyManager(Player player, Cake[] cakes, int cakesCount) {
        this.player = player;
        this.cakes = cakes;
        this.cakesCount = cakesCount;
    }

    // יוצר את כל האויבים לפי השלב
    public Enemy[] createEnemies(int level) {

        // כל 3 שלבים רמת הקושי עולה
        int difficultyTier = (level - 1) / 3;

        // מספר האויבים הרגילים גדל לפי רמת הקושי
        int normalEnemies = 3 + difficultyTier;

        // מספר האויבים החכמים מוגבל עד 2
        int smartEnemies = Math.min(difficultyTier, 2);

        // יצירת מערך האויבים ומיקום האויבים במפה
        setupEnemiesForLevel(normalEnemies, smartEnemies);

        // הפעלת מצב תנועה לכל האויבים
        startEnemiesMovement();

        // החזרת מערך האויבים שנוצר
        return this.enemies;
    }

    // מעדכן את האויבים ומחזיר אם השחקן נפגע
    public boolean updateEnemies() {

        // מעבר על כל האויבים במערך
        for (int i = 0; i < this.enemies.length; i++) {

            // אם אין אויב במקום הזה במערך, מדלגים הלאה
            if (this.enemies[i] == null) {
                continue;
            }

            // שמירת המיקום הקודם של האויב לפני התנועה
            int oldX = this.enemies[i].getX();
            int oldY = this.enemies[i].getY();

            // הזזת האויב לפי הלוגיקה שלו
            this.enemies[i].move();

            // אם האויב פגע בעוגה או באויב אחר, מחזירים אותו אחורה
            if (enemyHitObstacle(i)) {
                moveEnemyBack(i, oldX, oldY);
            }

            // אם האויב פגע בשחקן, מחזירים true
            if (checkCollision(this.player, this.enemies[i])) {
                return true;
            }
        }

        // אם אף אויב לא פגע בשחקן, מחזירים false
        return false;
    }

    // בונה את מערך האויבים וממקם אותם במקומות תקינים
    private void setupEnemiesForLevel(int normalEnemies, int smartEnemies) {

        // מספר כל האויבים בשלב
        int totalEnemies = normalEnemies + smartEnemies;

        // יצירת מערך בגודל מתאים
        this.enemies = new Enemy[totalEnemies];

        // אובייקט לבחירת מיקומים אקראיים
        Random random = new Random();

        // חישוב מספר העמודות במפה לפי רוחב החלון וגודל תא
        int cols = Main.WINDOW_WIDTH / CAKE_SIZE;

        // חישוב מספר השורות במפה לפי גובה החלון וגודל תא
        int rows = Main.WINDOW_HEIGHT / CAKE_SIZE;

        // יצירת כל האויבים אחד אחד
        for (int i = 0; i < totalEnemies; i++) {

            // מציאת נקודת התחלה תקינה לאויב
            Point spawnPoint = findEnemySpawnPoint(random, cols, rows);

            // בחלק הראשון של המערך יוצרים אויבים רגילים
            if (i < normalEnemies) {
                createRegularEnemy(i, spawnPoint.x, spawnPoint.y);

                // בחלק האחרון של המערך יוצרים אויבים חכמים מסוג פלפל
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

    // מחפש נקודת התחלה תקינה לאויב
    private Point findEnemySpawnPoint(Random random, int cols, int rows) {
        int x;
        int y;

        // מגריל מיקום עד שמתקבל מיקום תקין
        do {

            // מגריל תא פנימי במפה ולא בקצוות
            int gridX = random.nextInt(cols - 2) + 1;
            int gridY = random.nextInt(rows - 2) + 1;

            // ממיר מיקום רשת למיקום פיקסלים על המסך
            x = (gridX * CAKE_SIZE) + 2;
            y = (gridY * CAKE_SIZE) + 2;

        } while (!isValidEnemyLocation(x, y));

        // מחזיר את המיקום התקין שמצאנו
        return new Point(x, y);
    }

    // בודק אם מיקום האויב פנוי מעוגות, שחקן ואויבים אחרים
    private boolean isValidEnemyLocation(int x, int y) {

        // יצירת מלבן שמייצג את גבולות האויב במיקום החדש
        Rectangle enemyRect = new Rectangle(x, y, ENEMY_SIZE, ENEMY_SIZE);

        // אם האויב נוגע בעוגה, המיקום לא תקין
        if (touchesCake(enemyRect)) {
            return false;
        }

        // אזור בטוח בתחילת המשחק, כדי שאויב לא יופיע קרוב מדי לשחקן
        Rectangle safeZone = new Rectangle(50, 50, 200, 200);

        // אם האויב בתוך האזור הבטוח, המיקום לא תקין
        if (enemyRect.intersects(safeZone)) {
            return false;
        }

        // המיקום תקין אם האויב לא נוגע באויב אחר
        return !touchesOtherEnemy(enemyRect);
    }

    // בודק אם מלבן נוגע בעוגה
    private boolean touchesCake(Rectangle rect) {

        // מעבר על כל העוגות הפעילות
        for (int i = 0; i < this.cakesCount; i++) {

            // אם קיימת עוגה והמלבן נוגע בה, מחזירים true
            if (this.cakes[i] != null && rect.intersects(this.cakes[i].getRect())) {
                return true;
            }
        }

        // אם אין נגיעה באף עוגה, מחזירים false
        return false;
    }

    // בודק אם מלבן נוגע באויב אחר
    private boolean touchesOtherEnemy(Rectangle enemyRect) {

        // אם מערך האויבים עדיין לא נוצר, אין אויבים לבדוק
        if (this.enemies == null) {
            return false;
        }

        // מעבר על כל האויבים שכבר קיימים במערך
        for (int i = 0; i < this.enemies.length; i++) {

            // אם יש אויב והמלבן נוגע בו, המיקום לא תקין
            if (this.enemies[i] != null && enemyRect.intersects(this.enemies[i].getRect())) {
                return true;
            }
        }

        // אם לא הייתה נגיעה באויב אחר, מחזירים false
        return false;
    }

    // יוצר אויב רגיל לפי סוג משתנה
    private void createRegularEnemy(int index, int x, int y) {

        // בחירת סוג אויב לפי האינדקס, במחזוריות של 4 סוגים
        int type = index % 4;

        // יצירת ברוקולי
        if (type == 0) {
            this.enemies[index] = new EnemyBroccoli(x, y, ENEMY_SIZE, ENEMY_SIZE);

            // יצירת חציל
        } else if (type == 1) {
            this.enemies[index] = new EnemyEggplant(x, y, ENEMY_SIZE, ENEMY_SIZE);

            // יצירת גזר דרך EnemyGeneric
        } else if (type == 2) {
            this.enemies[index] = new EnemyGeneric(x, y, ENEMY_SIZE, ENEMY_SIZE, "Carrot");

            // יצירת תירס דרך EnemyGeneric
        } else {
            this.enemies[index] = new EnemyGeneric(x, y, ENEMY_SIZE, ENEMY_SIZE, "Corn");
        }
    }

    // מפעיל תנועה לכל האויבים
    private void startEnemiesMovement() {

        // מעבר על כל האויבים
        for (int i = 0; i < this.enemies.length; i++) {

            // אם האויב קיים, מפעילים לו תנועה
            if (this.enemies[i] != null) {
                this.enemies[i].setIsMoving(true);
            }
        }
    }

    // בודק אם אויב פגע בעוגה או באויב אחר
    private boolean enemyHitObstacle(int enemyIndex) {

        // בדיקה אם האויב פגע בעוגה
        if (checkEnemyCakeCollision(this.enemies[enemyIndex])) {
            return true;
        }

        // בדיקה אם האויב פגע באויב אחר
        for (int i = 0; i < this.enemies.length; i++) {

            // לא בודקים אויב מול עצמו
            if (i != enemyIndex &&
                    this.enemies[i] != null &&
                    checkEnemyCollision(this.enemies[enemyIndex], this.enemies[i])) {
                return true;
            }
        }

        // אם לא הייתה פגיעה, מחזירים false
        return false;
    }

    // מחזיר אויב למיקום הקודם ומטפל בכיוון אחרי פגיעה
    private void moveEnemyBack(int enemyIndex, int oldX, int oldY) {

        // החזרת האויב למיקום שהיה בו לפני התנועה
        this.enemies[enemyIndex].setX(oldX);
        this.enemies[enemyIndex].setY(oldY);

        // אם מדובר בפלפל חכם, עוצרים רדיפה זמנית ומפעילים עקיפה
        if (this.enemies[enemyIndex] instanceof EnemyBellPepper) {
            ((EnemyBellPepper) this.enemies[enemyIndex]).suspendTracking(140);
            return;
        }

        // אויב רגיל פשוט הופך כיוון
        this.enemies[enemyIndex].reverseDirection();
    }

    // בודק התנגשות בין השחקן לאויב
    private boolean checkCollision(Player player, Enemy enemy) {

        // הקטנת גבולות הפגיעה של השחקן, כדי שההתנגשות תהיה מדויקת יותר
        int playerPadding = 15;

        // יצירת מלבן פגיעה קטן יותר עבור השחקן
        Rectangle playerHitbox = new Rectangle(
                player.getX() + playerPadding,
                player.getY() + playerPadding,
                player.getWidth() - playerPadding * 2,
                player.getHeight() - playerPadding * 2
        );

        // הקטנת גבולות הפגיעה של האויב
        int enemyPadding = 10;

        // יצירת מלבן פגיעה קטן יותר עבור האויב
        Rectangle enemyHitbox = new Rectangle(
                enemy.getX() + enemyPadding,
                enemy.getY() + enemyPadding,
                enemy.getWidth() - enemyPadding * 2,
                enemy.getHeight() - enemyPadding * 2
        );

        // אם שני המלבנים נחתכים, יש התנגשות
        return playerHitbox.intersects(enemyHitbox);
    }

    // בודק התנגשות בין שני אויבים
    private boolean checkEnemyCollision(Enemy enemy1, Enemy enemy2) {

        // בדיקת חפיפה בין גבולות שני האויבים
        return (enemy1.getX() + enemy1.getWidth() > enemy2.getX()) &&
                (enemy1.getX() < enemy2.getX() + enemy2.getWidth()) &&
                (enemy1.getY() + enemy1.getHeight() > enemy2.getY()) &&
                (enemy1.getY() < enemy2.getY() + enemy2.getHeight());
    }

    // בודק אם אויב נוגע בעוגה
    private boolean checkEnemyCakeCollision(Enemy enemy) {

        // יצירת מלבן שמייצג את גבולות האויב
        Rectangle enemyRect = enemy.getRect();

        // מעבר על כל העוגות הפעילות
        for (int i = 0; i < this.cakesCount; i++) {

            // אם קיימת עוגה והאויב נוגע בה, יש התנגשות
            if (cakes[i] != null && enemyRect.intersects(cakes[i].getRect())) {
                return true;
            }
        }

        // אם האויב לא נגע באף עוגה, אין התנגשות
        return false;
    }
}