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

/**
 * Drill skin upgrade shop.
 * 6 skins arranged in a 2×3 grid.
 * Ore balance is read from and written to SharedPreferences.
 * Selected skin index is saved under PREF_SKIN.
 */
public class UpgradeScreen implements Screen {

    // Skin definitions
    private static final String[] SKIN_NAMES   = {
        "CYAN",  "GOLD",    "RED",    "GREEN",   "PURPLE",  "PLASMA"
    };
    private static final int[] SKIN_PRICES = {
        Constants.SKIN_PRICE_CYAN,   Constants.SKIN_PRICE_GOLD,
        Constants.SKIN_PRICE_RED,    Constants.SKIN_PRICE_GREEN,
        Constants.SKIN_PRICE_PURPLE, Constants.SKIN_PRICE_PLASMA
    };
    // Drill ring colors (RGB) to display as a swatch in each card
    private static final float[][] SKIN_COLORS = {
        { 0f,    0.85f, 1f    }, // Cyan
        { 1f,    0.85f, 0f    }, // Gold
        { 1f,    0.27f, 0.27f }, // Red
        { 0.27f, 1f,    0.27f }, // Green
        { 0.7f,  0.27f, 1f    }, // Purple
        { 1f,    0.27f, 1f    }, // Plasma
    };

    // Grid layout — 2 columns × 3 rows
    private static final int   COLS      = 2;
    private static final float CARD_W    = 200f;
    private static final float CARD_H    = 130f;
    private static final float CARD_PADX = 20f;
    private static final float CARD_PADY = 18f;
    private static final float GRID_TOTAL_W = COLS * CARD_W + (COLS - 1) * CARD_PADX;
    private static final float GRID_X    = (Constants.WORLD_WIDTH - GRID_TOTAL_W) / 2f;
    private static final float GRID_TOP  = 680f; // Y of top of first row

    // Button inside each card
    private static final float CBTN_W = 130f;
    private static final float CBTN_H = 40f;

    // Main Menu button
    private static final float BTN_W = Constants.BTN_SECONDARY_W;
    private static final float BTN_H = Constants.BTN_SECONDARY_H;
    private static final float BTN_X = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float BTN_Y = 60f;

    // -----------------------------------------------------------------------

    private final MainGame game;
    private final OrthographicCamera camera;
    private final Viewport            viewport;
    private final Stage               stage;
    private final ShapeRenderer       sr;

    private int oreBalance;
    private int selectedSkin;
    private final boolean[] owned = new boolean[SKIN_NAMES.length];

    public UpgradeScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        loadPrefs();

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

