package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;

public class EnemyCorn extends Enemy {

    public EnemyCorn(int x, int y, int width, int height) {
        super(x, y, width, height);

        this.frontImage = loadImage("/Corn_Front.png");
        this.backImage = loadImage("/Corn_Back.png");
        this.rightImage = loadImage("/Corn_Right.png");
        this.leftImage = loadImage("/Corn_Left.png");

        this.currentImage = this.frontImage;
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

}