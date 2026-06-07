package org.example;

import java.awt.*;
import java.util.Random;

// מחלקה שאחראית על יצירת הפרסים במשחק
// היא ממקמת סוכריות רגילות וסוכריה מיוחדת במקומות תקינים במפה
public class PrizeManager {

    // גודל הסוכריות הרגילות
    private static final int CANDY_WIDTH = 15;
    private static final int CANDY_HEIGHT = 36;

    // גודל הסוכריה על מקל
    private static final int LOLLIPOP_WIDTH = 95;
    private static final int LOLLIPOP_HEIGHT = 65;

    // מרחק מינימלי מאויב, כדי שהסוכריה על מקל לא תהיה צמודה מדי לאויב
    private static final int LOLLIPOP_MIN_DISTANCE_FROM_ENEMY = 55;

    // מרחק מקסימלי מאויב, כדי שהסוכריה על מקל תהיה באזור מאתגר
    private static final int LOLLIPOP_MAX_DISTANCE_FROM_ENEMY = 230;

    // מרחק בדיקה מעוגות, כדי למקם את הסוכריה על מקל ליד קירות
    private static final int LOLLIPOP_NEAR_CAKE_DISTANCE = 85;

    // מרחק בטיחות מנקודת ההתחלה של השחקן
    private static final int LOLLIPOP_PLAYER_SAFE_DISTANCE = 280;

    // מערך העוגות במבוך
    private Cake[] cakes;

    // מספר העוגות הפעילות במערך
    private int cakesCount;

    // מערך האויבים בשלב
    private Enemy[] enemies;

    // מערך הפרסים שנוצרים בשלב
    private Prize[] prizes;

    // מערך נתיבי התמונות של הסוכריות הרגילות
    private String[] candyImages;

    // אובייקט להגרלת מיקומים ותמונות
    private Random random = new Random();

    // יוצר את כל הפרסים בשלב ושומר אותם במערך
    public Prize[] createPrizes(
            int regularCandiesAmount,
            Cake[] cakes,
            int cakesCount,
            Enemy[] enemies,
            String[] candyImages
    ) {
        // שמירת הנתונים שהתקבלו כדי שפונקציות אחרות במחלקה יוכלו לעבוד איתם
        this.cakes = cakes;
        this.cakesCount = cakesCount;
        this.enemies = enemies;
        this.candyImages = candyImages;

        // מערך של סוכריות רגילות וסוכריה על מקל
        this.prizes = new Prize[regularCandiesAmount + 1];

        // יצירת הסוכריות הרגילות
        createRegularCandies(regularCandiesAmount);

        // יצירת הסוכריה על מקל במקום האחרון במערך
        createLollipop(regularCandiesAmount);

        // החזרת מערך הפרסים לשלב
        return this.prizes;
    }

    // יוצר את הסוכריות הרגילות במקומות תקינים במפה
    private void createRegularCandies(int amount) {

        // מרווח בטיחות מהקירות של אזור המשחק
        int safeMargin = 20;

        // גבול שמאלי להגרלת מיקום
        int minX = GameSettings.WALL_LEFT + safeMargin;

        // גבול ימני להגרלת מיקום
        int maxX = Main.WINDOW_WIDTH - GameSettings.WALL_RIGHT - CANDY_WIDTH - safeMargin;

        // גבול עליון להגרלת מיקום
        int minY = GameSettings.WALL_TOP + safeMargin;

        // גבול תחתון להגרלת מיקום
        int maxY = Main.WINDOW_HEIGHT - GameSettings.WALL_BOTTOM - CANDY_HEIGHT - safeMargin;

        // יצירת כל הסוכריות הרגילות
        for (int i = 0; i < amount; i++) {
            int x;
            int y;

            // מגריל מיקום עד שנמצא מיקום תקין
            do {
                x = random.nextInt(maxX - minX) + minX;
                y = random.nextInt(maxY - minY) + minY;
            } while (!isValidPrizeLocation(x, y, CANDY_WIDTH, CANDY_HEIGHT, i));

            // בחירת תמונת סוכריה אקראית
            String randomCandy = candyImages[random.nextInt(candyImages.length)];

            // יצירת סוכריה רגילה ושמירתה במערך
            prizes[i] = new Prize(
                    x,
                    y,
                    CANDY_WIDTH,
                    CANDY_HEIGHT,
                    randomCandy,
                    10
            );
        }
    }

