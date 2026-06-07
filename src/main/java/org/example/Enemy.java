package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;
import java.util.Random;

// מחלקה שמייצגת אויב במשחק
// האויב יודע לזוז, להחליף כיוון, להחליף תמונה לפי כיוון, ולצייר את עצמו
public class Enemy {

    // קבועים שמייצגים כיווני תנועה
    public static final int RIGHT = 1;
    public static final int LEFT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;

    // מיקום האויב בציר X
    private int x;

    // מיקום האויב בציר Y
    private int y;

    // רוחב האויב
    private int width;

    // גובה האויב
    private int height;

    // הכיוון הנוכחי של האויב
    private int direction;

    // התמונה שמוצגת כרגע לפי כיוון התנועה
    private Image currentImage;

    // תמונה של האויב כשהוא פונה למעלה
    private Image backImage;

    // תמונה של האויב כשהוא פונה למטה
    private Image frontImage;

    // תמונה של האויב כשהוא פונה ימינה
    private Image rightImage;

    // תמונה של האויב כשהוא פונה שמאלה
    private Image leftImage;

    // קובע אם האויב זז כרגע או עומד
    private boolean isMoving = false;

    // אובייקט ליצירת בחירות אקראיות, למשל החלפת כיוון
    private Random random = new Random();

    // מרחק מגבול שמאל של אזור המשחק
    private int offsetLeft;

    // מרחק מגבול ימין של אזור המשחק
    private int offsetRight;

    // מרחק מגבול עליון של אזור המשחק
    private int offsetTop;

    // מרחק מגבול תחתון של אזור המשחק
    private int offsetBottom;

    // בנאי שמקבל מיקום וגודל לאויב
    public Enemy(int x, int y, int width, int height) {

        // שמירת המיקום והגודל שהתקבלו
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // כיוון התחלתי של האויב כלפי מטה
        this.direction = DOWN;

        // שמירת גבולות אזור המשחק מתוך GameSettings
        this.offsetLeft = GameSettings.WALL_LEFT;
        this.offsetRight = GameSettings.WALL_RIGHT;
        this.offsetTop = GameSettings.WALL_TOP;
        this.offsetBottom = GameSettings.WALL_BOTTOM;
    }

    // מזיזה את האויב בציר X לפי כמות שנשלחת
    //אחראי למהירות אופקית - נניח מגדיל את ה-x ב-5
    public void moveHorizontally(int amount) {
        this.x += amount;
    }

    // מזיזה את האויב בציר Y לפי כמות שנשלחת
    //אחראי למהירות אנכית - נניח מגדיל את ה-y ב-5
    public void moveVertically(int amount) {
        this.y += amount;
    }

    // בודק אם האויב הגיע לגבול הימני
    public boolean isAtRightBoundary() {
        return this.x + this.width >= Main.WINDOW_WIDTH - offsetRight;
    }

    // בודק אם האויב הגיע לגבול השמאלי
    public boolean isAtLeftBoundary() {
        return this.x <= offsetLeft;
    }

    // בודק אם האויב הגיע לגבול העליון
    public boolean isAtTopBoundary() {
        return this.y <= offsetTop;
    }

    // בודק אם האויב הגיע לגבול התחתון
    public boolean isAtBottomBoundary() {
        return this.y + this.height >= Main.WINDOW_HEIGHT - offsetBottom;
    }

    // מחזירה את מיקום X של האויב
    public int getX() {return this.x;}

    // מחזירה את מיקום Y של האויב
    public int getY() {return this.y;}

    // מחזירה את רוחב האויב
    public int getWidth() {return this.width;}

    // מחזירה את גובה האויב
    public int getHeight() {return this.height;}

    // מחזירה את הכיוון הנוכחי של האויב
    public int getDirection() {return this.direction;}

    // מעדכנת את מיקום X של האויב
    public void setX(int x) {this.x = x;}

    // מעדכנת את מיקום Y של האויב
    public void setY(int y) {this.y = y;}

    // מעדכנת את כיוון האויב
    public void setDirection(int direction) {this.direction = direction;}

    // מחזירה אם האויב נמצא במצב תנועה
    public boolean isMoving() {return this.isMoving;}

    // מעדכנת אם האויב זז או עומד
    public void setIsMoving(boolean moving) {this.isMoving = moving;}

    // מחזירה את אובייקט ה Random של האויב
    public Random getRandom() {return this.random;}

    // מעדכנת את התמונה הנוכחית של האויב
    public void setCurrentImage(Image img) {this.currentImage = img;}

    // מעדכנת את תמונת האויב כשהוא פונה למטה
    public void setFrontImage(Image img) {this.frontImage = img;}

