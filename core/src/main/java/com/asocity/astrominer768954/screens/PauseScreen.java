package com.asocity.astrominer768954.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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
 * Pause overlay.
 * Resume  → returns to the SAME GameScreen instance (no reset).
 * Restart → creates a NEW GameScreen for the same sector.
 * Main Menu → navigates to MainMenuScreen.
 */
public class PauseScreen implements Screen {

    private final MainGame    game;
    private final GameScreen  gameRef;    // same instance — resumed on Resume
    private final int         sector;
    private final OrthographicCamera camera;
    private final Viewport    viewport;
    private final Stage       stage;
    private final ShapeRenderer sr;

    // Panel
    private static final float PANEL_W = 380f;
    private static final float PANEL_H = 320f;
    private static final float PANEL_X = (Constants.WORLD_WIDTH  - PANEL_W) / 2f;
    private static final float PANEL_Y = (Constants.WORLD_HEIGHT - PANEL_H) / 2f;

    // Buttons — stacked inside the panel
    private static final float BTN_W = 280f;
    private static final float BTN_H = Constants.BTN_SECONDARY_H;
    private static final float BTN_X = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float BTN_RESUME_Y  = PANEL_Y + PANEL_H - 110f;
    private static final float BTN_RESTART_Y = PANEL_Y + PANEL_H - 185f;
    private static final float BTN_MENU_Y    = PANEL_Y + PANEL_H - 260f;

    public PauseScreen(MainGame game, GameScreen gameRef, int sector) {
        this.game    = game;
        this.gameRef = gameRef;
        this.sector  = sector;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    resumeGame();
                    return true;
                }
                return false;
            }
        }));

        // Stop gameplay music while paused — player can clearly see it's paused
        if (game.currentMusic != null) game.currentMusic.pause();
    }

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
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, "PAUSED",
                Constants.WORLD_WIDTH / 2f, 740f, UiFactory.PRIMARY);

        // Panel + buttons
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Panel background
        sr.setColor(0f, 0.03f, 0.08f, 0.92f);
        sr.rect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H);

        // Panel border glow
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.18f);
        drawBorder(sr, PANEL_X - 4, PANEL_Y - 4, PANEL_W + 8, PANEL_H + 8, 4f);

        // Panel border
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 1f);
        drawBorder(sr, PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 2f);

        // Buttons
        UiFactory.drawButton(sr, game.batch, game.fontBody, "RESUME",    BTN_X, BTN_RESUME_Y,  BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "RESTART",   BTN_X, BTN_RESTART_Y, BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "MAIN MENU", BTN_X, BTN_MENU_Y,    BTN_W, BTN_H);

        sr.end();

        stage.act(delta);

        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            handleTouch(touch.x, touch.y);
        }
    }

    private void handleTouch(float tx, float ty) {
        if (hitBox(tx, ty, BTN_X, BTN_RESUME_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_click.ogg");
            resumeGame();

        } else if (hitBox(tx, ty, BTN_X, BTN_RESTART_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_click.ogg");
            dispose();
            game.setScreen(new GameScreen(game, sector));

        } else if (hitBox(tx, ty, BTN_X, BTN_MENU_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_back.ogg");
            dispose();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void resumeGame() {
        dispose();
        game.setScreen(gameRef); // same GameScreen instance — state preserved
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
