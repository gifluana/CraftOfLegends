package com.lunazstudios.craftoflegends.render;


import com.lunazstudios.craftoflegends.bbmodel.BbModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class BbEntityModel<T extends LivingEntity> extends Model {
    private BbModel bb;

    public BbEntityModel(BbModel bb) {
        super(RenderLayer::getEntityTranslucent);
        this.bb = bb;
    }

    public Identifier textureId() {
        return bb != null ? bb.texture().id() : MissingSprite.getMissingSpriteId();
    }

    public void setBbModel(BbModel bb) {
        this.bb = bb;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vc, int light, int overlay, int color) {
        if (bb == null) return;

        bb.mesh().render(matrices, vc, light, OverlayTexture.DEFAULT_UV);
    }
}