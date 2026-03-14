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
 * "RUN COMPLETE" screen shown when the sector timer reaches zero.
 * Displays final score, ore collected, personal best, and sector played.
 * Buttons: Play Again (same sector) | Upgrade Shop | Main Menu
 */
public class ResultScreen implements Screen {

    private static final String[] SECTOR_NAMES = { "ORBIT FIELD", "DEEP BELT", "ALIEN ZONE" };
    private static final String[] PREF_HIGH    = {
        Constants.PREF_HIGH_ORBIT, Constants.PREF_HIGH_DEEP, Constants.PREF_HIGH_ALIEN
    };

    private final MainGame game;
    private final int      sector;
    private final int      score;
    private final int      ore;
    private final int      personalBest;

    private final OrthographicCamera camera;
    private final Viewport           viewport;
    private final Stage              stage;
    private final ShapeRenderer      sr;

    // Stats panel
    private static final float PANEL_W = 420f;
    private static final float PANEL_H = 300f;
    private static final float PANEL_X = (Constants.WORLD_WIDTH  - PANEL_W) / 2f;
    private static final float PANEL_Y = 370f;

    // Three buttons below panel
    private static final float BTN_W     = 200f;
    private static final float BTN_H     = Constants.BTN_SECONDARY_H;
    private static final float BTN_GAP   = 16f;
    private static final float BTNS_TOTAL = 3 * BTN_W + 2 * BTN_GAP;
    private static final float BTN_START  = (Constants.WORLD_WIDTH - BTNS_TOTAL) / 2f;
    private static final float BTN_Y      = 280f;

    private static final float BTN_AGAIN_X = BTN_START;
    private static final float BTN_SHOP_X  = BTN_START + BTN_W + BTN_GAP;
    private static final float BTN_MENU_X  = BTN_START + 2 * (BTN_W + BTN_GAP);

    public ResultScreen(MainGame game, int sector, int score, int ore) {
        this.game   = game;
        this.sector = sector;
        this.score  = score;
        this.ore    = ore;

        // Load personal best for this sector
        personalBest = Gdx.app.getPreferences(Constants.PREFS_NAME)
                             .getInteger(PREF_HIGH[sector], 0);

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

        game.playMusicOnce("sounds/music/music_game_over.ogg");
        playSfx("sounds/sfx/sfx_level_complete.ogg");
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
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, "RUN COMPLETE",
                Constants.WORLD_WIDTH / 2f, 760f, UiFactory.PRIMARY);

        // Sub-header — sector name
        UiFactory.drawCenteredLabel(game.batch, game.fontBody, SECTOR_NAMES[sector],
                Constants.WORLD_WIDTH / 2f, 706f, UiFactory.ACCENT);

        // Panel + buttons
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Panel background
        sr.setColor(0f, 0.03f, 0.08f, 0.92f);
        sr.rect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H);

        // Panel glow border
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.18f);
        drawBorder(sr, PANEL_X - 4, PANEL_Y - 4, PANEL_W + 8, PANEL_H + 8, 4f);

        // Panel border
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 1f);
        drawBorder(sr, PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 2f);

        // Divider inside panel
        float divY = PANEL_Y + PANEL_H - 90f;
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.3f);
        sr.rect(PANEL_X + 20, divY, PANEL_W - 40, 1.5f);

        // Buttons
        UiFactory.drawButton(sr, game.batch, game.fontSmall, "PLAY AGAIN",    BTN_AGAIN_X, BTN_Y, BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontSmall, "UPGRADE SHOP",  BTN_SHOP_X,  BTN_Y, BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontSmall, "MAIN MENU",     BTN_MENU_X,  BTN_Y, BTN_W, BTN_H);

        sr.end();

        // Panel content
        float cx = Constants.WORLD_WIDTH / 2f;

        // Score label
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall, "SCORE",
                cx, PANEL_Y + PANEL_H - 24f, UiFactory.PRIMARY);

        // Score value — large
        UiFactory.drawCenteredLabel(game.batch, game.fontTitle, String.valueOf(score),
                cx, PANEL_Y + PANEL_H - 72f, UiFactory.ACCENT);

        // Personal best
        boolean isNewBest = score >= personalBest && score > 0;
        String bestLine   = isNewBest ? "NEW BEST!" : "BEST: " + personalBest;
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall, bestLine,
                cx, PANEL_Y + PANEL_H - 120f,
                isNewBest ? UiFactory.ACCENT : UiFactory.PRIMARY);

        // Ore collected
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall, "ORE COLLECTED: " + ore,
                cx, PANEL_Y + PANEL_H - 170f, UiFactory.PRIMARY);

        // Tip
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall, "Ore added to your balance",
                cx, PANEL_Y + PANEL_H - 215f, UiFactory.PRIMARY);

        stage.act(delta);

        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            handleTouch(touch.x, touch.y);
        }
    }

    private void handleTouch(float tx, float ty) {
        if (hitBox(tx, ty, BTN_AGAIN_X, BTN_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_click.ogg");
            game.setScreen(new GameScreen(game, sector)); // NEW instance — fresh run

        } else if (hitBox(tx, ty, BTN_SHOP_X, BTN_Y, BTN_W, BTN_H)) {
            playSfx("sounds/sfx/sfx_button_click.ogg");
            game.setScreen(new UpgradeScreen(game));

        } else if (hitBox(tx, ty, BTN_MENU_X, BTN_Y, BTN_W, BTN_H)) {
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
