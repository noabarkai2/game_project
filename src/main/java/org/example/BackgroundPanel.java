package org.example;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BackgroundPanel extends JPanel {
    private static final String CUPCAKE_IMAGE_PATH = "/Front_no background.png";
    private static final int CUPCAKE_SIZE = 120;
    private static final int SPACING = 120;
    private static final int MOVEMENT_SPEED = 2;
    private static final Color BACHGROUND_COLOR = new Color(255, 248, 240);
    private Image cupcakeImage;
    private int offset;
    private boolean isRunning;

    public BackgroundPanel() {
        this.offset = 0;
        this.isRunning = true;
        this.setLayout(null);
        this.setBackground(BACHGROUND_COLOR);
        this.cupcakeImage = loadImage(CUPCAKE_IMAGE_PATH);
        startAnimationThread();
    }

    private void startAnimationThread() {
        new Thread(() -> {
            while (isRunning) {
                this.offset += MOVEMENT_SPEED;
                repaint();
                Utils.sleep(16);
            }
        }).start();
    }

    public void stopAnimation() {
        this.isRunning = false;
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (cupcakeImage != null) {
            int panelWidth = this.getWidth();
            int panelHeight = this.getHeight();
            int padding = 10;
            int trackWidth = panelWidth - CUPCAKE_SIZE - (2 * padding);//כדי שלא יראו רק חלק מהקאפקייקס והוא יחתך (כדי שהקצה הימני של הקאפקייקס יגיע לקצה המסלול ולא הקצה השמאלי- התהתחלה)
            int trackHeight = panelHeight - CUPCAKE_SIZE - (2 * padding);
            int perimeter = (2 * trackWidth) + (2 * trackHeight);
            int distanceOnEdge;
            if (perimeter > 0) {
                int amountOfCupcakes = perimeter / SPACING;//
                double exactSpacing = (double) perimeter / amountOfCupcakes;
                for (int i = 0; i < amountOfCupcakes; i++) {
                    int currentDistance = (int) ((i * exactSpacing) + this.offset) % perimeter;
                    int x = 0;
                    int y = 0;
                    if (currentDistance < trackWidth) {
                        x = padding + currentDistance;
                        y = padding;
                    } else if (currentDistance < trackWidth + trackHeight) {
                        distanceOnEdge = currentDistance - trackWidth;
                        x = padding + trackWidth;
                        y = padding + distanceOnEdge;
                    } else if (currentDistance < (2 * trackWidth) + trackHeight) {
                        distanceOnEdge = currentDistance - (trackWidth + trackHeight);
                        x = padding + trackWidth - distanceOnEdge;
                        y = padding + trackHeight;
                    } else {
                        distanceOnEdge = currentDistance - ((2 * trackWidth) + trackHeight);
                        x = padding;
                        y = padding + trackHeight - distanceOnEdge;
                    }
                    graphics.drawImage(cupcakeImage, x, y, CUPCAKE_SIZE, CUPCAKE_SIZE, this);
                }
            }
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