    private void loadPrefs() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        oreBalance   = prefs.getInteger(Constants.PREF_ORE_BALANCE, 0);
        selectedSkin = prefs.getInteger(Constants.PREF_SKIN, 0);
        owned[0]     = true; // default always owned
        for (int i = 1; i < owned.length; i++) {
            owned[i] = prefs.getBoolean(Constants.PREF_SKIN_OWNED + i, false);
        }
    }

    // -----------------------------------------------------------------------
    // Render
    // -----------------------------------------------------------------------

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(UiFactory.BG_COL.r, UiFactory.BG_COL.g, UiFactory.BG_COL.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        sr.setProjectionMatrix(camera.combined);

        // Background
        game.batch.begin();
        Texture bg = game.manager.get("backgrounds/bg_main.png", Texture.class);
        game.batch.draw(bg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Header
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, "UPGRADE SHOP",
                Constants.WORLD_WIDTH / 2f, 800f, UiFactory.PRIMARY);

        // Ore balance
        UiFactory.drawCenteredLabel(game.batch, game.fontBody, "ORE: " + oreBalance,
                Constants.WORLD_WIDTH / 2f, 748f, UiFactory.ACCENT);

        // Cards + Main Menu button
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < SKIN_NAMES.length; i++) {
            float[] pos = cardPos(i);
            float cx = pos[0], cy = pos[1];

            boolean isSelected = (i == selectedSkin);
            float[] col = SKIN_COLORS[i];

            // Card fill
            sr.setColor(0f, 0.04f, 0.1f, 0.88f);
            sr.rect(cx, cy, CARD_W, CARD_H);

            // Selected highlight fill
            if (isSelected) {
                sr.setColor(col[0] * 0.15f, col[1] * 0.15f, col[2] * 0.15f, 0.6f);
                sr.rect(cx, cy, CARD_W, CARD_H);
            }

            // Color swatch circle
            sr.setColor(col[0], col[1], col[2], 1f);
            sr.circle(cx + 30f, cy + CARD_H - 32f, 16f, 24);

            // Card border (colored if selected, primary otherwise)
            if (isSelected) {
                sr.setColor(col[0], col[1], col[2], 1f);
            } else {
                sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.8f);
            }
            drawBorder(sr, cx, cy, CARD_W, CARD_H, 2f);

            // Buy/Select/Owned button box
            float btnX = cx + (CARD_W - CBTN_W) / 2f;
            float btnY = cy + 10f;

            String btnLabel;
            if (!owned[i]) {
                btnLabel = String.valueOf(SKIN_PRICES[i]) + " ORE";
            } else if (isSelected) {
                btnLabel = "ACTIVE";
            } else {
                btnLabel = "SELECT";
            }
            UiFactory.drawButton(sr, game.batch, game.fontSmall, btnLabel, btnX, btnY, CBTN_W, CBTN_H);
        }

        // Main Menu button
        UiFactory.drawButton(sr, game.batch, game.fontBody, "MAIN MENU", BTN_X, BTN_Y, BTN_W, BTN_H);

        sr.end();

        // Card text labels (after SR to avoid batch conflicts)
        for (int i = 0; i < SKIN_NAMES.length; i++) {
            float[] pos = cardPos(i);
            float cx = pos[0], cy = pos[1];

            // Skin name
            UiFactory.drawLabel(game.batch, game.fontSmall, SKIN_NAMES[i],
                    cx + 55f, cy + CARD_H - 18f, UiFactory.PRIMARY);

            // Price or "FREE"
            String priceStr = (SKIN_PRICES[i] == 0) ? "FREE" : SKIN_PRICES[i] + " ORE";
            if (owned[i]) priceStr = "OWNED";
            UiFactory.drawLabel(game.batch, game.fontSmall, priceStr,
                    cx + 14f, cy + 65f, UiFactory.ACCENT);
        }

        stage.act(delta);

        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            handleTouch(touch.x, touch.y);
        }
    }

    private void handleTouch(float tx, float ty) {
        // Skin cards — check buy/select button area inside each card
        for (int i = 0; i < SKIN_NAMES.length; i++) {
            float[] pos  = cardPos(i);
            float   cx   = pos[0];
            float   cy   = pos[1];
            float   btnX = cx + (CARD_W - CBTN_W) / 2f;
            float   btnY = cy + 10f;

            if (hitBox(tx, ty, btnX, btnY, CBTN_W, CBTN_H)) {
                handleSkinAction(i);
                return;
            }

            // Tapping card body also triggers
            if (hitBox(tx, ty, cx, cy, CARD_W, CARD_H)) {
                handleSkinAction(i);
                return;
            }
        }

        // Main Menu button
        if (hitBox(tx, ty, BTN_X, BTN_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_back.ogg");
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void handleSkinAction(int index) {
        if (!owned[index]) {
            // Attempt purchase
            if (oreBalance >= SKIN_PRICES[index]) {
                oreBalance -= SKIN_PRICES[index];
                owned[index] = true;
                selectedSkin = index;
                savePrefs();
                playSfx("sounds/sfx/sfx_power_up.ogg");
            } else {
                // Not enough ore
                playSfx("sounds/sfx/sfx_button_back.ogg");
            }
        } else {
            // Select skin
            selectedSkin = index;
            savePrefs();
            playSfx("sounds/sfx/sfx_button_click.ogg");
        }
    }

    private void savePrefs() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        prefs.putInteger(Constants.PREF_ORE_BALANCE, oreBalance);
        prefs.putInteger(Constants.PREF_SKIN, selectedSkin);
        for (int i = 1; i < owned.length; i++) {
            prefs.putBoolean(Constants.PREF_SKIN_OWNED + i, owned[i]);
        }
        prefs.flush();
    }

    /** Returns {x, y} of the card at grid index i (0-based, row-major). */
    private static float[] cardPos(int index) {
        int col = index % COLS;
        int row = index / COLS;
        float x = GRID_X + col * (CARD_W + CARD_PADX);
        float y = GRID_TOP - row * (CARD_H + CARD_PADY) - CARD_H;
        return new float[]{ x, y };
    }

    // -----------------------------------------------------------------------

    private static void drawBorder(ShapeRenderer sr, float x, float y, float w, float h, float t) {
        sr.rect(x,         y + h - t, w, t);
        sr.rect(x,         y,         w, t);
        sr.rect(x,         y,         t, h);
        sr.rect(x + w - t, y,         t, h);
    }

    private static boolean hitBox(float tx, float ty, float bx, float by, float bw, float bh) {
        return tx >= bx && tx <= bx + bw && ty >= by && ty <= by + bh;
    }

    private void playSfx(String path) {
        if (game.sfxEnabled)
            game.manager.get(path, Sound.class).play(1.0f);
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void show()   { loadPrefs(); } // refresh balance if returning from game
    @Override public void hide()   {}
    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
