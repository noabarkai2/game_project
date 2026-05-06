package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;

public class EnemyBroccoli extends Enemy {

    public EnemyBroccoli(int x, int y, int width, int height) {
        super(x, y, width, height);

        this.frontImage = loadImage("/Broccoli_Front.png");
        this.backImage = loadImage("/Broccoli_Back.png");
        this.rightImage = loadImage("/Broccoli_Right.png");
        this.leftImage = loadImage("/Broccoli_Left.png");

        this.currentImage = this.rightImage;
        // חשוב: מגדירים כיוון התחלתי ימינה או שמאלה
        this.direction = RIGHT;
    }

    private Image loadImage(String imagePath) {
        try {
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                return ImageIO.read(imageStream);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void move() {
        if (!isMoving) return;

        boolean hitBoundary = false;

        // בודקים רק תנועה ימינה ושמאלה
        if (this.direction == RIGHT) {
            if (this.x + this.width < Main.WINDOW_WIDTH) {
                this.currentImage = rightImage;
                this.x += 2;
            } else {
                hitBoundary = true;
            }
        } else if (this.direction == LEFT) {
            if (this.x > 0) {
                this.currentImage = leftImage;
                this.x -= 2;
            } else {
                hitBoundary = true;
            }
        } else {
            // הגנת כשל: אם בטעות הוא קיבל כיוון למעלה/למטה, נכריח אותו לזוז ימינה
            this.direction = RIGHT;
        }

        // אם נתקענו בקיר, הופכים כיוון (ימינה לשמאלה, ושמאלה לימינה)
        // אם פגענו בקיר או שההגרלה יצאה 0
        if (hitBoundary || random.nextInt(150) == 0) {
            if (this.direction == RIGHT) {
                this.direction = LEFT; // אם זזנו ימינה, נשנה לשמאל
            } else {
                this.direction = RIGHT; // אחרת (זזנו שמאלה), נשנה לימין
            }
        }
    }
}