package com.lunazstudios.craftoflegends.networking;

import com.lunazstudios.craftoflegends.CraftOfLegends;
import com.lunazstudios.craftoflegends.control.ColControlState;
import com.lunazstudios.craftoflegends.packet.MoveTargetS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public final class ColClientNetworking {
    private ColClientNetworking() {}

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(MoveTargetS2CPacket.ID, (payload, ctx) -> {
            var mc = MinecraftClient.getInstance();
            var v = payload.pos();
            mc.execute(() -> {
                Vec3d tgt = new Vec3d(v.x, v.y, v.z);
                ColControlState.moveTarget = tgt;
                CraftOfLegends.LOGGER.info("[COL][Net][Client] Received MoveTargetS2C: set moveTarget={}", tgt);
            });
        });
    }
}
