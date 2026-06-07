package org.example;

// מחלקה ששומרת את התקדמות השחקן בשלבים
// המשתנים והפונקציות כאן static כי ההתקדמות שייכת למשחק כולו ולא לאובייקט מסוים
public class GameProgress {

    // השלב הגבוה ביותר שהשחקן פתח עד עכשיו
    // מתחיל מ 1 כי השלב הראשון פתוח בתחילת המשחק
    private static int highestUnlockedLevel = 1;

    // בודקת אם שלב מסוים פתוח לשחקן
    public static boolean isLevelUnlocked(int level) {

        // אם מספר השלב קטן או שווה לשלב הגבוה ביותר שנפתח, השלב פתוח
        return level <= highestUnlockedLevel;
    }

    // פותחת שלב חדש אם הוא גבוה מהשלב הנוכחי ועדיין בתוך מספר השלבים המותר
    public static void unlockLevel(int level) {

        // בודק שהשלב החדש מתקדם קדימה
        // וגם שלא עוברים את מספר השלבים המקסימלי שהוגדר במשחק
        if (level > highestUnlockedLevel && level <= GameSettings.MAX_LEVELS) {

            // עדכון השלב הגבוה ביותר שנפתח
            highestUnlockedLevel = level;
        }
    }

}