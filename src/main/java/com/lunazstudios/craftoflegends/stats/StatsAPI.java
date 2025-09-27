package com.lunazstudios.craftoflegends.stats;

import net.minecraft.server.network.ServerPlayerEntity;

public final class StatsAPI {
    private StatsAPI() {}

    public static PlayerStats get(ServerPlayerEntity player) {
        return ((PlayerStatsHolder) player).col$getStats();
    }

    public static void sync(ServerPlayerEntity player) {
        ((PlayerStatsHolder) player).col$syncStats();
    }

    public static void addGold(ServerPlayerEntity player, int amount) {
        PlayerStats s = get(player);
        s.setGold(Math.max(0, s.getGold() + amount));
        sync(player);
    }

    public static void addXp(ServerPlayerEntity player, int amount) {
        PlayerStats s = get(player);
        s.setXp(Math.max(0, s.getXp() + amount));
        tryLevelUp(player, s);
        sync(player);
    }

    public static int xpForLevel(int level) {
        return Math.max(100, 50 * (level - 1) + 100);
    }

    private static void tryLevelUp(ServerPlayerEntity player, PlayerStats s) {
        boolean leveled = false;
        while (s.getXp() >= xpForLevel(s.getLevel())) {
            s.setXp(s.getXp() - xpForLevel(s.getLevel()));
            s.setLevel(s.getLevel() + 1);
            s.setAbilityPoints(s.getAbilityPoints() + 1);
            leveled = true;
        }
        if (leveled) sync(player);
    }
}