    // מעדכנת את תמונת האויב כשהוא פונה למעלה
    public void setBackImage(Image img) {this.backImage = img;}

    // מעדכנת את תמונת האויב כשהוא פונה ימינה
    public void setRightImage(Image img) {this.rightImage = img;}

    // מעדכנת את תמונת האויב כשהוא פונה שמאלה
    public void setLeftImage(Image img) {this.leftImage = img;}

    // מחזירה את תמונת האויב כשהוא פונה למטה
    public Image getFrontImage() {return this.frontImage;}

    // מחזירה את תמונת האויב כשהוא פונה למעלה
    public Image getBackImage() {return this.backImage;}

    // מחזירה את תמונת האויב כשהוא פונה ימינה
    public Image getRightImage() {return this.rightImage;}

    // מחזירה את תמונת האויב כשהוא פונה שמאלה
    public Image getLeftImage() {return this.leftImage;}

    // מחזירה מלבן שמייצג את גבולות האויב
    // מתאים לבדיקת התנגשות עם שחקן או אובייקטים אחרים
    public Rectangle getRect() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    // מזיזה את האויב לפי הכיוון הנוכחי
    // אם האויב מגיע לגבול או לפי בחירה אקראית, הוא משנה כיוון
    // רק התירס והגזר(enemyGeneric) זזים לפי הפונקציה כל השאר דורסים
    public void move() {

        // אם האויב לא במצב תנועה, יוצאים מהפונקציה
        if (!isMoving) {
            return;
        }

        // משתנה שמסמן אם האויב פגע בגבול
        boolean hitBoundary = false;

        // בחירת פעולה לפי הכיוון הנוכחי
        switch (this.direction) {
            case RIGHT:

                // אם יש מקום לזוז ימינה, מזיזים ומשנים תמונה לימין
                if (this.x + this.width < Main.WINDOW_WIDTH - this.offsetRight) {
                    this.currentImage = rightImage;
                    this.x += 2;
                } else {
                    // אם אין מקום לזוז, מסמנים פגיעה בגבול
                    hitBoundary = true;
                }
                break;

            case LEFT:

                // אם יש מקום לזוז שמאלה, מזיזים ומשנים תמונה לשמאל
                if (this.x > this.offsetLeft) {
                    this.currentImage = leftImage;
                    this.x -= 2;
                } else {
                    hitBoundary = true;
                }
                break;

            case UP:

                // אם יש מקום לזוז למעלה, מזיזים ומשנים תמונה לגב
                if (this.y > this.offsetTop) {
                    this.currentImage = backImage;
                    this.y -= 2;
                } else {
                    hitBoundary = true;
                }
                break;

            case DOWN:

                // אם יש מקום לזוז למטה, מזיזים ומשנים תמונה לחזית
                if (this.y + this.height < Main.WINDOW_HEIGHT - this.offsetBottom) {
                    this.currentImage = frontImage;
                    this.y += 2;
                } else {
                    hitBoundary = true;
                }
                break;
        }

        // סיכוי קטן לשינוי כיוון אקראי
        if (random.nextInt(200) == 0) {
            this.direction = random.nextInt(1, 5);
        }

        // אם האויב פגע בגבול או לפי סיכוי אקראי, הופכים את הכיוון
        if (hitBoundary || random.nextInt(250) == 0) {
            reverseDirection();
        }
    }

    // הופכת את כיוון התנועה של האויב לכיוון הנגדי
    public void reverseDirection() {
        switch (this.direction) {
            case RIGHT:
                this.direction = LEFT;
                break;
            case LEFT:
                this.direction = RIGHT;
                break;
            case UP:
                this.direction = DOWN;
                break;
            case DOWN:
                this.direction = UP;
                break;
        }
    }

    // מציירת את האויב על המסך
    public void paint(Graphics graphics) {

        // מציירים רק אם קיימת תמונה נוכחית
        if (this.currentImage != null) {
            graphics.drawImage(this.currentImage, this.x, this.y, this.width, this.height, null);
        }
    }

    // טוענת תמונה מתוך resources לפי נתיב
    public Image loadImage(String imagePath) {
        try {

            // קריאת התמונה כזרם מתוך resources
            InputStream imageStream = getClass().getResourceAsStream(imagePath);

            // אם התמונה נמצאה, מחזירים אותה כאובייקט Image
            if (imageStream != null) {
                return ImageIO.read(imageStream);
            }

            // אם התמונה לא נמצאה, מחזירים null
            return null;

        } catch (Exception e) {

            // במקרה של שגיאה בטעינת התמונה, מחזירים null
            return null;
        }
    }
}