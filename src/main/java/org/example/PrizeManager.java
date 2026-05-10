package org.example;

import java.awt.*;
import java.util.Random;

public class PrizeManager {
    private static final int CANDY_WIDTH = 15;
    private static final int CANDY_HEIGHT = 36;

    private static final int LOLLIPOP_WIDTH = 95;
    private static final int LOLLIPOP_HEIGHT = 65;

    private static final int LOLLIPOP_MIN_DISTANCE_FROM_ENEMY = 55;
    private static final int LOLLIPOP_MAX_DISTANCE_FROM_ENEMY = 230;

    private static final int LOLLIPOP_NEAR_CAKE_DISTANCE = 85;
    private static final int LOLLIPOP_PLAYER_SAFE_DISTANCE = 280;

    private Cake[] cakes;
    private int cakesCount;
    private Enemy[] enemies;
    private Prize[] prizes;
    private String[] candyImages;
    private Random random = new Random();

    public Prize[] createPrizes(
            int regularCandiesAmount,
            Cake[] cakes,
            int cakesCount,
            Enemy[] enemies,
            String[] candyImages
    ) {
        this.cakes = cakes;
        this.cakesCount = cakesCount;
        this.enemies = enemies;
        this.candyImages = candyImages;
        this.prizes = new Prize[regularCandiesAmount + 1];

        createRegularCandies(regularCandiesAmount);
        createLollipop(regularCandiesAmount);

        return this.prizes;
    }

    private void createRegularCandies(int amount) {
        int safeMargin = 20;

        int minX = GameSettings.WALL_LEFT + safeMargin;
        int maxX = Main.WINDOW_WIDTH - GameSettings.WALL_RIGHT - CANDY_WIDTH - safeMargin;

        int minY = GameSettings.WALL_TOP + safeMargin;
        int maxY = Main.WINDOW_HEIGHT - GameSettings.WALL_BOTTOM - CANDY_HEIGHT - safeMargin;

        for (int i = 0; i < amount; i++) {
            int x;
            int y;

            do {
                x = random.nextInt(maxX - minX) + minX;
                y = random.nextInt(maxY - minY) + minY;
            } while (!isValidPrizeLocation(x, y, CANDY_WIDTH, CANDY_HEIGHT, i));

            String randomCandy = candyImages[random.nextInt(candyImages.length)];

            prizes[i] = new Prize(
                    x,
                    y,
                    CANDY_WIDTH,
                    CANDY_HEIGHT,
                    randomCandy,
                    10
            );
        }
    }

    private void createLollipop(int index) {
        Point point = findBestLollipopLocation(index);

        prizes[index] = new Prize(
                point.x,
                point.y,
                LOLLIPOP_WIDTH,
                LOLLIPOP_HEIGHT,
                "/Lollipop.png",
                20
        );
    }

    private Point findBestLollipopLocation(int prizeIndex) {
        Point bestPoint = null;
        int bestScore = -1;

        for (int i = 0; i < cakesCount; i++) {
            if (cakes[i] == null) {
                continue;
            }

            Rectangle cakeRect = cakes[i].getRect();

            int[] candidateX = {
                    cakeRect.x + 55,
                    cakeRect.x - LOLLIPOP_WIDTH - 5,
                    cakeRect.x,
                    cakeRect.x,
                    cakeRect.x + 55,
                    cakeRect.x - LOLLIPOP_WIDTH - 5,
                    cakeRect.x + 55,
                    cakeRect.x - LOLLIPOP_WIDTH - 5
            };

            int[] candidateY = {
                    cakeRect.y,
                    cakeRect.y,
                    cakeRect.y + 55,
                    cakeRect.y - LOLLIPOP_HEIGHT - 5,
                    cakeRect.y + 55,
                    cakeRect.y + 55,
                    cakeRect.y - LOLLIPOP_HEIGHT - 5,
                    cakeRect.y - LOLLIPOP_HEIGHT - 5
            };

            for (int j = 0; j < candidateX.length; j++) {
                int x = candidateX[j];
                int y = candidateY[j];

                int score = getLollipopLocationScore(x, y, prizeIndex);

                if (score > bestScore) {
                    bestScore = score;
                    bestPoint = new Point(x, y);
                }
            }
        }

        if (bestPoint != null) {
            return bestPoint;
        }

        return findFallbackLollipopLocation(prizeIndex);
    }

    private int getLollipopLocationScore(int x, int y, int prizeIndex) {
        if (!isValidPrizeLocation(x, y, LOLLIPOP_WIDTH, LOLLIPOP_HEIGHT, prizeIndex)) {
            return -1;
        }

        if (!isFarFromPlayerStart(x, y)) {
            return -1;
        }

        int nearbyCakes = countNearbyCakes(x, y);

        if (nearbyCakes < 2) {
            return -1;
        }

        int openSides = countOpenSides(x, y);

        if (openSides >= 4) {
            return -1;
        }

        int enemyScore = getEnemyScore(x, y);

        if (enemyScore <= 0) {
            return -1;
        }

        return nearbyCakes * 15 + enemyScore * 3 + (4 - openSides) * 10;
    }

