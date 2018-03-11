package com.pektusin.game.Objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.pektusin.game.BallGame;

import java.util.Iterator;

/**
 * Created by pektusin on 7/30/2016.
 */
public class Background {
    private float BACKGROUND_PARTICLE_SPAWN_DELTA = 100;
    private int BACKGROUND_PARTICLE_MAX_SIZE = 30;
    private long SPAWN_ROTATED_PARTICLE_DELTA = 5000;
    private int BACKGROUND_PARTICLE_MIN_SIZE = 15;

    private BackgroundParticle lastSpawnParticle;
    private long lastRotatedParticleSpawnTime = 0;

    private Vector2 position;
    private Vector2 velocity;

    private BallGame game;

    private ShapeRenderer shapeRenderer;

    private Color color;
    private Color nextColor;

    private boolean first = true;

    private int[] states;

    private Array<BackgroundParticle> backgroundParticles;
    private Iterator<BackgroundParticle> backgroundParticleIterator;

    public Background(int y, float velocity, BallGame game) {
        position = new Vector2(0, y);
        this.velocity = new Vector2(0, velocity);
        this.game = game;

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(game.camera.combined);
        shapeRenderer.setAutoShapeType(true);

        states = new int[2];

        color = new Color(0.392157f, 0.584314f, 0.929412f, 1);
        nextColor = new Color(0.55f, 0.584314f, 0.929412f, 1);

        backgroundParticles = new Array<BackgroundParticle>();

        for (int i = 0; i < 800 / BACKGROUND_PARTICLE_SPAWN_DELTA; i++) {
            spawnBackgroundParticle(i * BACKGROUND_PARTICLE_SPAWN_DELTA);
        }
    }

    private void spawnBackgroundParticle() {
        float width = MathUtils.random(BACKGROUND_PARTICLE_MIN_SIZE, BACKGROUND_PARTICLE_MAX_SIZE);

        float x = MathUtils.random(-10, 490);

        BackgroundParticle backgroundParticle = new BackgroundParticle(x, 800, width, width, game, MathUtils.random(0, 0.4f), false);
        backgroundParticles.add(backgroundParticle);

        lastSpawnParticle = backgroundParticle;
    }

    public void update(float delta, float velocity) {
        this.velocity.y = velocity;
        position.y -= (this.velocity.y - 25) * delta;
        if (first) {
            shapeRenderer.setProjectionMatrix(game.gameScreen.getCamera().combined);
            first = false;
        }

        if (800 - lastSpawnParticle.getY() > BACKGROUND_PARTICLE_SPAWN_DELTA)
            spawnBackgroundParticle();

        if (TimeUtils.millis() - lastRotatedParticleSpawnTime >= SPAWN_ROTATED_PARTICLE_DELTA)
            if (MathUtils.randomBoolean())
                spawnRotatedParticle();

        updateParticles(delta);
        updateColor(delta, color, 0.007f, 0);
        updateColor(delta, nextColor, 0.01f, 1);
    }

    public void updateRotatedParticles(float delta) {
        if (TimeUtils.millis() - lastRotatedParticleSpawnTime >= SPAWN_ROTATED_PARTICLE_DELTA)
            if (MathUtils.randomBoolean())
                spawnRotatedParticle();

        backgroundParticleIterator = backgroundParticles.iterator();

        BackgroundParticle backgroundParticle;

        while (backgroundParticleIterator.hasNext()) {
            backgroundParticle = backgroundParticleIterator.next();
            if (backgroundParticle.rotated)
                if (!backgroundParticle.update(delta))
                    backgroundParticleIterator.remove();
        }
    }

    private void updateParticles(float delta) {
        backgroundParticleIterator = backgroundParticles.iterator();

        while (backgroundParticleIterator.hasNext())
            if (!backgroundParticleIterator.next().update(delta))
                backgroundParticleIterator.remove();
    }

