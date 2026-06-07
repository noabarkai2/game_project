package org.example;

import javax.swing.*;
import java.awt.*;

// מחלקה שמייצגת כפתור של שלב במסך בחירת שלבים
// הכפתור מצייר את עצמו לבד לפי מצב השלב, פתוח או נעול
public class LevelButton extends JButton {

    // מספר השלב שהכפתור מייצג
    private int level;

    // האם השלב פתוח לשחקן או נעול
    private boolean unlocked;

    // בנאי שמקבל מספר שלב ומצב פתיחה
    public LevelButton(int level, boolean unlocked) {

        // קריאה לבנאי של JButton עם טקסט ריק
        // הטקסט מצויר ידנית בהמשך דרך paintComponent
        super("");

        // שמירת מספר השלב
        this.level = level;

        // שמירת מצב הפתיחה של השלב
        this.unlocked = unlocked;

        // מבטל ציור רקע רגיל של JButton
        // כי הכפתור מצויר ידנית בעיצוב מותאם
        setContentAreaFilled(false);

        // מבטל ציור מסגרת רגילה של JButton
        setBorderPainted(false);

        // מבטל סימון פוקוס רגיל סביב הכפתור
        setFocusPainted(false);

        // הכפתור לא יצייר רקע רגיל של Java
        setOpaque(false); //הכפתור לא יצייר רקע רגיל של Java

        // אם השלב פתוח, סמן העכבר נהיה יד
        if (unlocked) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // אם השלב נעול, סמן העכבר נשאר רגיל
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    // הפונקציה מציירת את הכפתור, Graphics2D נותן ציור חלק ומתקדם יותר
    @Override
    protected void paintComponent(Graphics graphics) {

        // המרה מ Graphics רגיל ל Graphics2D בשביל ציור מתקדם יותר
        Graphics2D g2 = (Graphics2D) graphics;

        // הפעלת החלקת קצוות כדי שהכפתור והציורים ייראו חלקים יותר
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // אם השלב פתוח, צבע הכפתור ורוד
        if (unlocked) {
            g2.setColor(new Color(255, 180, 193));

            // אם השלב נעול, צבע הכפתור חום
        } else {
            g2.setColor(new Color(160, 125, 65));
        }

        // ציור הרקע של הכפתור כמלבן עם פינות מעוגלות
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

        // קביעת צבע המסגרת של הכפתור
        g2.setColor(new Color(120, 80, 35));

        // קביעת עובי המסגרת
        g2.setStroke(new BasicStroke(3));

        // ציור מסגרת מעוגלת לכפתור
        g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 25, 25);

        // אם השלב פתוח, מציירים את מספר השלב
        if (unlocked) {
            drawLevelNumber(g2);

            // אם השלב נעול, מציירים מנעול
        } else {
            drawLock(g2);
        }
    }

    // מציירת את מספר השלב במרכז הכפתור
    private void drawLevelNumber(Graphics2D g2) {

        // המרת מספר השלב לטקסט
        String text = String.valueOf(level);

        // קביעת פונט למספר השלב
        g2.setFont(new Font("Arial", Font.BOLD, 28));

        // FontMetrics עוזר למדוד את גודל הטקסט
        // בעזרתו מחשבים מיקום מדויק למרכז הכפתור
        FontMetrics fm = g2.getFontMetrics();

        // חישוב מיקום X כך שהטקסט יהיה במרכז לרוחב
        int textX = (getWidth() - fm.stringWidth(text)) / 2;

        // חישוב מיקום Y כך שהטקסט יהיה במרכז לגובה
        int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

        // ציור צל שחור מאחורי המספר
        g2.setColor(Color.BLACK);
        g2.drawString(text, textX + 2, textY + 2);

        // ציור המספר עצמו בלבן מעל הצל
        g2.setColor(Color.WHITE);
        g2.drawString(text, textX, textY);
    }

    // מציירת אייקון של מנעול במרכז הכפתור
    private void drawLock(Graphics2D g2) {

        // רוחב גוף המנעול
        int lockWidth = 28;

        // גובה גוף המנעול
        int lockHeight = 24;

        // חישוב מיקום X כדי למרכז את המנעול
        int lockX = (getWidth() - lockWidth) / 2;

        // חישוב מיקום Y כדי למרכז את המנעול
        int lockY = (getHeight() - lockHeight) / 2 + 7;

        // קביעת עובי הקווים של המנעול
        g2.setStroke(new BasicStroke(5));

        // קביעת צבע לבן למנעול
        g2.setColor(Color.WHITE);

        //מצייר את הקשת של המנעול
        g2.drawArc(
                lockX + 5,
                lockY - 18,
                lockWidth - 10,
                28,
                0,
                180
        );

        //מצייר את הריבוע של המנעול
        g2.fillRoundRect(
                lockX,
                lockY,
                lockWidth,
                lockHeight,
                6,
                6
        );

        // קביעת צבע כהה לחור של המנעול
        g2.setColor(new Color(90, 60, 30));

        // את החור של המנעול
        g2.fillOval(lockX + 11, lockY + 8, 6, 6);

        // ציור החלק התחתון של חור המנעול
        g2.fillRect(lockX + 13, lockY + 13, 2, 7);
    }
}