package org.example;

// מחלקה שמייצגת אויב מסוג פלפל
// הפלפל יורש מ Enemy ולכן מקבל ממנו תנועה, גבולות, כיוונים ותמונות
public class EnemyBellPepper extends Enemy {

    // השחקן שאחריו הפלפל רודף
    private Player targetPlayer;

    // טיימרים שמסמנים שכיוון מסוים חסום לזמן קצר
    private int rightBlockedTimer = 0;
    private int leftBlockedTimer = 0;
    private int upBlockedTimer = 0;
    private int downBlockedTimer = 0;

    // כיוון עקיפה זמני כשהפלפל נתקע
    private int escapeDirection = 0;

    // מספר פריימים שבהם הפלפל ינסה לזוז בכיוון העקיפה
    private int escapeTimer = 0;

    // יוצר פלפל חדש, שומר את השחקן כמטרה וטוען תמונות לפי כיוון
    public EnemyBellPepper(int x, int y, int width, int height, Player player) {

        // קריאה לבנאי של Enemy כדי לאתחל מיקום, גודל, כיוון וגבולות
        super(x, y, width, height);

        // שמירת השחקן שהפלפל ירדוף אחריו
        this.targetPlayer = player;

        // טעינת התמונות של הפלפל לפי כיוון
        setFrontImage(loadImage("/BellPepper_Front.png"));
        setBackImage(loadImage("/BellPepper_Back.png"));
        setRightImage(loadImage("/BellPepper_Right.png"));
        setLeftImage(loadImage("/BellPepper_Left.png"));

        // קובע שהתמונה ההתחלתית תהיה תמונת חזית
        setCurrentImage(getFrontImage());
    }

    // מופעל כשהפלפל נתקע, שומר את הכיוון החסום ובוחר כיוון עקיפה זמני
    public void suspendTracking(int frames) {

        // שומר את הכיוון שבו הפלפל ניסה לזוז ונחסם
        int blockedDirection = getDirection();

        // מסמן את הכיוון הזה כחסום למספר פריימים
        blockDirection(blockedDirection, frames);

        // בוחר כיוון עקיפה זמני לפי הכיוון שנחסם והמיקום של השחקן
        this.escapeDirection = chooseEscapeDirection(blockedDirection);

        // קובע כמה זמן הפלפל ינסה לבצע עקיפה
        this.escapeTimer = frames;
    }

    // מריץ את תנועת הפלפל בכל פריים, קודם עקיפה אם קיימת, אחרת רדיפה אחרי השחקן
    @Override
    public void move() {

        // אם הפלפל לא בתנועה או שאין שחקן לרדוף אחריו, אין מה לבצע
        if (!isMoving() || this.targetPlayer == null) {
            return;
        }

        // מוריד את זמני החסימה של הכיוונים בכל פריים
        decreaseBlockedTimers();

        // אם יש עקיפה פעילה, הפלפל מנסה לזוז בכיוון העקיפה
        if (escapeTimer > 0) {
            escapeTimer--;

            // אם התנועה בכיוון העקיפה הצליחה, מסיימים את הפריים
            if (tryMove(escapeDirection)) {
                return;
            }

            // אם העקיפה נכשלה, מבטלים את מצב העקיפה
            escapeTimer = 0;
        }

        // חישוב המרחק בין הפלפל לשחקן בציר X
        int diffX = this.targetPlayer.getX() - this.getX();

        // חישוב המרחק בין הפלפל לשחקן בציר Y
        int diffY = this.targetPlayer.getY() - this.getY();

        // כיווני ניסיון לפי סדר עדיפויות
        int firstDirection;
        int secondDirection;
        int thirdDirection;
        int fourthDirection;

        // אם המרחק בציר X גדול יותר, הפלפל יעדיף לזוז ימינה או שמאלה
        if (Math.abs(diffX) > Math.abs(diffY)) {
            firstDirection = diffX > 0 ? RIGHT : LEFT;
            secondDirection = diffY > 0 ? DOWN : UP;
            thirdDirection = getOppositeDirection(secondDirection);
            fourthDirection = getOppositeDirection(firstDirection);

            // אם המרחק בציר Y גדול יותר, הפלפל יעדיף לזוז למעלה או למטה
        } else {
            firstDirection = diffY > 0 ? DOWN : UP;
            secondDirection = diffX > 0 ? RIGHT : LEFT;
            thirdDirection = getOppositeDirection(secondDirection);
            fourthDirection = getOppositeDirection(firstDirection);
        }

        // ניסיון לזוז בכיוון הכי קרוב לשחקן
        if (tryMove(firstDirection)) {
            return;
        }

        // אם הכיוון הראשון נכשל, מנסים כיוון שני
        if (tryMove(secondDirection)) {
            return;
        }

        // אם גם הכיוון השני נכשל, מנסים כיוון שלישי
        if (tryMove(thirdDirection)) {
            return;
        }

        // ניסיון אחרון בכיוון הרביעי
        tryMove(fourthDirection);
    }

