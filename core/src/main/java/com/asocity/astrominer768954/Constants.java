package com.asocity.astrominer768954;

public class Constants {

    // World dimensions
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 854f;

    // HUD
    public static final float HUD_BAR_HEIGHT = 60f;

    // Asteroid sizes
    public static final float ASTEROID_MIN_SIZE_ORBIT = 60f;
    public static final float ASTEROID_MAX_SIZE_ORBIT = 120f;
    public static final float ASTEROID_MIN_SIZE_DEEP  = 50f;
    public static final float ASTEROID_MAX_SIZE_DEEP  = 100f;
    public static final float ASTEROID_MIN_SIZE_ALIEN = 40f;
    public static final float ASTEROID_MAX_SIZE_ALIEN = 90f;

    // Asteroid counts visible at once per sector
    public static final int ASTEROID_COUNT_ORBIT = 10;
    public static final int ASTEROID_COUNT_DEEP  = 20;
    public static final int ASTEROID_COUNT_ALIEN = 30;

    // Taps required to drill an asteroid
    public static final int DRILL_TAPS_ORBIT = 5;
    public static final int DRILL_TAPS_DEEP  = 8;
    public static final int DRILL_TAPS_ALIEN = 12;

    // Asteroid spawn intervals (seconds)
    public static final float SPAWN_INTERVAL_ORBIT = 2.0f;
    public static final float SPAWN_INTERVAL_DEEP  = 1.2f;
    public static final float SPAWN_INTERVAL_ALIEN = 0.7f;

    // Score values
    public static final int SCORE_ASTEROID_ORBIT = 10;
    public static final int SCORE_ASTEROID_DEEP  = 20;
    public static final int SCORE_ASTEROID_ALIEN = 35;
    public static final int SCORE_RARE_MULTIPLIER = 3;

    // Ore values per asteroid
    public static final int ORE_ASTEROID_ORBIT = 5;
    public static final int ORE_ASTEROID_DEEP  = 10;
    public static final int ORE_ASTEROID_ALIEN = 20;
    public static final int ORE_RARE_MULTIPLIER = 5;

    // Rare asteroid chance per sector
    public static final float RARE_CHANCE_ORBIT = 0.05f;
    public static final float RARE_CHANCE_DEEP  = 0.10f;
    public static final float RARE_CHANCE_ALIEN = 0.15f;

    // Game timer (seconds) — 0 means survival/unlimited
    public static final float GAME_DURATION_ORBIT = 60f;
    public static final float GAME_DURATION_DEEP  = 60f;
    public static final float GAME_DURATION_ALIEN = 60f;

    // Alien zone flicker interval (seconds)
    public static final float ALIEN_FLICKER_INTERVAL = 4.0f;
    public static final float ALIEN_FLICKER_DURATION = 0.05f;

    // Rare asteroid pulse interval
    public static final float RARE_PULSE_INTERVAL = 0.5f;

    // Shop — drill skin prices
    public static final int SKIN_PRICE_CYAN   = 0;    // default — free
    public static final int SKIN_PRICE_GOLD   = 100;
    public static final int SKIN_PRICE_RED    = 150;
    public static final int SKIN_PRICE_GREEN  = 200;
    public static final int SKIN_PRICE_PURPLE = 250;
    public static final int SKIN_PRICE_PLASMA = 400;

    // UI sizes
    public static final float BTN_PRIMARY_W   = 240f;
    public static final float BTN_PRIMARY_H   = 70f;
    public static final float BTN_SECONDARY_W = 200f;
    public static final float BTN_SECONDARY_H = 60f;
    public static final float BTN_ICON_SIZE    = 60f;

    // Font sizes
    public static final int FONT_SIZE_TITLE  = 48;
    public static final int FONT_SIZE_HEADER = 36;
    public static final int FONT_SIZE_BODY   = 28;
    public static final int FONT_SIZE_SMALL  = 20;

    // Leaderboard
    public static final int LEADERBOARD_MAX_ENTRIES = 10;

    // SharedPreferences keys
    public static final String PREFS_NAME          = "GamePrefs";
    public static final String PREF_MUSIC          = "musicEnabled";
    public static final String PREF_SFX            = "sfxEnabled";
    public static final String PREF_ORE_BALANCE    = "oreBalance";
    public static final String PREF_SKIN           = "selectedSkin";
    public static final String PREF_HIGH_ORBIT     = "highScoreOrbit";
    public static final String PREF_HIGH_DEEP      = "highScoreDeep";
    public static final String PREF_HIGH_ALIEN     = "highScoreAlien";
    public static final String PREF_SKIN_OWNED     = "skinOwned_";   // append skin index
    public static final String PREF_LEADERBOARD_ORBIT = "leaderboard_orbit_";
    public static final String PREF_LEADERBOARD_DEEP  = "leaderboard_deep_";
    public static final String PREF_LEADERBOARD_ALIEN = "leaderboard_alien_";
}
