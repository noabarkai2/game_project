package org.example;

import javax.sound.sampled.*;
import java.net.URL;

// מחלקה שאחראית על טעינה וניהול של סאונד מתמשך
// מתאימה למוזיקת רקע או צליל שחוזר בלופ
public class SoundManager {

    // Clip הוא אובייקט שמחזיק קובץ סאונד ויודע לנגן, לעצור ולחזור עליו
    private Clip clip;

    //מוזיקת רקע
    // בנאי שמקבל נתיב לקובץ סאונד מתוך resources וטוען אותו לזיכרון
    public SoundManager(String filePath) {
        try {

            // מחפש את קובץ הסאונד לפי הנתיב שהתקבל
            URL soundURL = getClass().getResource(filePath);

            // אם הקובץ נמצא, טוענים אותו
            if (soundURL != null) {

                // יוצר זרם אודיו מתוך קובץ הסאונד
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundURL);

                // יוצר Clip חדש לניגון הסאונד
                clip = AudioSystem.getClip();

                // מכניס את קובץ הסאונד לתוך ה Clip
                clip.open(audioInput);

            } else {

                // אם הקובץ לא נמצא, מדפיסים הודעה לקונסול
                System.out.println("לא מצאתי את קובץ השמע: " + filePath);
            }

        } catch (Exception e) {

            // אם הייתה שגיאה בטעינת הסאונד, מדפיסים הודעה לקונסול
            System.out.println("שגיאה בטעינת הסאונד: " + filePath);

            // מדפיס פירוט מלא של השגיאה
            e.printStackTrace();
        }
    }

    // מפעיל סאונד בלופ מההתחלה
    public void playLoop() {

        // בודק שהסאונד נטען לפני שמנסים לנגן אותו
        if (clip != null) {

            // מחזיר את הסאונד להתחלה
            clip.setFramePosition(0);

            // מנגן את הסאונד בלולאה ללא הפסקה
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // עוצר סאונד שרץ עכשיו
    public void stop() {

        // עוצר את הסאונד בתנאי שהוא קיים וכרגע מתנגן
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}