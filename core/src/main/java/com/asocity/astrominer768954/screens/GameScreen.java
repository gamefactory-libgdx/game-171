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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.asocity.astrominer768954.Constants;
import com.asocity.astrominer768954.MainGame;

public class GameScreen implements Screen {

    public static final int SECTOR_ORBIT = 0;
    public static final int SECTOR_DEEP  = 1;
    public static final int SECTOR_ALIEN = 2;

    private static final String[] SECTOR_BG = {
        "backgrounds/bg_orbit_field.png",
        "backgrounds/bg_deep_belt.png",
        "backgrounds/bg_alien_zone.png"
    };

    private static final String[] SECTOR_NAMES = { "ORBIT FIELD", "DEEP BELT", "ALIEN ZONE" };

    private static final int[]   MAX_ASTEROIDS  = {
        Constants.ASTEROID_COUNT_ORBIT, Constants.ASTEROID_COUNT_DEEP,  Constants.ASTEROID_COUNT_ALIEN
    };
    private static final int[]   TAPS_PER_ROCK  = {
        Constants.DRILL_TAPS_ORBIT,     Constants.DRILL_TAPS_DEEP,      Constants.DRILL_TAPS_ALIEN
    };
    private static final float[] SPAWN_INTERVAL = {
        Constants.SPAWN_INTERVAL_ORBIT, Constants.SPAWN_INTERVAL_DEEP,  Constants.SPAWN_INTERVAL_ALIEN
    };
    private static final float[] MIN_SIZE       = {
        Constants.ASTEROID_MIN_SIZE_ORBIT, Constants.ASTEROID_MIN_SIZE_DEEP, Constants.ASTEROID_MIN_SIZE_ALIEN
    };
    private static final float[] MAX_SIZE       = {
        Constants.ASTEROID_MAX_SIZE_ORBIT, Constants.ASTEROID_MAX_SIZE_DEEP, Constants.ASTEROID_MAX_SIZE_ALIEN
    };
    private static final int[]   BASE_SCORE     = {
        Constants.SCORE_ASTEROID_ORBIT, Constants.SCORE_ASTEROID_DEEP, Constants.SCORE_ASTEROID_ALIEN
    };
    private static final int[]   BASE_ORE       = {
        Constants.ORE_ASTEROID_ORBIT, Constants.ORE_ASTEROID_DEEP, Constants.ORE_ASTEROID_ALIEN
    };
    private static final float[] RARE_CHANCE    = {
        Constants.RARE_CHANCE_ORBIT, Constants.RARE_CHANCE_DEEP, Constants.RARE_CHANCE_ALIEN
    };
    private static final float[] GAME_DURATION  = {
        Constants.GAME_DURATION_ORBIT, Constants.GAME_DURATION_DEEP, Constants.GAME_DURATION_ALIEN
    };

    // Drill ring color per skin (RGB — matches UpgradeScreen skin list)
    private static final float[][] SKIN_COLORS = {
        { 0f,    0.85f, 1f    }, // 0 Cyan   (default)
        { 1f,    0.85f, 0f    }, // 1 Gold
        { 1f,    0.27f, 0.27f }, // 2 Red
        { 0.27f, 1f,    0.27f }, // 3 Green
        { 0.7f,  0.27f, 1f    }, // 4 Purple
        { 1f,    0.27f, 1f    }, // 5 Plasma
    };

    // HUD geometry
    private static final float HUD_Y   = Constants.WORLD_HEIGHT - Constants.HUD_BAR_HEIGHT;
    // Pause button — bottom-right corner
    private static final float PAUSE_X = Constants.WORLD_WIDTH - 80f;
    private static final float PAUSE_Y = 20f;
    private static final float PAUSE_S = Constants.BTN_ICON_SIZE;

    // -----------------------------------------------------------------------

    private final MainGame game;
    final int sector; // package-visible for PauseScreen to restart
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Stage stage;
    private final ShapeRenderer sr;

    private final Array<AsteroidData> asteroids = new Array<>();
    private final Array<Spark>        sparks    = new Array<>();

    private float gameTimer;
    private float spawnTimer;
    private int   score;
    private int   ore;
    private boolean gameEnded;

    private final float[] drillColor;

    // Alien zone flicker state
    private float flickerClock;
    private float flickerAlpha;

    // Rare asteroid pulse (0..RARE_PULSE_INTERVAL*2 cycle)
    private float pulseTime;

