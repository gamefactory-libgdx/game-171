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

public class SettingsScreen implements Screen {

    private final MainGame game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Stage stage;
    private final ShapeRenderer sr;
    private final Preferences prefs;

    private boolean musicOn;
    private boolean sfxOn;

    // Toggle row geometry
    private static final float ROW_X      = 60f;
    private static final float ROW_W      = Constants.WORLD_WIDTH - 120f; // 360
    private static final float ROW_H      = 60f;
    private static final float MUSIC_ROW_Y = 520f;
    private static final float SFX_ROW_Y   = 420f;

    // Toggle box (right side of row)
    private static final float TOGGLE_W = 80f;
    private static final float TOGGLE_H = 40f;

    // Main Menu button
    private static final float BTN_MENU_W = Constants.BTN_SECONDARY_W;
    private static final float BTN_MENU_H = Constants.BTN_SECONDARY_H;
    private static final float BTN_MENU_X = (Constants.WORLD_WIDTH - BTN_MENU_W) / 2f;
    private static final float BTN_MENU_Y = 140f;

    public SettingsScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        prefs   = Gdx.app.getPreferences(Constants.PREFS_NAME);
        musicOn = prefs.getBoolean(Constants.PREF_MUSIC, true);
        sfxOn   = prefs.getBoolean(Constants.PREF_SFX, true);
        game.musicEnabled = musicOn;
        game.sfxEnabled   = sfxOn;

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
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, "SETTINGS",
                Constants.WORLD_WIDTH / 2f, 730f, UiFactory.PRIMARY);

        // Row labels
        UiFactory.drawLabel(game.batch, game.fontBody, "MUSIC",
                ROW_X, MUSIC_ROW_Y + ROW_H - 10f, UiFactory.PRIMARY);
        UiFactory.drawLabel(game.batch, game.fontBody, "SOUND FX",
                ROW_X, SFX_ROW_Y + ROW_H - 10f, UiFactory.PRIMARY);

        // Neon buttons + toggle boxes
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Draw toggle boxes
        drawToggle(MUSIC_ROW_Y, musicOn);
        drawToggle(SFX_ROW_Y,   sfxOn);

        // Main Menu button
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "MAIN MENU", BTN_MENU_X, BTN_MENU_Y, BTN_MENU_W, BTN_MENU_H);

        sr.end();

        // Toggle state labels (ON / OFF)
        float toggleX = ROW_X + ROW_W - TOGGLE_W;
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall,
                musicOn ? "ON" : "OFF",
                toggleX + TOGGLE_W / 2f, MUSIC_ROW_Y + TOGGLE_H / 2f + 10f,
                musicOn ? UiFactory.ACCENT : UiFactory.PRIMARY);
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall,
                sfxOn ? "ON" : "OFF",
                toggleX + TOGGLE_W / 2f, SFX_ROW_Y + TOGGLE_H / 2f + 10f,
                sfxOn ? UiFactory.ACCENT : UiFactory.PRIMARY);

        stage.act(delta);

        // Input
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            handleTouch(touch.x, touch.y);
        }
    }

    /**
     * Draws a toggle box outline + optional filled indicator on the right side of
     * the row at the given rowY.  SR must be in Filled state.
     */
    private void drawToggle(float rowY, boolean on) {
        float tx = ROW_X + ROW_W - TOGGLE_W;
        float ty = rowY + (ROW_H - TOGGLE_H) / 2f;

        // Glow
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.25f);
        sr.rect(tx - 3, ty - 3, TOGGLE_W + 6, TOGGLE_H + 6);

        // Box background (filled when ON)
        if (on) {
            sr.setColor(UiFactory.ACCENT.r, UiFactory.ACCENT.g, UiFactory.ACCENT.b, 0.25f);
            sr.rect(tx, ty, TOGGLE_W, TOGGLE_H);
        }

        // Box border
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 1f);
        sr.rect(tx,              ty + TOGGLE_H - 2, TOGGLE_W, 2); // top
        sr.rect(tx,              ty,                TOGGLE_W, 2); // bottom
        sr.rect(tx,              ty,                2, TOGGLE_H); // left
        sr.rect(tx + TOGGLE_W - 2, ty,              2, TOGGLE_H); // right
    }

    private void handleTouch(float tx, float ty) {
        float toggleX = ROW_X + ROW_W - TOGGLE_W;

        // Music toggle
        float musicToggleY = MUSIC_ROW_Y + (ROW_H - TOGGLE_H) / 2f;
        if (hitBox(tx, ty, toggleX, musicToggleY, TOGGLE_W, TOGGLE_H)) {
            musicOn = !musicOn;
            game.musicEnabled = musicOn;
            prefs.putBoolean(Constants.PREF_MUSIC, musicOn);
            prefs.flush();
            if (game.currentMusic != null) {
                if (musicOn) game.currentMusic.play();
                else         game.currentMusic.pause();
            }
            playSfx("sounds/sfx/sfx_toggle.ogg");
            return;
        }

        // SFX toggle
        float sfxToggleY = SFX_ROW_Y + (ROW_H - TOGGLE_H) / 2f;
        if (hitBox(tx, ty, toggleX, sfxToggleY, TOGGLE_W, TOGGLE_H)) {
            sfxOn = !sfxOn;
            game.sfxEnabled = sfxOn;
            prefs.putBoolean(Constants.PREF_SFX, sfxOn);
            prefs.flush();
            playSfx("sounds/sfx/sfx_toggle.ogg");
            return;
        }

        // Main Menu
        if (hitBox(tx, ty, BTN_MENU_X, BTN_MENU_Y, BTN_MENU_W, BTN_MENU_H)) {
            playSfx("sounds/sfx/sfx_button_back.ogg");
            game.setScreen(new MainMenuScreen(game));
        }
    }

    // -----------------------------------------------------------------------

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
