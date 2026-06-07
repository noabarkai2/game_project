package org.example;

// מחלקה שמציגה את מסך ההוראות של המשחק
// המחלקה יורשת מ BackgroundPanel, לכן יש לה תמונת רקע
public class InstructionsPanel extends BackgroundPanel {

    // בנאי שמקבל את גודל החלון, את פאנל התפריט הראשי ואת אובייקט התפריט הראשי
    public InstructionsPanel(int width, int height, BackgroundPanel menuPanel, MainMenu mainMenu) {

        // קריאה לבנאי של BackgroundPanel עם תמונת הרקע של מסך ההוראות
        super("/background_instructions.png");

        // קובע את המיקום והגודל של פאנל ההוראות בתוך החלון
        this.setBounds(0, 0, width, height);

        // יוצר כפתור חזרה לתפריט הראשי
        // הכפתור יודע להחזיר את המשתמש מהמסך הנוכחי אל menuPanel
        RoundedButton backButton = RoundedButton.createPanelBackButton(width, mainMenu, menuPanel);

        // מוסיף את כפתור החזרה לפאנל ההוראות
        this.add(backButton);

        // יוצר כפתור יציאה מהמשחק
        RoundedButton exitButton = RoundedButton.createExitButton(width);

        // מוסיף את כפתור היציאה לפאנל ההוראות
        this.add(exitButton);
    }
}