package com.lunazstudios.craftoflegends.registry;

import com.lunazstudios.craftoflegends.packet.MoveTargetC2SPacket;
import com.lunazstudios.craftoflegends.packet.MoveTargetS2CPacket;
import com.lunazstudios.craftoflegends.packet.SyncPlayerStatsS2CPacket;

public class PacketRegistry {
    public static void init() {
        MoveTargetC2SPacket.registerType();
        MoveTargetS2CPacket.registerType();
        SyncPlayerStatsS2CPacket.registerType();
    }
}
