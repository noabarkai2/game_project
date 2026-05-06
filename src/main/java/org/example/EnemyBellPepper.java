package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;

public class EnemyBellPepper extends Enemy {

    // משתנה שישמור את השחקן כדי שנוכל לעקוב אחריו
    private Player targetPlayer;

    // הוספנו את השחקן לבנאי כדי שהגמבה תכיר אותו ברגע יצירתה
    public EnemyBellPepper(int x, int y, int width, int height, Player player) {
        super(x, y, width, height);
        this.targetPlayer = player;

        this.frontImage = loadImage("/BellPepper_Front.png");
        this.backImage = loadImage("/BellPepper_Back.png");
        this.rightImage = loadImage("/BellPepper_Right.png");
        this.leftImage = loadImage("/BellPepper_Left.png");

        this.currentImage = this.frontImage;
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
        // אם היא לא אמורה לזוז, או ששכחנו להעביר לה שחקן - אל תעשה כלום
        if (!isMoving || targetPlayer == null) return;
        // חישוב ההפרש בין מיקום הגמבה למיקום השחקן
        int diffX = targetPlayer.getX() - this.x;
        int diffY = targetPlayer.getY() - this.y;

        // שימוש בערך מוחלט (Math.abs) כדי לדעת איזה מרחק גדול יותר
        // אם המרחק האופקי (X) גדול יותר - נזוז ימינה או שמאלה
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (diffX > 0) {
                this.direction = RIGHT;
                this.currentImage = rightImage;
                this.x += 2; // מהירות התנועה
            } else if (diffX < 0) {
                this.direction = LEFT;
                this.currentImage = leftImage;
                this.x -= 2;
            }
        }
        // אם המרחק האנכי (Y) גדול יותר (או שווה) - נזוז למעלה או למטה
        else {
            if (diffY > 0) {
                this.direction = DOWN;
                this.currentImage = frontImage;
                this.y += 2;
            } else if (diffY < 0) {
                this.direction = UP;
                this.currentImage = backImage;
                this.y -= 2;
            }
        }
    }
}