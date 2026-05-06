package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;

public class EnemyEggplant extends Enemy {

    public EnemyEggplant(int x, int y, int width, int height) {
        super(x, y, width, height);

        this.frontImage = loadImage("/Eggplant_Front.png");
        this.backImage = loadImage("/Eggplant_Back.png");
        this.rightImage = loadImage("/Eggplant_Right.png");
        this.leftImage = loadImage("/Eggplant_Left.png");

        this.currentImage = this.frontImage;

        // חשוב: מגדירים כיוון התחלתי למעלה או למטה
        this.direction = DOWN;
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
        // בודקים רק תנועה למעלה ולמטה
        if (this.direction == DOWN) {
            if (this.y + this.height < Main.WINDOW_HEIGHT - 40) {
                this.currentImage = frontImage;
                this.y += 2;
            } else {
                hitBoundary = true;
            }
        } else if (this.direction == UP) {
            if (this.y > 0) {
                this.currentImage = backImage;
                this.y -= 2;
            } else {
                hitBoundary = true;
            }
        } else {
            // הגנת כשל: אם בטעות הוא קיבל כיוון ימינה/שמאלה
            this.direction = DOWN;
        }

        // אם נתקענו בתקרה או ברצפה, הופכים כיוון
        if (hitBoundary || random.nextInt(150) == 0) {
            if (this.direction == DOWN) {
                this.direction = UP;
            } else {
                this.direction = DOWN;
            }
        }
    }
}