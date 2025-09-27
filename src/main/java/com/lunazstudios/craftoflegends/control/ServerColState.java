package com.lunazstudios.craftoflegends.control;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerColState {
    private static final Map<UUID, Vec3d> MOVE_TARGETS = new ConcurrentHashMap<>();
    private static final double SPEED_PER_TICK = 0.15;
    private static final double ARRIVE_RADIUS = 0.5;

    private ServerColState() {}

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(ServerColState::tickAll);
    }

    public static void setMoveTarget(ServerPlayerEntity player, Vec3d target) {
        MOVE_TARGETS.put(player.getUuid(), target);
    }

    public static Vec3d getMoveTarget(ServerPlayerEntity player) {
        return MOVE_TARGETS.get(player.getUuid());
    }

    public static void clearMoveTarget(ServerPlayerEntity player) {
        MOVE_TARGETS.remove(player.getUuid());
    }

    private static void tickAll(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Vec3d target = MOVE_TARGETS.get(player.getUuid());
            if (target == null) continue;

            Vec3d pos = player.getPos();
            double dx = target.x - pos.x;
            double dz = target.z - pos.z;
            double distSq = dx * dx + dz * dz;

            if (distSq <= ARRIVE_RADIUS * ARRIVE_RADIUS) {
                MOVE_TARGETS.remove(player.getUuid());
                player.setVelocity(Vec3d.ZERO);
                player.velocityModified = true;
                continue;
            }

            double dist = Math.sqrt(distSq);
            double step = Math.min(SPEED_PER_TICK, dist);
            double vx = (dx / dist) * step;
            double vz = (dz / dist) * step;

            player.setVelocity(vx, 0.0, vz);
            player.velocityModified = true;
        }
    }
}