    // יוצר את הסוכריה על מקל במיקום הכי מתאים שנמצא
    private void createLollipop(int index) {

        // מחפש נקודה טובה לסוכריה על מקל
        Point point = findBestLollipopLocation(index);

        // יצירת הסוכריה על מקל עם 20 נקודות
        prizes[index] = new Prize(
                point.x,
                point.y,
                LOLLIPOP_WIDTH,
                LOLLIPOP_HEIGHT,
                "/Lollipop.png",
                20
        );
    }

    // מחפש את המיקום הכי טוב לסוכריה על מקל ליד עוגות במבוך
    private Point findBestLollipopLocation(int prizeIndex) {

        // שומר את הנקודה הטובה ביותר שנמצאה
        Point bestPoint = null;

        // שומר את הציון הכי גבוה שנמצא
        int bestScore = -1;

        // מרווח קטן מהעוגה כדי שהסוכריה לא תיגע בה
        int gapFromCake = 8;

        // מעבר על כל העוגות במבוך
        for (int i = 0; i < cakesCount; i++) {
            if (cakes[i] == null) {
                continue;
            }

            // גבולות העוגה הנוכחית
            Rectangle cakeRect = cakes[i].getRect();

            // יוצר מיקומים אפשריים סביב העוגה
            Point[] candidates = {
                    // מימין לעוגה
                    new Point(cakeRect.x + cakeRect.width + gapFromCake, cakeRect.y),

                    // משמאל לעוגה
                    new Point(cakeRect.x - LOLLIPOP_WIDTH - gapFromCake, cakeRect.y),

                    // מתחת לעוגה
                    new Point(cakeRect.x, cakeRect.y + cakeRect.height + gapFromCake),

                    // מעל העוגה
                    new Point(cakeRect.x, cakeRect.y - LOLLIPOP_HEIGHT - gapFromCake),

                    // אלכסון ימין למטה
                    new Point(cakeRect.x + cakeRect.width + gapFromCake, cakeRect.y + cakeRect.height + gapFromCake),

                    // אלכסון שמאל למטה
                    new Point(cakeRect.x - LOLLIPOP_WIDTH - gapFromCake, cakeRect.y + cakeRect.height + gapFromCake),

                    // אלכסון ימין למעלה
                    new Point(cakeRect.x + cakeRect.width + gapFromCake, cakeRect.y - LOLLIPOP_HEIGHT - gapFromCake),

                    // אלכסון שמאל למעלה
                    new Point(cakeRect.x - LOLLIPOP_WIDTH - gapFromCake, cakeRect.y - LOLLIPOP_HEIGHT - gapFromCake)
            };

            // בדיקת כל המיקומים האפשריים סביב העוגה
            for (int j = 0; j < candidates.length; j++) {
                int x = candidates[j].x;
                int y = candidates[j].y;

                // חישוב ציון למיקום
                int score = getLollipopLocationScore(x, y, prizeIndex);

                // אם הציון טוב יותר, שומרים את המיקום
                if (score > bestScore) {
                    bestScore = score;
                    bestPoint = new Point(x, y);
                }
            }
        }

        // אם נמצא מיקום טוב, מחזירים אותו
        if (bestPoint != null) {
            return bestPoint;
        }

        // אם לא נמצא מיקום טוב, מחפשים מיקום חלופי
        return findFallbackLollipopLocation(prizeIndex);
    }

    // נותן ציון למיקום לפי עוגות אויבים ושטח פתוח
    private int getLollipopLocationScore(int x, int y, int prizeIndex) {

        // אם המיקום לא תקין, הציון נפסל
        if (!isValidPrizeLocation(x, y, LOLLIPOP_WIDTH, LOLLIPOP_HEIGHT, prizeIndex)) {
            return -1;
        }

        // הסוכריה לא תהיה קרובה מדי לנקודת ההתחלה של השחקן
        if (!isFarFromPlayerStart(x, y)) {
            return -1;
        }

        // ספירת עוגות קרובות
        int nearbyCakes = countNearbyCakes(x, y);

        // רוצים שהסוכריה תהיה ליד לפחות 2 עוגות
        if (nearbyCakes < 2) {
            return -1;
        }

        // ספירת צדדים פתוחים סביב הסוכריה
        int openSides = countOpenSides(x, y);

        // אם כל הצדדים פתוחים, המיקום קל מדי
        if (openSides >= 4) {
            return -1;
        }

        // ציון לפי מרחק מאויבים
        int enemyScore = getEnemyScore(x, y);

        // אם אין אויב מתאים באזור או שהמיקום קרוב מדי לאויב, נפסל
        if (enemyScore <= 0) {
            return -1;
        }

        // חישוב ציון סופי למיקום
        return nearbyCakes * 15 + enemyScore * 3 + (4 - openSides) * 10;
    }

