package com.pektusin.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.pektusin.game.BallGame;
import com.pektusin.game.Objects.Background;
import com.pektusin.game.Util.Animation.ScreensAnimation;
import com.pektusin.game.Util.InMainMenuScreen;
import com.pektusin.game.Util.MyScreen;

/**
 * Created by pektusin on 8/7/2016.
 */
public class ShopScreen extends InMainMenuScreen implements MyScreen {
    private final double DIRECTION_CHANGE_DELTA = 2500;
    public Vector2 ballsChords[];

    private BallGame game;
    private Background background;

    private BitmapFont font;
    private BitmapFont coinFont;

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private boolean direction = true;
    private double directionLastChangeTime = 0;
    private double notEnoughLastTime = 0;

    private int x;
    private int y;
    private int coinX = 175;

    private float height;

    private int buyBall;

    private float delta;

    private boolean firstDraw = true;
    private boolean firstDragg = true;

    public ShopScreen(BallGame game, OrthographicCamera camera) {
        this.game = game;

        font = BallGame.getNewFont();
        coinFont = BallGame.getNewScoreFont();

        this.camera = camera;

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        ballsChords = new Vector2[game.ballsCount];

        for (int i = 0; i < ballsChords.length; i++)
            ballsChords[i] = new Vector2();

        fonts = new BitmapFont[2];
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ScreensAnimation.checkReturning(this, delta, game);
        this.delta = delta;

        background = game.gameScreen.getWorld().getBackground();

        batch.begin();
        background.render(batch);
        background.updateParticlesColor(delta);
        background.updateRotatedParticles(delta);

        if (game.getState() == BallGame.State.SHOP) {

            drawBalls();

            batch.draw(game.backButtonTexture, 200, 75, 75, 75);

            setCoinX();

            coinFont.draw(batch, String.valueOf(game.coins), coinX, 770);
            batch.draw(game.coinTexturesPrimary[2], 275, 715, 50, 50);
            batch.setShader(BallGame.FONT_SHADER);

            if (TimeUtils.millis() - directionLastChangeTime >= DIRECTION_CHANGE_DELTA) {
                direction = !direction;
                directionLastChangeTime = TimeUtils.millis();
            }
        } else if (game.getState() == BallGame.State.SHOP_DIALOG || game.getState() == BallGame.State.SHOP_NOT_ENOUGH) {
            if (game.coins > game.ballsPrices[buyBall]) {
                font.draw(batch, "BUY FOR " + game.ballsPrices[buyBall] + " ?", 110, 400);
                font.draw(batch, "NO", 110, 325);
                font.draw(batch, "YES", 315, 325);
            } else {
                if (notEnoughLastTime == 0) {
                    notEnoughLastTime = TimeUtils.millis();
                }

                game.setState(BallGame.State.SHOP_NOT_ENOUGH);

                font.draw(batch, "NOT ENOUGH COINS", 20, 400);

                if (TimeUtils.millis() - notEnoughLastTime >= 250) {
                    game.setState(BallGame.State.SHOP);
                    notEnoughLastTime = 0;
                }
            }
        }
        batch.setShader(null);
        batch.end();
    }

    private void setCoinX() {
        if (game.coins >= 10000000)
            coinX = 5;
        if (game.coins >= 1000000 && game.coins < 10000000)
            coinX = 35;
        if (game.coins >= 100000 && game.coins < 1000000)
            coinX = 65;
        if (game.coins >= 10000 && game.coins < 100000)
            coinX = 95;
        if (game.coins >= 1000 && game.coins < 10000)
            coinX = 125;
        if (game.coins >= 100 && game.coins < 1000)
            coinX = 155;
        if (game.coins < 100 && game.coins >= 10)
            coinX = 185;
        else if (game.coins < 10)
            coinX = 215;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void drawBalls() {
        for (int i = 0; i < game.ballsCount; i++) {

            if (firstDraw) {
                x = 85;
                y = 595;
            }

            ballsChords[i].x = x;
            ballsChords[i].y = y - 100 * i;
            if (game.prefs.getBoolean("Ball" + i))
                batch.draw(game.ballTexturesLeft[i], ballsChords[i].x, ballsChords[i].y, 75, 75);

            if (i == game.currentBall) {
                font.draw(batch, " - SELECTED", ballsChords[i].x + 75, ballsChords[i].y + 50);
            }

            if (i > 0) {
                if (!game.prefs.getBoolean("Ball" + i)) {
                    font.draw(batch, " - " + String.valueOf(game.ballsPrices[i]), x + 75, y + 50 - (100 * i));
                    batch.draw(game.ballShopTexture, ballsChords[i].x, ballsChords[i].y, 75, 75);
                }
            }
        }


        firstDraw = false;
    }

    public void buy(int ball) {
        if (ball == 0) return;

        game.setState(BallGame.State.SHOP_DIALOG);

        buyBall = ball;
    }

    public void bought() {
        game.prefs.putBoolean("Ball" + buyBall, true);
        game.coins -= game.ballsPrices[buyBall];
        game.prefs.putInteger("Coins", game.coins);
        game.prefs.flush();
        game.setState(BallGame.State.SHOP);
    }

    public void move(float screenY) {
        if (game.ballsCount <= 5) return;

        if (firstDragg) {
            height = y - screenY;
            firstDragg = false;
        }

        if (screenY + height > 595 && screenY + height < 690)
            y = (int) (screenY + height);
    }

    public void resetDragg() {
        firstDragg = true;
    }

    public void reset() {
        isReturningToThisScreen = true;
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 0);
        coinFont.setColor(coinFont.getColor().r, coinFont.getColor().g, coinFont.getColor().b, 0);
        font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, 0);
    }

    public BallGame.State getState() {
        return BallGame.State.SHOP;
    }

    public void isReturningToMenuScreen(){
        isReturningToMenuScreen = true;
    }

    @Override
    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    @Override
    public BitmapFont[] getFont() {
        fonts[0] = font;
        fonts[1] = coinFont;
        return fonts;
    }
}