package com.lunazstudios.craftoflegends.packet;

import com.lunazstudios.craftoflegends.CraftOfLegends;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncPlayerStatsS2CPacket(
        int health, int mana, int level, int xp, int abilityPoints, int gold,
        int attackDamage, int abilityPower, int armor, int magicResist, int abilityHaste, int moveSpeed,
        float attackSpeed, float critChance
) implements CustomPayload {

    public static final Identifier PACKET_ID = Identifier.of(CraftOfLegends.MOD_ID, "sync_player_stats");
    public static final CustomPayload.Id<SyncPlayerStatsS2CPacket> ID = new CustomPayload.Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, SyncPlayerStatsS2CPacket> CODEC =
            PacketCodec.ofStatic(
                    (buf, pkt) -> {
                        buf.writeVarInt(pkt.health);
                        buf.writeVarInt(pkt.mana);
                        buf.writeVarInt(pkt.level);
                        buf.writeVarInt(pkt.xp);
                        buf.writeVarInt(pkt.abilityPoints);
                        buf.writeVarInt(pkt.gold);
                        buf.writeVarInt(pkt.attackDamage);
                        buf.writeVarInt(pkt.abilityPower);
                        buf.writeVarInt(pkt.armor);
                        buf.writeVarInt(pkt.magicResist);
                        buf.writeVarInt(pkt.abilityHaste);
                        buf.writeVarInt(pkt.moveSpeed);
                        buf.writeFloat(pkt.attackSpeed);
                        buf.writeFloat(pkt.critChance);
                    },
                    buf -> new SyncPlayerStatsS2CPacket(
                            buf.readVarInt(), // health
                            buf.readVarInt(), // mana
                            buf.readVarInt(), // level
                            buf.readVarInt(), // xp
                            buf.readVarInt(), // abilityPoints
                            buf.readVarInt(), // gold
                            buf.readVarInt(), // AD
                            buf.readVarInt(), // AP
                            buf.readVarInt(), // armor
                            buf.readVarInt(), // MR
                            buf.readVarInt(), // haste
                            buf.readVarInt(), // moveSpeed
                            buf.readFloat(),  // attackSpeed
                            buf.readFloat()   // critChance (percent)
                    )
            );

    public static void registerType() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}