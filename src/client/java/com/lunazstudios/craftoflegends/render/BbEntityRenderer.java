//package com.lunazstudios.craftoflegends.render;
//
//import com.lunazstudios.craftoflegends.bbmodel.BbModelLoader;
//import net.minecraft.client.render.entity.EntityRendererFactory;
//import net.minecraft.client.render.entity.LivingEntityRenderer;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.util.Identifier;
//
//public class BbEntityRenderer<T extends LivingEntity> extends LivingEntityRenderer<T, BbEntityModel<T>> {
//    private Identifier lastTex;
//
//    public BbEntityRenderer(EntityRendererFactory.Context ctx) {
//        super(ctx, new BbEntityModel<>(BbModelLoader.loadDefault()), 0.0f);
//    }
//
//    @Override
//    public Identifier getTexture(T entity) {
//        // A textura já está “embutida” no modelo. Aqui devolvemos qualquer id consistente.
//        if (lastTex == null) lastTex = BbModelLoader.loadDefault().texture().id();
//        return lastTex;
//    }
//}