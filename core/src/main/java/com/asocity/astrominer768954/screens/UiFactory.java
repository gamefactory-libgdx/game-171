package com.asocity.astrominer768954.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Neon button style: transparent fill, 2px solid primary border,
 * outer glow pass (4px wider, 30% alpha).
 *
 * Usage pattern per frame:
 *   sr.begin(ShapeRenderer.ShapeType.Filled);
 *   UiFactory.drawButton(sr, batch, font, label, x, y, w, h);
 *   ...
 *   sr.end();
 *
 * Pre/post condition: sr is active in ShapeType.Filled.
 */
public final class UiFactory {

    /** #CFD8DC — buttons, highlights */
    public static final Color PRIMARY = new Color(0xCFD8DCff);
    /** #FF6D00 — score, coins, special */
    public static final Color ACCENT  = new Color(0xFF6D00ff);
    /** #000814 — background */
    public static final Color BG_COL  = new Color(0x000814ff);

    private static final GlyphLayout layout = new GlyphLayout();

    /**
     * Draws a neon-style button.
     *
     * @param sr    ShapeRenderer in ShapeType.Filled state
     * @param batch SpriteBatch (not currently begun)
     * @param font  BitmapFont for the label
     */
    public static void drawButton(ShapeRenderer sr, SpriteBatch batch, BitmapFont font,
                                   String label, float x, float y, float w, float h) {
        // Glow pass — primary at 30% alpha, 4px wider on all sides
        sr.setColor(PRIMARY.r, PRIMARY.g, PRIMARY.b, 0.3f);
        drawBorder(sr, x - 4, y - 4, w + 8, h + 8, 4);

        // Border pass — 2px solid primary
        sr.setColor(PRIMARY.r, PRIMARY.g, PRIMARY.b, 1f);
        drawBorder(sr, x, y, w, h, 2);

        // Text — toggle to batch
        sr.end();
        batch.begin();
        Color prev = font.getColor().cpy();
        font.setColor(PRIMARY);
        layout.setText(font, label);
        font.draw(batch, label,
                x + (w - layout.width) / 2f,
                y + (h + layout.height) / 2f);
        font.setColor(prev);
        batch.end();

        // Restore SR
        sr.begin(ShapeRenderer.ShapeType.Filled);
    }

    /**
     * Draws a centered text label using the given font and color.
     * Batch must NOT be active; method opens and closes it.
     */
    public static void drawCenteredLabel(SpriteBatch batch, BitmapFont font, String text,
                                          float cx, float cy, Color color) {
        batch.begin();
        Color prev = font.getColor().cpy();
        font.setColor(color);
        layout.setText(font, text);
        font.draw(batch, text, cx - layout.width / 2f, cy + layout.height / 2f);
        font.setColor(prev);
        batch.end();
    }

    /**
     * Draws left-aligned text at (x, baselineY).
     * Batch must NOT be active.
     */
    public static void drawLabel(SpriteBatch batch, BitmapFont font, String text,
                                  float x, float y, Color color) {
        batch.begin();
        Color prev = font.getColor().cpy();
        font.setColor(color);
        font.draw(batch, text, x, y);
        font.setColor(prev);
        batch.end();
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /** Draws a rectangle outline as four filled thin rectangles. */
    private static void drawBorder(ShapeRenderer sr, float x, float y,
                                    float w, float h, float t) {
        sr.rect(x,         y + h - t, w, t); // top
        sr.rect(x,         y,         w, t); // bottom
        sr.rect(x,         y,         t, h); // left
        sr.rect(x + w - t, y,         t, h); // right
    }

    private UiFactory() {}
}