    // סופר כמה עוגות קרובות למיקום של הסוכריה על מקל
    private int countNearbyCakes(int x, int y) {

        // אזור חיפוש סביב הסוכריה על מקל
        Rectangle searchArea = new Rectangle(
                x - LOLLIPOP_NEAR_CAKE_DISTANCE,
                y - LOLLIPOP_NEAR_CAKE_DISTANCE,
                LOLLIPOP_WIDTH + LOLLIPOP_NEAR_CAKE_DISTANCE * 2,
                LOLLIPOP_HEIGHT + LOLLIPOP_NEAR_CAKE_DISTANCE * 2
        );

        // מונה עוגות קרובות
        int count = 0;

        // מעבר על כל העוגות ובדיקת חפיפה עם אזור החיפוש
        for (int i = 0; i < cakesCount; i++) {
            if (cakes[i] != null && searchArea.intersects(cakes[i].getRect())) {
                count++;
            }
        }

        return count;
    }

    // סופר כמה צדדים סביב הסוכריה פתוחים בלי עוגות
    private int countOpenSides(int x, int y) {

        // מונה צדדים פתוחים
        int openSides = 0;

        // מרחק בדיקה מכל צד
        int checkDistance = 65;

        // אזור בדיקה מימין
        Rectangle rightArea = new Rectangle(
                x + LOLLIPOP_WIDTH,
                y,
                checkDistance,
                LOLLIPOP_HEIGHT
        );

        // אזור בדיקה משמאל
        Rectangle leftArea = new Rectangle(
                x - checkDistance,
                y,
                checkDistance,
                LOLLIPOP_HEIGHT
        );

        // אזור בדיקה למטה
        Rectangle downArea = new Rectangle(
                x,
                y + LOLLIPOP_HEIGHT,
                LOLLIPOP_WIDTH,
                checkDistance
        );

        // אזור בדיקה למעלה
        Rectangle upArea = new Rectangle(
                x,
                y - checkDistance,
                LOLLIPOP_WIDTH,
                checkDistance
        );

        // אם אזור ימין לא נוגע בעוגה, הצד פתוח
        if (!areaTouchesCake(rightArea)) {
            openSides++;
        }

        // אם אזור שמאל לא נוגע בעוגה, הצד פתוח
        if (!areaTouchesCake(leftArea)) {
            openSides++;
        }

        // אם אזור תחתון לא נוגע בעוגה, הצד פתוח
        if (!areaTouchesCake(downArea)) {
            openSides++;
        }

        // אם אזור עליון לא נוגע בעוגה, הצד פתוח
        if (!areaTouchesCake(upArea)) {
            openSides++;
        }

        return openSides;
    }

    // בודק אם אזור מסוים נוגע בעוגה
    private boolean areaTouchesCake(Rectangle area) {

        // מעבר על כל העוגות
        for (int i = 0; i < cakesCount; i++) {

            // אם האזור נוגע בעוגה, מחזירים true
            if (cakes[i] != null && area.intersects(cakes[i].getRect())) {
                return true;
            }
        }

        return false;
    }

