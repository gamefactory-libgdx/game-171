# Astro Miner — Figma AI Design Brief

---

## 1. Art Style & Color Palette

**Art Style:** Flat geometric with soft sci-fi accents. Clean, minimalist shapes with subtle gradient fills and glowing edge highlights. No heavy shadows or realistic textures—keep it bright and arcade-feeling with a modern, polished look suitable for ages 8+.

**Primary Palette:**
- Deep Space Navy: `#0a1428`
- Cosmic Purple: `#6d28d9`
- Neon Cyan: `#00d9ff`
- Bright Ore Gold: `#ffd700`

**Accent Colors:**
- Warning Red/Rare Ore: `#ff4444`
- Soft Glow White: `#f0f4ff`

**Typography:** Geometric sans-serif (like Inter or Roboto). Use **bold** (700wt) for titles and CTAs, **semibold** (600wt) for headers, **regular** (400wt) for body text. All text should have subtle anti-aliasing and optional thin glow effect (`#00d9ff` at 20% opacity) on accent UI elements to reinforce the sci-fi theme.

---

## 2. App Icon — icon_512.png (512×512px)

**Background:** Radial gradient from Deep Space Navy (`#0a1428`) at corners to Cosmic Purple (`#6d28d9`) at center, creating an atmospheric void effect.

**Central Symbol:** A stylized asteroid with an integrated drill bit. The asteroid is a jagged polygon shape in Bright Ore Gold (`#ffd700`) with faceted edges to suggest crystal ore. Overlaid is a drill bit (geometric spiraling cone) in Neon Cyan (`#00d9ff`), centered on the asteroid's top, symbolizing the tap-to-drill mechanic.

**Effects:** 
- Inner glow around the asteroid in Bright Ore Gold at 40% opacity, creating luminous ore depth
- Outer aura halo in Neon Cyan (`#00d9ff`) at 30% opacity, 16px blur, representing energy/mining activity
- Subtle hard light on the drill bit's left edge in Soft Glow White (`#f0f4ff`) at 60% opacity for metallic dimensionality

**Overall Mood:** Energetic, futuristic, and immediately recognizable as a mining action game. The contrast between gold ore and cyan drill communicates the core mechanic. Safe zone: main artwork (asteroid + drill) contained within central 400×400px area.

---

## 3. Backgrounds (480×854 portrait)

**Zone/Theme List (derived from GDD):**
1. Main Menu / Title Screen
2. Orbit Field (easiest sector)
3. Deep Belt (medium sector)
4. Alien Zone (hardest sector)

---

### backgrounds/bg_main.png (480×854)
Serene entry point with a vertical starfield. Gradient from Deep Space Navy (`#0a1428`) at top to Cosmic Purple (`#6d28d9`) at bottom. Scatter 40–60 small star shapes (2–4px) in Soft Glow White (`#f0f4ff`) across the canvas, clustered more densely toward the top. Add 3–4 large, faint asteroids as geometric outlines (no fill) in Neon Cyan (`#00d9ff`) at 15% opacity in the corners, suggesting the worlds beyond. This sets a welcoming, arcade-ready tone without being overwhelming.

---

### backgrounds/bg_orbit_field.png (480×854)
Bright, beginner-friendly sector with a daylit asteroid belt. Gradient from light Cosmic Purple (`#7d38e8`) at top to Deep Space Navy (`#0a1428`) at bottom. Place 8–12 small, gently drifting asteroids as simple rounded polygons in Bright Ore Gold (`#ffd700`) and warm orange (`#ff9500`), semi-transparent (40–50% opacity), scattered throughout the mid-ground. Add a thin horizontal sun glow in the top third using a radial gradient in pale yellow (`#ffeb99`) at 20% opacity. Include a faint grid pattern (thin lines in Neon Cyan at 8% opacity) behind everything to suggest a mining map overlay. Overall mood: optimistic and accessible.

---

### backgrounds/bg_deep_belt.png (480×854)
Intense, medium-difficulty asteroid field. Gradient from dark Cosmic Purple (`#4c1d95`) at top to Deep Space Navy (`#0a1428`) at bottom. Pack 15–20 larger, more irregular asteroids in shades of gold, bronze (`#cd7f32`), and dark gray (`#4a5568`), semi-transparent (50–60% opacity), overlapping to create visual density. Highlight 3–4 asteroids with bright Neon Cyan edges (`#00d9ff`) at 30% opacity to suggest valuable ore veins. Add faint particle dust (20–30 small circles in white at 10% opacity) floating diagonally across the screen, implying motion. Include a subtle scanline texture (horizontal lines at 2% opacity in Neon Cyan). Overall mood: challenging and energetic.

---

