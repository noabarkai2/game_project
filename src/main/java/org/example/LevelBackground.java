package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;

// מחלקה שאחראית על תמונת הרקע של שלב במשחק
public class LevelBackground {

    // משתנה ששומר את תמונת הרקע אחרי טעינה
    private Image backgroundImage;

    // בנאי שטוען את תמונת הרקע של השלב מתוך resources
    public LevelBackground(){
        try{

            // טעינת קובץ התמונה מתוך resources לפי הנתיב
            InputStream inputStream = LevelBackground.class.getResourceAsStream("/background_level.jpeg");

            // קריאת התמונה מתוך הזרם ושמירתה במשתנה backgroundImage
            this.backgroundImage = ImageIO.read(inputStream);

        }catch (Exception e){

            // אם הייתה שגיאה בטעינת התמונה, מדפיסים אותה לקונסול
            e.printStackTrace();
        }
    }

    // מציירת את תמונת הרקע על המסך לפי רוחב וגובה שמתקבלים מבחוץ
    public void paint(Graphics graphics, int width, int height){

        // בודק שהתמונה נטענה לפני שמנסים לצייר אותה
        if(this.backgroundImage != null){

            // מצייר את תמונת הרקע מהפינה השמאלית העליונה על כל גודל המסך
            graphics.drawImage(this.backgroundImage, 0,0,width,height,null);
        }
    }
}