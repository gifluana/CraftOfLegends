package com.lunazstudios.craftoflegends.networking;

import com.lunazstudios.craftoflegends.CraftOfLegends;
import com.lunazstudios.craftoflegends.control.ColControlState;
import com.lunazstudios.craftoflegends.packet.MoveTargetS2CPacket;
import com.lunazstudios.craftoflegends.packet.SyncPlayerStatsS2CPacket;
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

        ClientPlayNetworking.registerGlobalReceiver(SyncPlayerStatsS2CPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (MinecraftClient.getInstance().player != null) {
                    StatsCache.update(payload);
                }
            });
        });
    }

    public static final class StatsCache {
        public static int health, mana, level, xp, abilityPoints, gold, ad, ap, armor, mr, haste, ms;
        public static float attackSpeed, critChance;
        private static void update(SyncPlayerStatsS2CPacket p) {
            health = p.health(); mana = p.mana(); level = p.level(); xp = p.xp(); abilityPoints = p.abilityPoints(); gold = p.gold();
            ad = p.attackDamage(); ap = p.abilityPower(); armor = p.armor(); mr = p.magicResist(); haste = p.abilityHaste(); ms = p.moveSpeed();
            attackSpeed = p.attackSpeed(); critChance = p.critChance();
        }
    }
}
