package com.lunazstudios.craftoflegends.hud;

import com.lunazstudios.craftoflegends.CraftOfLegends;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.List;

public final class ColAbilitiesHud {
    private static final Identifier TEX = Identifier.of(CraftOfLegends.MOD_ID, "textures/hud/hud.png");
    private static final int TEX_W = 512, TEX_H = 512;

    // --- Sprite model ---
    private record Sprite(int u, int v, int w, int h) {}

    // --- All sprites from the atlas (names clarify intent) ---
    private static final Sprite HUD_BAR   = new Sprite(0,   0,   253, 48);
    private static final Sprite XP_BAR    = new Sprite(0,   58,  13,  35);
    private static final Sprite HEALTH    = new Sprite(0,   48,  127, 5);
    private static final Sprite MANA      = new Sprite(0,   53,  127, 5);
    private static final Sprite PASSIVE   = new Sprite(0,   119, 12,  12);
    private static final Sprite Q_ABILITY = new Sprite(0,   131, 16,  16);
    private static final Sprite W_ABILITY = new Sprite(0,   148, 16,  16);
    private static final Sprite E_ABILITY = new Sprite(0,   164, 16,  16);
    private static final Sprite R_ABILITY = new Sprite(0,   179, 16,  16);
    private static final Sprite D_SPELL   = new Sprite(0,   107, 12,  12);
    private static final Sprite F_SPELL   = new Sprite(12,  107, 12,  12);

    private record Place(Sprite sprite, int dx, int dy) {}

    private static final List<Place> LAYOUT = List.of(
            // Base
            new Place(HUD_BAR,   0,   0),

            // Sub-bars
            new Place(XP_BAR,   28,   2),
            new Place(HEALTH,   46,  31),
            new Place(MANA,     46,  37),

            // Abilities
            new Place(PASSIVE,  47,   7),
            new Place(Q_ABILITY,63,   7),
            new Place(W_ABILITY,83,   7),
            new Place(E_ABILITY,103,  7),
            new Place(R_ABILITY,123,  7),

            // Summoners
            new Place(D_SPELL,  144,  7),
            new Place(F_SPELL,  160,  7)
    );

    private ColAbilitiesHud() {}

    public static void render(DrawContext ctx, RenderTickCounter counter) {
        render(ctx, 1.0f, 0);
    }

    /**
     * Flexible render with scale & bottom margin (useful pra ajustar com outros HUDs).
     */
    public static void render(DrawContext ctx, float scale, int bottomMargin) {
        final int sw = ctx.getScaledWindowWidth();
        final int sh = ctx.getScaledWindowHeight();

        final int baseW = HUD_BAR.w;
        final int baseH = HUD_BAR.h;

        final int hudWScaled = Math.round(baseW * scale);
        final int hudHScaled = Math.round(baseH * scale);

        final int x0 = (sw - hudWScaled) / 2;
        final int y0 = sh - hudHScaled - bottomMargin;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x0, y0, 0);
        ctx.getMatrices().scale(scale, scale, 1f);

        for (Place p : LAYOUT) {
            drawSprite(ctx, p.sprite, p.dx, p.dy);
        }

        ctx.getMatrices().pop();
    }

    private static void drawSprite(DrawContext ctx, Sprite s, int x, int y) {
        ctx.drawTexture(TEX, x, y, s.u, s.v, s.w, s.h, TEX_W, TEX_H);
    }
}
