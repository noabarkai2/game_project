package org.example;

// מחלקה שאחראית לבניית המבוך בעזרת מערך של עוגות
// כל עוגה משמשת כחלק מקיר או מכשול במשחק
public class MazeBuilder {

    // גודל קבוע של כל עוגה בתאים של המבוך
    private final int CAKE_SIZE = 50;

    // הגודל המקסימלי של מערך העוגות
    private final int CAKE_ARRAY_SIZE = 500;

    // מערך ששומר את כל העוגות שנוצרו במבוך
    private Cake[] cakes;

    // סופר כמה עוגות נוצרו בפועל
    private int cakesCount;

    // בונה מבוך לפי תבנית, גודל מסך ורמת קושי
    public Cake[] buildMaze(int templateIndex, int width, int height, int difficulty) {

        // יצירת מערך חדש לעוגות בתחילת כל בניית שלב
        this.cakes = new Cake[CAKE_ARRAY_SIZE];

        // איפוס מספר העוגות שנוצרו
        this.cakesCount = 0;

        // חישוב מספר העמודות לפי רוחב המסך וגודל עוגה
        int cols = width / CAKE_SIZE; //עמודות

        // חישוב מספר השורות לפי גובה המסך וגודל עוגה
        int rows = height / CAKE_SIZE; // שורות

        // בחירת תבנית המבוך לפי templateIndex
        switch (templateIndex) {
            case 0:
                drawExpandedFortress(cols, rows, difficulty);
                break;
            case 1:
                drawCityGrid(cols, rows, difficulty);
                break;
            case 2:
                drawFullSlalom(cols, rows, difficulty);
                break;
        }

        // החזרת מערך העוגות שנבנה
        return this.cakes;
    }

    // מחזירה כמה עוגות נוצרו בפועל במבוך
    public int getCakesCount() {
        return this.cakesCount;
    }

    //תבנית לשלבים לרמה 0
    private void drawExpandedFortress(int cols, int rows, int difficulty) {

        // מרחק מהקירות החיצוניים של המסך
        int padding = 4;

        // מסגרת פנימית גדולה
        for (int i = padding; i < cols - padding; i++) {

            // משאיר פתח רחב באמצע החלק העליון והתחתון של המסגרת
            //פתח של ארבע עמודות באמצע
            if (i < cols/2 - 2 || i > cols/2 + 2) { // פתחים רחבים באמצע הלמעלה/למטה
                addCake(i, padding);
                addCake(i, rows - padding - 1);
            }
        }

        // יצירת הצדדים הימני והשמאלי של המסגרת
        for (int j = padding; j < rows - padding; j++) {

            // משאיר פתחים רחבים באמצע הצדדים
            if (j < rows/2 - 2 || j > rows/2 + 2) { // פתחים רחבים בצדדים
                addCake(padding, j);
                addCake(cols - padding - 1, j);
            }
        }

        // חישוב מרכז המסך לפי כמות העמודות והשורות
        // במרכז, מבנה קטן עם שינוי קושי
        int midX = cols / 2;
        int midY = rows / 2;

        // יצירת ארבע עוגות סביב מרכז המבוך
        addCake(midX - 2, midY - 2);
        addCake(midX + 2, midY - 2);
        addCake(midX - 2, midY + 2);
        addCake(midX + 2, midY + 2);

        // ברמת קושי גבוהה יותר מוסיפים מכשול במרכז
        if (difficulty >= 1) {
            addCake(midX, midY); // מכשול באמצע
        }
    }

    // מפוזר על כל המסך בצורה שווה עם חסימות שזזות
    private void drawCityGrid(int cols, int rows, int difficulty) {

        // שינוי קטן במיקום לפי רמת הקושי, כדי שהמבוך לא ייראה אותו דבר
        int shift = difficulty % 2;

        // מעבר בעמודות בקפיצות, כדי ליצור בלוקים במרווחים קבועים
        for (int i = 5 + shift; i < cols - 3; i += 5) {

            // מעבר בשורות בקפיצות, כדי לפזר חסימות על כל המסך
            for (int j = 3; j < rows - 3; j += 4) {

                // יוצר בלוקים קטנים בכל רחבי המסך
                addCake(i, j);
                addCake(i + 1, j);

                // ברמת קושי גבוהה יותר מוסיפים עוד עוגה שמקשה על מעבר
                // הוספת קיר שמקשה על מעבר אנכי ברמות קשות
                if (difficulty >= 1 && j % 2 == 0) {
                    addCake(i, j + 1);
                }
            }
        }

        // שורת עוגות רצופה שתחתוך את המסך לרוחב (כמעט עד הסוף)
        for (int i = 3; i < cols - 6; i++) {

            // משאיר פתחים קטנים בשורה כדי שהשחקן יוכל לעבור
            if (i % 4 != 0) {
                addCake(i, rows / 2);
            }
        }
    }

    //  תבנית 2: סלאלום מלא מקצה לקצה
    private void drawFullSlalom(int cols, int rows, int difficulty) {

        // מרווח בין קירות הסלאלום
        int wallSpacing = 5; // מרווח בין העמודים שמאפשר מעבר קל

        // קובע אם הפתח הראשון יהיה למטה או למעלה לפי רמת הקושי
        boolean gapAtBottom = (difficulty % 2 == 0);

        // יצירת קירות אנכיים לאורך המסך
        for (int i = 5; i < cols - 2; i += wallSpacing) {
            if (gapAtBottom) {

                // חומה שיורדת מלמעלה עד כמעט תחתית המסך
                for (int j = 1; j < rows - 4; j++) {
                    addCake(i, j);
                }
            } else {

                // חומה שעולה מלמטה עד כמעט ראש המסך
                for (int j = 4; j < rows - 1; j++) {
                    addCake(i, j);
                }
            }

            // בכל קיר מחליפים את צד הפתח כדי ליצור מסלול סלאלום
            gapAtBottom = !gapAtBottom;
        }
    }

    // מוסיפה עוגה למבוך לפי מיקום ברשת
    private void addCake(int gridX, int gridY) {

        // אזור הגנה סביב נקודת ההתחלה של השחקן
        // אם המיקום קרוב להתחלה, לא מוסיפים שם קיר
        if (gridX >= 1 && gridX <= 4 && gridY >= 1 && gridY <= 4) {
            return;
        }

        // בדיקה שיש מקום פנוי במערך לפני הוספת עוגה חדשה
        if (cakesCount < cakes.length) {

            // המרת מיקום רשת למיקום פיקסלים ויצירת עוגה חדשה
            cakes[cakesCount++] = new Cake(gridX * CAKE_SIZE, gridY * CAKE_SIZE, CAKE_SIZE, CAKE_SIZE);
        }
    }
}