    // נותן ציון לפי המרחק של הסוכריה מהאויבים
    private int getEnemyScore(int x, int y) {

        // אזור קרוב מדי לאויב
        Rectangle tooCloseArea = new Rectangle(
                x - LOLLIPOP_MIN_DISTANCE_FROM_ENEMY,
                y - LOLLIPOP_MIN_DISTANCE_FROM_ENEMY,
                LOLLIPOP_WIDTH + LOLLIPOP_MIN_DISTANCE_FROM_ENEMY * 2,
                LOLLIPOP_HEIGHT + LOLLIPOP_MIN_DISTANCE_FROM_ENEMY * 2
        );

        // אזור טוב ביחס לאויב, לא קרוב מדי ולא רחוק מדי
        Rectangle goodArea = new Rectangle(
                x - LOLLIPOP_MAX_DISTANCE_FROM_ENEMY,
                y - LOLLIPOP_MAX_DISTANCE_FROM_ENEMY,
                LOLLIPOP_WIDTH + LOLLIPOP_MAX_DISTANCE_FROM_ENEMY * 2,
                LOLLIPOP_HEIGHT + LOLLIPOP_MAX_DISTANCE_FROM_ENEMY * 2
        );

        // ציון התחלתי
        int score = 0;

        // מעבר על כל האויבים
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i] == null) {
                continue;
            }

            // גבולות האויב
            Rectangle enemyRect = enemies[i].getRect();

            // אם הסוכריה קרובה מדי לאויב, המיקום נפסל
            if (tooCloseArea.intersects(enemyRect)) {
                return -1;
            }

            // אם יש אויב באזור טוב, מוסיפים ציון
            if (goodArea.intersects(enemyRect)) {
                score += 40;
            }
        }

        return score;
    }

    // בודק שהסוכריה על מקל לא קרובה מדי לנקודת ההתחלה של השחקן
    private boolean isFarFromPlayerStart(int x, int y) {

        // מלבן שמייצג את הסוכריה על מקל
        Rectangle lollipopRect = new Rectangle(x, y, LOLLIPOP_WIDTH, LOLLIPOP_HEIGHT);

        // אזור בטיחות סביב נקודת התחלה של השחקן
        Rectangle playerStartArea = new Rectangle(
                100 - LOLLIPOP_PLAYER_SAFE_DISTANCE / 2,
                100 - LOLLIPOP_PLAYER_SAFE_DISTANCE / 2,
                LOLLIPOP_PLAYER_SAFE_DISTANCE,
                LOLLIPOP_PLAYER_SAFE_DISTANCE
        );

        // אם הסוכריה לא נוגעת באזור הבטיחות, המיקום רחוק מספיק
        return !lollipopRect.intersects(playerStartArea);
    }

    // מחפש מיקום חלופי לסוכריה על מקל אם לא נמצא מיקום טוב
    private Point findFallbackLollipopLocation(int prizeIndex) {

        // גבולות הגרלה בציר X
        int minX = GameSettings.WALL_LEFT + 20;
        int maxX = Main.WINDOW_WIDTH - GameSettings.WALL_RIGHT - LOLLIPOP_WIDTH - 20;

        // גבולות הגרלה בציר Y
        int minY = GameSettings.WALL_TOP + 20;
        int maxY = Main.WINDOW_HEIGHT - GameSettings.WALL_BOTTOM - LOLLIPOP_HEIGHT - 20;

        // מנסה למצוא מיקום חלופי עד 1000 ניסיונות
        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(maxX - minX) + minX;
            int y = random.nextInt(maxY - minY) + minY;

            // בדיקה שהמיקום עומד בכל התנאים
            if (isValidPrizeLocation(x, y, LOLLIPOP_WIDTH, LOLLIPOP_HEIGHT, prizeIndex)
                    && isFarFromPlayerStart(x, y)
                    && countNearbyCakes(x, y) >= 1
                    && countOpenSides(x, y) <= 3
                    && getEnemyScore(x, y) > 0) {
                return new Point(x, y);
            }
        }

        // אם לא נמצא מיקום מתאים, מחזירים את מרכז המסך
        return new Point(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT / 2);
    }

    // בודק שהפרס לא מחוץ למפה ולא נוגע בעוגה או בפרס אחר
    private boolean isValidPrizeLocation(int x, int y, int width, int height, int currentPrizeIndex) {

        // בדיקה שהפרס נמצא בתוך אזור המשחק
        if (!GameSettings.isInsidePlayArea(x, y, width, height)) {
            return false;
        }

        // מלבן שמייצג את הפרס
        Rectangle prizeRect = new Rectangle(x, y, width, height);

        // בדיקה שהפרס לא נוגע בעוגות
        for (int i = 0; i < cakesCount; i++) {
            if (cakes[i] != null && prizeRect.intersects(cakes[i].getRect())) {
                return false;
            }
        }

        // מרווח בטיחות מפרסים אחרים
        int padding = 40;

        // אזור בטיחות סביב הפרס
        Rectangle safeZone = new Rectangle(
                x - padding,
                y - padding,
                width + padding * 2,
                height + padding * 2
        );

        // בדיקה שהפרס לא קרוב מדי לפרסים שכבר נוצרו
        for (int i = 0; i < currentPrizeIndex; i++) {
            if (prizes[i] != null && safeZone.intersects(prizes[i].getBounds())) {
                return false;
            }
        }

        return true;
    }
}