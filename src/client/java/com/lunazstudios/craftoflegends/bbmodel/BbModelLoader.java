package com.lunazstudios.craftoflegends.bbmodel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class BbModelLoader {
    private static Identifier defaultModelId;

    public static void setDefaultModelId(String id) {
        defaultModelId = Identifier.of(id);
    }

    public static BbModel loadDefault() {
        if (defaultModelId == null) {
            throw new IllegalStateException("Default .bbmodel id not set");
        }
        return load(defaultModelId);
    }

    public static BbModel load(Identifier id) {
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
                        if (src.startsWith("data:image/png;base64,")) {
                            dataPng = src.substring("data:image/png;base64,".length());
                        }
                    }
                }
            }

            BbTexture texture = BbTexture.fromBase64Png(dataPng);

            BbMesh mesh = new BbMesh();
            Map<String, JsonObject> elementById = new HashMap<>();
            if (root.has("elements")) {
                for (JsonElement el : root.getAsJsonArray("elements")) {
                    JsonObject cube = el.getAsJsonObject();
                    String uuid = cube.has("uuid") ? cube.get("uuid").getAsString() : null;
                    if (uuid != null) elementById.put(uuid, cube);
                }
            }

            if (root.has("outliner")) {
                for (JsonElement node : root.getAsJsonArray("outliner")) {
                    traverseNode(node, new Matrix4f(), mesh, elementById, texture.getWidth(), texture.getHeight());
                }
            } else if (root.has("elements")) {
                for (JsonElement el : root.getAsJsonArray("elements")) {
                    JsonObject cube = el.getAsJsonObject();
                    addCubeWithLocalTransform(cube, new Matrix4f(), mesh, texture.getWidth(), texture.getHeight());
                }
            }

            return new BbModel(mesh, texture);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bbmodel: " + id + " -> " + e.getMessage(), e);
        }
    }

    private static void traverseNode(JsonElement node, Matrix4f parent,
                                     BbMesh mesh, Map<String, JsonObject> byId,
                                     int tw, int th) {
        if (node.isJsonPrimitive()) {
            String uuid = node.getAsString();
            JsonObject cube = byId.get(uuid);
            if (cube != null) addCubeWithLocalTransform(cube, new Matrix4f(parent), mesh, tw, th);
            return;
        }

        JsonObject obj = node.getAsJsonObject();

        float[] origin = readVec3Or(obj, "origin", 0,0,0);
        float[] rot    = readVec3Or(obj, "rotation", 0,0,0);

        Matrix4f local = new Matrix4f(parent);
        BbTransforms.appendZYX(local, rot[0], rot[1], rot[2], origin[0], origin[1], origin[2]);

        if (obj.has("children")) {
            for (JsonElement child : obj.getAsJsonArray("children")) {
                traverseNode(child, local, mesh, byId, tw, th);
            }
        }
    }

    private static void addCubeWithLocalTransform(JsonObject cube, Matrix4f parent,
                                                  BbMesh mesh, int tw, int th) {
        float[] from   = readVec3(cube.getAsJsonArray("from"));
        float[] to     = readVec3(cube.getAsJsonArray("to"));
        JsonObject faces = cube.getAsJsonObject("faces");

        float inflate = cube.has("inflate") ? cube.get("inflate").getAsFloat() : 0f;
        if (inflate != 0f) {
            from[0] -= inflate; from[1] -= inflate; from[2] -= inflate;
            to[0]   += inflate; to[1]   += inflate; to[2]   += inflate;
        }

        float[] origin = readVec3Or(cube, "origin", 0,0,0);
        float[] rot    = readVec3Or(cube, "rotation", 0,0,0);

        Matrix4f model = new Matrix4f(parent);
        BbTransforms.appendZYX(model, rot[0], rot[1], rot[2], origin[0], origin[1], origin[2]);

        Matrix3f normalMat = BbTransforms.normalOf(model);

        mesh.addCube(from, to, faces, tw, th, model, normalMat);
    }

    public static Identifier getDefaultModelId() {
        if (defaultModelId == null) throw new IllegalStateException("Default .bbmodel id not set");
        return defaultModelId;
    }

    private static float[] readVec3(JsonArray arr) {
        return new float[]{ arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat() };
    }

    private static float[] readVec3Or(JsonObject obj, String key, float dx, float dy, float dz) {
        if (!obj.has(key)) return new float[]{dx,dy,dz};
        JsonArray arr = obj.getAsJsonArray(key);
        return readVec3(arr);
    }
}