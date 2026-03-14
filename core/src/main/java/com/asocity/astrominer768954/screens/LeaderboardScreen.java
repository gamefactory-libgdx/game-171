package com.asocity.astrominer768954.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.asocity.astrominer768954.Constants;
import com.asocity.astrominer768954.MainGame;

public class LeaderboardScreen implements Screen {

    // -----------------------------------------------------------------------
    // Static helpers — called by GameScreen variants to record scores
    // -----------------------------------------------------------------------

    /** Saves score into the Orbit Field leaderboard (default). */
    public static void addScore(int score) {
        addScore(Constants.PREF_LEADERBOARD_ORBIT, score);
    }

    /** Saves score into the Orbit Field leaderboard. */
    public static void addOrbitScore(int score) {
        addScore(Constants.PREF_LEADERBOARD_ORBIT, score);
    }

    /** Saves score into the Deep Belt leaderboard. */
    public static void addDeepScore(int score) {
        addScore(Constants.PREF_LEADERBOARD_DEEP, score);
    }

    /** Saves score into the Alien Zone leaderboard. */
    public static void addAlienScore(int score) {
        addScore(Constants.PREF_LEADERBOARD_ALIEN, score);
    }

    /** Loads top-10 for the given prefix, inserts the new score, saves back. */
    private static void addScore(String prefix, int score) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int[] scores = loadScores(prefs, prefix);
        // Insert
        for (int i = 0; i < scores.length; i++) {
            if (score > scores[i]) {
                // Shift down
                for (int j = scores.length - 1; j > i; j--) {
                    scores[j] = scores[j - 1];
                }
                scores[i] = score;
                break;
            }
        }
        saveScores(prefs, prefix, scores);
    }

    // -----------------------------------------------------------------------
    // Instance fields
    // -----------------------------------------------------------------------

    private final MainGame game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Stage stage;
    private final ShapeRenderer sr;

    /** 0 = Orbit, 1 = Deep, 2 = Alien */
    private int selectedTab = 0;

    // Tab geometry
    private static final float TAB_W  = 130f;
    private static final float TAB_H  = 40f;
    private static final float TAB_Y  = 728f;
    private static final float TAB_GAP = 10f;
    private static final float TABS_TOTAL = 3 * TAB_W + 2 * TAB_GAP;
    private static final float TAB_START_X = (Constants.WORLD_WIDTH - TABS_TOTAL) / 2f;

    // Table
    private static final float TABLE_X   = 40f;
    private static final float TABLE_TOP = 700f;
    private static final float ROW_H     = 52f;

    // Main Menu button
    private static final float BTN_W = Constants.BTN_SECONDARY_W;
    private static final float BTN_H = Constants.BTN_SECONDARY_H;
    private static final float BTN_X = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float BTN_Y = 100f;

    private static final String[] TAB_LABELS  = { "ORBIT", "DEEP", "ALIEN" };
    private static final String[] PREF_PREFIXES = {
        Constants.PREF_LEADERBOARD_ORBIT,
        Constants.PREF_LEADERBOARD_DEEP,
        Constants.PREF_LEADERBOARD_ALIEN
    };

    public LeaderboardScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));

        game.playMusic("sounds/music/music_menu.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(UiFactory.BG_COL.r, UiFactory.BG_COL.g,
                             UiFactory.BG_COL.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Background
        game.batch.begin();
        Texture bg = game.manager.get("backgrounds/bg_main.png", Texture.class);
        game.batch.draw(bg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Header
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, "LEADERBOARD",
                Constants.WORLD_WIDTH / 2f, 820f, UiFactory.PRIMARY);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Tabs
        for (int i = 0; i < 3; i++) {
            float tx = TAB_START_X + i * (TAB_W + TAB_GAP);
            boolean active = (i == selectedTab);

            // Background fill when active
            if (active) {
                sr.setColor(UiFactory.ACCENT.r, UiFactory.ACCENT.g, UiFactory.ACCENT.b, 0.3f);
                sr.rect(tx, TAB_Y, TAB_W, TAB_H);
            }

            // Border
            sr.setColor(active ? UiFactory.ACCENT.r : UiFactory.PRIMARY.r,
                        active ? UiFactory.ACCENT.g : UiFactory.PRIMARY.g,
                        active ? UiFactory.ACCENT.b : UiFactory.PRIMARY.b,
                        1f);
            drawRectBorder(sr, tx, TAB_Y, TAB_W, TAB_H, 2);
        }

        // Alternate row backgrounds
        Preferences prefs   = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int[]        scores  = loadScores(prefs, PREF_PREFIXES[selectedTab]);

        for (int i = 0; i < scores.length; i++) {
            float ry = TABLE_TOP - (i + 1) * ROW_H;
            if (i % 2 == 0) {
                sr.setColor(0f, 0.04f, 0.08f, 0.6f);
            } else {
                sr.setColor(0.02f, 0.06f, 0.12f, 0.6f);
            }
            sr.rect(TABLE_X, ry, Constants.WORLD_WIDTH - 2 * TABLE_X, ROW_H - 2);
        }

        // Divider below tabs
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.4f);
        sr.rect(TABLE_X, TAB_Y - 4, Constants.WORLD_WIDTH - 2 * TABLE_X, 2);

        // Main Menu button
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "MAIN MENU", BTN_X, BTN_Y, BTN_W, BTN_H);

        sr.end();

        // Tab labels
        for (int i = 0; i < 3; i++) {
            float tx = TAB_START_X + i * (TAB_W + TAB_GAP);
            boolean active = (i == selectedTab);
            UiFactory.drawCenteredLabel(game.batch, game.fontSmall, TAB_LABELS[i],
                    tx + TAB_W / 2f, TAB_Y + TAB_H / 2f + 10f,
                    active ? UiFactory.ACCENT : UiFactory.PRIMARY);
        }

        // Score rows
        for (int i = 0; i < scores.length; i++) {
            float ry    = TABLE_TOP - (i + 1) * ROW_H;
            float textY = ry + ROW_H - 14f;

            UiFactory.drawLabel(game.batch, game.fontSmall,
                    "#" + (i + 1), TABLE_X + 8, textY,
                    UiFactory.PRIMARY);

            if (scores[i] > 0) {
                UiFactory.drawLabel(game.batch, game.fontSmall,
                        String.valueOf(scores[i]),
                        Constants.WORLD_WIDTH - TABLE_X - 80, textY,
                        UiFactory.ACCENT);
            } else {
                UiFactory.drawLabel(game.batch, game.fontSmall,
                        "---",
                        Constants.WORLD_WIDTH - TABLE_X - 80, textY,
                        UiFactory.PRIMARY);
            }
        }

        stage.act(delta);

        // Input
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            handleTouch(touch.x, touch.y);
        }
    }

    private void handleTouch(float tx, float ty) {
        // Tab buttons
        for (int i = 0; i < 3; i++) {
            float tabX = TAB_START_X + i * (TAB_W + TAB_GAP);
            if (hitBox(tx, ty, tabX, TAB_Y, TAB_W, TAB_H)) {
                if (selectedTab != i) {
                    selectedTab = i;
                    playSfx("sounds/sfx/sfx_button_click.ogg");
                }
                return;
            }
        }

        // Main Menu
        if (hitBox(tx, ty, BTN_X, BTN_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_back.ogg");
            game.setScreen(new MainMenuScreen(game));
        }
    }

    // -----------------------------------------------------------------------
    // Shared persistence helpers
    // -----------------------------------------------------------------------

    private static int[] loadScores(Preferences prefs, String prefix) {
        int[] scores = new int[Constants.LEADERBOARD_MAX_ENTRIES];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = prefs.getInteger(prefix + i, 0);
        }
        return scores;
    }

    private static void saveScores(Preferences prefs, String prefix, int[] scores) {
        for (int i = 0; i < scores.length; i++) {
            prefs.putInteger(prefix + i, scores[i]);
        }
        prefs.flush();
    }

    // -----------------------------------------------------------------------

    private static void drawRectBorder(ShapeRenderer sr, float x, float y,
                                        float w, float h, float t) {
        sr.rect(x,         y + h - t, w, t);
        sr.rect(x,         y,         w, t);
        sr.rect(x,         y,         t, h);
        sr.rect(x + w - t, y,         t, h);
    }

    private static boolean hitBox(float tx, float ty,
                                   float bx, float by, float bw, float bh) {
        return tx >= bx && tx <= bx + bw && ty >= by && ty <= by + bh;
    }

    private void playSfx(String path) {
        if (game.sfxEnabled)
            game.manager.get(path, Sound.class).play(1.0f);
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void show()   {}
    @Override public void hide()   {}
    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
