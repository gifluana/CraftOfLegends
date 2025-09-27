package com.lunazstudios.craftoflegends.mixin.client;

import com.lunazstudios.craftoflegends.control.ColControlState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void col$clickToMove_Tick(CallbackInfo ci) {
        if (!ColControlState.LOL_MODE) return;
        ClientPlayerEntity p = (ClientPlayerEntity) (Object) this;

        p.setPitch(0f);

        if (ColControlState.moveTarget == null) {
            if (p.input != null) {
                p.input.movementForward  = 0f;
                p.input.movementSideways = 0f;
                p.input.jumping = false;
                p.input.sneaking = false;
            }
            return;
        }

        var pos = p.getPos();
        var tgt = ColControlState.moveTarget;

        var dir = new Vec3d(tgt.x - pos.x, 0, tgt.z - pos.z);
        double dist = dir.length();
        if (dist <= ColControlState.stopRadius) {
            ColControlState.moveTarget = null;
            if (p.input != null) {
                p.input.movementForward = 0f;
                p.input.movementSideways = 0f;
            }
            return;
        }
        dir = dir.normalize();

        float desiredYaw = (float) (MathHelper.atan2(dir.x, dir.z) * (180.0/Math.PI));
        float current = p.getYaw();
        float delta = MathHelper.wrapDegrees(desiredYaw - current);
        p.setYaw(current + delta * ColControlState.turnLerp);
        p.headYaw = p.getYaw();

        if (p.input != null) {
            float yawRad = (float) Math.toRadians(p.getYaw());
            float sin = MathHelper.sin(yawRad);
            float cos = MathHelper.cos(yawRad);

            float forward  = (float) ( dir.z * cos + dir.x * sin);
            float sideways = (float) (-dir.z * sin + dir.x * cos);

            float len = (float) Math.hypot(forward, sideways);
            if (len > 0.0001f) {
                forward  /= len;
                sideways /= len;
            }

            p.input.movementForward  = MathHelper.clamp(forward,  -1f, 1f) * 0.8f;
            p.input.movementSideways = MathHelper.clamp(sideways, -1f, 1f) * 0.8f;
            p.input.jumping = false;
            p.input.sneaking = false;
        }
    }
}