package com.pektusin.game.Util.Animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;
import com.pektusin.game.BallGame;
import com.pektusin.game.Objects.Coin;

import java.util.Iterator;

/**
 * Created by pektusin on 8/8/2016.
 */
public class CoinAnimation {
    boolean playing = false;
    private float time;
    private BallGame game;
    private Texture coinTextures[];
    private Iterator<Coin> coinIterator;
    private Coin coin;
    private double lastChangeTime = 0;
    private int i = 0;

    public CoinAnimation(BallGame game) {
        this.game = game;
        coinTextures = game.coinTextures;
        time = 1 / coinTextures.length;
    }

    public void play() {
        i++;
        lastChangeTime = TimeUtils.millis();
        playing = true;
    }

    public void update() {
        if (playing) {
            coinIterator = game.gameScreen.getWorld().getCoins().iterator();
            if (TimeUtils.millis() - lastChangeTime >= time) {
                while (coinIterator.hasNext()) {
                    coin = coinIterator.next();
                    if (i < 5)
                        coin.animTexture = coinTextures[i];
                    coin.animation = true;
                }
                if (i < coinTextures.length - 1) i++;
                else stop();

                lastChangeTime = TimeUtils.millis();
            }
        }
    }

    public void stop() {
        i = 0;

        playing = false;

        for (Coin coin : game.gameScreen.getWorld().getCoins())
            coin.animation = false;
    }
}
