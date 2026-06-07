package org.example;

import javax.swing.*;
import java.awt.*;

// מחלקה שמציגה את מסך בחירת השלבים
// המחלקה יורשת מ BackgroundPanel ולכן יש לה תמונת רקע
public class LevelMapPanel extends BackgroundPanel {

    // רוחב כפתור שלב
    private static final int LEVEL_BUTTON_WIDTH = 80;

    // גובה כפתור שלב
    private static final int LEVEL_BUTTON_HEIGHT = 65;

    // רווח אופקי בין כפתורי השלבים
    private static final int LEVEL_BUTTON_GAP_X = 25;

    // רווח אנכי בין שורות כפתורי השלבים
    private static final int LEVEL_BUTTON_GAP_Y = 22;

    // מספר כפתורים בכל שורה
    private static final int COLUMNS = 5;

    // מיקום התחלתי בציר Y של כפתורי השלבים
    private static final int START_Y = 300;

    // שמירת חלון התפריט הראשי כדי לסגור אותו כשפותחים משחק
    private MainMenu mainMenu;

    // בנאי שמקבל את גודל המסך, פאנל התפריט הראשי ואת חלון התפריט הראשי
    public LevelMapPanel(int width, int height, BackgroundPanel menuPanel, MainMenu mainMenu) {

        // קריאה לבנאי של BackgroundPanel עם תמונת הרקע של מסך השלבים
        super("/LevelsBackground.png");

        // שמירת התפריט הראשי במשתנה של המחלקה
        this.mainMenu = mainMenu;

        // קביעת מיקום וגודל הפאנל בתוך החלון
        this.setBounds(0, 0, width, height);

        // ביטול מנהל פריסה, כדי למקם רכיבים ידנית בעזרת setBounds
        this.setLayout(null);

        // יצירת כפתור חזרה לתפריט הראשי
        RoundedButton backButton = RoundedButton.createPanelBackButton(width, mainMenu, menuPanel);

        // הוספת כפתור החזרה לפאנל
        this.add(backButton);

        // יצירת כפתור יציאה מהמשחק
        RoundedButton exitButton = RoundedButton.createExitButton(width);

        // הוספת כפתור היציאה לפאנל
        this.add(exitButton);

        // יצירת כל כפתורי השלבים במסך
        createLevelButtons(width);
    }

    // יוצרת את כפתורי השלבים וממקמת אותם במבנה של שורות ועמודות
    private void createLevelButtons(int screenWidth) {

        // חישוב הרוחב הכולל של שורת כפתורים
        // כולל רוחב הכפתורים והרווחים ביניהם
        int totalWidth =
                COLUMNS * LEVEL_BUTTON_WIDTH +
                        (COLUMNS - 1) * LEVEL_BUTTON_GAP_X;

        // חישוב נקודת ההתחלה בציר X כדי למרכז את הכפתורים במסך
        int startX = (screenWidth - totalWidth) / 2;

        // מעבר על כל השלבים במשחק
        for (int level = 1; level <= GameSettings.MAX_LEVELS; level++) {

            // המרה של מספר השלב לאינדקס שמתחיל מ 0
            int index = level - 1;

            // חישוב מספר השורה של הכפתור
            int row = index / COLUMNS;

            // חישוב מספר העמודה של הכפתור
            int col = index % COLUMNS;

            // חישוב מיקום X לפי העמודה
            int x = startX + col * (LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_GAP_X);

            // חישוב מיקום Y לפי השורה
            int y = START_Y + row * (LEVEL_BUTTON_HEIGHT + LEVEL_BUTTON_GAP_Y);

            // בדיקה אם השלב פתוח לשחקן
            boolean unlocked = GameProgress.isLevelUnlocked(level);

            // יצירת כפתור שלב לפי מספר השלב והאם הוא פתוח
            LevelButton levelButton = new LevelButton(level, unlocked);

            // קביעת מיקום וגודל הכפתור במסך
            levelButton.setBounds(x, y, LEVEL_BUTTON_WIDTH, LEVEL_BUTTON_HEIGHT);

            // אם השלב פתוח, מוסיפים לו פעולה בלחיצה
            if (unlocked) {

                // שמירת מספר השלב במשתנה מקומי כדי להשתמש בו בתוך ActionListener
                int selectedLevel = level;

                // בלחיצה על הכפתור, נפתח המשחק בשלב שנבחר
                levelButton.addActionListener(e -> {
                    openGame(selectedLevel);
                });
            }

            // הוספת כפתור השלב לפאנל
            this.add(levelButton);
        }
    }

    // פותחת חלון משחק חדש לפי השלב שנבחר
    private void openGame(int selectedLevel) {

        // סוגרת את חלון התפריט הראשי
        mainMenu.dispose();

        // יצירת חלון חדש למשחק
        JFrame window = new JFrame("Sugar Rush");

        // קביעת גודל החלון לפי הקבועים של Main
        window.setSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

        // הסרת המסגרת הרגילה של החלון
        window.setUndecorated(true);

        // מניעת שינוי גודל החלון
        window.setResizable(false);

        // מיקום החלון במרכז המסך
        window.setLocationRelativeTo(null);

        // ביטול מנהל פריסה כדי למקם את פאנל המשחק ידנית
        window.setLayout(null);

        // סגירת התוכנית כאשר סוגרים את חלון המשחק
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // הוספת פאנל המשחק לחלון
        // selectedLevel מועבר כדי לדעת איזה שלב להפעיל
        window.add(
                new MainScenePanel(
                        0,
                        0,
                        Main.WINDOW_WIDTH,
                        Main.WINDOW_HEIGHT,
                        selectedLevel
                )
        );

        // הצגת חלון המשחק
        window.setVisible(true);
    }
}