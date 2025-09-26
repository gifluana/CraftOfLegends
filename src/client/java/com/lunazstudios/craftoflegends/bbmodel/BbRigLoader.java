package com.lunazstudios.craftoflegends.bbmodel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class BbRigLoader {

    public static final class Rig {
        public final BbTypes.Node root;
        public final List<BbTypes.Clip> clips;
        public final BbTexture texture;
        public Rig(BbTypes.Node root, List<BbTypes.Clip> clips, BbTexture tex) {
            this.root=root; this.clips=clips; this.texture=tex;
        }
    }

    public static Rig load(Identifier id) {
        try {
            Resource res = MinecraftClient.getInstance().getResourceManager().getResource(id).orElseThrow();
            JsonObject root = JsonParser.parseReader(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

            String dataPng = null;
            if (root.has("textures")) {
                JsonArray arr = root.getAsJsonArray("textures");
                if (!arr.isEmpty()) {
                    JsonObject t0 = arr.get(0).getAsJsonObject();
                    if (t0.has("source")) {
                        String src = t0.get("source").getAsString();
                        if (src.startsWith("data:image/png;base64,")) dataPng = src.substring("data:image/png;base64,".length());
                    }
                }
            }
            BbTexture tex = BbTexture.fromBase64Png(dataPng);

            Map<String, JsonObject> elemById = new HashMap<>();
            if (root.has("elements")) {
                for (JsonElement el : root.getAsJsonArray("elements")) {
                    JsonObject cube = el.getAsJsonObject();
                    String uuid = cube.has("uuid") ? cube.get("uuid").getAsString() : UUID.randomUUID().toString();
                    elemById.put(uuid, cube);
                }
            }

            BbTypes.Node rootNode = new BbTypes.Node("root", "root", new float[]{0,0,0}, new float[]{0,0,0});
            if (root.has("outliner")) {
                for (JsonElement child : root.getAsJsonArray("outliner")) {
                    rootNode.children.add(parseNode(child, elemById));
                }
            } else {
                for (JsonObject cube : elemById.values()) {
                    rootNode.cubes.add(toCube(cube));
                }
            }

            List<BbTypes.Clip> clips = new ArrayList<>();
            if (root.has("animations")) {
                for (JsonElement aEl : root.getAsJsonArray("animations")) {
                    JsonObject a = aEl.getAsJsonObject();
                    String name = a.get("name").getAsString();
                    float length = a.get("length").getAsFloat();
                    boolean loop = a.has("loop") && !"once".equals(a.get("loop").getAsString());
                    BbTypes.Clip clip = new BbTypes.Clip(name, length, loop);

                    JsonObject animators = a.getAsJsonObject("animators");
                    for (Map.Entry<String, JsonElement> e : animators.entrySet()) {
                        String uuid = e.getKey();
                        JsonObject anim = e.getValue().getAsJsonObject();
                        BbTypes.Track t = new BbTypes.Track();

                        JsonArray kfs = anim.getAsJsonArray("keyframes");
                        if (kfs == null) continue;

                        for (JsonElement kEl : kfs) {
                            JsonObject k = kEl.getAsJsonObject();
                            if (!k.has("channel")) continue;
                            String ch = k.get("channel").getAsString();
                            if (!k.has("data_points")) continue;

                            JsonArray dps = k.getAsJsonArray("data_points");
                            if (dps == null || dps.isEmpty()) continue;

                            JsonObject dp = dps.get(0).getAsJsonObject();
                            float time = getFloat(k, "time", 0f);
                            float[] v = getVec3(dp);

                            switch (ch) {
                                case "rotation" -> t.rot.add(new BbTypes.Key(time, v[0], v[1], v[2]));
                                case "position" -> t.pos.add(new BbTypes.Key(time, v[0], v[1], v[2]));
                                case "scale"    -> t.scl.add(new BbTypes.Key(time, v[0], v[1], v[2]));
                                default -> {}
                            }
                        }
                        Comparator<BbTypes.Key> byT = Comparator.comparingDouble(k0 -> k0.time);
                        t.rot.sort(byT); t.pos.sort(byT); t.scl.sort(byT);

                        if (!t.isEmpty()) clip.tracks.put(uuid, t);
                    }
                    clips.add(clip);
                }
            }

            return new Rig(rootNode, clips, tex);
        } catch (Exception e) {
            throw new RuntimeException("Rig load failed: "+id+" -> "+e.getMessage(), e);
        }
    }

    private static BbTypes.Node parseNode(JsonElement nodeEl, Map<String, JsonObject> elemById) {
        if (nodeEl.isJsonPrimitive()) {
            String uuid = nodeEl.getAsString();
            JsonObject cube = elemById.get(uuid);
            BbTypes.Node n = new BbTypes.Node(uuid, uuid, new float[]{0,0,0}, new float[]{0,0,0});
            if (cube != null) n.cubes.add(toCube(cube));
            return n;
        }
        JsonObject obj = nodeEl.getAsJsonObject();
        String uuid = obj.has("uuid") ? obj.get("uuid").getAsString() : UUID.randomUUID().toString();
        String name = obj.has("name") ? obj.get("name").getAsString() : uuid;
        float[] origin = readVec3Or(obj, "origin", 0,0,0);
        float[] rot    = readVec3Or(obj, "rotation", 0,0,0);

        BbTypes.Node n = new BbTypes.Node(uuid, name, origin, rot);
        if (obj.has("children")) {
            for (JsonElement ch : obj.getAsJsonArray("children")) {
                if (ch.isJsonPrimitive()) {
                    String cu = ch.getAsString();
                    JsonObject cube = elemById.get(cu);
                    if (cube != null) n.cubes.add(toCube(cube));
                } else {
                    n.children.add(parseNode(ch, elemById));
                }
            }
        }
        return n;
    }

    private static float getFloat(JsonObject o, String k, float def) {
        JsonElement e = o.get(k);
        if (e == null || e.isJsonNull()) return def;
        try { return e.getAsFloat(); } catch (Exception ex) {
            try { return Float.parseFloat(e.getAsString()); } catch (Exception ex2) { return def; }
        }
    }
    private static float[] getVec3(JsonObject dp) {
        if (dp.has("x") || dp.has("y") || dp.has("z")) {
            return new float[]{
                    getFloat(dp, "x", 0f),
                    getFloat(dp, "y", 0f),
                    getFloat(dp, "z", 0f)
            };
        }
        if (dp.has("vector") && dp.get("vector").isJsonArray()) {
            var a = dp.getAsJsonArray("vector");
            return new float[]{
                    a.size()>0 ? a.get(0).getAsFloat() : 0f,
                    a.size()>1 ? a.get(1).getAsFloat() : 0f,
                    a.size()>2 ? a.get(2).getAsFloat() : 0f
            };
        }
        // fallback
        return new float[]{0f,0f,0f};
    }

    private static BbTypes.Cube toCube(JsonObject cube) {
        float[] from = read3(cube.getAsJsonArray("from"));
        float[] to   = read3(cube.getAsJsonArray("to"));
        float inflate = cube.has("inflate") ? cube.get("inflate").getAsFloat() : 0f;
        JsonObject faces = cube.getAsJsonObject("faces");
        return new BbTypes.Cube(from, to, inflate, faces);
    }

    private static float[] read3(JsonArray a){ return new float[]{a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat()}; }
    private static float[] readVec3Or(JsonObject obj, String key, float dx, float dy, float dz) {
        if (!obj.has(key)) return new float[]{dx,dy,dz};
        return read3(obj.getAsJsonArray(key));
    }
    private static float parseFloat(JsonElement e){ return e==null?0f : e.getAsFloat(); }
}