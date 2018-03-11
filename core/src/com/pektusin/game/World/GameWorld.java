package com.pektusin.game.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.pektusin.game.BallGame;
import com.pektusin.game.Objects.Background;
import com.pektusin.game.Objects.Ball;
import com.pektusin.game.Objects.Block;
import com.pektusin.game.Objects.Coin;
import com.pektusin.game.Screens.GameScreen;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by pektusin on 7/28/2016.
 */
public class GameWorld {
    public final int PRIMARY_SCORE_SHIFT_X = 10;

    private final double ANIMATION_DELTA = 1500;
    private final double ANIMATION_PLAY_DELTA = 100;

    private final double DECREASE_SPAWN_DELTA_TIME = 10;
    private final double SPAWN_MIN_DELTA = 400;
    private final int BLOCK_SPAWN_DISTANCE = 200;

    private final float INCREASE_BLOCKS_VELOCITY = 2;
    private final float BLOCKS_MAX_VELOCITY_X = 150;
    private final float BLOCKS_MAX_VELOCITY_Y = 425;
    private final float BLOCK_VELOCITY_PRIMARY = 125;
    private final float INCREASE_BLOCK_VELOCITY_X = 1;
    private final float BLOCKS_VELOCITY_X_PRIMARY = 50;

    private final int MAX_BLOCKS_IN_COLUMN = 2;

    private final int BLOCK_HEIGHT = 70;
    private final int BLOCK_SPAWN_Y = BallGame.SCREEN_HEIGHT - 40;
    private final int BLOCK_MIN_SIZE = 150;
    private final int BLOCK_MAX_SIZE = 380;

    private final float BALL_MAX_VELOCITY_X = 400;
    private final float INCREASE_BALL_VELOCITY = 1;

    private final double COIN_SPAWN_DELTA_PRIMARY = 3000;

    public boolean newBestScore = false;
    public boolean isBestScored = false;

    public boolean coinAdded = false;
    public Vector2 coinAddedChords;
    public int coinValue = 1;
    public int coinsInGame = 0;
    private Iterator<Coin> coinIterator;

    public float blocksVelocityY;
    public boolean going = false;

    BitmapFont scoreFont;

    private Block lastBlock;
    private Iterator<Block> blockIterator;

    private Array<Block> blocks;
    private Block block;
    private boolean blockSpawnDirection = true;
    private boolean previousBlockDirection = false;
    private boolean blockMovingX = false;
    private boolean increaseVelocity = false;
    private float blocksVelocityX;
    private int previousBlockWidth;

    private double lastAnimationPlayTime = 0;
    private double lastCoinAnimationUpdateTime = 0;
    private double coinSpawnDelta;
    private double coinSpawnLastTime = 10000;
    private Array<Coin> coins;
    private Coin coin;

    private int changesCount = 0;
    private Ball ball;

    private int score = 0;
    private int bestScore;
    private int bestScoreY = BallGame.SCREEN_HEIGHT - 50;
    private boolean newScore = false;

    private GameScreen gameScreen;
    private Background background;

    private Random random;

    private boolean showingAds = false;

    private boolean scaleUp = true;

    public GameWorld(GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        blocks = new Array<Block>();
        coins = new Array<Coin>();

        score = 0;

        blocksVelocityY = BLOCK_VELOCITY_PRIMARY;
        coinSpawnDelta = COIN_SPAWN_DELTA_PRIMARY;

        background = new Background(0, blocksVelocityY, gameScreen.game);

        bestScore = gameScreen.getHighScore();

        random = new Random();

        ball = new Ball(BallGame.SCREEN_WIDTH / 2, BallGame.SCREEN_HEIGHT / 2 - 225, 40, gameScreen.game.BALL_VELOCITY_X, 0, gameScreen.game);

        previousBlockWidth = 0;

        blocksVelocityX = BLOCKS_VELOCITY_X_PRIMARY;

        coinAddedChords = new Vector2();
    }

    public void update(float delta) {
        blockIterator = blocks.iterator();
        coinIterator = coins.iterator();

        updateBlocks(delta);

        updateCoins(delta);

        ball.update(delta);

        if (newScore)
            updateScoreFont(delta);

        if (increaseVelocity)
            increaseVelocity();

        background.update(delta, blocksVelocityY);

        checkBestScore(delta);

        if (lastBlock != null) {
            if (BLOCK_SPAWN_Y - lastBlock.getY() >= BLOCK_SPAWN_DISTANCE)
                spawnBlock();
        } else {
            spawnBlock();
        }

        if (TimeUtils.millis() - coinSpawnLastTime >= coinSpawnDelta)
            spawnCoin();

        if (coins.size > 0)
            if (TimeUtils.millis() - lastAnimationPlayTime > ANIMATION_DELTA) {
                gameScreen.game.coinAnimation.play();
                lastAnimationPlayTime = TimeUtils.millis();
            }

        if (TimeUtils.millis() - lastCoinAnimationUpdateTime > ANIMATION_PLAY_DELTA) {
            gameScreen.game.coinAnimation.update();
            lastCoinAnimationUpdateTime = TimeUtils.millis();
        }

        if (coinAdded) {
            coinAddedChords.y -= blocksVelocityY * delta;
            if (coinAddedChords.y <= -25)
                coinAdded = false;
            gameScreen.getRenderer().increaseCoinFontAlpha(delta);
        }
    }

