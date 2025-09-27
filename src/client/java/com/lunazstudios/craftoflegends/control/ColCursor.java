package com.lunazstudios.craftoflegends.control;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public final class ColCursor {
    private static boolean hidden = false;

    private ColCursor() {}

    public static void update(boolean lolMode) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;

        boolean inGame = mc.currentScreen == null;
        boolean shouldHide = lolMode && inGame;

        long handle = mc.getWindow().getHandle();

        if (shouldHide && !hidden) {
            GLFW.glfwSetInputMode(handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
            hidden = true;
        } else if (!shouldHide && hidden) {
            GLFW.glfwSetInputMode(handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            hidden = false;
        }
    }
}