    private void updateColor(float delta, Color color, float constant, Integer state) {
        switch (states[state]) {
            case -1:
                if (color.r < 0.392157)
                    color.r += delta * constant;
                if (color.g < 0.584314)
                    color.g += delta * constant;
                if (color.b < 0.929412)
                    color.b += delta * constant;
                if (color.r >= 0.392157 && color.g >= 0.584314 && color.b >= 0.929412)
                    states[state]++;
            case 0:
                if (color.r < 0.8)
                    color.r += delta * constant;
                if (color.r >= 0.8)
                    states[state]++;
                break;
            case 1:
                if (color.b > 0.445098) {
                    color.b -= delta * constant;
                }
                if (color.r > 0.3190668)
                    color.r -= delta * constant;
                if (color.g > 0.10335352)
                    color.g -= delta * constant;
                if (color.b <= 0.445098 && color.r <= 0.3190668 && color.g <= 0.10335352)
                    states[state]++;
                break;
            case 2:
                if (color.r > 0.0980392)
                    color.r -= delta * constant;
                if (color.g > 0.0980392)
                    color.g -= delta * constant;
                if (color.b >= 0.439216)
                    color.b -= delta * constant;
                if (color.r <= 0.0980392 && color.g <= 0.0980392 && color.b <= 0.439216)
                    states[state]++;
                break;
            case 3:
                if (color.r > 0)
                    color.r -= delta * constant;
                if (color.g > 0)
                    color.g -= delta * constant;
                if (color.b > 0.2)
                    color.b -= delta * constant;
                if (color.b <= 0.2)
                    states[state]++;
                break;
            case 4:
                if (color.r < 0.254902)
                    color.r += delta * constant;
                if (color.g < 0.411765)
                    color.g += delta * constant;
                if (color.b < 0.882353)
                    color.b += delta * constant;
                if (color.r >= 0.254902 && color.g >= 0.411765 && color.b >= 0.882353)
                    states[state] = -1;
        }
    }

    public float getY() {
        return position.y;
    }

    public void reset(float velocity) {
        this.velocity.y = velocity;
    }

    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, BallGame.SCREEN_WIDTH, BallGame.SCREEN_HEIGHT, color, color, nextColor, nextColor);
        shapeRenderer.end();

        renderBackgroundParticles();
        batch.begin();
    }

    private void renderBackgroundParticles() {
        backgroundParticleIterator = backgroundParticles.iterator();

        while (backgroundParticleIterator.hasNext())
            backgroundParticleIterator.next().render();
    }

    public float getVelocity() {
        return velocity.y;
    }

    public void updateParticlesColor(float delta) {
        backgroundParticleIterator = backgroundParticles.iterator();

        while (backgroundParticleIterator.hasNext())
            backgroundParticleIterator.next().updateColor(delta);
    }

    public void spawnBackgroundParticle(float y) {
        float width = MathUtils.random(BACKGROUND_PARTICLE_MIN_SIZE, BACKGROUND_PARTICLE_MIN_SIZE);

        float x = MathUtils.random(-10, BallGame.SCREEN_WIDTH);

        BackgroundParticle backgroundParticle = new BackgroundParticle(x, y, width, width, game, MathUtils.random(0, 0.4f), false);

        backgroundParticles.add(backgroundParticle);

        lastSpawnParticle = backgroundParticle;
    }

    public void spawnRotatedParticle() {
        float width = MathUtils.random(BACKGROUND_PARTICLE_MIN_SIZE, BACKGROUND_PARTICLE_MAX_SIZE);
        float x = MathUtils.random(10, 150);
        float y = BallGame.SCREEN_HEIGHT;
        BackgroundParticle backgroundParticle = new BackgroundParticle(x, y, width, width, game, MathUtils.random(0, 0.4f), true);
        backgroundParticles.add(backgroundParticle);

        lastRotatedParticleSpawnTime = TimeUtils.millis();
    }
}