    private int countNearbyCakes(int x, int y) {
        Rectangle searchArea = new Rectangle(
                x - LOLLIPOP_NEAR_CAKE_DISTANCE,
                y - LOLLIPOP_NEAR_CAKE_DISTANCE,
                LOLLIPOP_WIDTH + LOLLIPOP_NEAR_CAKE_DISTANCE * 2,
                LOLLIPOP_HEIGHT + LOLLIPOP_NEAR_CAKE_DISTANCE * 2
        );

        int count = 0;

        for (int i = 0; i < cakesCount; i++) {
            if (cakes[i] != null && searchArea.intersects(cakes[i].getRect())) {
                count++;
            }
        }

        return count;
    }

    private int countOpenSides(int x, int y) {
        int openSides = 0;
        int checkDistance = 65;

        Rectangle rightArea = new Rectangle(
                x + LOLLIPOP_WIDTH,
                y,
                checkDistance,
                LOLLIPOP_HEIGHT
        );

        Rectangle leftArea = new Rectangle(
                x - checkDistance,
                y,
                checkDistance,
                LOLLIPOP_HEIGHT
        );

        Rectangle downArea = new Rectangle(
                x,
                y + LOLLIPOP_HEIGHT,
                LOLLIPOP_WIDTH,
                checkDistance
        );

        Rectangle upArea = new Rectangle(
                x,
                y - checkDistance,
                LOLLIPOP_WIDTH,
                checkDistance
        );

        if (!areaTouchesCake(rightArea)) {
            openSides++;
        }

        if (!areaTouchesCake(leftArea)) {
            openSides++;
        }

        if (!areaTouchesCake(downArea)) {
            openSides++;
        }

        if (!areaTouchesCake(upArea)) {
            openSides++;
        }

        return openSides;
    }

    private boolean areaTouchesCake(Rectangle area) {
        for (int i = 0; i < cakesCount; i++) {
            if (cakes[i] != null && area.intersects(cakes[i].getRect())) {
                return true;
            }
        }

        return false;
    }

    private int getEnemyScore(int x, int y) {
        Rectangle tooCloseArea = new Rectangle(
                x - LOLLIPOP_MIN_DISTANCE_FROM_ENEMY,
                y - LOLLIPOP_MIN_DISTANCE_FROM_ENEMY,
                LOLLIPOP_WIDTH + LOLLIPOP_MIN_DISTANCE_FROM_ENEMY * 2,
                LOLLIPOP_HEIGHT + LOLLIPOP_MIN_DISTANCE_FROM_ENEMY * 2
        );

        Rectangle goodArea = new Rectangle(
                x - LOLLIPOP_MAX_DISTANCE_FROM_ENEMY,
                y - LOLLIPOP_MAX_DISTANCE_FROM_ENEMY,
                LOLLIPOP_WIDTH + LOLLIPOP_MAX_DISTANCE_FROM_ENEMY * 2,
                LOLLIPOP_HEIGHT + LOLLIPOP_MAX_DISTANCE_FROM_ENEMY * 2
        );

        int score = 0;

        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i] == null) {
                continue;
            }

            Rectangle enemyRect = enemies[i].getRect();

            if (tooCloseArea.intersects(enemyRect)) {
                return -1;
            }

            if (goodArea.intersects(enemyRect)) {
                score += 40;
            }
        }

        return score;
    }

    private boolean isFarFromPlayerStart(int x, int y) {
        Rectangle lollipopRect = new Rectangle(x, y, LOLLIPOP_WIDTH, LOLLIPOP_HEIGHT);

        Rectangle playerStartArea = new Rectangle(
                100 - LOLLIPOP_PLAYER_SAFE_DISTANCE / 2,
                100 - LOLLIPOP_PLAYER_SAFE_DISTANCE / 2,
                LOLLIPOP_PLAYER_SAFE_DISTANCE,
                LOLLIPOP_PLAYER_SAFE_DISTANCE
        );

        return !lollipopRect.intersects(playerStartArea);
    }

    private Point findFallbackLollipopLocation(int prizeIndex) {
        int minX = GameSettings.WALL_LEFT + 20;
        int maxX = Main.WINDOW_WIDTH - GameSettings.WALL_RIGHT - LOLLIPOP_WIDTH - 20;

        int minY = GameSettings.WALL_TOP + 20;
        int maxY = Main.WINDOW_HEIGHT - GameSettings.WALL_BOTTOM - LOLLIPOP_HEIGHT - 20;

        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(maxX - minX) + minX;
            int y = random.nextInt(maxY - minY) + minY;

            if (isValidPrizeLocation(x, y, LOLLIPOP_WIDTH, LOLLIPOP_HEIGHT, prizeIndex)
                    && isFarFromPlayerStart(x, y)
                    && countNearbyCakes(x, y) >= 1
                    && countOpenSides(x, y) <= 3
                    && getEnemyScore(x, y) > 0) {
                return new Point(x, y);
            }
        }

        return new Point(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT / 2);
    }

    private boolean isValidPrizeLocation(int x, int y, int width, int height, int currentPrizeIndex) {
        if (!GameSettings.isInsidePlayArea(x, y, width, height)) {
            return false;
        }

        Rectangle prizeRect = new Rectangle(x, y, width, height);

        for (int i = 0; i < cakesCount; i++) {
            if (cakes[i] != null && prizeRect.intersects(cakes[i].getRect())) {
                return false;
            }
        }

        int padding = 40;

        Rectangle safeZone = new Rectangle(
                x - padding,
                y - padding,
                width + padding * 2,
                height + padding * 2
        );

        for (int i = 0; i < currentPrizeIndex; i++) {
            if (prizes[i] != null && safeZone.intersects(prizes[i].getBounds())) {
                return false;
            }
        }

        return true;
    }
}