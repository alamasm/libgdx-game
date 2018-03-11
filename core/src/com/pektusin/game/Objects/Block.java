package com.pektusin.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pektusin.game.BallGame;

/**
 * Created by pektusin on 7/28/2016.
 */
public class Block {
    private Vector2 velocity;
    private Vector2 position;
    private Vector2 size;

    private float primaryX;

    private boolean disposed = false;

    private Rectangle boundingRectangle;

    private Texture blockTexture;

    private BallGame game;

    private boolean going;
    private boolean first = true;

    public Block(float vel, int x, int y, int width, int height, BallGame game) {
        velocity = new Vector2(0, vel);

        position = new Vector2(x, y);
        size = new Vector2(width, height);

        this.game = game;

        boundingRectangle = new Rectangle();

        boundingRectangle = new Rectangle();

        boundingRectangle.set(x, y, width, height);

        blockTexture = game.blockTextures[0];

        primaryX = position.x;

        velocity.x = 0;
    }

    public void update(float delta) {
        position.y -= velocity.y * delta;
        if (primaryX == 0) {
            move(-50, 0, delta);
            if (first) {
                going = false;
                first = false;
            }
        } else {
            move(0, 50, delta);
            if (first) {
                going = true;
                first = false;
            }
        }

        boundingRectangle.setX(position.x);
        boundingRectangle.setY(position.y);
    }

    public void dispose() {
        velocity.y += 50;
        velocity.x = 0;
        disposed = true;
    }

    private void move(int min, int max, float delta) {
        if (going) {
            position.x += velocity.x * delta;
            if (position.x - primaryX >= max)
                going = false;
        } else if (!going) {
            position.x -= velocity.x * delta;
            if (position.x - primaryX <= min)
                going = true;
        }
    }

    public int getX() {
        return (int) position.x;
    }

    public int getY() {
        return (int) position.y;
    }

    public int getWidth() {
        return (int) size.x;
    }

    public int getHeight() {
        return (int) size.y;
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public void setVelocity(float velocity) {
        this.velocity.y = velocity;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void render(SpriteBatch batch) {
        batch.draw(blockTexture, position.x, position.y, size.x, size.y);
    }

    public int getVelocityX() {
        return (int) velocity.x;
    }

    public void setVelocityX(float velocityX) {
        velocity.x = velocityX;
    }
}
