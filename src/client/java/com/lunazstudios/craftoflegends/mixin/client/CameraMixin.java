package com.lunazstudios.craftoflegends.mixin.client;

import com.lunazstudios.craftoflegends.camera.TopDownCamera;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    public abstract void setRotation(float yaw, float pitch);
    @Shadow public abstract void setPos(double x, double y, double z);

    @Inject(method = "update", at = @At("TAIL"))
    private void col$forceTopDown(BlockView area, Entity focus, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!TopDownCamera.ENABLED || focus == null) return;

        float yaw = TopDownCamera.YAW;
        float pitch = TopDownCamera.PITCH;
        setRotation(yaw, pitch);

        Vec3d target = focus.getLerpedPos(tickDelta)
                .add(0.0, focus.getStandingEyeHeight(), 0.0);

        Vec3d forward = Vec3d.fromPolar(pitch, yaw);

        Vec3d camPos = target
                .subtract(forward.multiply(TopDownCamera.DISTANCE))
                .add(0.0, TopDownCamera.HEIGHT_OFFSET, 0.0);

        setPos(camPos.x, camPos.y, camPos.z);
    }
}