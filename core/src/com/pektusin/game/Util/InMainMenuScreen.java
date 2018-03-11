package com.pektusin.game.Util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by pektusin on 8/26/2016.
 */
public abstract class InMainMenuScreen {
    public boolean isReturningToMenuScreen;
    public boolean isReturningToThisScreen;

    public BitmapFont[] fonts;

    public abstract SpriteBatch getSpriteBatch();
    public abstract BitmapFont getFont()[];

}
