package com.pektusin.game.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.pektusin.game.BallGame;
import com.pektusin.game.Objects.Ball;

/**
 * Created by pektusin on 7/28/2016.
 */
public class InputHandler implements InputProcessor {
    Vector3 touchPos = new Vector3();
    private Ball ball;
    private BallGame game;
    private boolean changing = false;
    private double lastChangeTime = 0;

    public InputHandler(Ball ball, BallGame game) {
        super();
        this.ball = ball;
        this.game = game;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPos.x = screenX;
        touchPos.y = screenY;
        touchPos.z = 0;
        game.mainMenuScreen.getCamera().unproject(touchPos);

        switch (game.getState()) {
            case RUNNING:
                ball.click();
                if (game.sound)
                    game.clickSound.play();
                break;
            case MAIN_MENU:
                if (touchPos.y >= 50 && touchPos.y <= 125) {
                    if (touchPos.x >= 50 && touchPos.x <= 125)
                        game.mainMenuScreen.nextScreen = game.settingsScreen;
                    else if (touchPos.x >= 200 && touchPos.x <= 275)
                        game.mainMenuScreen.nextScreen = game.bestsScreen;
                    else if (touchPos.x >= 350 && touchPos.x <= 425) {
                        game.shopScreen.reset();
                        game.mainMenuScreen.nextScreen = game.shopScreen;
                    }
                } else if (touchPos.x >= 165 && touchPos.x <= 365 && touchPos.y >= 375 && touchPos.y <= 575) {
                    game.mainMenuScreen.nextScreen = game.gameScreen;
                    game.gameScreen.getWorld().going = true;
                }
                if (game.mainMenuScreen.nextScreen != null)
                    game.mainMenuScreen.isReturningToNextScreen = true;
                break;
            case SETTINGS:
                if (touchPos.x >= 38 && touchPos.x <= 317 && touchPos.y >= 368 && touchPos.y <= 400) {
                    if (TimeUtils.millis() - lastChangeTime > 300) {
                        changing = true;
                        lastChangeTime = TimeUtils.millis();
                    }
                    if (changing) {
                        game.sound = !game.sound;
                        game.prefs.putBoolean("Sound", game.sound);
                        game.prefs.flush();
                        changing = false;
                    }
                } else {
                    if (checkMenu(game.settingsScreen)) ;
                    else {
                        if (touchPos.x >= 42 && touchPos.x <= 346 && touchPos.y >= 439 && touchPos.y <= 476) {
                            if (TimeUtils.millis() - lastChangeTime > 300) {
                                changing = true;
                                lastChangeTime = TimeUtils.millis();
                            }
                            if (changing) {
                                game.vibrate = !game.vibrate;
                                game.prefs.putBoolean("Vibrate", game.vibrate);
                                game.prefs.flush();
                                changing = false;
                            }
                        }
                    }
                }
                break;
            case GAMEOVER:
                if (touchPos.x >= 150 && touchPos.x <= 310 && touchPos.y >= 105 && touchPos.y <= 150 && !game.gameOverScreen.isReturningToGameOverScreen) {
                    game.mainMenuScreen.reset();
                    game.gameOverScreen.nextScreen = game.mainMenuScreen;
                    game.gameOverScreen.isReturningToNextScreen = true;
                } else if (!game.gameOverScreen.isReturningToGameOverScreen && touchPos.x >= 165 && touchPos.x <= 365 && touchPos.y >= 375 && touchPos.y <= 575) {
                    game.adHandler.loadInterstitial();
                    game.gameOverScreen.nextScreen = game.gameScreen;
                    game.gameOverScreen.isReturningToNextScreen = true;
                    game.gameScreen.getWorld().going = true;
                }
                break;
            case BESTS:
                if (checkMenu(game.bestsScreen)) ;
                break;
            case SHOP:
                if (checkMenu(game.shopScreen)) ;
                else if (touchPos.x >= 85 && touchPos.x <= 155) {
                    for (int i = 0; i < game.ballsCount; i++) {
                        if (touchPos.y >= game.shopScreen.ballsChords[i].y && touchPos.y <= game.shopScreen.ballsChords[i].y + 75)
                            checkBall(i);
                    }
                }
                game.selectBallTexture(game.currentBall);
                break;
            case SHOP_DIALOG:
                if (touchPos.x >= 110 && touchPos.x <= 180 && touchPos.y >= 290 && touchPos.y <= 330) {
                    game.setState(BallGame.State.SHOP);
                } else if (touchPos.x >= 310 && touchPos.x <= 390 && touchPos.y >= 290 && touchPos.y <= 330) {
                    game.shopScreen.bought();
                }
        }

        return true;
    }

    private boolean checkMenu(MyScreen screen) {
        if (touchPos.x >= 140 && touchPos.x <= 300 && touchPos.y >= 105 && touchPos.y <= 150) {
            game.mainMenuScreen.reset();
            /*
            game.setScreen(game.mainMenuScreen);
            game.setState(BallGame.State.MAIN_MENU);
            */
            screen.isReturningToMenuScreen();
            return true;
        }
        return false;
    }

    private void checkBall(int ball) {
        if (game.prefs.getBoolean("Ball" + ball)) {
            game.currentBall = ball;
            game.prefs.putInteger("CurrentBall", game.currentBall);
            game.prefs.flush();
        } else game.shopScreen.buy(ball);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        switch (game.getState()) {
            case SHOP:
                game.shopScreen.resetDragg();
                break;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touchPos.y = screenY;
        game.mainMenuScreen.getCamera().unproject(touchPos);
        switch (game.getState()) {
            case SHOP:
                game.shopScreen.move(touchPos.y);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
