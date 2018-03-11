package com.pektusin.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pektusin.game.BallGame;
import com.pektusin.game.Objects.Background;
import com.pektusin.game.Objects.Ball;
import com.pektusin.game.Util.MyScreen;

/**
 * Created by pektusin on 7/28/2016.
 */
public class GameOverScreen implements MyScreen {
    public boolean isReturningToNextScreen = false;
    public boolean isReturningToGameOverScreen = true;

    public MyScreen nextScreen;

    private int x;
    private int y;
    private int menuX;
    private int menuY;
    public int scoreShiftX;
    public float backgroundY;

    private Ball ball;
    private BallGame game;

    private BitmapFont font;
    private BitmapFont scoreFont;

    private Background background;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture playButtonTexture;
    private ShapeRenderer shapeRenderer;

    public GameOverScreen(BallGame game, OrthographicCamera camera) {
        this.game = game;

        scoreFont = BallGame.getNewScoreFont();
        font = BallGame.getNewFont();

        this.camera = camera;

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        x = BallGame.SCREEN_WIDTH / 2;
        y = BallGame.SCREEN_HEIGHT / 2 - 50;
        menuX = BallGame.SCREEN_WIDTH / 2 - 50;
        menuY = BallGame.SCREEN_HEIGHT / 4;

        ball = game.gameScreen.getWorld().getBall();

        playButtonTexture = game.playButtonTexture;

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        reset();
    }

    public void resize(int width, int height) {

    }

    public void dispose() {
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
            font.setColor(font.getColor().r, font.getColor().g, font.getColor().b,
                    font.getColor().a - (delta * 5));
            scoreFont.setColor(scoreFont.getColor().r, scoreFont.getColor().g,
                    scoreFont.getColor().b, scoreFont.getColor().a - (delta * 5));

            if (nextScreen == game.gameScreen) {
                if (playButtonTexture != game.playButtonClickedTexture) {
                    playButtonTexture = game.playButtonClickedTexture;
                }
            }

            if (batch.getColor().a <= 0.1) {
                game.setScreen(nextScreen);
                game.setState(nextScreen.getState());
                if (nextScreen == game.gameScreen) {
                    game.gameScreen.getWorld().resetScore();
                    game.gameScreen.getWorld().resetCoins();
                }
                isReturningToNextScreen = false;
                isReturningToGameOverScreen = true;
            }
        } else if (isReturningToGameOverScreen) {
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b,
                    batch.getColor().a + (delta * 5));
            font.setColor(font.getColor().r, font.getColor().g, font.getColor().b,
                    font.getColor().a + (delta * 5));
            scoreFont.setColor(scoreFont.getColor().r, scoreFont.getColor().g,
                    scoreFont.getColor().b, scoreFont.getColor().a + (delta * 5));


            if (playButtonTexture != game.playButtonTexture) {
                playButtonTexture = game.playButtonTexture;
            }

            if (batch.getColor().a >= 0.9) {
                isReturningToGameOverScreen = false;
            }
        }

        if (game.gameScreen.getWorld().getScore() < 10)
            scoreShiftX = 0;
        else if (game.gameScreen.getWorld().getScore() >= 10 && game.gameScreen.getWorld().getScore() < 100)
            scoreShiftX = game.gameScreen.getWorld().PRIMARY_SCORE_SHIFT_X;
        else if (game.gameScreen.getWorld().getScore() >= 100)
            scoreShiftX = game.gameScreen.getWorld().PRIMARY_SCORE_SHIFT_X + 10;

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, BallGame.SCREEN_HEIGHT - 70, BallGame.SCREEN_WIDTH, 75, game.firstColor,  game.firstColor, game.secondColor, game.secondColor);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();

        batch.setShader(BallGame.FONT_SHADER);
        scoreFont.draw(batch, "best " + game.gameScreen.getHighScore(), x - 105, y + 300);
        scoreFont.draw(batch, String.valueOf(game.gameScreen.getWorld().getScore()), x - scoreShiftX, 795);
        batch.setShader(null);

        batch.draw(playButtonTexture, x - 77, y - 25, 150, 150);
        font.draw(batch, "MENU", menuX, menuY);
        batch.end();
    }

    public void show() {

    }

    public void hide() {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void reset() {
        isReturningToNextScreen = false;

        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 0);
        font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, 0);
        scoreFont.setColor(scoreFont.getColor().r, scoreFont.getColor().g, scoreFont.getColor().b, 0);
    }

    public void setBackgroundY(float y) {
        backgroundY = y;
    }

    public BallGame.State getState() {
        return BallGame.State.GAMEOVER;
    }

    @Override
    public void isReturningToMenuScreen() {

    }
}
