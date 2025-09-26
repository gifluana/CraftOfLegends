package com.lunazstudios.craftoflegends.bbmodel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.util.Base64;

public final class BbTexture {
    private final Identifier id;
    private final int width;
    private final int height;

    private BbTexture(Identifier id, int w, int h) {
        this.id = id;
        this.width = w;
        this.height = h;
    }

    public static BbTexture fromBase64Png(String base64) throws Exception {
        if (base64 == null) {
            NativeImage img = new NativeImage(1,1,true);
            img.setColor(0,0,0xFFFFFFFF);
            return registerDynamic(img, "bbmodel_fallback");
        }
        byte[] png = Base64.getDecoder().decode(base64);
        NativeImage img = NativeImage.read(png);
        return registerDynamic(img, "bbmodel_dyn");
    }

    private static BbTexture registerDynamic(NativeImage img, String name) {
        var tex = new NativeImageBackedTexture(img);
        Identifier id = Identifier.of("craftoflegends", "dynamic/"+name+"_"+System.nanoTime());
        MinecraftClient.getInstance().getTextureManager().registerTexture(id, tex);
        return new BbTexture(id, img.getWidth(), img.getHeight());
    }

    public Identifier id() { return id; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Identifier identifier() {
        return id;
    }
}