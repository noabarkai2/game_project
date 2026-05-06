package org.example;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private int radius;

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    public void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getModel().isArmed()) {
            g2.setColor(getBackground().darker());
        } else {
            g2.setColor(getBackground());
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), this.radius, this.radius);
        super.paintComponent(graphics);
    }
}