package org.example;

import javax.sound.sampled.*;
import java.net.URL;

// מחלקה שאחראית על ניגון אפקטים קצרים של סאונד במשחק
public class SoundEffects {

    // מנגן אפקט קצר פעם אחת
    //סוכריה,תיקתוק של השעון, ניצחון והפסד
    public static void play(String soundFileName) {
        try {
            // מחפש את קובץ הסאונד בתוך תיקיית resources לפי הנתיב שנשלח
            URL soundURL = SoundEffects.class.getResource(soundFileName);

            // אם הקובץ נמצא, טוענים ומנגנים אותו
            if (soundURL != null) {

                // יוצר זרם אודיו מתוך קובץ הסאונד
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundURL);

                // יוצר Clip, רכיב שמסוגל לנגן קטע סאונד קצר
                Clip clip = AudioSystem.getClip();

                // טוען את קובץ הסאונד לתוך ה Clip
                clip.open(audioInput);

                // מתחיל לנגן את הסאונד
                clip.start();

            } else {

                // אם הקובץ לא נמצא, מדפיסים הודעה לקונסול
                System.out.println("לא מצאתי את קובץ הסאונד: " + soundFileName);
            }

        } catch (Exception e) {

            // אם הייתה שגיאה בזמן טעינה או ניגון של הסאונד, מדפיסים הודעת שגיאה
            System.out.println("שגיאה בניגון אפקט סאונד: " + soundFileName);

            // מדפיס פירוט מלא של השגיאה לקונסול
            e.printStackTrace();
        }
    }
}