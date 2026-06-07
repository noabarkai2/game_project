package org.example;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// מחלקה שמאזינה ללחיצות מקלדת ומזיזה את השחקן לפי החצים
// המחלקה בודקת גם אם השחקן פגע בעוגה, ואז מחזירה אותו למיקום הקודם
public class MovementListener implements KeyListener {

    // השחקן שאותו מזיזים
    private Player player;

    // פאנל המשחק, דרכו בודקים אם יש התנגשות עם עוגות
    private MainScenePanel panel;

    // בנאי שמקבל את פאנל המשחק ואת השחקן
    public MovementListener(MainScenePanel panel, Player player) {

        // שמירת פאנל המשחק
        this.panel = panel;

        // שמירת השחקן
        this.player = player;
    }

    // מופעלת כאשר מקלידים תו רגיל מהמקלדת
    // כאן אין צורך בפעולה, לכן הפונקציה ריקה
    public void keyTyped(KeyEvent e) {
    }

    // מופעלת כאשר לוחצים על מקש
    public void keyPressed(KeyEvent e) {

        // אם המשחק בעצירה, לא מזיזים את השחקן
        if (this.panel.isPaused()) {
            return;
        }

        //  שומרים את המיקום הישן של השחקן לפני התזוזה
        int oldX = this.player.getX();
        int oldY = this.player.getY();

        // אם נלחץ אחד ממקשי החצים, מסמנים שהשחקן נמצא בתנועה
        if (e.getKeyCode() == KeyEvent.VK_RIGHT ||
                e.getKeyCode() == KeyEvent.VK_LEFT||
                e.getKeyCode() == KeyEvent.VK_DOWN||
                e.getKeyCode() == KeyEvent.VK_UP) {
            this.player.setIsMoving(true);
        }

        //  מזיזים את השחקן לפי החץ שנלחץ
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.player.moveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            this.player.moveLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.player.moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            this.player.moveUp();
        }

        // שואלים את הלוח אם השחקן נגע עכשיו בעוגה
        if (this.panel.checkCakeCollision()) {

            // אם כן מחזירים אותו מיד למיקום הישן
            this.player.setX(oldX);
            this.player.setY(oldY);
        }
    }

    // מופעלת כאשר משחררים מקש
    public void keyReleased(KeyEvent e) {

        // אם המשחק בעצירה, לא מבצעים פעולה
        if (this.panel.isPaused()) {
            return;
        }

        // אם שוחרר אחד ממקשי החצים, מסמנים שהשחקן הפסיק לנוע
        if (e.getKeyCode() == KeyEvent.VK_RIGHT ||
                e.getKeyCode() == KeyEvent.VK_LEFT ||
                e.getKeyCode() == KeyEvent.VK_DOWN ||
                e.getKeyCode() == KeyEvent.VK_UP) {
            this.player.setIsMoving(false);
        }
    }
}