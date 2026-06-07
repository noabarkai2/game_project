package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;
import java.util.Random;

// מחלקה שמייצגת עוגה - מחסום במשחק
// לכל עוגה יש מיקום, גודל ותמונה
public class Cake{

    // מיקום העוגה בציר X
    private int x;

    // מיקום העוגה בציר Y
    private int y;

    // רוחב העוגה על המסך
    private int width;

    // גובה העוגה על המסך
    private int height;

    // משתנה ששומר את תמונת העוגה
    private Image cakeImage;

    // בנאי שמקבל מיקום וגודל לעוגה
    // בזמן יצירת העוגה נבחרת תמונה אקראית מתוך כמה תמונות
    public Cake(int x, int y, int width, int height){

        // שמירת המיקום והגודל שהתקבלו בבנאי
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // מערך שמכיל את הנתיבים של תמונות העוגות מתוך resources
        String[] imagePaths = {"/cake1.png", "/cake2.png", "/cake3.png", "/cake4.png"};

        // יצירת אובייקט Random כדי לבחור תמונה אקראית
        Random random = new Random();

        // בחירת אינדקס אקראי לפי מספר התמונות במערך
        int randomIndex = random.nextInt(imagePaths.length);

        try{

            // שמירת הנתיב של התמונה שנבחרה
            String selectedImagePath = imagePaths[randomIndex];

            // טעינת התמונה מתוך resources כזרם נתונים
            InputStream inputStream = Cake.class.getResourceAsStream(selectedImagePath);

            // קריאת התמונה מתוך הזרם ושמירתה במשתנה cakeImage
            this.cakeImage = ImageIO.read(inputStream);

        }catch (Exception e){

            // הדפסת שגיאה לקונסול אם טעינת התמונה נכשלה
            e.printStackTrace();
        }
    }

    // מציירת את העוגה על המסך
    public void paint(Graphics graphics){

        // בדיקה שהתמונה נטענה לפני שמציירים אותה
        if(this.cakeImage != null){

            // ציור תמונת העוגה לפי המיקום והגודל שלה
            graphics.drawImage(this.cakeImage, this.x, this.y, this.width, this.height, null);
        }
    }

    // מחזירה מלבן שמייצג את גבולות העוגה
    // מלבן כזה עוזר לבדוק התנגשות עם שחקן או אובייקטים אחרים
    public Rectangle getRect(){

        // יצירת Rectangle לפי מיקום וגודל העוגה
        Rectangle rectangle = new Rectangle(this.x, this.y, this.width, this.height);

        // החזרת המלבן
        return rectangle;
    }
}