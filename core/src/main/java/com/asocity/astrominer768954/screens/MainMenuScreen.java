package com.asocity.astrominer768954.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
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

public class MainMenuScreen implements Screen {

    private final MainGame game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Stage stage;
    private final ShapeRenderer sr;

    // Button geometry (bottom-left origin)
    private static final float BTN_PLAY_W  = Constants.BTN_PRIMARY_W;
    private static final float BTN_PLAY_H  = Constants.BTN_PRIMARY_H;
    private static final float BTN_PLAY_X  = (Constants.WORLD_WIDTH - BTN_PLAY_W) / 2f;
    private static final float BTN_PLAY_Y  = 480f;

    private static final float BTN_SEC_W = Constants.BTN_SECONDARY_W;
    private static final float BTN_SEC_H = Constants.BTN_SECONDARY_H;
    private static final float BTN_SEC_X = (Constants.WORLD_WIDTH - BTN_SEC_W) / 2f;

    private static final float BTN_SETTINGS_Y    = 380f;
    private static final float BTN_LEADERBOARD_Y = 290f;

    public MainMenuScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                // Main menu — back key exits gracefully
                if (keycode == Input.Keys.BACK) {
                    Gdx.app.exit();
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

        // --- Background + labels ---
        game.batch.begin();
        Texture bg = game.manager.get("backgrounds/bg_main.png", Texture.class);
        game.batch.draw(bg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Title
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, "ASTRO MINER",
                Constants.WORLD_WIDTH / 2f, 720f, UiFactory.PRIMARY);

        // Tagline
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall, "TAP TO DRILL",
                Constants.WORLD_WIDTH / 2f, 668f, UiFactory.ACCENT);

        // --- Neon buttons ---
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "PLAY", BTN_PLAY_X, BTN_PLAY_Y, BTN_PLAY_W, BTN_PLAY_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "SETTINGS", BTN_SEC_X, BTN_SETTINGS_Y, BTN_SEC_W, BTN_SEC_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "LEADERBOARD", BTN_SEC_X, BTN_LEADERBOARD_Y, BTN_SEC_W, BTN_SEC_H);

        sr.end();

        stage.act(delta);

        // --- Input ---
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            handleTouch(touch.x, touch.y);
        }
    }

    private void handleTouch(float tx, float ty) {
        if (hitBtn(tx, ty, BTN_PLAY_X, BTN_PLAY_Y, BTN_PLAY_W, BTN_PLAY_H)) {
            playSfx("sounds/sfx/sfx_button_click.ogg");
            game.setScreen(new SectorSelectScreen(game));

        } else if (hitBtn(tx, ty, BTN_SEC_X, BTN_SETTINGS_Y, BTN_SEC_W, BTN_SEC_H)) {
            playSfx("sounds/sfx/sfx_button_click.ogg");
            game.setScreen(new SettingsScreen(game));

        } else if (hitBtn(tx, ty, BTN_SEC_X, BTN_LEADERBOARD_Y, BTN_SEC_W, BTN_SEC_H)) {
            playSfx("sounds/sfx/sfx_button_click.ogg");
            game.setScreen(new LeaderboardScreen(game));
        }
    }

    // -----------------------------------------------------------------------

    private static boolean hitBtn(float tx, float ty,
                                   float bx, float by, float bw, float bh) {
        return tx >= bx && tx <= bx + bw && ty >= by && ty <= by + bh;
    }

    private void playSfx(String path) {
        if (game.sfxEnabled)
            game.manager.get(path, com.badlogic.gdx.audio.Sound.class).play(1.0f);
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
