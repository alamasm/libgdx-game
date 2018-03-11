package com.pektusin.game.Objects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.pektusin.game.BallGame;

/**
 * Created by pektusin on 8/25/2016.
 */
public class BackgroundParticle {
    public final double PARTICLE_LIFE_TIME = 5000;

    public boolean rotated;
    private float rotation;

    private ShapeRenderer shapeRenderer;

    private Vector2 size;
    private Vector2 position;
    private Vector2[] increasePosition;
    private Vector2[] increaseSize;

    private int state = 0;
    private int quantity;

    private BallGame game;
    private Color color;

    private long particleSpawnTime;

    public BackgroundParticle(float x, float y, float width, float heght, BallGame game, float alpha, boolean rotated) {
        shapeRenderer = new ShapeRenderer();

        this.rotated = rotated;

        size = new Vector2(width, heght);
        position = new Vector2(x, y);
        if (!rotated) {
            quantity = MathUtils.random(1, 3);
            increasePosition = new Vector2[quantity];
            increaseSize = new Vector2[quantity];

            for (int i = 0; i < increasePosition.length; i++) {
                increasePosition[i] = new Vector2();
                increaseSize[i] = new Vector2();

                increasePosition[i].x = MathUtils.random(5, size.x);
                increaseSize[i].x = MathUtils.random(-(size.x / 2), size.x / 2);
            }
        } else {
            quantity = 0;
            rotation = 55f;
            particleSpawnTime = TimeUtils.millis();
        }

        this.game = game;

        color = new Color(1, 1, 1, alpha);

        shapeRenderer.setProjectionMatrix(game.camera.combined);
        shapeRenderer.setAutoShapeType(true);
    }

    public void render() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);


        if (!rotated) {
            shapeRenderer.rect(position.x, position.y, size.x, size.y);

            for (int i = 0; i < increaseSize.length; i++) {
                shapeRenderer.rect(position.x + increasePosition[i].x, position.y + increasePosition[i].y, size.x + increaseSize[i].x, +size.y + increaseSize[i].x);
            }
        } else {
            shapeRenderer.rect(position.x, position.y, position.x + size.x / 2, position.y + size.y / 2, size.x, size.y, 1.0f, 1.0f, rotation);
        }
        shapeRenderer.end();
    }

    public boolean update(float delta) {
        updateColor(delta);
        if (!rotated) {
            position.y -= delta * (game.gameScreen.getWorld().getBackground().getVelocity() - 25);
            if (position.y < 0 - 75)
                return false;
        } else {
            position.y -= delta * 400;
            if (TimeUtils.millis() - particleSpawnTime >= PARTICLE_LIFE_TIME)
                return false;
        }
        return true;
    }

    public void updateColor(float delta) {
        switch (state) {
            case 0:
                color.a -= delta * 0.2;
                if (color.a <= 0)
                    state = 1;
                break;
            case 1:
                color.a += delta * 0.2;
                if (color.a >= 0.4)
                    state = 0;
                break;
        }
    }

    public float getY() {
        return position.y;
    }

    public float getX() {
        return position.x;
    }
}
