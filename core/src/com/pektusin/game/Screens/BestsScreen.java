package com.pektusin.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pektusin.game.BallGame;
import com.pektusin.game.Objects.Background;
import com.pektusin.game.Util.Animation.ScreensAnimation;
import com.pektusin.game.Util.InMainMenuScreen;
import com.pektusin.game.Util.MyScreen;

/**
 * Created by pektusin on 8/1/2016.
 */
public class BestsScreen extends InMainMenuScreen implements MyScreen {
    private BallGame game;

    private BitmapFont font;

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private Background background;

    public BestsScreen(BallGame game, OrthographicCamera camera) {
        this.game = game;

        font = BallGame.getNewFont();

        this.camera = camera;

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        fonts = new BitmapFont[1];
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ScreensAnimation.checkReturning(this, delta, game);

        batch.begin();
        background = game.gameScreen.getWorld().getBackground();
        background.render(batch);
        background.updateParticlesColor(delta);
        background.updateRotatedParticles(delta);

        batch.setShader(BallGame.FONT_SHADER);
        font.draw(batch, "YOU : " + game.gameScreen.getHighScore(), BallGame.SCREEN_WIDTH / 2, BallGame.SCREEN_WIDTH / 2 - 80);
        batch.setShader(null);

        batch.draw(game.backButtonTexture, BallGame.SCREEN_WIDTH / 2 - 80, 75, 75, 75);//200
        batch.end();
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

    public BallGame.State getState() {
        return BallGame.State.BESTS;
    }

    public void isReturningToMenuScreen() {
        isReturningToMenuScreen = true;
    }

    public void reset() {
        isReturningToThisScreen = true;
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 0);
        font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, 0);
    }

    public SpriteBatch getSpriteBatch(){
        return batch;
    }

    public BitmapFont[] getFont(){
        fonts[0] = font;
        return fonts;
    }
}
