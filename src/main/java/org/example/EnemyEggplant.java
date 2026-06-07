package org.example;


// מחלקה שמייצגת אויב מסוג חציל
// החציל יורש מ Enemy ולכן משתמש במיקום, גודל, כיוון, גבולות ותמונות מהמחלקה הכללית
public class EnemyEggplant extends Enemy {

    // בנאי שמקבל מיקום וגודל לחציל
    public EnemyEggplant(int x, int y, int width, int height) {

        // קריאה לבנאי של Enemy כדי לאתחל מיקום, גודל, כיוון וגבולות
        super(x, y, width, height);

        // טעינת תמונת החציל כשהוא פונה למטה
        setFrontImage(loadImage("/Eggplant_Front.png"));

        // טעינת תמונת החציל כשהוא פונה למעלה
        setBackImage(loadImage("/Eggplant_Back.png"));

        // קביעת התמונה ההתחלתית של החציל לתמונת חזית
        setCurrentImage(getFrontImage());

        // קביעת כיוון התחלתי למטה
        setDirection(DOWN);
    }

    // פונקציה שמזיזה את החציל בכל פריים
    // החציל זז למעלה ולמטה בלבד
    @Override
    public void move() {

        // אם החציל לא במצב תנועה, יוצאים מהפונקציה
        if (!isMoving()) return;

        // משתנה שמסמן אם החציל הגיע לגבול המסך
        boolean hitBoundary = false;

        // אם הכיוון הנוכחי הוא למטה
        if (getDirection() == DOWN) {

            // אם החציל לא הגיע לגבול התחתון, מזיזים אותו למטה
            if (!isAtBottomBoundary()) {

                // מעדכן את התמונה לתמונה שפונה למטה
                setCurrentImage(getFrontImage());

                // מזיז את החציל 2 פיקסלים למטה
                moveVertically(2);

            } else {

                // אם החציל הגיע לגבול התחתון, מסמנים פגיעה בגבול
                hitBoundary = true;
            }

            // אם הכיוון הנוכחי הוא למעלה
        } else if (getDirection() == UP) {

            // אם החציל לא הגיע לגבול העליון, מזיזים אותו למעלה
            if (!isAtTopBoundary()) {

                // מעדכן את התמונה לתמונה שפונה למעלה
                setCurrentImage(getBackImage());

                // מזיז את החציל 2 פיקסלים למעלה
                moveVertically(-2);

            } else {

                // אם החציל הגיע לגבול העליון, מסמנים פגיעה בגבול
                hitBoundary = true;
            }

        } else {

            // אם הכיוון אינו למעלה או למטה, מחזירים את החציל לכיוון למטה
            setDirection(DOWN);
        }

        // אם החציל פגע בגבול או לפי סיכוי אקראי, מחליפים לו כיוון
        if (hitBoundary || getRandom().nextInt(250) == 0) {

            // אם הכיוון היה למטה, משנים למעלה
            // אם הכיוון היה למעלה, משנים למטה
            setDirection(getDirection() == DOWN ? UP : DOWN);
        }
    }
}