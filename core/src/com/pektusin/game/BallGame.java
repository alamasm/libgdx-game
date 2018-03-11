package com.pektusin.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.pektusin.game.Screens.BestsScreen;
import com.pektusin.game.Screens.GameOverScreen;
import com.pektusin.game.Screens.GameScreen;
import com.pektusin.game.Screens.MainMenuScreen;
import com.pektusin.game.Screens.SettingsScreen;
import com.pektusin.game.Screens.ShopScreen;
import com.pektusin.game.Util.AdHandler;
import com.pektusin.game.Util.Animation.CoinAnimation;


public class BallGame extends Game {
    private static final String SCORE_FONT_PATH = "Data/ScoreFont.fnt";
    private static final String SCORE_FONT_BITMAP_PATH = "Data/ScoreFont.png";
    private static final String FONT_PATH = "Data/SnapITC.fnt";
    private static final String FONT_BITMAP_PATH = "Data/SnapITC.png";

    private static final String SHADER_VERT_PATH = "shaders/font.vert";
    private static final String SHADER_FRAG_PATH = "shaders/font.frag";
    public static ShaderProgram FONT_SHADER;

    public final int BALL_VELOCITY_X = 275;
    public final int LEVELS = 5;
    public final int BLOCKS = 1;

    public MainMenuScreen mainMenuScreen;
    public GameScreen gameScreen;
    public GameOverScreen gameOverScreen;
    public SettingsScreen settingsScreen;
    public BestsScreen bestsScreen;
    public ShopScreen shopScreen;

    public Preferences prefs;
    public State state;
    public CoinAnimation coinAnimation;
    public BitmapFont font;
    public BitmapFont scoreFont;

    public Texture shopTexture;

    public Texture[] blockTextures;

    public Texture ballTexturesLeft[];
    public Texture ballTextureLeft;
    public Texture ballShopTexture;

    public Texture coinTextures[];
    public Texture coinTexture;
    public Texture coinTexturesPrimary[];

    public Texture settingsButtonTexture;
    public Texture shopButtonTexture;
    public Texture playButtonTexture;
    public Texture playButtonClickedTexture;
    public Texture backButtonTexture;
    public Texture bestsButtonTexture;

    public Sound clickSound;
    public Sound endGameSound;
    public Sound coinSound;

    public Color secondColor, firstColor;

    public boolean sound = false;
    public boolean vibrate = false;

    public static final int SCREEN_HEIGHT = 960;
    public static final int SCREEN_WIDTH = 576;

    public float screenSizeY;
    public float screenSizeX;

    public int level = 1;
    public int ballsCount = 6;
    public int currentBall = 0;
    public int[] ballsPrices;
    public int coins = 0;

    public int currentCoinValue = 0;

    public ParticleEffect particleEffect;

    public AdHandler adHandler;

    public OrthographicCamera camera;

    public BallGame(AdHandler adHandler) {
        super();
        this.adHandler = adHandler;
    }

    @Override
    public void create() {
        screenSizeY = SCREEN_HEIGHT / Gdx.graphics.getHeight();
        screenSizeX = SCREEN_WIDTH / Gdx.graphics.getWidth();

        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        prefs = Gdx.app.getPreferences("BallGame");

        this.state = State.MAIN_MENU;

        ballTexturesLeft = new Texture[ballsCount];
        ballsPrices = new int[ballsCount];

        coinTexturesPrimary = new Texture[3];
        coinTextures = new Texture[5];

        initPrefs();

        changeLevel(level);

        loadAssets();

        gameScreen = new GameScreen(this, camera);
        gameOverScreen = new GameOverScreen(this, camera);
        mainMenuScreen = new MainMenuScreen(this, camera);
        settingsScreen = new SettingsScreen(this, camera);
        bestsScreen = new BestsScreen(this, camera);
        shopScreen = new ShopScreen(this, camera);

        coinAnimation = new CoinAnimation(this);

        this.state = State.MAIN_MENU;
        setScreen(mainMenuScreen);
        adHandler.loadInterstitial();

        firstColor = new Color(0.2f, 0.2f, 0.2f, 0.3f);
        secondColor = new Color(0.2f, 0.2f, 0.2f, 0.6f);
    }

    private void loadAssets() {

        scoreFont = getNewScoreFont();
        font = getNewFont();
        FONT_SHADER = new ShaderProgram(Gdx.files.internal(SHADER_VERT_PATH), Gdx.files.internal(SHADER_FRAG_PATH));

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("Data/Particles.par"), Gdx.files.internal(""));
        particleEffect.start();

        blockTextures = new Texture[LEVELS];
        initBlockTextures();
        initBallTextures();
        initCoinTextures();
        selectBallTexture(currentBall);
        coinTexture = new Texture("Coin/Coin_50x50_0.png");
        shopTexture = new Texture("Shop_Screen_480x800.png");

