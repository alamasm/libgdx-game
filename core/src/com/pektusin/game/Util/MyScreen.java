package com.pektusin.game.Util;

import com.badlogic.gdx.Screen;
import com.pektusin.game.BallGame;

/**
 * Created by pektusin on 8/10/2016.
 */
public interface MyScreen extends Screen {
    BallGame.State getState();
    void isReturningToMenuScreen();
    void reset();
}
