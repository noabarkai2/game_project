package org.example;

import java.awt.*;
import java.util.Random;

public class Enemy {
    public int x;
    public int y;
    public int width;
    public int height;
    public int direction;

    public static final int RIGHT = 1;
    public static final int LEFT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;

    public Image currentImage;
    public Image backImage;
    public Image frontImage;
    public Image rightImage;
    public Image leftImage;

    public boolean isMoving = false;
    public Random random = new Random();

    public Enemy(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = DOWN;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setIsMoving(boolean moving) {
        this.isMoving = moving;
    }

    public void move() {
        if (!isMoving) {
            return;
        }
        switch (this.direction) {
            case RIGHT:
                if (this.x + this.width < Main.WINDOW_WIDTH) {
                    this.currentImage = rightImage;
                    this.x += 2;
                }
                break;
            case LEFT:
                if (this.x > 0) {
                    this.currentImage = leftImage;
                    this.x -= 2;
                }
                break;
            case UP:
                if (this.y > 0) {
                    this.currentImage = backImage;
                    this.y -= 2;
                }
                break;
            case DOWN:
                if (this.y + this.height < Main.WINDOW_HEIGHT - 40) {
                    this.currentImage = frontImage;
                    this.y += 2;
                }
                break;
        }

        if (random.nextInt(100) == 0) {
            this.direction = random.nextInt(1, 5);
        }
    }

    public void addSpeed(int speed) {
        if (isMoving) {
            switch (this.direction) {
                case RIGHT:
                    this.x += speed;
                    break;
                case LEFT:
                    this.x -= speed;
                    break;
                case UP:
                    this.y -= speed;
                    break;
                case DOWN:
                    this.y += speed;
                    break;
            }
            if (random.nextInt(150) == 0) {
                this.direction = random.nextInt(1, 5);
            }
        }
    }

    // פעולה שהופכת את כיוון התנועה ב-180 מעלות
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

    public void paint(Graphics graphics) {
        if (this.currentImage != null) {
            graphics.drawImage(this.currentImage, this.x, this.y, this.width, this.height, null);
        }
    }
}