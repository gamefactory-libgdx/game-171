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

public class SectorSelectScreen implements Screen {

    private final MainGame game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Stage stage;
    private final ShapeRenderer sr;

    // Sector card geometry — three stacked cards
    private static final float CARD_W  = 420f;
    private static final float CARD_H  = 110f;
    private static final float CARD_X  = (Constants.WORLD_WIDTH - CARD_W) / 2f;
    private static final float CARD_Y0 = 530f; // Orbit Field  (top)
    private static final float CARD_Y1 = 400f; // Deep Belt    (mid)
    private static final float CARD_Y2 = 270f; // Alien Zone   (bottom)

    private static final float[] CARD_Y = { CARD_Y0, CARD_Y1, CARD_Y2 };

    // Main menu button
    private static final float BTN_W = Constants.BTN_SECONDARY_W;
    private static final float BTN_H = Constants.BTN_SECONDARY_H;
    private static final float BTN_X = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float BTN_Y = 100f;

    private static final String[] SECTOR_NAMES  = { "ORBIT FIELD", "DEEP BELT",   "ALIEN ZONE"  };
    private static final String[] SECTOR_DESC   = { "BEGINNER",    "INTERMEDIATE","EXPERT"      };
    private static final String[] SECTOR_DETAIL = {
        "8-12 asteroids  |  5 taps each",
        "16-24 asteroids  |  8 taps each",
        "25-35 asteroids  |  12 taps each"
    };

    // Card accent colors (filled tint per sector)
    private static final float[][] CARD_TINTS = {
        { 0.30f, 0.10f, 0.70f }, // purple — orbit
        { 0.20f, 0.07f, 0.50f }, // dark purple — deep
        { 0.40f, 0.05f, 0.05f }, // dark red — alien
    };

    public SectorSelectScreen(MainGame game) {
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
        Gdx.gl.glClearColor(UiFactory.BG_COL.r, UiFactory.BG_COL.g, UiFactory.BG_COL.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Background
        game.batch.begin();
        Texture bg = game.manager.get("backgrounds/bg_main.png", Texture.class);
        game.batch.draw(bg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Header
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, "SELECT SECTOR",
                Constants.WORLD_WIDTH / 2f, 760f, UiFactory.PRIMARY);

        // Sub-header
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall, "CHOOSE YOUR MINING ZONE",
                Constants.WORLD_WIDTH / 2f, 710f, UiFactory.ACCENT);

        // Cards and buttons
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Draw sector cards
        for (int i = 0; i < 3; i++) {
            float cy = CARD_Y[i];
            float[] tint = CARD_TINTS[i];

            // Card fill
            sr.setColor(tint[0], tint[1], tint[2], 0.55f);
            sr.rect(CARD_X, cy, CARD_W, CARD_H);

            // Card glow
            sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.15f);
            drawBorder(sr, CARD_X - 4, cy - 4, CARD_W + 8, CARD_H + 8, 4);

            // Card border
            sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.9f);
            drawBorder(sr, CARD_X, cy, CARD_W, CARD_H, 2);

            // Difficulty badge fill (right side strip)
            sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.15f);
            sr.rect(CARD_X + CARD_W - 120, cy, 120, CARD_H);
        }

        // Main Menu button
        UiFactory.drawButton(sr, game.batch, game.fontBody, "MAIN MENU", BTN_X, BTN_Y, BTN_W, BTN_H);

        sr.end();

        // Card text labels
        for (int i = 0; i < 3; i++) {
            float cy = CARD_Y[i];

            // Sector name
            UiFactory.drawLabel(game.batch, game.fontBody, SECTOR_NAMES[i],
                    CARD_X + 18f, cy + CARD_H - 20f, UiFactory.PRIMARY);

            // Detail line
            UiFactory.drawLabel(game.batch, game.fontSmall, SECTOR_DETAIL[i],
                    CARD_X + 18f, cy + 30f, UiFactory.PRIMARY);

            // Difficulty badge
            UiFactory.drawCenteredLabel(game.batch, game.fontSmall, SECTOR_DESC[i],
                    CARD_X + CARD_W - 60f, cy + CARD_H / 2f + 10f,
                    i == 2 ? UiFactory.ACCENT : UiFactory.PRIMARY);
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
        // Sector cards
        for (int i = 0; i < 3; i++) {
            if (hitBox(tx, ty, CARD_X, CARD_Y[i], CARD_W, CARD_H)) {
                playSfx("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new GameScreen(game, i));
                return;
            }
        }

        // Main Menu button
        if (hitBox(tx, ty, BTN_X, BTN_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_back.ogg");
            game.setScreen(new MainMenuScreen(game));
        }
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
