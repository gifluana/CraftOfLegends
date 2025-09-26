package com.lunazstudios.craftoflegends.bbmodel;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class BbTransforms {
    public static void appendZYX(Matrix4f m, float rxDeg, float ryDeg, float rzDeg, float ox, float oy, float oz) {
        float rx = (float)Math.toRadians(rxDeg);
        float ry = (float)Math.toRadians(ryDeg);
        float rz = (float)Math.toRadians(rzDeg);

        m.translate(ox, oy, oz);
        if (rz != 0) m.rotateZ(rz);
        if (ry != 0) m.rotateY(ry);
        if (rx != 0) m.rotateX(rx);
        m.translate(-ox, -oy, -oz);
    }

    public static Matrix3f normalOf(Matrix4f model) {
        Matrix3f nm = new Matrix3f();
        model.normal(nm);
        return nm;
    }
}