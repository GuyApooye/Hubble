package com.github.guyapooye.hubble.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class BoxRenderer {
    public final static int[] BOX_X = {-1, +1, +1, -1, -1, +1, +1, -1, -1, -1, -1, -1, +1, +1, +1, +1, +1, +1, -1, -1, -1, -1, +1, +1};
    public final static int[] BOX_Y = {-1, -1, -1, -1, +1, +1, +1, +1, -1, +1, +1, -1, -1, +1, +1, -1, -1, +1, +1, -1, -1, +1, +1, -1};
    public final static int[] BOX_Z = {-1, -1, +1, +1, +1, +1, -1, -1, +1, +1, -1, -1, -1, -1, +1, +1, +1, +1, +1, +1, -1, -1, -1, -1};
    public final static int[] BOX_U = {1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1};
    public final static int[] BOX_V = {1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0};
    public final static int[][] BOX_NORMAL = {
            {0, -1, 0},
            {0, 1, 0},
            {-1, 0, 0},
            {1, 0, 0},
            {0, 0, -1},
            {0, 0, 1}
    };

    public static void renderBoxQuads(PoseStack.Pose pose, Vector3f center, Vector3f dim, Vector4f color, VertexConsumer builder) {
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 3; j++) {
                int ij = 4 * i + j;
                builder.addVertex(pose, center.x + BOX_X[ij] * dim.x, center.y + BOX_Y[ij] * dim.y, center.z + BOX_Z[ij] * dim.z).setUv(BOX_V[ij], BOX_U[ij]).setLight(15728880).setNormal(pose, BOX_NORMAL[i][0],BOX_NORMAL[i][1],BOX_NORMAL[i][2]).setColor(color.x, color.y, color.z, color.w());
            }
        }
    }

    public static void renderBoxQuads(PoseStack.Pose pose, Vector3f center, Vector3f dim, VertexConsumer builder) {
        renderBoxQuads(pose, center, dim, new Vector4f(1.0f,1.0f,1.0f,1.0f), builder);
    }

    public static void renderBoxQuadsIO(PoseStack.Pose pose, Vector3f center, Vector3f dim, Vector4f color, VertexConsumer builder) {
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 3; j++) {
                int ij = 4 * i + j;
                builder.addVertex(pose, center.x - BOX_X[ij] * dim.x, center.y - BOX_Y[ij] * dim.y, center.z - BOX_Z[ij] * dim.z).setUv(BOX_V[ij], BOX_U[ij]).setLight(15728880).setNormal(pose, BOX_NORMAL[i][0],BOX_NORMAL[i][1],BOX_NORMAL[i][2]).setColor(color.x, color.y, color.z, color.w());
            }
        }
    }

    public static void renderBoxQuadsIO(PoseStack.Pose pose, Vector3f center, Vector3f dim, Vector3f color, VertexConsumer builder) {
        renderBoxQuadsIO(pose, center, dim, new Vector4f(color,1.0f), builder);
    }

    public static void renderBoxQuadsIO(PoseStack.Pose pose, Vector3f center, Vector3f dim, VertexConsumer builder) {
        renderBoxQuadsIO(pose, center, dim, new Vector4f(1.0f,1.0f,1.0f,1.0f), builder);
    }
}
