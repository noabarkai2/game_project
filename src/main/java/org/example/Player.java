package org.example;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
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

    // מערך שישמור את התמונות שנחלץ מתוך ה-GIF
    private BufferedImage[] frames;

    // משתנים לניהול האנימציה בצורה חלקה
    private int currentFrameIndex = 0;
    private int animationCounter = 0;
    private int animationSpeed = 2; // קצב האנימציה - תוכל לשנות את המספר הזה כדי להאיץ או להאט

    private boolean isMoving = false;
    private double gifScaleMultiplier = 2.8;
    private int gifDrawWidth;
    private int gifDrawHeight;
    private int gifOffsetX;
    private int gifOffsetY;


    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        updateGifDimensions();


        this.downImage = loadImage("/Front_no background.png");
        this.upImage = loadImage("/Back_no background.png");
        this.rightImage = loadImage("/Right_no background.png");
        this.leftImage = loadImage("/Left_no background.png");

        this.currentImage = this.downImage;

        // קוראים לפונקציה שתחלץ את התמונות מה-GIF ישר לתוך המערך
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
    private void updateGifDimensions() {
        // הגיף צריך להיות גדול יותר כדי שהתוכן שלו יתאים לגודל ה-width/height
        this.gifDrawWidth = (int)(this.width * gifScaleMultiplier);
        this.gifDrawHeight = (int)(this.height * gifScaleMultiplier);

        // כדי שהמרכז של הקאפקייק בגיף יהיה במרכז של x ו-y
        this.gifOffsetX = (this.width - this.gifDrawWidth) / 2;
        this.gifOffsetY = (this.height - this.gifDrawHeight) / 2;
    }

    public void moveRight() {
        if(this.x+ this.width< Main.WINDOW_WIDTH) {
            this.x += 5;
        }
        this.currentImage = this.rightImage;
    }

    public void moveLeft() {
        if(this.x>0) {
            this.x -= 5;
        }
        this.currentImage = this.leftImage;
    }

    public void moveDown() {
        if (this.y+ this.height < Main.WINDOW_HEIGHT-40){
            this.y += 5;
        }
        this.currentImage = this.downImage;
    }

    public void moveUp() {
        if(this.y>0){
            this.y -= 5;
        }
        this.currentImage = this.upImage;
    }

    // פונקציה חדשה שקוראת את ה-GIF ומחלצת ממנו את הפריימים אוטומטית למערך
    private void loadGifFrames(String path) {
        ArrayList<BufferedImage> framesList = new ArrayList<>();
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                // כלים של Java שנועדו לקרוא קבצי תמונה מורכבים כמו GIF
                ImageInputStream stream = ImageIO.createImageInputStream(is);
                Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");

                if (readers.hasNext()) {
                    ImageReader reader = readers.next();
                    reader.setInput(stream);

                    // בודקים כמה פריימים יש ב-GIF וקוראים אותם אחד-אחד
                    int count = reader.getNumImages(true);
                    for (int i = 0; i < count; i++) {
                        framesList.add(reader.read(i));
                    }
                }
            } else {
                System.out.println("לא מצאתי את הקובץ: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ממירים את הרשימה חזרה למערך מסודר כדי שנוכל לצייר אותו
        this.frames = framesList.toArray(new BufferedImage[0]);
    }


    private void updateAnimation() {
        // אם ה-GIF מכיל רק תמונה אחת או לא נטען, אין טעם לקדם אנימציה
        if (this.frames == null || this.frames.length <= 1) return;

        this.animationCounter++;

        // אם המונה הגיע למהירות שהגדרנו, מתקדמים לפריים הבא
        if (this.animationCounter >= this.animationSpeed) {
            this.animationCounter = 0;
            this.currentFrameIndex++;

            // חוזרים להתחלה אם סיימנו את כל הפריימים
            if (this.currentFrameIndex >= this.frames.length) {
                this.currentFrameIndex = 0;
            }
        }
    }
    // פונקציה שתאפשר לך לשנות את הגודל של השחקן וזה יעדכן גם את הגיף
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        updateGifDimensions();
    }

    public void paint(Graphics graphics) {
        if (isMoving) {
            if (this.currentImage != null) {
                graphics.drawImage(this.currentImage, this.x, this.y, this.width, this.height, null);
            }
        } else {

            // שלב 1: מעדכנים את האנימציה במידת הצורך
            updateAnimation();
            // שלב 2: מציירים את הפריים הנוכחי
            if (this.frames != null && this.frames.length > 0) {
                graphics.drawImage(this.frames[currentFrameIndex], this.x+this.gifOffsetX, this.y+this.gifOffsetY, this.gifDrawWidth, this.gifDrawHeight, null);
            }
        }
    }
}