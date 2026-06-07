package org.example;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

// מחלקת עזר כללית למשחק
// מרכזת פעולות שחוזרות בכמה מקומות, כמו שינה, מוזיקה, כפתור סאונד ושינוי גודל אייקונים
public class Utils {

    // אובייקט שמנהל את מוזיקת הרקע של המשחק
    public static SoundManager backgroundMusic;

    // שומר האם מוזיקת הרקע פועלת כרגע
    public static boolean isMusicPlaying = true;

    // אייקון שמוצג כאשר המוזיקה פועלת
    private static ImageIcon soundOnIcon;

    // אייקון שמוצג כאשר המוזיקה כבויה
    private static ImageIcon soundOffIcon;

    // עוצרת את התוכנית לזמן קצר לפי מספר מילישניות
    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // טוען את מוזיקת הרקע פעם אחת ומפעיל אותה
    public static void initializeMusic(String path) {

        // אם עוד לא נוצר אובייקט מוזיקה, יוצרים אותו
        // כך לא טוענים את אותו קובץ שוב ושוב
        if (backgroundMusic == null) {
            backgroundMusic = new SoundManager(path);
            backgroundMusic.playLoop();
        }
    }

    // עוצר את מוזיקת הרקע
    public static void stopMusic() {

        // בודק שקיים אובייקט מוזיקה לפני שמנסים לעצור אותו
        if (backgroundMusic != null) {
            backgroundMusic.stop();

            // מעדכן שהמוזיקה לא פועלת כרגע
            isMusicPlaying = false;
        }
    }

    // מפעיל את מוזיקת הרקע מחדש
    public static void playMusic() {

        // בודק שקיים אובייקט מוזיקה לפני שמנסים להפעיל אותו
        if (backgroundMusic != null) {
            backgroundMusic.playLoop();

            // מעדכן שהמוזיקה פועלת כרגע
            isMusicPlaying = true;
        }
    }

    // מסנכרן את תמונת כפתור הסאונד לפי מצב המוזיקה
    public static void syncButtonIcon(JButton button) {

        // אם המוזיקה פועלת ויש אייקון מתאים, מציגים אייקון של סאונד פועל
        if (isMusicPlaying && soundOnIcon != null) {
            button.setIcon(soundOnIcon);

            // אם המוזיקה כבויה ויש אייקון מתאים, מציגים אייקון של סאונד כבוי
        } else if (!isMusicPlaying && soundOffIcon != null) {
            button.setIcon(soundOffIcon);
        }
    }

    // יוצר כפתור שמדליק ומכבה מוזיקת רקע
    public static JButton createSoundButton() {

        // טוען את האייקונים פעם אחת בלבד
        if (soundOnIcon == null || soundOffIcon == null) {
            soundOnIcon = resizeIcon("/sound_on.png", 50, 50);
            soundOffIcon = resizeIcon("/sound_off.png", 50, 50);
        }

        // יצירת כפתור סאונד
        JButton soundButton = new JButton();

        // קביעת מיקום וגודל הכפתור
        soundButton.setBounds(20, 20, 50, 50);

        // ביטול סימון פוקוס רגיל סביב הכפתור
        soundButton.setFocusPainted(false);

        // ביטול רקע רגיל של JButton
        soundButton.setContentAreaFilled(false);

        // ביטול מסגרת רגילה של JButton
        soundButton.setBorderPainted(false);

        // מונע מהכפתור לקבל פוקוס מהמקלדת
        soundButton.setFocusable(false);

        // קובע את האייקון הנכון לפי מצב המוזיקה הנוכחי
        syncButtonIcon(soundButton);

        // פעולה בזמן לחיצה על כפתור הסאונד
        soundButton.addActionListener(e -> {

            // אם המוזיקה פועלת, עוצרים אותה
            if (isMusicPlaying) {
                stopMusic();

                // אם המוזיקה כבויה, מפעילים אותה
            } else {
                playMusic();
            }

            // אחרי שינוי מצב המוזיקה, מעדכנים את האייקון בכפתור
            syncButtonIcon(soundButton);
        });

        // מחזיר את הכפתור המוכן
        return soundButton;
    }

    // משנה גודל של אייקון
    private static ImageIcon resizeIcon(String path, int width, int height) {

        // מחפש את קובץ האייקון בתוך resources
        URL imgUrl = Utils.class.getResource(path);

        // אם האייקון נמצא, טוענים אותו ומשנים לו גודל
        if (imgUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imgUrl);
            Image img = originalIcon.getImage();
            Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            return new ImageIcon(resizedImage);
        }

        // אם האייקון לא נמצא, מדפיסים הודעה לקונסול
        System.out.println("לא מצאתי את קובץ האייקון: " + path);
        return null;
    }
}