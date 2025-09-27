package com.lunazstudios.craftoflegends.mixin;

import com.lunazstudios.craftoflegends.network.ColNetworking;
import com.lunazstudios.craftoflegends.stats.PlayerStats;
import com.lunazstudios.craftoflegends.stats.PlayerStatsHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerStatsHolder {
    @Unique
    private final PlayerStats col$stats = new PlayerStats();

    @Override
    public PlayerStats col$getStats() {
        return col$stats;
    }

    @Override
    public void col$syncStats() {
        if (((Object)this) instanceof ServerPlayerEntity serverPlayer) {
            ColNetworking.sendStats(serverPlayer, ((PlayerStatsHolder) serverPlayer).col$getStats());
        }
    }

    // Save
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void col$writeStats(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("col_stats", col$stats.toNbt());
    }

    // Load
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void col$readStats(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("col_stats")) {
            col$stats.fromNbt(nbt.getCompound("col_stats"));
        }
    }
}