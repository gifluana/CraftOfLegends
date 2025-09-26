package com.lunazstudios.craftoflegends.bbmodel;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BbTypes {
    public static final class Cube {
        public final float[] from, to;
        public final float inflate;
        public final JsonObject faces;

        public Cube(float[] from, float[] to, float inflate, JsonObject faces) {
            this.from = from; this.to = to; this.inflate = inflate; this.faces = faces;
        }
    }

    public static final class Node {
        public final String uuid;
        public final String name;
        public final float[] origin;
        public final float[] baseRotDeg;
        public final List<Cube> cubes = new ArrayList<>();
        public final List<Node> children = new ArrayList<>();

        public Node(String uuid, String name, float[] origin, float[] baseRotDeg) {
            this.uuid = uuid; this.name = name; this.origin = origin; this.baseRotDeg = baseRotDeg;
        }
    }

    // --- Animação ---
    public static final class Key {
        public final float time;
        public final float x, y, z;
        public Key(float t, float x, float y, float z) { this.time=t; this.x=x; this.y=y; this.z=z; }
    }
    public static final class Track {
        public final List<Key> rot = new ArrayList<>();
        public final List<Key> pos = new ArrayList<>();
        public final List<Key> scl = new ArrayList<>();
        public boolean isEmpty() { return rot.isEmpty() && pos.isEmpty() && scl.isEmpty(); }
    }
    public static final class Clip {
        public final String name;
        public final float length;
        public final boolean loop;
        public final Map<String, Track> tracks = new HashMap<>();
        public Clip(String name, float length, boolean loop) {
            this.name=name; this.length=length; this.loop=loop;
        }
    }
}