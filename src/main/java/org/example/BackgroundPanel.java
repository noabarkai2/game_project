package org.example;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BackgroundPanel extends JPanel {
    private static final String BACKGROUND_IMAGE_PATH = "/background_menu.jpeg";
    private Image backgroundImage;

    public BackgroundPanel() {
        this.setLayout(null);
        this.backgroundImage = loadImage(BACKGROUND_IMAGE_PATH);
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (this.backgroundImage != null) {
            graphics.drawImage(this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    private Image loadImage(String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource != null) {
                return new ImageIcon(resource).getImage();
            } else {
                System.out.println("לא מצאתי את הקובץ: " + path);
            }
        } catch (Exception e) {
            System.out.println("שגיאה בטעינת התמונה: " + path);
        }
        return null;
    }
}