package org.example;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Iterator;

// מחלקה שמייצגת את השחקן במשחק
// המחלקה אחראית על מיקום, תנועה, תמונות, אנימציית GIF וציור השחקן
public class Player {

    // קבועים שמייצגים כיווני תנועה
    private static final int RIGHT = 1;
    private static final int LEFT = 2;
    private static final int UP = 3;
    private static final int DOWN = 4;

    // גבולות המשחק לפי ההגדרות הכלליות
    // משתמשים בהם כדי למנוע מהשחקן לצאת מחוץ לאזור המשחק
    private static final int OFFSET_RIGHT = GameSettings.WALL_RIGHT;
    private static final int OFFSET_LEFT = GameSettings.WALL_LEFT;
    private static final int OFFSET_BOTTOM = GameSettings.WALL_BOTTOM;
    private static final int OFFSET_TOP = GameSettings.WALL_TOP;

    // מיקום השחקן בציר X
    private int x;

    // מיקום השחקן בציר Y
    private int y;

    // רוחב השחקן
    private int width;

    // גובה השחקן
    private int height;

    // התמונה שמוצגת כרגע לפי כיוון התנועה
    private Image currentImage;

    // תמונת השחקן כשהוא פונה למעלה
    private Image upImage;

    // תמונת השחקן כשהוא פונה למטה
    private Image downImage;

    // תמונת השחקן כשהוא פונה ימינה
    private Image rightImage;

    // תמונת השחקן כשהוא פונה שמאלה
    private Image leftImage;

    // מערך ששומר את כל הפריימים של קובץ ה GIF
    private BufferedImage[] frames;

    // האינדקס של הפריים הנוכחי באנימציה
    private int currentFrameIndex = 0;

    // מונה שעוזר לשלוט בקצב החלפת הפריימים
    private int animationCounter = 0;

    // קובע כל כמה עדכונים מחליפים פריים באנימציה
    private int animationSpeed = 2;

    // שומר אם לפני עצירת המשחק הוצג GIF
    // כך בזמן pause התצוגה נשמרת כמו שהייתה
    private boolean wasShowingGif = false;

    // שומר אם השחקן בתנועה
    private boolean isMoving = false;

    // מכפיל שמגדיל את ה GIF ביחס לגודל השחקן
    private double gifScaleMultiplier = 2.8;

    // רוחב הציור של ה GIF
    private int gifDrawWidth;

    // גובה הציור של ה GIF
    private int gifDrawHeight;

    // הזזת ה GIF בציר X כדי למרכז אותו ביחס לשחקן
    private int gifOffsetX;

    // הזזת ה GIF בציר Y כדי למרכז אותו ביחס לשחקן
    private int gifOffsetY;

    // הזמן האחרון שבו השחקן זז
    // משמש לזיהוי מצב עמידה במקום
    private long lastMoveTime;

    // הכיוון האחרון שבו השחקן זז
    // משמש להגברת מהירות רגעית בזמן שינוי כיוון
    private int lastDirection = DOWN;

    // בנאי שמקבל מיקום וגודל לשחקן
    public Player(int x, int y, int width, int height) {

        // שמירת מיקום וגודל השחקן
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // חישוב גודל ומיקום ה GIF לפי גודל השחקן
        updateGifDimensions();

        // טעינת תמונות השחקן לפי כיוון
        this.downImage = loadImage("/Front_no background.png");
        this.upImage = loadImage("/Back_no background.png");
        this.rightImage = loadImage("/Right_no background.png");
        this.leftImage = loadImage("/Left_no background.png");

        // תמונת ברירת המחדל היא תמונה קדמית
        this.currentImage = this.downImage;

        // שמירת זמן התחלה כדי לחשב מתי השחקן עומד במקום
        this.lastMoveTime = System.currentTimeMillis();

        // טעינת פריימים של ה GIF
        loadGifFrames("/cupcake.gif");
    }

    // מחזיר את מיקום X של השחקן
    public int getX() {
        return this.x;
    }

    // מחזיר את מיקום Y של השחקן
    public int getY() {
        return this.y;
    }

    // מעדכן את מיקום X של השחקן
    public void setX(int x) {
        this.x = x;
    }

    // מעדכן את מיקום Y של השחקן
    public void setY(int y) {
        this.y = y;
    }

    // מחזיר את רוחב השחקן
    public int getWidth() {
        return this.width;
    }

    // מחזיר את גובה השחקן
    public int getHeight() {
        return this.height;
    }