### backgrounds/bg_alien_zone.png (480×854)
Chaotic, otherworldly hardest sector. Gradient from very dark purple (`#2d1b4e`) at top to deep navy (`#0a0f28`) at bottom, with a sickly green tint overlay in `#1a4d3a` at 15% opacity in the lower half. Populate with 20–30 wildly shaped asteroids in irregular polygons, using colors: dark gold, murky cyan (`#004d66`), and acidic lime (`#b3ff00`) for rare dangerous ore. Make asteroids more angular and jagged than other sectors. Add 4–6 pulsar effects (concentric circles in Warning Red `#ff4444` and Neon Cyan, 5% opacity each) scattered across the canvas, suggesting alien energy. Include a distorted grid overlay (wavy horizontal lines in Neon Cyan at 12% opacity) and faint vertical light streaks in Neon Cyan at 8% opacity. Overall mood: intense, alien, and slightly unsettling to signal maximum difficulty.

---

## 4. UI Screens (480×854 portrait)

### main_menu.png (480×854)
Uses bg_main.png. Large title "ASTRO MINER" (bold, 48–56px, Neon Cyan `#00d9ff`) centered horizontally in upper-middle area, with a thin glow effect. Below the title, centered at y=380, a large "PLAY" button (semantic center, 140×60px, Bright Ore Gold `#ffd700` background, Deep Space Navy text, bold 24px). Four small icon buttons (60×60px each) arranged horizontally in a row at the bottom: Leaderboard, Upgrade Shop, Settings, and Credits (from left to right, evenly spaced, centered horizontally). Each button uses Neon Cyan `#00d9ff` icon on transparent background with a subtle rounded rectangle outline. All text/buttons centered and well-spaced for touch accessibility.

---

### sector_select.png (480×854)
Uses bg_main.png. Header "SELECT SECTOR" (semibold, 32px, Neon Cyan `#00d9ff`) at top-center (y=60). Three large vertical cards stacked below (each ~140×100px, rounded corners), positioned at y=150, y=270, y=390. **Card 1 (Orbit Field):** Cosmic Purple background (`#6d28d9`), "ORBIT FIELD" label (bold, 18px, Soft Glow White), small descriptive text (12px, gray). **Card 2 (Deep Belt):** slightly darker purple, "DEEP BELT" label, descriptive text. **Card 3 (Alien Zone):** darkest purple with Warning Red accent border, "ALIEN ZONE" label, "HARD" badge in red. Each card is tappable (plays transition to gameplay screen). "BACK" button (60×40px, outline style) bottom-left. Simple, clear hierarchy emphasizing difficulty progression.

---

### game_orbit_field.png (480×854)
Uses bg_orbit_field.png. **HUD:** Top bar (480×60px) with sector name "ORBIT FIELD" (16px, white, left-aligned at x=20), ore counter "ORE: 0" (16px, Bright Ore Gold `#ffd700`, right-aligned at x=460), and score/time display. Large asteroids spawn across the screen (varying sizes, 60–120px), each showing a circular drill progress indicator (thin Neon Cyan ring, fills with Bright Ore Gold as taps accumulate). Tapped asteroids briefly flash white. Bottom-right corner: pause button (40×40px, outline icon). Game area occupies full screen minus HUD bar. Particles (small white circles) emit from successfully drilled asteroids. Visual feedback is immediate and clear to support rapid tapping.

---

### game_deep_belt.png (480×854)
Uses bg_deep_belt.png. **HUD:** Identical layout to game_orbit_field (sector name, ore counter, score). Asteroids spawn faster and at more unpredictable intervals, covering more of the screen area (16–24 asteroids visible at once vs. 8–12 in Orbit Field). Drill progress indicators remain visible but smaller (due to density). Critical rare asteroids highlighted with a pulsing Warning Red border (`#ff4444`, 2px, pulse every 0.5s). Particles more aggressive. Overall visual intensity increased while maintaining readability. Pause button in same position (bottom-right).

---

### game_alien_zone.png (480×854)
Uses bg_alien_zone.png. **HUD:** Same top bar layout. Asteroids spawn in chaotic, unpredictable patterns (25–35 simultaneous). Drill progress indicators hidden by default until tapped (then appears as bright Neon Cyan ring). Rare ore asteroids glow with animated acidic lime (`#b3ff00`) pulse and Warning Red aura, signaling high value and risk. Tapping generates larger particle bursts in Neon Cyan and lime. Screen occasionally flickers with brief Neon Cyan flash (1–2 frames every 3–5s) to heighten tension. Pause button bottom-right (same position). The overall design pushes the visual chaos while keeping tap targets clear.

---

