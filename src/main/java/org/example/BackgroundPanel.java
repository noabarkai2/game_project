package org.example;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

// מחלקה שיוצרת פאנל עם תמונת רקע
// המחלקה יורשת מ JPanel, לכן היא מתנהגת כמו פאנל רגיל ב Swing
public class BackgroundPanel extends JPanel {

    // משתנה ששומר את תמונת הרקע אחרי שהיא נטענת
    private Image backgroundImage;

    // בנאי שמקבל נתיב לתמונה
    // בזמן יצירת האובייקט, הבנאי טוען את התמונה ושומר אותה במשתנה backgroundImage
    public BackgroundPanel(String imagePath) {

        // קובע שאין מנהל פריסה אוטומטי
        // במצב כזה מציבים רכיבים ידנית בעזרת setBounds
        this.setLayout(null);

        // טוען את התמונה לפי הנתיב שהתקבל בבנאי
        this.backgroundImage = loadImage(imagePath);
    }

    // מתודה שמציירת את הפאנל על המסך
    // Swing מפעיל אותה בכל פעם שצריך לרענן או לצייר מחדש את הפאנל
    @Override
    protected void paintComponent(Graphics graphics) {

        // קורא לציור הרגיל של JPanel
        // חשוב להשאיר את השורה הזאת כדי שהפאנל יצויר בצורה תקינה
        super.paintComponent(graphics);

        // בודק שהתמונה נטענה בהצלחה לפני שמנסים לצייר אותה
        if (this.backgroundImage != null) {

            // מצייר את תמונת הרקע על כל שטח הפאנל
            // 0,0 הם נקודת ההתחלה של הציור
            // getWidth ו getHeight גורמים לתמונה להתאים לגודל הפאנל
            graphics.drawImage(this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    // מתודה פרטית שטוענת תמונה לפי נתיב
    // היא מחזירה אובייקט Image אם התמונה נמצאה
    private Image loadImage(String path) {
        try {

            // מחפש את התמונה בתוך resources לפי הנתיב שנשלח
            URL resource = getClass().getResource(path);

            // אם נמצאה תמונה, יוצרים ImageIcon ומחזירים מתוכו את התמונה
            if (resource != null) {
                return new ImageIcon(resource).getImage();
            }

        } catch (Exception e) {

            // אם הייתה שגיאה בטעינת התמונה, מדפיסים אותה לקונסול
            e.printStackTrace();
        }

        // אם התמונה לא נמצאה או הייתה שגיאה, מחזירים null
        return null;
    }
}