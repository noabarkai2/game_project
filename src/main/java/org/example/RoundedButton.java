package org.example;

import javax.swing.*;
import java.awt.*;

// מחלקה לכפתור מעוגל בעיצוב מותאם
// יורשת מ JButton, אבל מציירת את הרקע בעצמה עם פינות מעוגלות
public class RoundedButton extends JButton {

    // רדיוס הפינות המעוגלות של הכפתור
    private int radius;

    // רוחב הכפתורים העליונים, יציאה וחזרה
    private static final int TOP_BUTTON_WIDTH = 50;

    // גובה הכפתורים העליונים
    private static final int TOP_BUTTON_HEIGHT = 38;

    // מיקום Y של הכפתורים העליונים
    private static final int TOP_BUTTON_Y = 12;

    // המרחק של כפתור היציאה מהצד הימני של המסך
    private static final int EXIT_RIGHT_MARGIN = 30;

    // רווח בין כפתור החזרה לכפתור היציאה
    private static final int BUTTON_GAP = 5;

    // בנאי שמקבל טקסט ורדיוס לפינות הכפתור
    public RoundedButton(String text, int radius) {

        // קריאה לבנאי של JButton עם הטקסט שיופיע על הכפתור
        super(text);

        // שמירת רדיוס הפינות
        this.radius = radius;

        // מבטל ציור רקע רגיל של JButton
        setContentAreaFilled(false);

        // מבטל סימון פוקוס רגיל סביב הכפתור
        setFocusPainted(false);

        // מבטל מסגרת רגילה של JButton
        setBorderPainted(false);

        // הכפתור לא מצייר רקע אטום רגיל
        setOpaque(false);
    }

    // מחשבת את מיקום X של כפתור היציאה לפי רוחב המסך
    private static int getExitButtonX(int screenWidth) {
        return screenWidth - EXIT_RIGHT_MARGIN - TOP_BUTTON_WIDTH;
    }

    // מחשבת את מיקום X של כפתור החזרה לפי מיקום כפתור היציאה
    private static int getBackButtonX(int screenWidth) {
        return getExitButtonX(screenWidth) - BUTTON_GAP - TOP_BUTTON_WIDTH;
    }

    // יוצרת כפתור יציאה קבוע למסכים שונים במשחק
    public static RoundedButton createExitButton(int screenWidth) {

        // יצירת כפתור מעוגל עם סימן X
        RoundedButton exitButton = new RoundedButton("✕", 25);

        // קביעת מיקום וגודל הכפתור
        exitButton.setBounds(
                getExitButtonX(screenWidth),
                TOP_BUTTON_Y,
                TOP_BUTTON_WIDTH,
                TOP_BUTTON_HEIGHT
        );

        // עיצוב צבעים, פונט וסמן עכבר
        exitButton.setBackground(new Color(255, 95, 110));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // מאזין לעכבר, משנה צבע כאשר העכבר מעל הכפתור
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                exitButton.setBackground(new Color(230, 65, 80));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                exitButton.setBackground(new Color(255, 95, 110));
            }
        });

        // בלחיצה על הכפתור, סוגרים את התוכנית
        exitButton.addActionListener(e -> System.exit(0));

        // החזרת הכפתור המוכן
        return exitButton;
    }

    // יוצרת כפתור חזרה מתוך מסך המשחק אל התפריט הראשי
    public static RoundedButton createBackButton(int screenWidth, JPanel panel) {

        // יצירת כפתור מעוגל עם סימן חזרה
        RoundedButton backButton = new RoundedButton("↩", 25);

        // קביעת מיקום וגודל הכפתור
        backButton.setBounds(
                getBackButtonX(screenWidth),
                TOP_BUTTON_Y,
                TOP_BUTTON_WIDTH,
                TOP_BUTTON_HEIGHT
        );

        // עיצוב צבעים, פונט וסמן עכבר
        backButton.setBackground(new Color(91, 137, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // מאזין לעכבר, משנה צבע כאשר העכבר מעל הכפתור
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backButton.setBackground(new Color(70, 115, 145));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backButton.setBackground(new Color(91, 137, 166));
            }
        });

        // פעולה בזמן לחיצה על כפתור חזרה
        backButton.addActionListener(e -> {

            // אם הפאנל הוא מסך משחק, עוצרים את לולאת המשחק והצלילים
            if (panel instanceof MainScenePanel) {
                ((MainScenePanel) panel).stopGame();
            }

            // קבלת החלון שמכיל את הפאנל הנוכחי
            Window window = SwingUtilities.getWindowAncestor(panel);

            // סגירת החלון הנוכחי אם נמצא חלון
            if (window != null) {
                window.dispose();
            }

            // פתיחת תפריט ראשי חדש
            new MainMenu();
        });

        // החזרת הכפתור המוכן
        return backButton;
    }

    // יוצרת כפתור חזרה בין פאנלים בתוך אותו חלון
    public static RoundedButton createPanelBackButton(
            int screenWidth,
            JFrame frame,
            JPanel targetPanel
    ) {
        // יצירת כפתור מעוגל עם סימן חזרה
        RoundedButton backButton = new RoundedButton("↩", 25);

        // קביעת מיקום וגודל הכפתור
        backButton.setBounds(
                getBackButtonX(screenWidth),
                TOP_BUTTON_Y,
                TOP_BUTTON_WIDTH,
                TOP_BUTTON_HEIGHT
        );

        // עיצוב צבעים, פונט וסמן עכבר
        backButton.setBackground(new Color(91, 137, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // מאזין לעכבר, משנה צבע כאשר העכבר מעל הכפתור
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backButton.setBackground(new Color(70, 115, 145));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backButton.setBackground(new Color(91, 137, 166));
            }
        });

        // בלחיצה מחליפים את תוכן החלון לפאנל היעד
        backButton.addActionListener(e -> {
            frame.setContentPane(targetPanel);
            frame.revalidate();
            frame.repaint();
        });

        // החזרת הכפתור המוכן
        return backButton;
    }

    // מציירת את הרקע המעוגל של הכפתור
    @Override
    public void paintComponent(Graphics graphics) {

        // המרה ל Graphics2D בשביל ציור מתקדם וחלק יותר
        Graphics2D g2 = (Graphics2D) graphics;

        // הפעלת החלקת קצוות כדי שהפינות המעוגלות ייראו חלקות
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // אם הכפתור לחוץ, מציירים צבע כהה יותר
        if (getModel().isArmed()) {
            g2.setColor(getBackground().darker());

            // אם הכפתור לא לחוץ, מציירים את צבע הרקע הרגיל
        } else {
            g2.setColor(getBackground());
        }

        // ציור מלבן מעוגל בגודל הכפתור
        g2.fillRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                this.radius,
                this.radius
        );

        // קורא לציור הרגיל של JButton כדי לצייר את הטקסט מעל הרקע
        super.paintComponent(graphics);
    }
}