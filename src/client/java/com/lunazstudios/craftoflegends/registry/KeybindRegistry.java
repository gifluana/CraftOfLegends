package com.lunazstudios.craftoflegends.registry;

import com.lunazstudios.craftoflegends.CraftOfLegends;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindRegistry {
    private static KeyBinding qAbility;
    private static KeyBinding wAbility;
    private static KeyBinding eAbility;
    private static KeyBinding rAbility;
    private static KeyBinding dSpell;
    private static KeyBinding fSpell;
    private static KeyBinding goBase;
    private static KeyBinding openShop;
    private static KeyBinding item1;
    private static KeyBinding item2;
    private static KeyBinding item3;
    private static KeyBinding trinket;
    private static KeyBinding item5;
    private static KeyBinding item6;
    private static KeyBinding item7;
    private static KeyBinding toggleDetachCamera;
    private static KeyBinding centralizeCameraOnPlayer;

    public static void registerKeybinds() {
        qAbility = register("qAbility", GLFW.GLFW_KEY_Q);
        wAbility = register("wAbility", GLFW.GLFW_KEY_W);
        eAbility = register("eAbility", GLFW.GLFW_KEY_E);
        rAbility = register("rAbility", GLFW.GLFW_KEY_R);

        dSpell = register("dSpell", GLFW.GLFW_KEY_D);
        fSpell = register("fSpell", GLFW.GLFW_KEY_F);

        goBase = register("goBase", GLFW.GLFW_KEY_B);
        openShop = register("openShop", GLFW.GLFW_KEY_P);

        item1 = register("item1", GLFW.GLFW_KEY_1);
        item2 = register("item2", GLFW.GLFW_KEY_2);
        item3 = register("item3", GLFW.GLFW_KEY_3);
        trinket = register("trinket", GLFW.GLFW_KEY_4);
        item5 = register("item5", GLFW.GLFW_KEY_5);
        item6 = register("item6", GLFW.GLFW_KEY_6);
        item7 = register("item7", GLFW.GLFW_KEY_7);

        toggleDetachCamera = register("toggleDetachCamera", GLFW.GLFW_KEY_Y);
        centralizeCameraOnPlayer = register("centralizeCameraOnPlayer", GLFW.GLFW_KEY_SPACE);
    }

    public static void registerKeybindHandlers() {

    }

    private static KeyBinding register(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + CraftOfLegends.MOD_ID + "." + name,
                InputUtil.Type.KEYSYM,
                key,
                "category." + CraftOfLegends.MOD_ID
        ));
    }
}
