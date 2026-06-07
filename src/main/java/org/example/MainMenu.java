package org.example;

import javax.swing.*;
import java.awt.*;

// מחלקה שמייצגת את חלון התפריט הראשי של המשחק
// המחלקה יורשת מ JFrame, לכן היא חלון בפני עצמו
public class MainMenu extends JFrame {

    // רוחב קבוע לכפתורי התפריט
    private static final int BUTTON_WIDTH = 300;

    // גובה קבוע לכפתורי התפריט
    private static final int BUTTON_HEIGHT = 70;

    // מיקום Y של כפתור Start
    private static final int START_BUTTON_Y = 200;

    // מיקום Y של כפתור ההוראות, נמצא מתחת לכפתור Start
    private static final int INSTRUCTIONS_BUTTON_Y = START_BUTTON_Y + 90;

    // גודל הפונט של כפתורי התפריט
    private static final int BUTTON_FONT_SIZE = 30;

    // צבע אחיד לכפתורי התפריט
    private static final Color BUTTON_COLOR = new Color(255, 180, 193);

    // הנתיב של קובץ המוזיקה מתוך resources
    private static final String MUSIC_PATH = "/The_Victory_Lap.wav";

    // בנאי שיוצר את חלון התפריט הראשי ומוסיף אליו את כל הכפתורים
    public MainMenu() {

        // קובע את גודל החלון לפי הקבועים שנמצאים במחלקת Main
        this.setSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

        // סוגר את התוכנית כאשר סוגרים את החלון
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // מונע שינוי גודל של החלון
        this.setResizable(false);

        // ממקם את החלון במרכז המסך
        this.setLocationRelativeTo(null);

        // מבטל את הפס העליון של החלון
        this.setUndecorated(true);//מבטל את הפס העליון של החלון

        // מפעיל את מוזיקת הרקע של המשחק
        Utils.initializeMusic(MUSIC_PATH);

        // יצירת פאנל רקע למסך התפריט
        BackgroundPanel backgroundPanel = new BackgroundPanel("/background_menu.jpeg");

        // קובע שהפאנל עם הרקע יהיה התוכן הראשי של החלון
        this.setContentPane(backgroundPanel); // במקום שבהוראות יעביר למסך חדש יעשה מעבר חלק למסך פתיחה

        // יצירת כפתור יציאה מהמשחק
        RoundedButton exitButton = RoundedButton.createExitButton(Main.WINDOW_WIDTH);

        // הוספת כפתור היציאה לחלון
        this.add(exitButton);

        // חישוב מיקום X כך שכפתורי התפריט יהיו במרכז המסך
        int buttonX = (Main.WINDOW_WIDTH - BUTTON_WIDTH) / 2; // קובע מיקום התחלתי ב-x באמצע של שני הכפתורים במסך פתיחה

        // יצירת כפתור התחלת המשחק
        RoundedButton startButton = new RoundedButton("Start", 40);

        // קביעת פונט לכפתור Start
        startButton.setFont(new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE));

        // קביעת צבע רקע לכפתור Start
        startButton.setBackground(BUTTON_COLOR);

        // קביעת צבע טקסט לכפתור Start
        startButton.setForeground(Color.WHITE);

        // ביטול סימון פוקוס רגיל סביב הכפתור
        startButton.setFocusPainted(false);

        // קביעת מיקום וגודל לכפתור Start
        startButton.setBounds(buttonX, START_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);

        //פותח שלבים
        startButton.addActionListener(e -> {

            // יצירת מסך בחירת שלבים
            LevelMapPanel levelMapPanel =
                    new LevelMapPanel(
                            Main.WINDOW_WIDTH,
                            Main.WINDOW_HEIGHT,
                            backgroundPanel,
                            this
                    );

            // החלפת מסך התפריט במסך בחירת השלבים
            this.setContentPane(levelMapPanel);

            // רענון סידור הרכיבים אחרי החלפת הפאנל
            this.revalidate();

            // ציור מחדש של החלון אחרי החלפת הפאנל
            this.repaint();
        });

        // הוספת כפתור Start לפאנל הרקע
        backgroundPanel.add(startButton);

        // יצירת כפתור למסך ההוראות
        RoundedButton instructionButton = new RoundedButton("How to play", 40);

        // קביעת פונט לכפתור ההוראות
        instructionButton.setFont(new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE));

        // קביעת צבע רקע לכפתור ההוראות
        instructionButton.setBackground(BUTTON_COLOR);

        // קביעת צבע טקסט לכפתור ההוראות
        instructionButton.setForeground(Color.WHITE);

        // ביטול סימון פוקוס רגיל סביב הכפתור
        instructionButton.setFocusPainted(false);

        // קביעת מיקום וגודל לכפתור ההוראות
        instructionButton.setBounds(buttonX, INSTRUCTIONS_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);

        // פעולה שמתרחשת בלחיצה על כפתור ההוראות
        instructionButton.addActionListener(e -> {

            // יצירת פאנל ההוראות
            InstructionsPanel instructionsPanel = new InstructionsPanel(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, backgroundPanel, this);

            //  מחליפים את התצוגה מפאנל התפריט לפאנל ההוראות
            this.setContentPane(instructionsPanel);

            // רענון סידור הרכיבים אחרי החלפת הפאנל
            this.revalidate();

            // ציור מחדש של החלון אחרי החלפת הפאנל
            this.repaint();
        });

        // הוספת כפתור ההוראות לפאנל הרקע
        backgroundPanel.add(instructionButton);

        // יצירת כפתור לשליטה בסאונד
        JButton soundButton = Utils.createSoundButton();

        // הוספת כפתור הסאונד לפאנל הרקע
        backgroundPanel.add(soundButton);

        // רענון סידור הרכיבים במסך
        this.revalidate();//רענון הסידור של המסך

        // ציור מחדש של המסך
        this.repaint();// 2 הפקודות הן רענון חדפ

        // הצגת החלון
        this.setVisible(true);
    }

    private void startGame(BackgroundPanel backgroundPanel) {

        this.dispose(); // סוגר את החלון של הבאקראונד
        JFrame window = new JFrame("Sugar Rush");
        window.setSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        window.setUndecorated(true);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setLayout(null);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new MainScenePanel(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT));
        window.setVisible(true);

    }
}