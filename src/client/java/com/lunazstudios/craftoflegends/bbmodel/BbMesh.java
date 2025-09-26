package com.lunazstudios.craftoflegends.bbmodel;

import com.google.gson.JsonObject;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public final class BbMesh {
    public static final class Quad {
        public final float[] pos;
        public final float[] uv;
        public final float[] nrm;
        public final Direction face;

        public Quad(float[] pos, float[] uv, float[] nrm, Direction face) {
            this.pos = pos;
            this.uv = uv;
            this.nrm = nrm;
            this.face = face;
        }
    }

    private final List<Quad> quads = new ArrayList<>();

    public List<Quad> quads() { return quads; }

    public void addCube(float[] from, float[] to, JsonObject faces,
                        int tw, int th,
                        Matrix4f model,
                        Matrix3f normalMat) {
        mapFace(from, to, faces, "north", Direction.NORTH, tw, th, model, normalMat);
        mapFace(from, to, faces, "south", Direction.SOUTH, tw, th, model, normalMat);
        mapFace(from, to, faces, "west",  Direction.WEST,  tw, th, model, normalMat);
        mapFace(from, to, faces, "east",  Direction.EAST,  tw, th, model, normalMat);
        mapFace(from, to, faces, "up",    Direction.UP,    tw, th, model, normalMat);
        mapFace(from, to, faces, "down",  Direction.DOWN,  tw, th, model, normalMat);
    }

    public void addCube(float[] from, float[] to, JsonObject faces, int tw, int th) {
        addCube(from, to, faces, tw, th, new org.joml.Matrix4f(), new org.joml.Matrix3f().identity());
    }

    private void mapFace(float[] from, float[] to, JsonObject faces, String bbKey, Direction dir,
                         int tw, int th, org.joml.Matrix4f model, org.joml.Matrix3f normalMat) {
        if (!faces.has(bbKey)) return;

        JsonObject f = faces.getAsJsonObject(bbKey);
        var uv = f.getAsJsonArray("uv");

        float u0 = uv.get(0).getAsFloat() / tw;
        float v0 = uv.get(1).getAsFloat() / th;
        float u1 = uv.get(2).getAsFloat() / tw;
        float v1 = uv.get(3).getAsFloat() / th;

        float x0 = from[0], y0 = from[1], z0 = from[2];
        float x1 = to[0],   y1 = to[1],   z1 = to[2];

        float[] pos, uvs;

        switch (dir) {
            case NORTH -> { // -Z
                pos = new float[]{ x1,y0,z0,  x0,y0,z0,  x0,y1,z0,  x1,y1,z0 };
                uvs = new float[]{ u0,v1,     u1,v1,     u1,v0,     u0,v0 };
            }
            case SOUTH -> { // +Z
                pos = new float[]{ x0,y0,z1,  x1,y0,z1,  x1,y1,z1,  x0,y1,z1 };
                uvs = new float[]{ u0,v1,     u1,v1,     u1,v0,     u0,v0 };
            }
            case WEST -> { // -X
                pos = new float[]{ x0,y0,z1, x0,y0,z0, x0,y1,z0, x0,y1,z1 };
                uvs = new float[]{ u1,v1, u0,v1, u0,v0, u1,v0 };
            }
            case EAST -> {  // +X
                pos = new float[]{ x1,y0,z0,  x1,y0,z1,  x1,y1,z1,  x1,y1,z0 };
                uvs = new float[]{ u1,v1,     u0,v1,     u0,v0,     u1,v0 };
            }
            case UP -> {    // +Y
                pos = new float[]{ x0,y1,z1,  x1,y1,z1,  x1,y1,z0,  x0,y1,z0 };
                uvs = new float[]{ u0,v1,     u1,v1,     u1,v0,     u0,v0 };
            }
            case DOWN -> {  // -Y
                pos = new float[]{ x0,y0,z0,  x1,y0,z0,  x1,y0,z1,  x0,y0,z1 };
                uvs = new float[]{ u0,v1,     u1,v1,     u1,v0,     u0,v0 };
            }
            default -> { return; }
        }

        float nx=0, ny=0, nz=0;
        switch (dir) {
            case NORTH -> nz = -1;
            case SOUTH -> nz =  1;
            case WEST  -> nx = -1;
            case EAST  -> nx =  1;
            case UP    -> ny =  1;
            case DOWN  -> ny = -1;
        }

        float[] outPos = new float[12];
        org.joml.Vector4f tmp = new org.joml.Vector4f();
        for (int i=0;i<4;i++) {
            tmp.set(pos[i*3], pos[i*3+1], pos[i*3+2], 1f).mul(model);
            outPos[i*3]   = tmp.x;
            outPos[i*3+1] = tmp.y;
            outPos[i*3+2] = tmp.z;
        }

        org.joml.Vector3f n = new org.joml.Vector3f(nx,ny,nz).mul(normalMat);
        float[] nrm = new float[]{ n.x, n.y, n.z, n.x, n.y, n.z, n.x, n.y, n.z, n.x, n.y, n.z };

        quads.add(new Quad(outPos, uvs, nrm, dir));
    }

    public void render(MatrixStack matrices, VertexConsumer vc, int light, int overlay) {
        var entry = matrices.peek();
        for (Quad q : quads) {
            for (int i = 0; i < 4; i++) {
                float x = q.pos[i*3];
                float y = q.pos[i*3+1];
                float z = q.pos[i*3+2];
                float u = q.uv[i*2];
                float v = q.uv[i*2+1];
                float nx = q.nrm[i*3];
                float ny = q.nrm[i*3+1];
                float nz = q.nrm[i*3+2];

                vc.vertex(entry, x, y, z)
                        .color(255, 255, 255, 255)
                        .texture(u, v)
                        .overlay(overlay)
                        .light(light)
                        .normal(entry, nx, ny, nz);
            }
        }
    }
}