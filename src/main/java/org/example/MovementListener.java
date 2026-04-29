package org.example;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class MovementListener implements KeyListener {
    private Player player;

    public MovementListener(Player player) {
        this.player = player;
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT ||
                e.getKeyCode() == KeyEvent.VK_LEFT||
                e.getKeyCode() == KeyEvent.VK_DOWN||
                e.getKeyCode() == KeyEvent.VK_UP) {
            this.player.setIsMoving(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.player.moveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            this.player.moveLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.player.moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            this.player.moveUp();
        }


    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT ||
                e.getKeyCode() == KeyEvent.VK_LEFT||
                e.getKeyCode() == KeyEvent.VK_DOWN||
                e.getKeyCode() == KeyEvent.VK_UP) {
            this.player.setIsMoving(false);
        }
    }
}