    // מעדכן אם השחקן נמצא בתנועה
    public void setIsMoving(boolean moving) {
        this.isMoving = moving;
    }

    // מעדכן את זמן התנועה האחרון לזמן הנוכחי
    public void updateLastMoveTime() {
        this.lastMoveTime = System.currentTimeMillis();
    }

    // טוען תמונה מתוך תיקיית המשאבים
    private Image loadImage(String imagePath) {
        try {

            // פתיחת קובץ התמונה מתוך resources
            InputStream imageStream = getClass().getResourceAsStream(imagePath);

            // אם הקובץ נמצא, קוראים אותו ומחזירים תמונה
            if (imageStream != null) {
                return ImageIO.read(imageStream);
            }

            // אם הקובץ לא נמצא, מחזירים null
            return null;
        } catch (Exception e) {

            // במקרה של שגיאה בטעינת תמונה, מחזירים null
            return null;
        }
    }

    // מחשב את הגודל והמיקום של הגיף ביחס לשחקן
    private void updateGifDimensions() {

        // חישוב רוחב ה GIF לפי רוחב השחקן והמכפיל
        this.gifDrawWidth = (int) (this.width * gifScaleMultiplier);

        // חישוב גובה ה GIF לפי גובה השחקן והמכפיל
        this.gifDrawHeight = (int) (this.height * gifScaleMultiplier);

        // חישוב הזזה בציר X כדי שה GIF יהיה ממורכז
        this.gifOffsetX = (this.width - this.gifDrawWidth) / 2;

        // חישוב הזזה בציר Y כדי שה GIF יהיה ממורכז
        this.gifOffsetY = (this.height - this.gifDrawHeight) / 2;
    }

    // מחזיר מהירות תנועה רגילה או מהירה יותר בזמן שינוי כיוון
    private int getMovementSpeed(int newDirection) {

        // כל תנועה מעדכנת את זמן התנועה האחרון
        this.lastMoveTime = System.currentTimeMillis();

        // מהירות רגילה
        //פה משנים מהירות
        int speed = 5;

        // אם הכיוון השתנה, נותנים מהירות גבוהה יותר לרגע
        if (this.lastDirection != newDirection) {
            speed = 8;
        }

        // שמירת הכיוון החדש ככיוון האחרון
        this.lastDirection = newDirection;

        // החזרת מהירות התנועה
        return speed;
    }

    // מזיז את השחקן ימינה אם הוא לא עבר את גבול המסך
    public void moveRight() {

        // קבלת מהירות לפי הכיוון החדש
        int speed = getMovementSpeed(RIGHT);

        // בדיקה שהשחקן לא יוצא מהגבול הימני
        if (this.x + this.width < Main.WINDOW_WIDTH - OFFSET_RIGHT) {
            this.x += speed;
        }

        // עדכון תמונת השחקן לכיוון ימין
        this.currentImage = this.rightImage;
    }

    // מזיז את השחקן שמאלה אם הוא לא עבר את גבול המסך
    public void moveLeft() {

        // קבלת מהירות לפי הכיוון החדש
        int speed = getMovementSpeed(LEFT);

        // בדיקה שהשחקן לא יוצא מהגבול השמאלי
        if (this.x > OFFSET_LEFT) {
            this.x -= speed;
        }

        // עדכון תמונת השחקן לכיוון שמאל
        this.currentImage = this.leftImage;
    }

    // מזיז את השחקן למטה אם הוא לא עבר את גבול המסך
    public void moveDown() {

        // קבלת מהירות לפי הכיוון החדש
        int speed = getMovementSpeed(DOWN);

        // בדיקה שהשחקן לא יוצא מהגבול התחתון
        if (this.y + this.height < Main.WINDOW_HEIGHT - OFFSET_BOTTOM) {
            this.y += speed;
        }

        // עדכון תמונת השחקן לכיוון מטה
        this.currentImage = this.downImage;
    }

    // מזיז את השחקן למעלה אם הוא לא עבר את גבול המסך
    public void moveUp() {

        // קבלת מהירות לפי הכיוון החדש
        int speed = getMovementSpeed(UP);

        // בדיקה שהשחקן לא יוצא מהגבול העליון
        if (this.y > OFFSET_TOP) {
            this.y -= speed;
        }

        // עדכון תמונת השחקן לכיוון מעלה
        this.currentImage = this.upImage;
    }

