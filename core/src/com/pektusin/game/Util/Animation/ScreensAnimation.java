package com.pektusin.game.Util.Animation;

import com.pektusin.game.BallGame;
import com.pektusin.game.Util.InMainMenuScreen;

/**
 * Created by pektusin on 9/2/2016.
 */
public class ScreensAnimation {
    public static void checkReturning(InMainMenuScreen screen, float delta, BallGame game) {
        if (screen.isReturningToThisScreen) {

            screen.getSpriteBatch().setColor(screen.getSpriteBatch().getColor().r,
                    screen.getSpriteBatch().getColor().g, screen.getSpriteBatch().getColor().b,
                    screen.getSpriteBatch().getColor().a + (delta * 5));

            for (int i = 0; i < screen.getFont().length; i++)
                screen.getFont()[i].setColor(screen.getFont()[i].getColor().r, screen.getFont()[i].getColor().g,
                        screen.getFont()[i].getColor().b, screen.getFont()[i].getColor().a + (delta * 5));

            if (screen.getFont()[0].getColor().a >= 0.9 && screen.getSpriteBatch().getColor().a >= 0.1) {
                screen.getSpriteBatch().setColor(screen.getSpriteBatch().getColor().r, screen.getSpriteBatch().getColor().g, screen.getSpriteBatch().getColor().b, 1);
                screen.isReturningToThisScreen = false;
            }
        } else if (screen.isReturningToMenuScreen) {

            screen.getSpriteBatch().setColor(screen.getSpriteBatch().getColor().r,
                    screen.getSpriteBatch().getColor().g, screen.getSpriteBatch().getColor().b,
                    screen.getSpriteBatch().getColor().a - (delta * 5));

            for (int i = 0; i < screen.getFont().length; i++)
                screen.getFont()[i].setColor(screen.getFont()[i].getColor().r, screen.getFont()[i].getColor().g,
                        screen.getFont()[i].getColor().b, screen.getFont()[i].getColor().a - (delta * 5));

            if (screen.getFont()[0].getColor().a <= 0.1 && screen.getSpriteBatch().getColor().a <= 0.1) {
                screen.isReturningToMenuScreen = false;
                game.setState(game.mainMenuScreen.getState());
                game.setScreen(game.mainMenuScreen);
            }
        }
    }
}
