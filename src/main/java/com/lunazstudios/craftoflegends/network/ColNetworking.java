package com.lunazstudios.craftoflegends.network;

import com.lunazstudios.craftoflegends.CraftOfLegends;
import com.lunazstudios.craftoflegends.control.ServerColState;
import com.lunazstudios.craftoflegends.packet.MoveTargetC2SPacket;
import com.lunazstudios.craftoflegends.packet.MoveTargetS2CPacket;
import com.lunazstudios.craftoflegends.packet.SyncPlayerStatsS2CPacket;
import com.lunazstudios.craftoflegends.stats.PlayerStats;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.joml.Vector3d;

public final class ColNetworking {
    private ColNetworking() {}

    public static void init() {
        CraftOfLegends.LOGGER.info("[COL][Net] Registering payload types & receivers...");
        ServerPlayNetworking.registerGlobalReceiver(
                MoveTargetC2SPacket.ID,
                (payload, ctx) -> {
                    ServerPlayerEntity player = ctx.player();
                    ServerWorld world = player.getServerWorld();

                    ctx.server().execute(() -> {

                        var v = payload.pos();
                        BlockPos base = BlockPos.ofFloored(v.x, v.y, v.z);
                        CraftOfLegends.LOGGER.info("[COL][Net][Server] Received MoveTargetC2S from {}: raw=({}, {}, {}) base={}",
                                player.getName().getString(),
                                String.format("%.2f", v.x), String.format("%.2f", v.y), String.format("%.2f", v.z),
                                base);

                        if (player.squaredDistanceTo(v.x, player.getY(), v.z) > 128 * 128) {
                            CraftOfLegends.LOGGER.info("[COL][Net][Server] Ignored target: too far.");
                            return;
                        }

                        int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, base.getX(), base.getZ());
                        if (topY <= world.getBottomY()) {
                            CraftOfLegends.LOGGER.info("[COL][Net][Server] Ignored target: no safe ground.");
                            return;
                        }

                        Vec3d safe = new Vec3d(base.getX() + 0.5, player.getY(), base.getZ() + 0.5);

                        ServerColState.setMoveTarget(player, safe);
                        CraftOfLegends.LOGGER.info("[COL][Net][Server] Set moveTarget={} for {}", safe, player.getName().getString());

                        ServerPlayNetworking.send(player, new MoveTargetS2CPacket(new Vector3d(safe.x, safe.y, safe.z)));
                        CraftOfLegends.LOGGER.info("[COL][Net][Server] Sent MoveTargetS2C to {}: {}", player.getName().getString(), safe);
                    });
                }
        );
    }

    public static void sendStats(ServerPlayerEntity player, PlayerStats s) {
        ServerPlayNetworking.send(player, new SyncPlayerStatsS2CPacket(
                s.getHealth(), s.getMana(), s.getLevel(), s.getXp(), s.getAbilityPoints(), s.getGold(),
                s.getAttackDamage(), s.getAbilityPower(), s.getArmor(), s.getMagicResist(), s.getAbilityHaste(), s.getMoveSpeed(),
                s.getAttackSpeed(), s.getCritChance()
        ));
    }
}
