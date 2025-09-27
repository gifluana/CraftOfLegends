package com.lunazstudios.craftoflegends.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.joml.Vector3d;

public record MoveTargetS2CPacket(Vector3d pos) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of("craftoflegends", "move_target_ack");
    public static final Id<MoveTargetS2CPacket> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, MoveTargetS2CPacket> CODEC =
            PacketCodec.ofStatic(
                    (buf, pkt) -> {
                        Vector3d v = pkt.pos();
                        buf.writeDouble(v.x);
                        buf.writeDouble(v.y);
                        buf.writeDouble(v.z);
                    },
                    buf -> new MoveTargetS2CPacket(new Vector3d(
                            buf.readDouble(), buf.readDouble(), buf.readDouble()
                    ))
            );

    public static void registerType() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}