    public void updateScoreFont(float delta) {
        scoreFont = gameScreen.getGame().scoreFont;

        if (scaleUp)
            if (scoreFont.getData().scaleX < 1.2f) {
                scoreFont.getData().scaleX += delta * 3;
                scoreFont.getData().scaleY += delta * 3;
            } else
                scaleUp = false;
        else {
            if (scoreFont.getData().scaleX > 1.0f) {
                scoreFont.getData().scaleX -= delta * 3;
                scoreFont.getData().scaleY -= delta * 3;
            } else {
                scaleUp = true;
                newScore = false;
            }
        }
    }

    public Ball getBall() {
        return ball;
    }

    public Array<Block> getBlocks() {
        return blocks;
    }

    private void chooseBlockDirection() {
        if (random.nextBoolean()) {
            blockSpawnDirection = !blockSpawnDirection;
        } else {
            changesCount++;
            if (changesCount >= MAX_BLOCKS_IN_COLUMN) {
                blockSpawnDirection = !blockSpawnDirection;
                changesCount = 0;
            }
        }
    }

    private void spawnCoin() {
        int coinX = MathUtils.random(10,  BallGame.SCREEN_WIDTH - 25);
        boolean spawnCoin = true;
        coin = new Coin(coinX, BLOCK_SPAWN_Y, gameScreen.game, blocksVelocityY, coinValue);
        for (Block block : blocks)
            if (coin.overlaps(block) || block.getBoundingRectangle().contains(coinX + 12.5f, BLOCK_SPAWN_Y + 12.5f)) {
                spawnCoin = false;
                break;
            }
        if (spawnCoin)
            coins.add(coin);
        coinSpawnLastTime = TimeUtils.millis();
    }

    private void spawnBlock() {
        int x;
        int blockWidth;

        chooseBlockDirection();

        if (blockSpawnDirection == previousBlockDirection)
            blockWidth = MathUtils.random(BLOCK_MIN_SIZE, BLOCK_MAX_SIZE);
        else if (BLOCK_MAX_SIZE - previousBlockWidth > BLOCK_MIN_SIZE)
            blockWidth = MathUtils.random(BLOCK_MIN_SIZE, BLOCK_MAX_SIZE - previousBlockWidth);
        else
            blockWidth = BLOCK_MAX_SIZE - previousBlockWidth;

        if (blockSpawnDirection)
            x = 0;
        else
            x =  BallGame.SCREEN_WIDTH - blockWidth;

        if (blockWidth <= 50)
            blockMovingX = false;
        else
            blockMovingX = random.nextBoolean();

        addBlock(blockWidth, x, blockMovingX);

        previousBlockWidth = blockWidth;
        previousBlockDirection = blockSpawnDirection;
    }

    private void addBlock(int blockWidth, int x, boolean blockMovingX) {
        block = new Block(blocksVelocityY, x, BLOCK_SPAWN_Y, blockWidth, BLOCK_HEIGHT, gameScreen.game);
        if (blockMovingX)
            block.setVelocityX(blocksVelocityX);
        blocks.add(block);

        lastBlock = block;
    }

    private void endGame() {
        if (showingAds)
            gameScreen.game.adHandler.showInterstitial();

        if (gameScreen.game.sound)
            gameScreen.game.endGameSound.play();
        if (gameScreen.game.vibrate)
            Gdx.input.vibrate(5);

        gameScreen.game.setState(BallGame.State.GAMEOVER);
        gameScreen.game.gameOverScreen.isReturningToGameOverScreen = true;
        gameScreen.game.setScreen(gameScreen.game.gameOverScreen);
        gameScreen.game.gameOverScreen.setBackgroundY(background.getY());
        gameScreen.game.prefs.putInteger("Coins", gameScreen.game.coins);
        gameScreen.game.prefs.flush();
        gameScreen.game.particleEffect.reset();

        if (score > bestScore) {
            bestScore = score;
            gameScreen.setHighScore(bestScore);
        }
        ball.setX(BallGame.SCREEN_WIDTH / 2);
        clear();

        going = false;
    }

