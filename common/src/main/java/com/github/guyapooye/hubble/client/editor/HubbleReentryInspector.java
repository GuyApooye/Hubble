package com.github.guyapooye.hubble.client.editor;

import foundry.veil.api.client.editor.SingleWindowInspector;
import imgui.ImGui;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class HubbleReentryInspector extends SingleWindowInspector {

    private final Vector3f direction = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector4f lightColor = new Vector4f(1.0f, 0.5f, 0.2f, 1.0f);
    private final Vector4f trailColor = new Vector4f(1.0f, 0.15f, 0.0f, 0.0f);
    private final Vector4f trailColorHot = new Vector4f(1.0f, 0.85f, 0.3f, 0.05f);
    private final Vector4f bowShockColor = new Vector4f(0.5f, 0.5f, 0.99f, 0.1f);
    private float length = 75.0f;
    private float strength = 1.0f;
    private float velocity = 1.0f;
    private float increment = 0.25f;
    private float taperSize = 0.5f;
    private float bowShockOffset = 0.5f;
    private float bowShockColorLerpOffset = 0.5f;


    @Override
    protected void renderComponents() {
        if (ImGui.treeNode("Lighting")) {
            renderLightingComponents();
            ImGui.treePop();
        }
        if (ImGui.treeNode("Trail")) {
            renderTrailComponents();
            ImGui.treePop();
        }
        if (ImGui.treeNode("Bow Shock")) {
            renderBowShockComponents();
            ImGui.treePop();
        }
    }

    private void renderLightingComponents() {

        float[] newLightColor = new float[]{lightColor.x, lightColor.y, lightColor.z, lightColor.w};

        if (ImGui.treeNode("Light Color")) {
            if (ImGui.colorPicker4("##lightColor", newLightColor)) {
                lightColor.set(newLightColor);
            }

            ImGui.treePop();
        }
    }

    private void renderTrailComponents() {
//        Quaternionf dirQuat = new Vector3f(0.0f,0.0f,-1.0f).rotationTo(direction, new Quaternionf());
//        Vector3f dirEuler = dirQuat.getEulerAnglesXYZ(new Vector3f());
        float[] newDir = new float[]{direction.x, direction.y, direction.z};
        float[] newLength = new float[]{length};
        float[] newVelocity = new float[]{velocity};
        float[] newIncrement = new float[]{increment};
        float[] newTaperSize = new float[]{taperSize};
        float[] newStrength = new float[]{strength};
        float[] newTrailColor = new float[]{trailColor.x, trailColor.y, trailColor.z, trailColor.w};
        float[] newTrailColorHot = new float[]{trailColorHot.x, trailColorHot.y, trailColorHot.z, trailColorHot.w};

        ImGui.pushItemWidth(300);
        ImGui.text("Direction");
        if (ImGui.dragFloat3("##direction", newDir, 0.005f)) {
//            Quaternionf newQuat = new Quaternionf().rotationXYZ(newDir[0], newDir[1], 0.0f);
//            direction = newQuat.transform(new Vector3f(0.0f, 0.0f,-1.0f));
            direction.set(newDir);
        }

        ImGui.text("Length");
        if (ImGui.dragFloat("##length", newLength, 0.5f)) {
            length = newLength[0];
        }

        ImGui.text("Strength");
        if (ImGui.dragFloat("##strength", newStrength, 0.005f)) {
            strength = newStrength[0];
        }

        ImGui.text("Velocity");
        if (ImGui.dragFloat("##velocity", newVelocity, 0.5f)) {
            velocity = newVelocity[0];
        }

        ImGui.text("Increment");
        if (ImGui.dragFloat("##increment", newIncrement, 0.005f)) {
            increment = newIncrement[0];
        }

        ImGui.text("Taper Size");
        if (ImGui.dragFloat("##taperSize", newTaperSize, 0.005f)) {
            taperSize = newTaperSize[0];
        }

        if (ImGui.treeNode("Trail Color")) {
            if (ImGui.colorPicker4("##trailColor", newTrailColor)) {
                trailColor.set(newTrailColor);
            }

            ImGui.treePop();
        }

        if (ImGui.treeNode("Trail Color Hot")) {
            if (ImGui.colorPicker4("##trailColorHot", newTrailColorHot)) {
                trailColorHot.set(newTrailColorHot);
            }

            ImGui.treePop();
        }


        ImGui.popItemWidth();
    }

    private void renderBowShockComponents() {

        float[] newBowShockOffset = new float[]{bowShockOffset};
        float[] newBowShockColorLerpOffset = new float[]{bowShockColorLerpOffset};
        float[] newBowShockColor = new float[]{bowShockColor.x, bowShockColor.y, bowShockColor.z, bowShockColor.w};

        ImGui.text("Bow Shock Offset");
        if (ImGui.dragFloat("##bowShockOffset", newBowShockOffset, 0.005f)) {
            bowShockOffset = newBowShockOffset[0];
        }

        ImGui.text("Bow Shock Color Lerp Offset");
        if (ImGui.dragFloat("##bowShockColorLerpOffset", newBowShockColorLerpOffset, 0.005f)) {
            bowShockColorLerpOffset = newBowShockColorLerpOffset[0];
        }

        if (ImGui.treeNode("Bow Shock Color")) {
            if (ImGui.colorPicker4("##bowShockColor", newBowShockColor)) {
                bowShockColor.set(newBowShockColor);
            }

            ImGui.treePop();
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("editor.hubble.inspector.reentry.title");
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getVelocity() {
        return velocity;
    }

    public float getLength() {
        return length;
    }

    public Vector4f getLightColor() {
        return lightColor;
    }

    public Vector4f getTrailColor() {
        return trailColor;
    }

    public Vector4f getTrailColorHot() {
        return trailColorHot;
    }

    public float getStrength() {
        return strength;
    }

    public float getIncrement() {
        return increment;
    }

    public float getTaperSize() {
        return taperSize;
    }

    public float getBowShockOffset() {
        return bowShockOffset;
    }

    public Vector4f getBowShockColor() {
        return bowShockColor;
    }

    public float getBowShockColorLerpOffset() {
        return bowShockColorLerpOffset;
    }
}