    // -----------------------------------------------------------------------

    public GameScreen(MainGame game, int sector) {
        this.game   = game;
        this.sector = sector;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        gameTimer  = GAME_DURATION[sector];
        spawnTimer = 0f;
        score      = 0;
        ore        = 0;
        gameEnded  = false;

        // Load drill skin color from preferences
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int skin = prefs.getInteger(Constants.PREF_SKIN, 0);
        if (skin < 0 || skin >= SKIN_COLORS.length) skin = 0;
        drillColor = SKIN_COLORS[skin];

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new PauseScreen(game, GameScreen.this, GameScreen.this.sector));
                    return true;
                }
                return false;
            }
        }));

        game.playMusic("sounds/music/music_gameplay.ogg");

        // Pre-spawn a few asteroids so screen is not empty on start
        int preSpawn = Math.min(4, MAX_ASTEROIDS[sector]);
        for (int i = 0; i < preSpawn; i++) {
            spawnAsteroid();
        }
    }

    // -----------------------------------------------------------------------
    // Update
    // -----------------------------------------------------------------------

    private void update(float delta) {
        if (gameEnded) return;

        gameTimer -= delta;
        if (gameTimer <= 0f) {
            gameTimer = 0f;
            endGame();
            return;
        }

        // Spawn asteroids
        spawnTimer -= delta;
        if (spawnTimer <= 0f) {
            spawnTimer = SPAWN_INTERVAL[sector];
            spawnAsteroid();
        }

        // Spark life
        for (int i = sparks.size - 1; i >= 0; i--) {
            Spark s = sparks.get(i);
            s.x   += s.vx * delta;
            s.y   += s.vy * delta;
            s.life -= delta;
            if (s.life <= 0f) sparks.removeIndex(i);
        }

        // Rare pulse cycle
        pulseTime += delta;
        float pulseFullCycle = Constants.RARE_PULSE_INTERVAL * 2f;
        if (pulseTime > pulseFullCycle) pulseTime -= pulseFullCycle;

        // Alien zone flicker
        if (sector == SECTOR_ALIEN) {
            if (flickerAlpha > 0f) {
                flickerAlpha -= delta / Constants.ALIEN_FLICKER_DURATION;
                if (flickerAlpha < 0f) flickerAlpha = 0f;
            }
            flickerClock += delta;
            if (flickerClock >= Constants.ALIEN_FLICKER_INTERVAL) {
                flickerClock -= Constants.ALIEN_FLICKER_INTERVAL;
                flickerAlpha = 1f;
            }
        }
    }

    private void spawnAsteroid() {
        if (asteroids.size >= MAX_ASTEROIDS[sector]) return;

        AsteroidData a  = new AsteroidData();
        a.size          = MIN_SIZE[sector] + MathUtils.random() * (MAX_SIZE[sector] - MIN_SIZE[sector]);
        float halfSize  = a.size / 2f;
        float margin    = halfSize + 14f;

        float spawnW    = Constants.WORLD_WIDTH - margin * 2f;
        float spawnH    = HUD_Y - margin * 2f;

        if (spawnW <= 0f || spawnH <= 0f) return; // safety

        a.x          = margin + MathUtils.random() * spawnW;
        a.y          = margin + MathUtils.random() * spawnH;
        a.tapsNeeded = TAPS_PER_ROCK[sector];
        a.tapsDone   = 0;
        a.isRare     = MathUtils.random() < RARE_CHANCE[sector];
        a.everTapped = false;

        asteroids.add(a);
    }

    private void drillComplete(AsteroidData a) {
        int scoreGain = BASE_SCORE[sector] * (a.isRare ? Constants.SCORE_RARE_MULTIPLIER : 1);
        int oreGain   = BASE_ORE[sector]   * (a.isRare ? Constants.ORE_RARE_MULTIPLIER   : 1);
        score += scoreGain;
        ore   += oreGain;
        spawnSparks(a.x, a.y, 8);
        asteroids.removeValue(a, true);
        playSfx("sounds/sfx/sfx_coin.ogg");
    }

    private void spawnSparks(float cx, float cy, int count) {
        for (int i = 0; i < count; i++) {
            Spark s     = new Spark();
            s.x         = cx;
            s.y         = cy;
            float angle = MathUtils.random() * MathUtils.PI2;
            float speed = 60f + MathUtils.random() * 120f;
            s.vx        = MathUtils.cos(angle) * speed;
            s.vy        = MathUtils.sin(angle) * speed;
            s.maxLife   = 0.4f + MathUtils.random() * 0.5f;
            s.life      = s.maxLife;
            sparks.add(s);
        }
    }

    private void endGame() {
        if (gameEnded) return;
        gameEnded = true;

        // Persist ore earned
        Preferences prefs    = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int currentOreBalance = prefs.getInteger(Constants.PREF_ORE_BALANCE, 0);
        prefs.putInteger(Constants.PREF_ORE_BALANCE, currentOreBalance + ore);

        // Persist high score and leaderboard for this sector
        String highKey;
        if (sector == SECTOR_ORBIT) {
            highKey = Constants.PREF_HIGH_ORBIT;
            LeaderboardScreen.addOrbitScore(score);
        } else if (sector == SECTOR_DEEP) {
            highKey = Constants.PREF_HIGH_DEEP;
            LeaderboardScreen.addDeepScore(score);
        } else {
            highKey = Constants.PREF_HIGH_ALIEN;
            LeaderboardScreen.addAlienScore(score);
        }
        if (score > prefs.getInteger(highKey, 0)) {
            prefs.putInteger(highKey, score);
        }
        prefs.flush();

        game.setScreen(new ResultScreen(game, sector, score, ore));
    }

    // -----------------------------------------------------------------------
    // Render
    // -----------------------------------------------------------------------

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(UiFactory.BG_COL.r, UiFactory.BG_COL.g, UiFactory.BG_COL.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        sr.setProjectionMatrix(camera.combined);

        // ---- Phase 1: batch — background + asteroid textures + pause icon ----
        game.batch.begin();
        Texture bg = game.manager.get(SECTOR_BG[sector], Texture.class);
        game.batch.draw(bg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        for (AsteroidData a : asteroids) {
            Texture tex = getAsteroidTex(a.size);
            game.batch.draw(tex, a.x - a.size / 2f, a.y - a.size / 2f, a.size, a.size);
        }

        Texture pauseIcon = game.manager.get("sprites/icon_pause.png", Texture.class);
        game.batch.draw(pauseIcon, PAUSE_X, PAUSE_Y, PAUSE_S, PAUSE_S);
        game.batch.end();

        // ---- Phase 2: SR Filled — HUD bar, rare borders, sparks ----
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // HUD bar background
        sr.setColor(0f, 0.03f, 0.08f, 0.9f);
        sr.rect(0, HUD_Y, Constants.WORLD_WIDTH, Constants.HUD_BAR_HEIGHT);

        // HUD bar bottom border
        sr.setColor(UiFactory.PRIMARY.r, UiFactory.PRIMARY.g, UiFactory.PRIMARY.b, 0.6f);
        sr.rect(0, HUD_Y, Constants.WORLD_WIDTH, 2f);

        // Rare asteroid pulsing border
        float pulseAlpha = 0.4f + 0.6f * Math.abs(MathUtils.sin(pulseTime * MathUtils.PI / Constants.RARE_PULSE_INTERVAL));
        for (AsteroidData a : asteroids) {
            if (!a.isRare) continue;
            // Glow backdrop
            sr.setColor(1f, 0.27f, 0.27f, pulseAlpha * 0.25f);
            float gs = a.size + 16f;
            sr.rect(a.x - gs / 2f, a.y - gs / 2f, gs, gs);
            // Solid border
            sr.setColor(1f, 0.27f, 0.27f, pulseAlpha);
            drawBorder(sr, a.x - a.size / 2f - 3f, a.y - a.size / 2f - 3f, a.size + 6f, a.size + 6f, 2f);
        }

        // Sparks
        for (Spark s : sparks) {
            float alpha = s.life / s.maxLife;
            sr.setColor(drillColor[0], drillColor[1], drillColor[2], alpha);
            sr.circle(s.x, s.y, 4f * alpha, 6);
        }

        sr.end();

        // ---- Phase 3: SR Line — drill progress rings ----
        sr.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(3f);

        for (AsteroidData a : asteroids) {
            boolean showRing = (sector != SECTOR_ALIEN || a.everTapped);
            if (!showRing) continue;

            float ringR   = a.size / 2f + 8f;
            float progress = (float) a.tapsDone / (float) a.tapsNeeded;

            // Dark ring background
            sr.setColor(0f, 0f, 0f, 0.45f);
            sr.circle(a.x, a.y, ringR, 36);

            // Colored fill arc
            if (progress > 0f) {
                sr.setColor(drillColor[0], drillColor[1], drillColor[2], 1f);
                sr.arc(a.x, a.y, ringR, 90f, 360f * progress, 36);
            }
        }

        Gdx.gl.glLineWidth(1f);
        sr.end();

        // ---- Phase 4: Alien zone screen flicker overlay ----
        if (sector == SECTOR_ALIEN && flickerAlpha > 0f) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(0f, 0.85f, 1f, flickerAlpha * 0.14f);
            sr.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
            sr.end();
        }

        // ---- Phase 5: HUD text labels ----
        float hudMidY    = HUD_Y + Constants.HUD_BAR_HEIGHT / 2f;
        float hudLabelY  = HUD_Y + Constants.HUD_BAR_HEIGHT - 15f;

        // Sector name — left
        UiFactory.drawLabel(game.batch, game.fontSmall, SECTOR_NAMES[sector],
                16f, hudLabelY, UiFactory.PRIMARY);

        // Score — center
        UiFactory.drawCenteredLabel(game.batch, game.fontBody, String.valueOf(score),
                Constants.WORLD_WIDTH / 2f, hudMidY + 10f, UiFactory.ACCENT);

        // Ore — right side
        UiFactory.drawLabel(game.batch, game.fontSmall, "ORE:" + ore,
                Constants.WORLD_WIDTH - 170f, hudLabelY, UiFactory.ACCENT);

        // Timer — below HUD bar left
        int timerSecs   = (int) Math.ceil(gameTimer);
        boolean urgency = timerSecs <= 10;
        UiFactory.drawCenteredLabel(game.batch, game.fontSmall, "TIME:" + timerSecs,
                Constants.WORLD_WIDTH / 2f, HUD_Y - 18f,
                urgency ? UiFactory.ACCENT : UiFactory.PRIMARY);

        stage.act(delta);

        // Input
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            handleTouch(touch.x, touch.y);
        }
    }

    private void handleTouch(float tx, float ty) {
        if (gameEnded) return;

        // Pause button
        if (tx >= PAUSE_X && tx <= PAUSE_X + PAUSE_S && ty >= PAUSE_Y && ty <= PAUSE_Y + PAUSE_S) {
            playSfx("sounds/sfx/sfx_button_click.ogg");
            game.setScreen(new PauseScreen(game, this, sector));
            return;
        }

        // Ignore touches in HUD area
        if (ty >= HUD_Y) return;

        // Asteroid tap — find the first hit asteroid (smallest touch target first for fairness)
        for (AsteroidData a : asteroids) {
            float dx = tx - a.x;
            float dy = ty - a.y;
            float r  = a.size / 2f;
            if (dx * dx + dy * dy <= r * r) {
                a.tapsDone++;
                a.everTapped = true;
                playSfx("sounds/sfx/sfx_hit.ogg");
                if (a.tapsDone >= a.tapsNeeded) {
                    drillComplete(a);
                }
                return; // only one asteroid per tap frame
            }
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Texture getAsteroidTex(float size) {
        if (size >= 90f) return game.manager.get("sprites/asteroid_big.png",   Texture.class);
        if (size >= 65f) return game.manager.get("sprites/asteroid_med.png",   Texture.class);
        return              game.manager.get("sprites/asteroid_small.png", Texture.class);
    }

    private static void drawBorder(ShapeRenderer sr, float x, float y, float w, float h, float t) {
        sr.rect(x,         y + h - t, w, t);
        sr.rect(x,         y,         w, t);
        sr.rect(x,         y,         t, h);
        sr.rect(x + w - t, y,         t, h);
    }

    private void playSfx(String path) {
        if (game.sfxEnabled)
            game.manager.get(path, Sound.class).play(1.0f);
    }

    @Override
    public void show() {
        // Ensure music keeps playing when returning from PauseScreen
        game.playMusic("sounds/music/music_gameplay.ogg");
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void hide()   {}
    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
    }

    // -----------------------------------------------------------------------
    // Inner data classes
    // -----------------------------------------------------------------------

    private static class AsteroidData {
        float x, y, size;
        int   tapsNeeded, tapsDone;
        boolean isRare;
        boolean everTapped; // alien zone: ring hidden until first tap
    }

    private static class Spark {
        float x, y, vx, vy, life, maxLife;
    }
}