    // מפרק את קובץ הגיף לפריימים ושומר אותם במערך
    private void loadGifFrames(String path) {
        try {

            // פתיחת קובץ ה GIF מתוך resources
            InputStream is = getClass().getResourceAsStream(path);

            // אם הקובץ נמצא, מתחילים לקרוא אותו
            if (is != null) {

                // יצירת זרם שמתאים לקריאת תמונות
                ImageInputStream stream = ImageIO.createImageInputStream(is);

                // קבלת reader שמתאים לפורמט gif
                Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");

                // אם קיים reader מתאים, משתמשים בו
                if (readers.hasNext()) {
                    ImageReader reader = readers.next();

                    // חיבור ה reader לזרם של קובץ ה GIF
                    reader.setInput(stream);

                    // קבלת מספר הפריימים בתוך ה GIF
                    int count = reader.getNumImages(true);

                    // יצירת מערך בגודל מספר הפריימים
                    this.frames = new BufferedImage[count];

                    // קריאת כל פריים ושמירתו במערך
                    for (int i = 0; i < count; i++) {
                        this.frames[i] = reader.read(i);
                    }
                }
            } else {

                // הודעה לקונסול אם קובץ ה GIF לא נמצא
                System.out.println("לא מצאתי את הקובץ: " + path);
            }
        } catch (Exception e) {

            // הדפסת שגיאה במקרה של בעיה בקריאת ה GIF
            e.printStackTrace();
        }
    }

    // מעביר את הגיף לפריים הבא לפי קצב האנימציה
    private void updateAnimation() {

        // אם אין פריימים או שיש פריים אחד בלבד, אין אנימציה לעדכן
        if (this.frames == null || this.frames.length <= 1) {
            return;
        }

        // העלאת מונה האנימציה
        this.animationCounter++;

        // כאשר המונה מגיע למהירות שנקבעה, עוברים פריים
        if (this.animationCounter >= this.animationSpeed) {
            this.animationCounter = 0;
            this.currentFrameIndex++;

            // אם עברנו את הפריים האחרון, חוזרים להתחלה
            if (this.currentFrameIndex >= this.frames.length) {
                this.currentFrameIndex = 0;
            }
        }
    }

    // משנה את גודל השחקן ומחשב מחדש את גודל הגיף
    public void setSize(int width, int height) {

        // עדכון רוחב וגובה השחקן
        this.width = width;
        this.height = height;

        // חישוב מחדש של גודל ה GIF ביחס לגודל החדש
        updateGifDimensions();
    }

    // מצייר את השחקן כתמונה בזמן תנועה או כגיף אחרי עמידה במקום
    public void paint(Graphics graphics, boolean isPaused) {

        // חישוב כמה זמן עבר מאז התנועה האחרונה
        long idleTime = System.currentTimeMillis() - this.lastMoveTime;

        // אחרי שנייה של עמידה במקום, עוברים להצגת GIF
        boolean shouldShowGif = idleTime >= 1000;

        // בזמן pause שומרים את מצב התצוגה שהיה לפני העצירה
        if (isPaused) {
            shouldShowGif = this.wasShowingGif;

            // אם לפני העצירה לא הוצג GIF, מעדכנים זמן כדי שלא יופיע GIF בזמן pause
            if (!shouldShowGif) {
                this.lastMoveTime = System.currentTimeMillis();
            }

            // בזמן משחק רגיל שומרים אם הוצג GIF
        } else {
            this.wasShowingGif = shouldShowGif;
        }

        // אם לא צריך להציג GIF, מציירים תמונה רגילה לפי הכיוון
        if (!shouldShowGif) {
            if (this.currentImage != null) {
                graphics.drawImage(
                        this.currentImage,
                        this.x,
                        this.y,
                        this.width,
                        this.height,
                        null
                );
            }

            return;
        }

        // אם המשחק לא בעצירה, מעדכנים את פריים האנימציה
        if (!isPaused) {
            updateAnimation();
        }

        // ציור פריים ה GIF הנוכחי
        if (this.frames != null && this.frames.length > 0) {
            graphics.drawImage(
                    this.frames[currentFrameIndex],
                    this.x + this.gifOffsetX,
                    this.y + this.gifOffsetY,
                    this.gifDrawWidth,
                    this.gifDrawHeight,
                    null
            );
        }
    }

    // מחזיר מלבן פגיעה של השחקן
    public Rectangle getRect() {

        // המלבן משמש לבדיקות התנגשות עם עוגות, אויבים ופרסים
        return new Rectangle(this.x, this.y, this.width, this.height);
    }
}