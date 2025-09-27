package com.lunazstudios.craftoflegends.mixin.client;

import com.lunazstudios.craftoflegends.control.ColControlState;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void col$overrideInputs(boolean slowDown, float f, CallbackInfo ci) {
        if (!ColControlState.LOL_MODE) return;
        KeyboardInput self = (KeyboardInput) (Object) this;
        self.pressingForward = false;
        self.pressingBack = false;
        self.pressingLeft = false;
        self.pressingRight = false;
        self.movementForward = 0f;
        self.movementSideways = 0f;
        self.jumping = false;
        self.sneaking = false;
    }
}