package com.asocity.astrominer768954;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.asocity.astrominer768954.screens.MainMenuScreen;

public class MainGame extends Game {

    public SpriteBatch batch;
    public AssetManager manager;

    // Shared fonts
    public BitmapFont fontTitle;   // Orbitron — titles, scores
    public BitmapFont fontBody;    // Roboto — body, buttons, HUD
    public BitmapFont fontSmall;   // Roboto small

    // Music state
    public boolean musicEnabled = true;
    public boolean sfxEnabled   = true;
    public Music   currentMusic = null;

    @Override
    public void create() {
        batch   = new SpriteBatch();
        manager = new AssetManager();

        generateFonts();
        loadAssets();
        manager.finishLoading();

        setScreen(new MainMenuScreen(this));
    }

    // -----------------------------------------------------------------------
    // Font generation
    // -----------------------------------------------------------------------

    private void generateFonts() {
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Orbitron-Regular.ttf"));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Roboto-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size = Constants.FONT_SIZE_TITLE;
        fontTitle = titleGen.generateFont(p);

        p.size = Constants.FONT_SIZE_BODY;
        fontBody = bodyGen.generateFont(p);

        p.size = Constants.FONT_SIZE_SMALL;
        fontSmall = bodyGen.generateFont(p);

        titleGen.dispose();
        bodyGen.dispose();
    }

    // -----------------------------------------------------------------------
    // Asset loading
    // -----------------------------------------------------------------------

    private void loadAssets() {
        // Backgrounds
        manager.load("backgrounds/bg_main.png",         Texture.class);
        manager.load("backgrounds/bg_orbit_field.png",  Texture.class);
        manager.load("backgrounds/bg_deep_belt.png",    Texture.class);
        manager.load("backgrounds/bg_alien_zone.png",   Texture.class);

        // Music
        manager.load("sounds/music/music_menu.ogg",      Music.class);
        manager.load("sounds/music/music_gameplay.ogg",  Music.class);
        manager.load("sounds/music/music_game_over.ogg", Music.class);

        // SFX
        manager.load("sounds/sfx/sfx_button_click.ogg",   Sound.class);
        manager.load("sounds/sfx/sfx_button_back.ogg",    Sound.class);
        manager.load("sounds/sfx/sfx_toggle.ogg",         Sound.class);
        manager.load("sounds/sfx/sfx_coin.ogg",           Sound.class);
        manager.load("sounds/sfx/sfx_hit.ogg",            Sound.class);
        manager.load("sounds/sfx/sfx_game_over.ogg",      Sound.class);
        manager.load("sounds/sfx/sfx_level_complete.ogg", Sound.class);
        manager.load("sounds/sfx/sfx_power_up.ogg",       Sound.class);

        // Sprites — ships
        manager.load("sprites/player_ship.png",             Texture.class);
        manager.load("sprites/player_ship_alt.png",         Texture.class);

        // Asteroids
        manager.load("sprites/asteroid_big.png",            Texture.class);
        manager.load("sprites/asteroid_med.png",            Texture.class);
        manager.load("sprites/asteroid_small.png",          Texture.class);

        // UI buttons
        manager.load("sprites/button_blue.png",             Texture.class);
        manager.load("sprites/button_blue_pressed.png",     Texture.class);
        manager.load("sprites/button_grey.png",             Texture.class);
        manager.load("sprites/button_grey_pressed.png",     Texture.class);
        manager.load("sprites/button_green.png",            Texture.class);
        manager.load("sprites/button_green_pressed.png",    Texture.class);
        manager.load("sprites/button_red.png",              Texture.class);
        manager.load("sprites/button_red_pressed.png",      Texture.class);
        manager.load("sprites/button_yellow.png",           Texture.class);
        manager.load("sprites/button_yellow_pressed.png",   Texture.class);
        manager.load("sprites/button_round_blue.png",       Texture.class);
        manager.load("sprites/button_round_blue_pressed.png", Texture.class);
        manager.load("sprites/button_round_grey.png",       Texture.class);

        // HUD icons
        manager.load("sprites/icon_settings.png",           Texture.class);
        manager.load("sprites/icon_leaderboard.png",        Texture.class);
        manager.load("sprites/icon_music_on.png",           Texture.class);
        manager.load("sprites/icon_music_off.png",          Texture.class);
        manager.load("sprites/icon_sfx_on.png",             Texture.class);
        manager.load("sprites/icon_sfx_off.png",            Texture.class);
        manager.load("sprites/icon_timer.png",              Texture.class);
        manager.load("sprites/icon_star.png",               Texture.class);
        manager.load("sprites/icon_trophy.png",             Texture.class);
        manager.load("sprites/icon_pause.png",              Texture.class);
        manager.load("sprites/icon_home.png",               Texture.class);
        manager.load("sprites/icon_shop.png",               Texture.class);
        manager.load("sprites/icon_heart.png",              Texture.class);
        manager.load("sprites/icon_close.png",              Texture.class);
    }

    // -----------------------------------------------------------------------
    // Music helpers
    // -----------------------------------------------------------------------

    public void playMusic(String path) {
        Music requested = manager.get(path, Music.class);
        if (requested == currentMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = requested;
        currentMusic.setLooping(true);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    public void playMusicOnce(String path) {
        if (currentMusic != null) currentMusic.stop();
        currentMusic = manager.get(path, Music.class);
        currentMusic.setLooping(false);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    // -----------------------------------------------------------------------
    // Dispose
    // -----------------------------------------------------------------------

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        manager.dispose();
        fontTitle.dispose();
        fontBody.dispose();
        fontSmall.dispose();
    }
}
