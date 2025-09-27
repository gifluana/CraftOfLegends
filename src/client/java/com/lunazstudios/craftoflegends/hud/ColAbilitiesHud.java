package com.lunazstudios.craftoflegends.hud;

import com.lunazstudios.craftoflegends.CraftOfLegends;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class ColAbilitiesHud {
    private static final Identifier TEX = Identifier.of(CraftOfLegends.MOD_ID, "textures/hud/hud.png");

    private static final int TEX_W = 512;
    private static final int TEX_H = 512;

    private static final int U = 0;
    private static final int V = 0;
    private static final int W = 253;
    private static final int H = 48;

    public static void render(DrawContext ctx, RenderTickCounter counter) {
        final int sw = ctx.getScaledWindowWidth();
        final int sh = ctx.getScaledWindowHeight();

        final int margin = 0;
        final int x = (sw - W) / 2;
        final int y = sh - H - margin;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Hud
        ctx.drawTexture(TEX, x, y, 0, 0, 253, 48, TEX_W, TEX_H);

        // XP
        ctx.drawTexture(TEX, x + 28, y + 2, 0, 58, 13, 35, TEX_W, TEX_H);

        // Health
        ctx.drawTexture(TEX, x + 46, y + 31, 0, 48, 127, 5, TEX_W, TEX_H);

        // Mana
        ctx.drawTexture(TEX, x + 46, y + 37, 0, 53, 127, 5, TEX_W, TEX_H);

        // Passive
        ctx.drawTexture(TEX, x + 47, y + 7, 0, 119, 12, 12, TEX_W, TEX_H);

        // Q
        ctx.drawTexture(TEX, x + 63, y + 7, 0, 131, 16, 16, TEX_W, TEX_H);

        // W
        ctx.drawTexture(TEX, x + 83, y + 7, 0, 148, 16, 16, TEX_W, TEX_H);

        // E
        ctx.drawTexture(TEX, x + 103, y + 7, 0, 164, 16, 16, TEX_W, TEX_H);

        // R
        ctx.drawTexture(TEX, x + 123, y + 7, 0, 179, 16, 16, TEX_W, TEX_H);

        // D
        ctx.drawTexture(TEX, x + 144, y + 7, 0, 107, 12, 12, TEX_W, TEX_H);

        // F
        ctx.drawTexture(TEX, x + 160, y + 7, 12, 107, 12, 12, TEX_W, TEX_H);
    }

}
