package org.example;

// מחלקה שמייצגת אויב מסוג ברוקולי
// הברוקולי יורש מ Enemy ולכן משתמש בתנועה, גבולות, כיוונים ותמונות מהמחלקה הכללית
public class EnemyBroccoli extends Enemy {

    // בנאי שמקבל מיקום וגודל לברוקולי
    public EnemyBroccoli(int x, int y, int width, int height) {

        // קריאה לבנאי של Enemy כדי לאתחל מיקום, גודל, כיוון וגבולות
        super(x, y, width, height);

        // טעינת תמונת הברוקולי כשהוא פונה ימינה
        setRightImage(loadImage("/Broccoli_Right.png"));

        // טעינת תמונת הברוקולי כשהוא פונה שמאלה
        setLeftImage(loadImage("/Broccoli_Left.png"));

        // קביעת התמונה ההתחלתית של הברוקולי לצד ימין
        setCurrentImage(getRightImage());

        // קביעת כיוון התחלתי לימין
        setDirection(RIGHT);
    }

    // פונקציה שמזיזה את הברוקולי בכל פריים
    // הברוקולי זז ימינה ושמאלה בלבד
    @Override
    public void move() {

        // אם הברוקולי לא במצב תנועה, לא עושים כלום
        if (!isMoving()) return;

        // משתנה שמסמן אם הברוקולי הגיע לגבול המסך
        boolean hitBoundary = false;

        // אם הכיוון הנוכחי הוא ימינה
        if (getDirection() == RIGHT) {

            // אם הברוקולי לא הגיע לגבול הימני, מזיזים אותו ימינה
            if (!isAtRightBoundary()) {

                // מעדכן את התמונה לתמונה שפונה ימינה
                setCurrentImage(getRightImage());

                // מזיז את הברוקולי 2 פיקסלים ימינה
                moveHorizontally(2);

            } else {

                // אם הברוקולי הגיע לגבול הימני, מסמנים פגיעה בגבול
                hitBoundary = true;
            }

            // אם הכיוון הנוכחי הוא שמאלה
        } else if (getDirection() == LEFT) {

            // אם הברוקולי לא הגיע לגבול השמאלי, מזיזים אותו שמאלה
            if (!isAtLeftBoundary()) {

                // מעדכן את התמונה לתמונה שפונה שמאלה
                setCurrentImage(getLeftImage());

                // מזיז את הברוקולי 2 פיקסלים שמאלה
                moveHorizontally(-2);

            } else {

                // אם הברוקולי הגיע לגבול השמאלי, מסמנים פגיעה בגבול
                hitBoundary = true;
            }

        } else {

            // אם מסיבה כלשהי הכיוון אינו ימין או שמאל, מחזירים את הברוקולי לימין
            setDirection(RIGHT);
        }

        // אם הברוקולי פגע בגבול או לפי סיכוי אקראי, מחליפים לו כיוון
        if (hitBoundary || getRandom().nextInt(250) == 0) {

            // אם הכיוון היה ימין, משנים לשמאל
            // אם הכיוון היה שמאל, משנים לימין
            setDirection(getDirection() == RIGHT ? LEFT : RIGHT);
        }
    }
}