package com.pektusin.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.pektusin.game.BallGame;

/**
 * Created by pektusin on 8/1/2016.
 */
public class Coin {
    private final float RADIUS = 12.5f;

    public Texture animTexture;
    public int value;
    public boolean animation = false;

    private Vector2 position;
    private Vector2 velocity;

    private Circle boundingCircle;

    private Texture coinTexture;

    private BallGame game;

    public Coin(int x, int y, BallGame game, float velocity, int value) {
        this.game = game;
        position = new Vector2(x, y);
        boundingCircle = new Circle(position.x + RADIUS, position.y, RADIUS);
        this.velocity = new Vector2(0, velocity);
        this.value = value;
        coinTexture = game.coinTexture;
    }

    public void render(SpriteBatch batch) {
        batch.draw(coinTexture, position.x, position.y, 20, 20);

        if (animation)
            batch.draw(animTexture, position.x, position.y, 20, 20);
    }

    public boolean update(float delta) {
        position.y -= velocity.y * delta;

        boundingCircle.y = position.y + RADIUS;

        return position.y >= -30;
    }

    public void dispose() {
        velocity.y += 50;
    }

    public void setVelocity(float velocity) {
        this.velocity.y = velocity;
    }

    public Circle getBoundingCircle() {
        return boundingCircle;
    }

    public boolean overlaps(Block block) {
        return Intersector.overlaps(boundingCircle, block.getBoundingRectangle());
    }

    public boolean overlaps(Ball ball) {
        return boundingCircle.overlaps(ball.getBoundingCircle());
    }

    public float getY() {
        return position.y;
    }

    public float getRadius() {
        return RADIUS;
    }
}
