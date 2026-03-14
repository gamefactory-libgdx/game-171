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

public class GameOverScreen implements Screen {

    private final MainGame game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Stage stage;
    private final ShapeRenderer sr;

    private final int score;
    private final int ore;
    private final int personalBest;

    // Panel
    private static final float PANEL_W = 400f;
    private static final float PANEL_H = 280f;
    private static final float PANEL_X = (Constants.WORLD_WIDTH - PANEL_W) / 2f;
    private static final float PANEL_Y = 320f;

    // Buttons — side by side
    private static final float BTN_W  = Constants.BTN_SECONDARY_W;
    private static final float BTN_H  = Constants.BTN_SECONDARY_H;
    private static final float BTN_Y  = 220f;
    private static final float GAP    = 20f;
    private static final float BTN_RETRY_X = Constants.WORLD_WIDTH / 2f - BTN_W - GAP / 2f;
    private static final float BTN_MENU_X  = Constants.WORLD_WIDTH / 2f + GAP / 2f;

    public GameOverScreen(MainGame game, int score, int ore) {
        this.game  = game;
        this.score = score;
        this.ore   = ore;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int bestOrbit = prefs.getInteger(Constants.PREF_HIGH_ORBIT, 0);
        int bestDeep  = prefs.getInteger(Constants.PREF_HIGH_DEEP,  0);
        int bestAlien = prefs.getInteger(Constants.PREF_HIGH_ALIEN, 0);
        personalBest  = Math.max(bestOrbit, Math.max(bestDeep, bestAlien));

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

        game.playMusicOnce("sounds/music/music_game_over.ogg");
        playSfx("sounds/sfx/sfx_game_over.ogg");
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
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, "GAME OVER",
                Constants.WORLD_WIDTH / 2f, 700f, UiFactory.ACCENT);

        // Neon panel + buttons
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Panel background (dark fill)
        sr.setColor(0f, 0.04f, 0.1f, 0.9f);
        sr.rect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H);

        // Panel border — neon primary
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 1f);
        drawRectBorder(sr, PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 2);

        // Glow behind panel
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.15f);
        drawRectBorder(sr, PANEL_X - 4, PANEL_Y - 4, PANEL_W + 8, PANEL_H + 8, 4);

        // Buttons
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "PLAY AGAIN", BTN_RETRY_X, BTN_Y, BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "MAIN MENU",  BTN_MENU_X,  BTN_Y, BTN_W, BTN_H);

        sr.end();

        // Panel content labels
        float cx = Constants.WORLD_WIDTH / 2f;
        UiFactory.drawCenteredLabel(game.batch, game.fontBody, "SCORE",
                cx, PANEL_Y + PANEL_H - 30f, UiFactory.PRIMARY);
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, String.valueOf(score),
                cx, PANEL_Y + PANEL_H - 80f, UiFactory.ACCENT);
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall,
                "BEST: " + personalBest,
                cx, PANEL_Y + PANEL_H - 140f, UiFactory.PRIMARY);
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall,
                "ORE COLLECTED: " + ore,
                cx, PANEL_Y + PANEL_H - 185f, UiFactory.PRIMARY);

        stage.act(delta);

        // Input
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            handleTouch(touch.x, touch.y);
        }
    }

    private void handleTouch(float tx, float ty) {
        if (hitBox(tx, ty, BTN_RETRY_X, BTN_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_click.ogg");
            game.setScreen(new SectorSelectScreen(game));

        } else if (hitBox(tx, ty, BTN_MENU_X, BTN_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_back.ogg");
            game.setScreen(new MainMenuScreen(game));
        }
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