        settingsButtonTexture = new Texture("GUI/Settings_Button_200x200.png");
        shopButtonTexture = new Texture("GUI/Shop_Button_200x200.png");
        playButtonTexture = new Texture("GUI/Play_Button_75x75.png");
        playButtonClickedTexture = new Texture("GUI/Play_Button_Clicked_75x75.png");
        backButtonTexture = new Texture("GUI/Back_Button_200x200.png");
        bestsButtonTexture = new Texture("GUI/Bests_Button_200x200.png");

        clickSound = Gdx.audio.newSound(Gdx.files.internal("Data/clickSound.wav"));
        endGameSound = Gdx.audio.newSound(Gdx.files.internal("Data/endGame.wav"));
        coinSound = Gdx.audio.newSound(Gdx.files.internal("Data/coinSound.wav"));
    }

    public static BitmapFont getNewScoreFont() {
        Texture fontTexture = new Texture(Gdx.files.internal(SCORE_FONT_BITMAP_PATH));
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return new BitmapFont(Gdx.files.internal(SCORE_FONT_PATH), new TextureRegion(fontTexture), false);
    }

    public static BitmapFont getNewFont() {
        Texture fontTexture = new Texture(Gdx.files.internal(FONT_BITMAP_PATH));
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return new BitmapFont(Gdx.files.internal(FONT_PATH), new TextureRegion(fontTexture), false);
    }

    private void initPrefs() {
        if (!prefs.contains("BestScore")) {
            prefs.putInteger("BestScore", 0);
            prefs.flush();
        }

        if (!prefs.contains("Sound")) {
            prefs.putBoolean("Sound", sound);
            prefs.flush();
        }

        if (!prefs.contains("Vibrate")) {
            prefs.putBoolean("Vibrate", vibrate);
            prefs.flush();
        }

        if (!prefs.contains("Coins")) {
            prefs.putInteger("Coins", coins);
            prefs.flush();
        }

        if (!prefs.contains("Ball")) {
            prefs.putInteger("Ball", currentBall);
            prefs.flush();
        }

        if (!prefs.contains("CurrentBall")) {
            prefs.putInteger("CurrentBall", currentBall);
            prefs.flush();
        }

        for (int i = 0; i < ballsCount; i++) {
            if (!prefs.contains("Ball" + i)) {
                if (i == 0)
                    prefs.putBoolean("Ball" + i, true);
                else
                    prefs.putBoolean("Ball" + i, false);
            }
        }

        coins = prefs.getInteger("Coins");
        sound = prefs.getBoolean("Sound");
        vibrate = prefs.getBoolean("Vibrate");
        currentBall = prefs.getInteger("CurrentBall");
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void changeLevel(int level) {
        if (level - 1 >= LEVELS)
            level = 1;

        this.level = level;
    }

    public void initBlockTextures() {
        for (int i = 0; i < BLOCKS; i++)
            blockTextures[i] = new Texture("Blocks/Block_205x70_level" + (i + 1) + ".png");

        for (int i = 0; i < coinTexturesPrimary.length; i++)
            coinTexturesPrimary[i] = new Texture("Coin/Coin_50x50_" + i + ".png");

    }

    public void initBallTextures() {
        for (int i = 0; i < ballsCount; i++) {
            ballTexturesLeft[i] = new Texture("Ball/Ball_Left_50x50_" + i + ".png");

            ballsPrices[i] = 50 * i;
        }
        ballShopTexture = new Texture("Ball/Ball_Shop_50x50.png");
    }

    private void initCoinTextures() {
        for (int i = 0; i < 5; i++) {
            coinTextures[i] = new Texture("Coin/Animation_" + i + ".png");
        }
    }

    @Override
    public void dispose() {
        ballTextureLeft.dispose();
        ballShopTexture.dispose();
        coinTexture.dispose();
        settingsButtonTexture.dispose();
        shopButtonTexture.dispose();
        playButtonTexture.dispose();
        playButtonClickedTexture.dispose();
        backButtonTexture.dispose();
        bestsButtonTexture.dispose();
        clickSound.dispose();
        endGameSound.dispose();
        coinSound.dispose();
        font.dispose();
        scoreFont.dispose();

        for (Texture t : ballTexturesLeft) t.dispose();
        for (Texture t : coinTexturesPrimary) t.dispose();
        for (Texture t : coinTextures) t.dispose();
        for (Texture t : blockTextures) t.dispose();
    }

    public void selectBallTexture(int ball) {
        ballTextureLeft = ballTexturesLeft[ball];
    }

    public void setCoin(int currentCoin) {
        coinTexture = coinTexturesPrimary[currentCoin];
    }

    public enum State {
        MAIN_MENU, RUNNING, GAMEOVER, SETTINGS, BESTS, SHOP, SHOP_DIALOG, SHOP_NOT_ENOUGH
    }
}
