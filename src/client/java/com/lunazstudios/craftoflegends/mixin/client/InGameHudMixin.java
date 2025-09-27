package com.lunazstudios.craftoflegends.mixin.client;

import com.lunazstudios.craftoflegends.control.ColControlState;
import com.lunazstudios.craftoflegends.control.ColCursor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private static final Identifier CURSOR = Identifier.of("craftoflegends", "textures/gui/cursor.png");
    private static boolean col$off() { return ColControlState.isLolModeActive(); }


    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void col$hideHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (col$off()) ci.cancel();
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void col$hideStatusBars(DrawContext ctx, CallbackInfo ci) {
        if (col$off()) ci.cancel();
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void col$hideXp(DrawContext ctx, int x, CallbackInfo ci) {
        if (col$off()) ci.cancel();
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void col$hideHeldItemTooltip(DrawContext ctx, CallbackInfo ci) {
        if (col$off()) ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void col$hideCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (col$off()) ci.cancel();
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void col$hideVignette(DrawContext context, Entity entity, CallbackInfo ci) {
        if (col$off()) ci.cancel();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void col$renderCursor(DrawContext ctx, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ColControlState.LOL_MODE) return;
        ColCursor.update(ColControlState.LOL_MODE);
        MinecraftClient mc = MinecraftClient.getInstance();
        int x = (int) (mc.mouse.getX() * (double) mc.getWindow().getScaledWidth()  / mc.getWindow().getFramebufferWidth());
        int y = (int) (mc.mouse.getY() * (double) mc.getWindow().getScaledHeight() / mc.getWindow().getFramebufferHeight());

        MatrixStack matrices = ctx.getMatrices();
        matrices.push();
        ctx.drawTexture(CURSOR, x - 8, y - 8, 0, 0, 16, 16, 16, 16);

        matrices.pop();
    }
}