    public void clear() {
        blocks.clear();
        coins.clear();
        blocksVelocityY = BLOCK_VELOCITY_PRIMARY;
        blocksVelocityX = BLOCKS_VELOCITY_X_PRIMARY;
        previousBlockWidth = 0;

        isBestScored = false;
        bestScoreY = BallGame.SCREEN_HEIGHT - 100;
        newBestScore = false;
        background.reset(blocksVelocityY);

        gameScreen.game.gameOverScreen.reset();
        gameScreen.game.level = 1;
        gameScreen.game.changeLevel(gameScreen.game.level);
        ball.setVelocityX(gameScreen.game.BALL_VELOCITY_X);
        coinAdded = false;
        coinValue = 1;
        gameScreen.game.currentCoinValue = 0;
        gameScreen.game.setCoin(gameScreen.game.currentCoinValue);
        gameScreen.getRenderer().resetFont();
        coinSpawnDelta = COIN_SPAWN_DELTA_PRIMARY;
        lastBlock = null;
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
    }

    public void resetCoins() {
        coinsInGame = 0;
    }

    public int getBestScoreY() {
        return bestScoreY;
    }

    public Background getBackground() {
        return background;
    }

    private void updateBlocks(float delta) {
        while (blockIterator.hasNext()) {
            block = blockIterator.next();

            if (ball.overlaps(block))
                endGame();

            if (block.getY() + block.getHeight() < ball.getY() - 10) {
                if (!block.isDisposed()) {
                    newScore = true;
                    score++;
                    setRendererCoinX();
                    checkScore();
                }

                block.dispose();
            }

            if (block.getY() + block.getHeight() <= 0) {
                blockIterator.remove();
                increaseVelocity = true;
            }
            block.update(delta);
        }
    }

    private void checkScore() {
        updateLevel();

        if (!isBestScored && score > bestScore) {
            newBestScore = true;
            isBestScored = true;
        }
    }

    private void updateLevel() {
        if (score % 25 == 0 && score > 0) {
            gameScreen.game.changeLevel(++gameScreen.game.level);
            if (gameScreen.game.currentCoinValue < 2) {
                gameScreen.game.setCoin(++gameScreen.game.currentCoinValue);
                coinValue = gameScreen.game.currentCoinValue + 1;
            }
        }
    }

    private void updateCoins(float delta) {
        while (coinIterator.hasNext()) {
            coin = coinIterator.next();

            if (coin.getY() + coin.getRadius() * 2 < ball.getY() - 10)
                coin.dispose();

            if (!coin.update(delta))
                coinIterator.remove();

            if (coin.overlaps(ball)) {
                if (gameScreen.game.sound)
                    gameScreen.game.coinSound.play();
                gameScreen.game.coins += coin.value;

                coinAddedChords.set(coin.getBoundingCircle().x, coin.getBoundingCircle().y);
                coinIterator.remove();
                coinAdded = true;
                gameScreen.getRenderer().setValue(coin.value);
            }
        }
    }

    private void setRendererCoinX() {
        if (getScore() < 10)
            gameScreen.getRenderer().scoreX = BallGame.SCREEN_WIDTH / 2;
        else if (getScore() >= 10 && getScore() < 100)
            gameScreen.getRenderer().scoreX = BallGame.SCREEN_WIDTH / 2 - PRIMARY_SCORE_SHIFT_X;
        else if (getScore() >= 100)
            gameScreen.getRenderer().scoreX = BallGame.SCREEN_WIDTH / 2 - PRIMARY_SCORE_SHIFT_X - 10;
    }

    private void increaseVelocity() {
        if (blocksVelocityY < BLOCKS_MAX_VELOCITY_Y)
            blocksVelocityY += INCREASE_BLOCKS_VELOCITY;

        if (ball.getVelocityX() < BALL_MAX_VELOCITY_X && ball.getVelocityX() > -BALL_MAX_VELOCITY_X) {
            if (ball.getVelocityX() < 0)
                ball.setVelocityX(ball.getVelocityX() - INCREASE_BALL_VELOCITY);
            else
                ball.setVelocityX(ball.getVelocityX() + INCREASE_BALL_VELOCITY);

            if (ball.changeDirectionScale > Ball.BALL_MAX_CHANGE_DIRECTION_SCALE)
                ball.changeDirectionScale += 10;
        }

        if (blocksVelocityX < BLOCKS_MAX_VELOCITY_X)
            blocksVelocityX += INCREASE_BLOCK_VELOCITY_X;

        if (coinSpawnDelta > SPAWN_MIN_DELTA)
            coinSpawnDelta -= DECREASE_SPAWN_DELTA_TIME / 2;

        for (Block block : blocks) {
            block.setVelocity(blocksVelocityY);
            if (block.getVelocityX() != 0)
                block.setVelocityX(blocksVelocityX);
        }

        for (Coin coin : coins) {
            coin.setVelocity(blocksVelocityY);
        }
        increaseVelocity = false;
    }

    private void checkBestScore(float delta) {
        if (bestScoreY >= -30 && newBestScore) {
            bestScoreY -= blocksVelocityY * delta;
        } else {
            newBestScore = false;
        }
    }

    public GameScreen getScreen() {
        return gameScreen;
    }

    public float getBlocksVelocityY() {
        return blocksVelocityY;
    }

    public Array<Coin> getCoins() {
        return coins;
    }
}

