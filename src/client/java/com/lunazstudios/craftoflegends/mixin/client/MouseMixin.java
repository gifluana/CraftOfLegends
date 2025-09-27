package com.lunazstudios.craftoflegends.mixin.client;

import com.lunazstudios.craftoflegends.CraftOfLegends;
import com.lunazstudios.craftoflegends.control.ColControlState;
import com.lunazstudios.craftoflegends.control.PickUtil;
import com.lunazstudios.craftoflegends.packet.MoveTargetC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicLong;

@Mixin(Mouse.class)
public class MouseMixin {

    private static final AtomicLong CLICK_SEQ = new AtomicLong(0);
    private static long lastLogNanos = 0L;
    private static final long LOG_COOLDOWN_NS = 8_0_0_0_0_0L; // ~80ms

    private static boolean allowLogNow() {
        long now = System.nanoTime();
        if (now - lastLogNanos > LOG_COOLDOWN_NS) {
            lastLogNanos = now;
            return true;
        }
        return false;
    }

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void col$keepCursorUnlocked(CallbackInfo ci) {
        CraftOfLegends.LOGGER.debug("[COL][Mouse] lockCursor() called (LOL_MODE={})", ColControlState.LOL_MODE);
        if (!ColControlState.LOL_MODE) return;

        var mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.world != null && mc.currentScreen == null) {
            CraftOfLegends.LOGGER.info("[COL][Mouse] lockCursor() CANCEL (LoL mode, in-game, no screen)");
            ci.cancel();
        }
    }

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void col$clickToMove_named(long window, int button, int action, int mods, CallbackInfo ci) {
        rawOnMouseLog(window, button, action, mods, ci, "named");
    }

    @Inject(method = "onMouseButton(JIII)V", at = @At("HEAD"), cancellable = true)
    private void col$clickToMove_desc(long window, int button, int action, int mods, CallbackInfo ci) {
        rawOnMouseLog(window, button, action, mods, ci, "desc");
    }

    private void rawOnMouseLog(long window, int button, int action, int mods, CallbackInfo ci, String tag) {
        final var mc = MinecraftClient.getInstance();

        if (allowLogNow()) {
            CraftOfLegends.LOGGER.debug("[COL][Mouse:{}] onMouseButton(btn={}, act={}, mods={}, LOL_MODE={}, screen={})",
                    tag, button, action, mods, ColControlState.LOL_MODE,
                    mc != null && mc.currentScreen != null ? mc.currentScreen.getClass().getSimpleName() : "null");
        }

        if (mc == null || mc.player == null || mc.world == null) return;
        if (mc.currentScreen != null) return;
        if (!ColControlState.LOL_MODE) return;

        if (button == 1 && action == 1) {
            long id = CLICK_SEQ.incrementAndGet();

            double mx = mc.mouse.getX();
            double my = mc.mouse.getY();
            int sw = mc.getWindow().getScaledWidth();
            int sh = mc.getWindow().getScaledHeight();

            var win = mc.getWindow();
            CraftOfLegends.LOGGER.info(
                    "[COL][Click:{}] RMB at screen=({}, {}) window=({}x{}) framebuffer=({}x{}) yawPitch=({},{})",
                    id, mx, my, win.getWidth(), win.getHeight(), win.getFramebufferWidth(), win.getFramebufferHeight(),
                    mc.player.getYaw(), mc.player.getPitch()
            );
            double ndcX = (mx / sw) * 2.0 - 1.0;
            double ndcY = -((my / sh) * 2.0 - 1.0);

            var hit = PickUtil.raycastFromScreen(mx, my, 256.0);

            CraftOfLegends.LOGGER.info("[COL][Click:{}] Hit={}", id, hit.getType());

            if (hit.getType() == HitResult.Type.BLOCK) {
                var bhr = (BlockHitResult) hit;
                var center = bhr.getBlockPos().toCenterPos();
                var tgt = new Vec3d(center.x, mc.player.getY(), center.z);

                ColControlState.moveTarget = tgt;

                CraftOfLegends.LOGGER.info("[COL][Click:{}] Target set: {} (block={} face={})",
                        id, vec3dStr(tgt), bhr.getBlockPos(), bhr.getSide());

                ClientPlayNetworking.send(new MoveTargetC2SPacket(new Vector3d(tgt.x, tgt.y, tgt.z)));

                CraftOfLegends.LOGGER.info("[COL][Click:{}] C2S MoveTarget sent", id);
                ci.cancel();
            } else {
                CraftOfLegends.LOGGER.info("[COL][Click:{}] Miss (no block hit) ndc=({},{})", id,
                        String.format("%.3f", ndcX), String.format("%.3f", ndcY));
            }
        }
    }

    private static String vec3dStr(Vec3d v) {
        return String.format("(%.3f, %.3f, %.3f)", v.x, v.y, v.z);
    }
}
