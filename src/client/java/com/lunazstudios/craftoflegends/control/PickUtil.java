package com.lunazstudios.craftoflegends.control;

import com.lunazstudios.craftoflegends.CraftOfLegends;
import com.lunazstudios.craftoflegends.camera.TopDownCamera;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.Window;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class PickUtil {
    private PickUtil() {}

    public static HitResult raycastFromScreen(double mouseX, double mouseY, double maxDistance) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.world == null) {
            return BlockHitResult.createMissed(Vec3d.ZERO, Direction.UP, BlockPos.ORIGIN);
        }

        var win = mc.getWindow();
        // Use o TAMANHO DA JANELA (mesmo espaço do mouse), não o scaled GUI
        int ww = win.getWidth();
        int wh = win.getHeight();

        // Screen -> NDC ([-1,1]) com origem no centro. Y da janela cresce pra baixo.
        float ndcX = (float) ((mouseX / ww) * 2.0 - 1.0);
        float ndcY = (float) (1.0 - (mouseY / wh) * 2.0); // equivalente a -((y/wh)*2-1)

        float fovDeg = mc.options.getFov().getValue();
        float aspect = (float) ww / (float) wh;
        float tan = (float) Math.tan(Math.toRadians(fovDeg) * 0.5f);

        // Ray em espaço de câmera (vertical FOV)
        Vector3f dirView = new Vector3f(ndcX * aspect * tan, ndcY * tan, -1f).normalize();

        Camera cam = mc.gameRenderer.getCamera();
        Vector3f dirWorld3f = new Quaternionf(cam.getRotation()).transform(dirView).normalize();

        Vec3d origin = cam.getPos();
        Vec3d end = origin.add(dirWorld3f.x * maxDistance, dirWorld3f.y * maxDistance, dirWorld3f.z * maxDistance);

        return mc.world.raycast(new RaycastContext(
                origin, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player
        ));
    }

}
