package com.pektusin.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pektusin.game.BallGame;
import com.pektusin.game.Objects.Background;
import com.pektusin.game.Util.MyScreen;

/**
 * Created by pektusin on 7/28/2016.
 */
public class MainMenuScreen implements MyScreen {
    public boolean isReturningToNextScreen = false;
    public boolean isReturningToMenuScreen = true;

    public MyScreen nextScreen;
    public Texture playButtonTexture;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private BallGame game;
    private Background background;

    private int x;
    private int y;
    private int settingsX;
    private int settingsY;

    private ShapeRenderer shapeRenderer;

    public MainMenuScreen(BallGame game, OrthographicCamera camera) {
        super();

        this.camera = camera;

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        this.game = game;

        playButtonTexture = game.playButtonTexture;

        x = BallGame.SCREEN_WIDTH / 2 - 150;
        y = BallGame.SCREEN_HEIGHT / 2 - 100;
        settingsX = BallGame.SCREEN_WIDTH / 3;
        settingsY = BallGame.SCREEN_HEIGHT / 3;

        reset();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    public void dispose() {
    }

    public void show() {
    }

    public void hide() {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void resize(int height, int width) {

    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        background = game.gameScreen.getWorld().getBackground();
        background.render(batch);
        background.updateParticlesColor(delta);
        background.updateRotatedParticles(delta);

        if (isReturningToNextScreen) {
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b,
                    batch.getColor().a - (delta * 5));

            if (playButtonTexture != game.playButtonClickedTexture && nextScreen == game.gameScreen) {
                playButtonTexture = game.playButtonClickedTexture;
            }

            if (batch.getColor().a <= 0.1) {
                if (nextScreen != game.gameScreen)
                    nextScreen.reset();

                game.setState(nextScreen.getState());
                game.setScreen(nextScreen);
                isReturningToNextScreen = false;
                if (nextScreen == game.gameScreen) {
                    game.gameScreen.getWorld().resetScore();
                }
                isReturningToMenuScreen = true;
            }
        } else if (isReturningToMenuScreen) {
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b,
                    batch.getColor().a + (delta * 5));

            if (playButtonTexture != game.playButtonTexture) {
                playButtonTexture = game.playButtonTexture;
            }

            if (batch.getColor().a >= 0.9) {
                isReturningToMenuScreen = false;
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1);
            }
        }

        batch.draw(playButtonTexture, x + 65, y - 25, 150, 150);
        batch.draw(game.settingsButtonTexture, settingsX - 100, settingsY - 100, 75, 75);
        batch.draw(game.shopButtonTexture, settingsX + 200, settingsY - 100, 75, 75);
        batch.draw(game.bestsButtonTexture, settingsX + 50, settingsY - 100, 75, 75);

        batch.end();
    }

    public void reset() {
        isReturningToMenuScreen = true;
        nextScreen = null;
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 0);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public BallGame.State getState() {
        return BallGame.State.MAIN_MENU;
    }

    @Override
    public void isReturningToMenuScreen() {

    }
}
