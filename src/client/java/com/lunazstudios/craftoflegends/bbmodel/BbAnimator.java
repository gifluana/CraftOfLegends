package com.lunazstudios.craftoflegends.bbmodel;

import java.util.List;

public final class BbAnimator {
    private BbTypes.Clip current;
    private float t;

    public void play(List<BbTypes.Clip> clips, String name) {
        for (BbTypes.Clip c : clips) if (c.name.equals(name)) { current = c; t = 0f; return; }
    }

    public void update(float dt) {
        if (current == null) return;
        t += dt;
        if (t > current.length) {
            if (current.loop) t = t % current.length;
            else t = current.length;
        }
    }

    public void setTime(float seconds) {
        if (current == null) return;
        if (current.loop) {
            float L = current.length;
            t = (seconds % L + L) % L;
        } else {
            t = Math.min(seconds, current.length);
        }
    }

    public float[] rotFor(String uuid, float[] baseRotDeg) {
        if (current == null) return baseRotDeg;
        BbTypes.Track tr = current.tracks.get(uuid);
        if (tr == null || tr.rot.isEmpty()) return baseRotDeg;
        float[] add = sampleVec(tr.rot, t);
        return new float[]{ baseRotDeg[0] + add[0], baseRotDeg[1] + add[1], baseRotDeg[2] + add[2] };
    }

    public float[] posFor(String uuid) {
        if (current == null) return ZERO3;
        BbTypes.Track tr = current.tracks.get(uuid);
        if (tr == null || tr.pos.isEmpty()) return ZERO3;
        return sampleVec(tr.pos, t);
    }

    public float[] sclFor(String uuid) {
        if (current == null) return ONE3;
        BbTypes.Track tr = current.tracks.get(uuid);
        if (tr == null || tr.scl.isEmpty()) return ONE3;
        return sampleVec(tr.scl, t);
    }

    private static final float[] ZERO3 = new float[]{0,0,0};
    private static final float[] ONE3  = new float[]{1,1,1};

    private static float[] sampleVec(List<BbTypes.Key> ks, float time) {
        if (time <= ks.get(0).time) { var k=ks.get(0); return new float[]{k.x,k.y,k.z}; }
        if (time >= ks.get(ks.size()-1).time) { var k=ks.get(ks.size()-1); return new float[]{k.x,k.y,k.z}; }
        int hi=1; while (hi<ks.size() && ks.get(hi).time < time) hi++;
        int lo=hi-1;
        var a=ks.get(lo); var b=ks.get(hi);
        float u = (time - a.time) / (b.time - a.time);
        return new float[]{
                a.x + (b.x - a.x)*u,
                a.y + (b.y - a.y)*u,
                a.z + (b.z - a.z)*u
        };
    }
}