### result_screen.png (480×854)
Uses bg_main.png. Header "RUN COMPLETE" (bold, 36px, Neon Cyan `#00d9ff`) at top-center (y=80). Large central panel (420×320px, centered horizontally, background Deep Space Navy with thin Neon Cyan border, rounded corners) displays: **Sector Name** (semibold, 20px, white, top-left of panel), **Score** (bold, 28px, Bright Ore Gold, center), **Ore Collected** (16px, white, below score), **Unlocks** (if any—e.g., "New Skin Unlocked!", 14px, Warning Red). Three large buttons below panel at y=500: "PLAY AGAIN" (center, Bright Ore Gold), "UPGRADE SHOP" (left), "MAIN MENU" (right). Each button 100×50px, rounded, clear labels. All text centered for clarity. Simple, reward-focused layout that drives players toward progression.

---

### game_over_screen.png (480×854)
Uses bg_main.png. Header "GAME OVER" (bold, 40px, Warning Red `#ff4444`) at top-center (y=100). Central display panel (400×280px, centered, Deep Space Navy background with thin Neon Cyan border, rounded corners) shows: **Final Score** (bold, 32px, Bright Ore Gold, center-top), **Sector Played** (16px, white, mid-panel), **Time Survived** (16px, white, mid-panel), **Ore Collected** (16px, white, mid-panel). Two large buttons below at y=480: "PLAY AGAIN" (left, Bright Ore Gold, 120×50px), "MAIN MENU" (right, outline style, 120×50px). Buttons horizontally centered with 20px gap. Clear, somber layout with emphasis on score and retry action.

---

### upgrade_screen.png (480×854)
Uses bg_main.png. Header "UPGRADE SHOP" (bold, 32px, Neon Cyan `#00d9ff`) at top (y=50). Ore balance display top-right: "ORE: 2,450" (16px, Bright Ore Gold, semibold). Six cosmetic drill skin cards arranged in a 2×3 grid (each card 180×140px, spaced 20px apart, starting at y=120, left-aligned at x=20). Each card: thumbnail image of drill skin (80×80px, centered), drill name (14px bold, white, below thumbnail), ore cost (12px, Bright Ore Gold, below name), and a "PURCHASE" or "OWNED" button (60×30px, below cost). Owned skins show a checkmark icon instead of button. "BACK" button (60×40px, outline) bottom-left (y=800). Clear grid layout and immediate cost visibility drive purchasing decisions.

---

### leaderboard_screen.png (480×854)
Uses bg_main.png. Header "LEADERBOARD" (bold, 32px, Neon Cyan `#00d9ff`) at top (y=50). Three small tab buttons below header (100×35px each, centered): "ORBIT FIELD", "DEEP BELT", "ALIEN ZONE" (currently selected tab highlighted in Bright Ore Gold). Leaderboard table starts at y=130: rows of rank (#1–#10, bold white, left-aligned), player name (18px, white, center), score (16px, Bright Ore Gold, right). Alternate row backgrounds (every other row in Deep Space Navy `#0a1428` vs. `#0d1a2d`) for readability. "BACK" button (60×40px, outline) bottom-left. Clean, competitive, scanline-inspired layout.

---

### settings_screen.png (480×854)
Uses bg_main.png. Header "SETTINGS" (bold, 32px, Neon Cyan `#00d9ff`) at top (y=50). Settings list starting at y=140 (480px width, left-aligned at x=20). Each setting row (440×60px, vertical spacing 70px): setting label (16px, white, left) + toggle/slider control (60×30px, right-aligned at x=440). **Row 1:** "SOUND" toggle (on/off, toggle background Cosmic Purple when on). **Row 2:** "VIBRATION" toggle (same style). **Row 3:** "RESET PROGRESS" as a button (140×40px, outline style, Warning Red text, centered within row). Below settings: "CREDITS" button (140×40px, Neon Cyan outline, centered at y=600). "BACK" button (60×40px, outline) bottom-left. Straightforward, sparse layout focusing on key controls.

---

## 5. Export Checklist

- icon_512.png (512×512)
- backgrounds/bg_main.png (480×854)
- backgrounds/bg_orbit_field.png (480×854)
- backgrounds/bg_deep_belt.png (480×854)
- backgrounds/bg_alien_zone.png (480×854)
- ui/main_menu.png (480×854)
- ui/sector_select.png (480×854)
- ui/game_orbit_field.png (480×854)
- ui/game_deep_belt.png (480×854)
- ui/game_alien_zone.png (480×854)
- ui/result_screen.png (480×854)
- ui/game_over_screen.png (480×854)
- ui/upgrade_screen.png (480×854)
- ui/leaderboard_screen.png (480×854)
- ui/settings_screen.png (480×854)
