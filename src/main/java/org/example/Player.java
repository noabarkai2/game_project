package org.example;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Iterator;

public class Player {
    private int x;
    private int y;
    private int width;
    private int height;
    private Image currentImage;
    private Image upImage;
    private Image downImage;
    private Image rightImage;
    private Image leftImage;

    // --- משתנים שקשורים רק לגיף ---

    // המערך הרגיל שלנו - פה נשמור את כל התמונות הבודדות (הפריימים) שמרכיבות את הגיף
    private BufferedImage[] frames;

    // משתנה שזוכר באיזו תמונה (איזה תא במערך) אנחנו נמצאים כרגע כדי להציג אותה
    private int currentFrameIndex = 0;

    // טיימר (מונה) שסופר את הזמן שעובר כדי לדעת מתי להחליף לתמונה הבאה במערך
    private int animationCounter = 0;

    // קובע את קצב ההחלפה - כמה זמן נחכה עד שנעבור לתא הבא במערך (אפשר לשנות את המספר כדי להאיץ/להאט)
    private int animationSpeed = 2;

    private boolean isMoving = false;

    // מקדם הגדלה: מגדיר פי כמה נרצה להגדיל את התמונה של הגיף כדי שתיראה טוב יותר על המסך
    private double gifScaleMultiplier = 2.8;

    // משתנים שישמרו את הרוחב והגובה הסופיים של הגיף אחרי ההגדלה
    private int gifDrawWidth;
    private int gifDrawHeight;

    // משתנים שישמרו את המיקום המדויק בצירים כדי שהגיף יצויר בדיוק באמצע של השחקן
    private int gifOffsetX;
    private int gifOffsetY;

    // ------------------------------

    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // קוראים לפונקציה שמחשבת את הגודל של הגיף כבר בתחילת המשחק
        updateGifDimensions();

        this.downImage = loadImage("/Front_no background.png");
        this.upImage = loadImage("/Back_no background.png");
        this.rightImage = loadImage("/Right_no background.png");
        this.leftImage = loadImage("/Left_no background.png");

        this.currentImage = this.downImage;

        // קוראים לפונקציה שתחלץ את התמונות מה-GIF ישר לתוך המערך שבנינו
        loadGifFrames("/cupcake.gif");
    }

    public void setIsMoving(boolean moving) {
        this.isMoving = moving;
    }

    private Image loadImage(String imagePath) {
        try {
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                return ImageIO.read(imageStream);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // --- פונקציה שקשורה רק לגיף ---
    private void updateGifDimensions() {
        // חישוב הגודל החדש של הגיף: מכפילים את הרוחב והגובה הרגילים של השחקן במקדם ההגדלה
        this.gifDrawWidth = (int) (this.width * gifScaleMultiplier);
        this.gifDrawHeight = (int) (this.height * gifScaleMultiplier);

        // חישוב המיקום: מוצאים את ההפרש בין גודל השחקן לגודל הגיף, ומחלקים ב-2 כדי שהגיף ישב בול באמצע
        this.gifOffsetX = (this.width - this.gifDrawWidth) / 2;
        this.gifOffsetY = (this.height - this.gifDrawHeight) / 2;
    }

    public void moveRight() {
        if (this.x + this.width < Main.WINDOW_WIDTH) {
            this.x += 5;
        }
        this.currentImage = this.rightImage;
    }

    public void moveLeft() {
        if (this.x > 0) {
            this.x -= 5;
        }
        this.currentImage = this.leftImage;
    }

    public void moveDown() {
        if (this.y + this.height < Main.WINDOW_HEIGHT - 40) {
            this.y += 5;
        }
        this.currentImage = this.downImage;
    }

    public void moveUp() {
        if (this.y > 0) {
            this.y -= 5;
        }
        this.currentImage = this.upImage;
    }

    // --- פונקציה שקשורה רק לגיף ---
    private void loadGifFrames(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                // פותחים זרם נתונים לקריאת קובץ התמונה
                ImageInputStream stream = ImageIO.createImageInputStream(is);
                // מבקשים מ-Java כלי שמיועד ספציפית לפענוח קבצי GIF
                Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");

                if (readers.hasNext()) {
                    ImageReader reader = readers.next();
                    reader.setInput(stream);

                    // השלב הקריטי: שואלים את הכלי כמה תמונות (פריימים) יש בסך הכל בגיף הזה
                    int count = reader.getNumImages(true);

                    // יוצרים את המערך הרגיל שלנו, בדיוק בגודל של כמות התמונות שמצאנו
                    this.frames = new BufferedImage[count];

                    // עוברים בלולאה פשוטה מ-0 ועד כמות התמונות
                    for (int i = 0; i < count; i++) {
                        // קוראים את התמונה הנוכחית ושומרים אותה בתוך התא המתאים (i) במערך שלנו
                        this.frames[i] = reader.read(i);
                    }
                }
            } else {
                System.out.println("לא מצאתי את הקובץ: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- פונקציה שקשורה רק לגיף ---
    private void updateAnimation() {
        // אם המערך ריק או שיש בו רק תמונה אחת, אין טעם באנימציה אז יוצאים מהפונקציה
        if (this.frames == null || this.frames.length <= 1) return;

        // מגדילים את המונה שלנו בעוד צעד
        this.animationCounter++;

        // בודקים אם המונה הגיע למהירות שהגדרנו. אם כן, הגיע הזמן להחליף תמונה!
        if (this.animationCounter >= this.animationSpeed) {

            // מאפסים את המונה חזרה ל-0 לקראת התמונה הבאה
            this.animationCounter = 0;

            // מקדמים את האינדקס באחד, כדי שנעבור לתא הבא במערך (התמונה הבאה)
            this.currentFrameIndex++;

            // בודקים: האם הגענו לסוף המערך? אם כן, מאפסים את האינדקס ל-0 כדי להתחיל מחדש את האנימציה
            if (this.currentFrameIndex >= this.frames.length) {
                this.currentFrameIndex = 0;
            }
        }
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        // גם כאן אנחנו קוראים לפונקציה של הגיף כדי לעדכן את המידות שלו אם גודל השחקן משתנה
        updateGifDimensions();
    }

    public void paint(Graphics graphics) {
        if (isMoving) {
            if (this.currentImage != null) {
                graphics.drawImage(this.currentImage, this.x, this.y, this.width, this.height, null);
            }
        } else {
            // --- החלק שקשור לציור הגיף ---

            // בודקים איזה פריים (תא במערך) צריך לצייר עכשיו
            updateAnimation();

            // מוודאים שהמערך באמת קיים ושיש בו תמונות
            if (this.frames != null && this.frames.length > 0) {
                // this.frames[currentFrameIndex]: שולפים את התמונה מהתא הנוכחי במערך
                // this.x + this.gifOffsetX: קובעים את המיקום בציר ה-X עם חישוב האמצע
                // this.y + this.gifOffsetY: קובעים את המיקום בציר ה-Y עם חישוב האמצע
                // this.gifDrawWidth, this.gifDrawHeight: מציירים את התמונה בגודל המוגדל
                graphics.drawImage(this.frames[currentFrameIndex], this.x + this.gifOffsetX, this.y + this.gifOffsetY, this.gifDrawWidth, this.gifDrawHeight, null);
            }
        }
    }
}