package com.lunazstudios.craftoflegends.control;


import net.minecraft.util.math.Vec3d;

public final class ColControlState {
    public static boolean isLolModeActive() {
        return LOL_MODE;
    }

    public static void setLolMode(boolean lolMode) {
        LOL_MODE = lolMode;
    }

    public static boolean LOL_MODE = true;

    public static Vec3d moveTarget = null;

    public static float moveSpeed = 0.11f;
    public static float stopRadius = 0.45f;
    public static float turnLerp = 0.45f;

    private ColControlState() {}
}