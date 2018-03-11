package com.pektusin.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.pektusin.game.BallGame;
import com.pektusin.game.Util.InputHandler;
import com.pektusin.game.Util.MyScreen;
import com.pektusin.game.World.GameRenderer;
import com.pektusin.game.World.GameWorld;

/**
 * Created by pektusin on 7/28/2016.
 */
public class GameScreen implements MyScreen {
    public BallGame game;
    private GameWorld world;
    private GameRenderer renderer;
    private OrthographicCamera camera;


    public GameScreen(BallGame game, OrthographicCamera camera) {
        this.game = game;

        this.camera = camera;

        world = new GameWorld(this);

        renderer = new GameRenderer(world, camera);

        Gdx.input.setInputProcessor(new InputHandler(world.getBall(), this.game));
    }

    public void dispose() {
    }

    public void hide() {

    }

    public void render(float delta) {
        if (world.going) {
            renderer.render();
            world.update(delta);
        }
    }

    public void resize(int height, int width) {

    }

    public void show() {
    }

    public void pause() {

    }

    public void resume() {

    }

    public GameWorld getWorld() {
        return world;
    }

    public int getHighScore() {
        return game.prefs.getInteger("BestScore");
    }

    public void setHighScore(int score) {
        if (score > game.prefs.getInteger("BestScore")) {
            game.prefs.putInteger("BestScore", score);
            game.prefs.flush();
        }
    }

    public GameRenderer getRenderer() {
        return renderer;
    }

    public BallGame getGame() {
        return game;
    }

    public BallGame.State getState() {
        return BallGame.State.RUNNING;
    }

    @Override
    public void isReturningToMenuScreen() {

    }

    @Override
    public void reset() {

    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
