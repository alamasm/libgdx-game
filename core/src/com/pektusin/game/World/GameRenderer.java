package com.pektusin.game.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.pektusin.game.BallGame;
import com.pektusin.game.Objects.Ball;
import com.pektusin.game.Objects.Block;
import com.pektusin.game.Objects.Coin;

import java.util.Iterator;

/**
 * Created by pektusin on 7/28/2016.
 */
public class GameRenderer {
    public int scoreX = BallGame.SCREEN_WIDTH / 2;
    private GameWorld world;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Ball ball;

    private int coinValue;

    private BitmapFont font;
    private BitmapFont coinFont;
    private BitmapFont scoreFont;

    private Iterator<Block> blockIterator;
    private Iterator<Coin> coinIterator;

    private ShapeRenderer shapeRenderer;

    public GameRenderer(GameWorld world, OrthographicCamera camera) {
        this.world = world;

        this.camera = camera;

        batch = new SpriteBatch();
        batch.setProjectionMatrix(this.camera.combined);

        font = world.getScreen().getGame().font;
        scoreFont = world.getScreen().getGame().scoreFont;
        coinFont = new BitmapFont(Gdx.files.internal("Data/SnapITC.fnt"));

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    public void render() {
        Gdx.gl.glClearColor(50, 50, 50, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ball = world.getBall();

        blockIterator = world.getBlocks().iterator();
        coinIterator = world.getCoins().iterator();

        batch.begin();

        batch.disableBlending();
        world.getBackground().render(batch);
        batch.enableBlending();

        ball.render(batch);

        while (blockIterator.hasNext())
            blockIterator.next().render(batch);
        while (coinIterator.hasNext())
            coinIterator.next().render(batch);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, BallGame.SCREEN_HEIGHT - 100, BallGame.SCREEN_WIDTH, 75, world.getScreen().getGame().firstColor,//0, 730, 480, 75
                world.getScreen().getGame().firstColor, world.getScreen().getGame().secondColor,
                world.getScreen().getGame().secondColor);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        batch.setShader(BallGame.FONT_SHADER);
        scoreFont.draw(batch, String.valueOf(world.getScore()), scoreX, BallGame.SCREEN_HEIGHT - 50);

        if (world.newBestScore) {
            font.draw(batch, "NEW BEST", BallGame.SCREEN_WIDTH / 2 - 100, world.getBestScoreY());
        }

        if (world.coinAdded) {
            if (world.coinAddedChords.x <= BallGame.SCREEN_WIDTH - 20)
                coinFont.draw(batch, "+" + coinValue, world.coinAddedChords.x, world.coinAddedChords.y);
            else
                coinFont.draw(batch, "+" + coinValue, world.coinAddedChords.x - 40, world.coinAddedChords.y);
        }
        batch.setShader(null);

        batch.end();
    }

    public void setValue(int coinValue) {
        this.coinValue = coinValue;
    }

    public void increaseCoinFontAlpha(float delta) {
        coinFont.setColor(coinFont.getColor().r, coinFont.getColor().g, coinFont.getColor().b, coinFont.getColor().a - world.getBlocksVelocityY() * 0.009f * delta);
        if (coinFont.getColor().a <= 0) {
            world.coinAdded = false;
            coinFont.setColor(coinFont.getColor().r, coinFont.getColor().g, coinFont.getColor().b, 1);
        }
    }

    public void resetFont() {
        coinFont.setColor(coinFont.getColor().r, coinFont.getColor().g, coinFont.getColor().b, 1);
    }
}
