package com.pektusin.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.pektusin.game.BallGame;

/**
 * Created by pektusin on 7/28/2016.
 */
public class Ball {
    public static float BALL_MAX_CHANGE_DIRECTION_SCALE = 6000;

    public State state;

    public float changeDirectionScale = 4000;
    private Vector2 position;
    private Vector2 velocity;
    private float rotation = 0;
    private float velocityX;

    private Circle boundingCircle;

    private Texture ballTextureLeft;
    private TextureRegion tr;

    private BallGame game;

    private int d;
    private int r;

    private boolean changingVelocity = false;

    public Ball(int x, int y, int d, float velocityX, float velocityY, BallGame game) {
        this.d = d;
        r = d / 2;

        position = new Vector2(x, y);
        velocity = new Vector2(velocityX, velocityY);
        this.velocityX = velocityX;
        this.game = game;

        boundingCircle = new Circle();
        boundingCircle.set(position.x + r, position.y + r, r);

        ballTextureLeft = game.ballTextureLeft;
        tr = new TextureRegion();

        state = State.RIGHT;
        game.particleEffect.setPosition(position.x, position.y);
    }

    public void update(float delta) {
        if (changingVelocity)
            changeVelocity(delta);

        if (state == State.RIGHT) {
            if (rotation > -90)
                rotation -= delta * 1000;
        } else if (state == State.LEFT)
            if (rotation < 0)
                rotation += delta * 1000;

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        boundingCircle.setX(position.x + r);
        boundingCircle.setY(position.y + r);

        game.particleEffect.setPosition(position.x + 25, position.y + 25);
        game.particleEffect.update(delta);
    }

    private void changeVelocity(float delta) {
        if (state == State.RIGHT) {
            if (velocity.x < velocityX)
                if (velocity.x + delta * changeDirectionScale > velocityX)
                    velocity.x = velocityX;
                else
                    velocity.x += delta * changeDirectionScale;
            else
                changingVelocity = false;
        } else {
            if (velocity.x > -velocityX)
                if (velocity.x - delta * changeDirectionScale < -velocityX)
                    velocity.x = -velocityX;
                else
                    velocity.x -= delta * changeDirectionScale;
            else
                changingVelocity = false;
        }
    }

    public int getX() {
        return (int) position.x;
    }

    public void setX(int x) {
        position.x = x;
    }

    public int getY() {
        return (int) position.y;
    }

    public void setY(int y) {
        position.y = y;
    }

    public void click() {
        if (state == State.LEFT) {
            state = State.RIGHT;
        } else {
            state = State.LEFT;
        }
        changingVelocity = true;
    }

    public boolean overlaps(Block block) {
        if (position.x <= 0 || position.x + 50 >= BallGame.SCREEN_WIDTH)
            return true;

        return Intersector.overlaps(boundingCircle, block.getBoundingRectangle());
    }

    public void render(SpriteBatch batch) {
        game.particleEffect.start();
        game.particleEffect.draw(batch);

        tr.setRegion(game.ballTextureLeft);
        batch.draw(tr, position.x, position.y, r, r, d, d, 1.0f, 1.0f, rotation);
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public Circle getBoundingCircle() {
        return boundingCircle;
    }

    public enum State {
        LEFT, RIGHT
    }
}
