package com.lunazstudios.craftoflegends.bbmodel;


import com.google.gson.JsonObject;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class BbRuntimeRenderer {

    private final BbRigLoader.Rig rig;
    private final BbAnimator animator = new BbAnimator();

    public BbRuntimeRenderer(BbRigLoader.Rig rig) {
        this.rig = rig;
    }

    public void play(String name) { animator.play(rig.clips, name); }
    public void setTime(float seconds) { animator.setTime(seconds); }
    public Identifier textureId() {
        return rig.texture.identifier();
    }
    public void render(MatrixStack ms, VertexConsumer vc, int light, int overlay) {
        final float PX = 1f / 16f;
        Matrix4f rootM = new Matrix4f().scale(PX);
        drawNode(rig.root, rootM, ms, vc, light, overlay);
    }

    private void drawNode(BbTypes.Node n, Matrix4f parent, MatrixStack ms, VertexConsumer vc, int light, int overlay) {
        float[] rot = animator.rotFor(n.uuid, n.baseRotDeg);
        float[] pos = animator.posFor(n.uuid);
        float[] scl = animator.sclFor(n.uuid);

        Matrix4f m = new Matrix4f(parent);
        if (!(pos[0]==0 && pos[1]==0 && pos[2]==0)) {
            m.translate(pos[0], pos[1], pos[2]);
        }
        m.translate(n.origin[0], n.origin[1], n.origin[2]);
        if (rot[2]!=0) m.rotateZ((float)Math.toRadians(rot[2]));
        if (rot[1]!=0) m.rotateY((float)Math.toRadians(rot[1]));
        if (rot[0]!=0) m.rotateX((float)Math.toRadians(rot[0]));
        if (!(scl[0]==1 && scl[1]==1 && scl[2]==1)) {
            m.scale(scl[0], scl[1], scl[2]);
        }
        m.translate(-n.origin[0], -n.origin[1], -n.origin[2]);

        Matrix3f nm = normalOf(m);

        for (BbTypes.Cube c : n.cubes) {
            emitCube(c, m, nm, rig.texture.getWidth(), rig.texture.getHeight(), vc, ms, light, overlay);
        }
        for (BbTypes.Node ch : n.children) drawNode(ch, m, ms, vc, light, overlay);
    }

    private static void appendZYX(Matrix4f m, float rxDeg, float ryDeg, float rzDeg, float ox, float oy, float oz) {
        float rx = (float)Math.toRadians(rxDeg);
        float ry = (float)Math.toRadians(ryDeg);
        float rz = (float)Math.toRadians(rzDeg);
        m.translate(ox, oy, oz);
        if (rz != 0) m.rotateZ(rz);
        if (ry != 0) m.rotateY(ry);
        if (rx != 0) m.rotateX(rx);
        m.translate(-ox, -oy, -oz);
    }

    private static Matrix3f normalOf(Matrix4f model) {
        Matrix3f nm = new Matrix3f();
        model.normal(nm);
        return nm;
    }

    private static void emitCube(BbTypes.Cube cube, Matrix4f model, Matrix3f normalMat, int tw, int th,
                                 VertexConsumer vc, MatrixStack ms, int light, int overlay) {
        float[] from = cube.from.clone();
        float[] to   = cube.to.clone();
        if (cube.inflate != 0f) {
            float f = cube.inflate;
            from[0]-=f; from[1]-=f; from[2]-=f;
            to[0]+=f;   to[1]+=f;   to[2]+=f;
        }
        emitFace(from, to, cube.faces, "north", Direction.NORTH, model, normalMat, tw, th, vc, ms, light, overlay);
        emitFace(from, to, cube.faces, "south", Direction.SOUTH, model, normalMat, tw, th, vc, ms, light, overlay);
        emitFace(from, to, cube.faces, "west",  Direction.WEST,  model, normalMat, tw, th, vc, ms, light, overlay);
        emitFace(from, to, cube.faces, "east",  Direction.EAST,  model, normalMat, tw, th, vc, ms, light, overlay);
        emitFace(from, to, cube.faces, "up",    Direction.UP,    model, normalMat, tw, th, vc, ms, light, overlay);
        emitFace(from, to, cube.faces, "down",  Direction.DOWN,  model, normalMat, tw, th, vc, ms, light, overlay);
    }

    private static void emitFace(float[] from, float[] to, JsonObject faces, String key, Direction dir,
                                 Matrix4f model, Matrix3f normalMat, int tw, int th,
                                 VertexConsumer vc, MatrixStack ms, int light, int overlay) {
        if (!faces.has(key)) return;
        JsonObject f = faces.getAsJsonObject(key);
        var uv = f.getAsJsonArray("uv");

        float u0 = uv.get(0).getAsFloat() / tw;
        float v0 = uv.get(1).getAsFloat() / th;
        float u1 = uv.get(2).getAsFloat() / tw;
        float v1 = uv.get(3).getAsFloat() / th;

        float x0 = from[0], y0 = from[1], z0 = from[2];
        float x1 = to[0],   y1 = to[1],   z1 = to[2];

        float[] pos, uvs;
        switch (dir) {
            case NORTH -> { pos=new float[]{ x1,y0,z0, x0,y0,z0, x0,y1,z0, x1,y1,z0 }; uvs=new float[]{ u0,v1, u1,v1, u1,v0, u0,v0 }; }
            case SOUTH -> { pos=new float[]{ x0,y0,z1, x1,y0,z1, x1,y1,z1, x0,y1,z1 }; uvs=new float[]{ u0,v1, u1,v1, u1,v0, u0,v0 }; }
            case WEST  -> { pos=new float[]{ x0,y0,z1, x0,y0,z0, x0,y1,z0, x0,y1,z1 }; uvs=new float[]{ u1,v1, u0,v1, u0,v0, u1,v0 }; }
            case EAST  -> { pos=new float[]{ x1,y0,z0, x1,y0,z1, x1,y1,z1, x1,y1,z0 }; uvs=new float[]{ u1,v1, u0,v1, u0,v0, u1,v0 }; }
            case UP    -> { pos=new float[]{ x0,y1,z1, x1,y1,z1, x1,y1,z0, x0,y1,z0 }; uvs=new float[]{ u0,v1, u1,v1, u1,v0, u0,v0 }; }
            case DOWN  -> { pos=new float[]{ x0,y0,z0, x1,y0,z0, x1,y0,z1, x0,y0,z1 }; uvs=new float[]{ u0,v1, u1,v1, u1,v0, u0,v0 }; }
            default -> { return; }
        }

        float nx=0,ny=0,nz=0;
        switch (dir) {
            case NORTH -> nz=-1; case SOUTH -> nz=1;
            case WEST  -> nx=-1; case EAST  -> nx=1;
            case UP    -> ny=1;  case DOWN  -> ny=-1;
        }
        Vector3f nn = new Vector3f(nx,ny,nz).mul(normalMat);
        Vector4f tmp = new Vector4f();
        var entry = ms.peek();

        for (int i=0;i<4;i++) {
            tmp.set(pos[i*3], pos[i*3+1], pos[i*3+2], 1f).mul(model);
            float u = uvs[i*2], v = uvs[i*2+1];
            vc.vertex(entry, tmp.x, tmp.y, tmp.z)
                    .color(255,255,255,255)
                    .texture(u,v)
                    .overlay(overlay)
                    .light(light)
                    .normal(entry, nn.x, nn.y, nn.z);
        }
    }
}