package org.example;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

// מחלקה שמייצגת פרס או סוכריה במשחק
// הפרס יודע לשמור מיקום, גודל, תמונה, ניקוד והאם נאסף
public class Prize {

    // מיקום הפרס בציר X
    private int x;

    // מיקום הפרס בציר Y
    private int y;

    // רוחב הפרס
    private int width;

    // גובה הפרס
    private int height;

    // תמונת הפרס
    private Image image;

    // האם הפרס נאסף כבר על ידי השחקן
    private boolean isCollected;

    // כמות הנקודות שהפרס נותן
    private int points;

    // בנאי שמקבל מיקום, גודל ותמונה
    // אם לא נשלח ניקוד, הפרס שווה 10 נקודות
    public Prize(int x, int y, int width, int height, String imagePath) {
        this(x, y, width, height, imagePath, 10);
    }

    // בנאי שמקבל מיקום, גודל, תמונה וניקוד
    public Prize(int x, int y, int width, int height, String imagePath, int points) {

        // שמירת מיקום וגודל הפרס
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // שמירת כמות הנקודות של הפרס
        this.points = points;

        // בתחילת המשחק הפרס עדיין לא נאסף
        this.isCollected = false;

        // חיפוש קובץ התמונה בתוך resources
        URL resource = getClass().getResource(imagePath);

        // אם התמונה נמצאה, שומרים אותה במשתנה image
        if (resource != null) {
            this.image = new ImageIcon(resource).getImage();

            // אם התמונה לא נמצאה, מדפיסים הודעה לקונסול
        } else {
            System.out.println("לא מצאתי את קובץ התמונה של הפרס: " + imagePath);
        }
    }

    // מציירת את הפרס על המסך
    public void draw(Graphics g) {

        // מציירים את הפרס בתנאי שהוא עדיין לא נאסף ויש לו תמונה
        if (!isCollected && image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }

    // מחזירה מלבן פגיעה של הפרס
    // המלבן משמש לבדיקת התנגשות עם השחקן
    public Rectangle getBounds() {

        // אם הפרס שווה 20 נקודות, נותנים לו אזור פגיעה מיוחד וצר יותר
        if (points == 20) {
            int hitWidth = width / 3;
            int hitHeight = (int) (height * 0.70);

            // ממרכזים את אזור הפגיעה בתוך הפרס
            int hitX = x + (width - hitWidth) / 2;
            int hitY = y + 4;

            // מחזירים מלבן פגיעה מותאם לפרס המיוחד
            return new Rectangle(
                    hitX,
                    hitY,
                    hitWidth,
                    hitHeight
            );
        }

        // הקטנת אזור הפגיעה הרגיל בציר X
        int trimX = 3;

        // הקטנת אזור הפגיעה הרגיל בציר Y
        int trimY = 5;

        // מחזירים מלבן פגיעה מעט קטן מגודל התמונה
        return new Rectangle(
                x + trimX,
                y + trimY,
                width - (2 * trimX),
                height - (2 * trimY)
        );
    }

    // מחזירה את מספר הנקודות שהפרס נותן
    public int getPoints() {
        return this.points;
    }

    // מחזירה האם הפרס נאסף
    public boolean isCollected() {
        return isCollected;
    }

    // מעדכנת האם הפרס נאסף
    public void setCollected(boolean collected) {
        this.isCollected = collected;
    }
}