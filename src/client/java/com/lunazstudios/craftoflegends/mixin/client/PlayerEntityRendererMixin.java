package com.lunazstudios.craftoflegends.mixin.client;

import com.lunazstudios.craftoflegends.bbmodel.BbModelLoader;
import com.lunazstudios.craftoflegends.bbmodel.BbRigLoader;
import com.lunazstudios.craftoflegends.bbmodel.BbRuntimeRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin
        extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    @Shadow protected abstract void setupTransforms(AbstractClientPlayerEntity e, MatrixStack ms, float yaw, float tickDelta, float animationProgress, float headYaw);
    @Shadow protected abstract void scale(AbstractClientPlayerEntity e, MatrixStack ms, float tickDelta);

    @Unique
    private BbRuntimeRenderer col$runtime;

    protected PlayerEntityRendererMixin(EntityRendererFactory.Context ctx,
                                        PlayerEntityModel<AbstractClientPlayerEntity> model,
                                        float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void col$init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        try {
            var id  = BbModelLoader.getDefaultModelId();
            var rig = BbRigLoader.load(id);
            this.col$runtime = new BbRuntimeRenderer(rig);
            this.col$runtime.play("idle");
        } catch (Throwable t) {
            System.err.println("[CraftOfLegends] BbRig load failed: " + t);
            this.col$runtime = null;
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void col$render(AbstractClientPlayerEntity player, float yaw, float tickDelta,
                            MatrixStack matrices, VertexConsumerProvider vertices, int light, CallbackInfo ci) {
        if (this.col$runtime == null) return;

        matrices.push();

        float animProgress = player.age + tickDelta;

        float body = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        float head = MathHelper.lerpAngleDegrees(tickDelta, player.prevHeadYaw,  player.headYaw);

        float headRel = MathHelper.wrapDegrees(head - body);

        headRel = MathHelper.clamp(headRel, -85.0F, 85.0F);

        this.setupTransforms(player, matrices, body, tickDelta, animProgress, headRel);
        this.scale(player, matrices, tickDelta);

        this.col$runtime.setTime((player.age + tickDelta) / 20f);
        var layer = RenderLayer.getEntityCutoutNoCull(this.col$runtime.textureId());
        var vc = vertices.getBuffer(layer);
        this.col$runtime.render(matrices, vc, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
        ci.cancel();
    }
}