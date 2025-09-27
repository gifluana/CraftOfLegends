package com.lunazstudios.craftoflegends.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.joml.Vector3d;

public record MoveTargetC2SPacket(Vector3d pos) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of("craftoflegends", "move_target");
    public static final Id<MoveTargetC2SPacket> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, MoveTargetC2SPacket> CODEC =
            PacketCodec.ofStatic(
                    // writer
                    (buf, pkt) -> {
                        Vector3d v = pkt.pos();
                        buf.writeDouble(v.x);
                        buf.writeDouble(v.y);
                        buf.writeDouble(v.z);
                    },
                    // reader
                    buf -> new MoveTargetC2SPacket(new Vector3d(
                            buf.readDouble(), buf.readDouble(), buf.readDouble()
                    ))
            );

    public static void registerType() {
        PayloadTypeRegistry.playC2S().register(ID, CODEC);
    }

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}