    // בוחר כיוון עקיפה לפי הכיוון שנחסם והמיקום של השחקן
    private int chooseEscapeDirection(int blockedDirection) {

        // חישוב מיקום השחקן ביחס לפלפל בציר X
        int diffX = this.targetPlayer.getX() - this.getX();

        // חישוב מיקום השחקן ביחס לפלפל בציר Y
        int diffY = this.targetPlayer.getY() - this.getY();

        // אם הכיוון שנחסם הוא למעלה או למטה, ננסה לעקוף ימינה או שמאלה
        if (blockedDirection == UP || blockedDirection == DOWN) {
            if (diffX >= 0 && !isDirectionBlocked(RIGHT)) {
                return RIGHT;
            }

            if (diffX < 0 && !isDirectionBlocked(LEFT)) {
                return LEFT;
            }

            if (!isDirectionBlocked(RIGHT)) {
                return RIGHT;
            }

            return LEFT;
        }

        // אם הכיוון שנחסם הוא שמאלה או ימינה, ננסה לעקוף למעלה או למטה
        if (blockedDirection == LEFT || blockedDirection == RIGHT) {
            if (diffY >= 0 && !isDirectionBlocked(DOWN)) {
                return DOWN;
            }

            if (diffY < 0 && !isDirectionBlocked(UP)) {
                return UP;
            }

            if (!isDirectionBlocked(DOWN)) {
                return DOWN;
            }

            return UP;
        }

        // כיוון ברירת מחדל במקרה שלא זוהה כיוון חסום תקין
        return DOWN;
    }

    // מסמן כיוון מסוים כחסום למשך מספר פריימים
    private void blockDirection(int direction, int frames) {
        if (direction == RIGHT) {
            rightBlockedTimer = frames;
        } else if (direction == LEFT) {
            leftBlockedTimer = frames;
        } else if (direction == UP) {
            upBlockedTimer = frames;
        } else if (direction == DOWN) {
            downBlockedTimer = frames;
        }
    }

    // מוריד בכל פריים את זמן החסימה מכל כיוון
    private void decreaseBlockedTimers() {
        if (rightBlockedTimer > 0) {
            rightBlockedTimer--;
        }

        if (leftBlockedTimer > 0) {
            leftBlockedTimer--;
        }

        if (upBlockedTimer > 0) {
            upBlockedTimer--;
        }

        if (downBlockedTimer > 0) {
            downBlockedTimer--;
        }
    }

    // מנסה להזיז את הפלפל לכיוון מסוים, מעדכן תמונה ומחזיר אם התנועה הצליחה
    private boolean tryMove(int direction) {

        // אם הכיוון חסום כרגע, אין ניסיון תנועה
        if (isDirectionBlocked(direction)) {
            return false;
        }

        // ניסיון תנועה ימינה
        if (direction == RIGHT) {
            if (isAtRightBoundary()) {
                return false;
            }

            setDirection(RIGHT);
            setCurrentImage(getRightImage());
            moveHorizontally(1);
            return true;
        }

        // ניסיון תנועה שמאלה
        if (direction == LEFT) {
            if (isAtLeftBoundary()) {
                return false;
            }

            setDirection(LEFT);
            setCurrentImage(getLeftImage());
            moveHorizontally(-1);
            return true;
        }

        // ניסיון תנועה למעלה
        if (direction == UP) {
            if (isAtTopBoundary()) {
                return false;
            }

            setDirection(UP);
            setCurrentImage(getBackImage());
            moveVertically(-1);
            return true;
        }

        // ניסיון תנועה למטה
        if (direction == DOWN) {
            if (isAtBottomBoundary()) {
                return false;
            }

            setDirection(DOWN);
            setCurrentImage(getFrontImage());
            moveVertically(1);
            return true;
        }

        // אם הכיוון לא תקין, מחזירים false
        return false;
    }

    // בודק אם הכיוון המבוקש חסום כרגע
    private boolean isDirectionBlocked(int direction) {
        if (direction == RIGHT) {
            return rightBlockedTimer > 0;
        }

        if (direction == LEFT) {
            return leftBlockedTimer > 0;
        }

        if (direction == UP) {
            return upBlockedTimer > 0;
        }

        if (direction == DOWN) {
            return downBlockedTimer > 0;
        }

        return false;
    }

    // מחזיר את הכיוון ההפוך לכיוון שנשלח
    private int getOppositeDirection(int direction) {
        if (direction == RIGHT) {
            return LEFT;
        }

        if (direction == LEFT) {
            return RIGHT;
        }

        if (direction == UP) {
            return DOWN;
        }

        return UP;
    }
}