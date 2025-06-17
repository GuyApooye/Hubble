package com.github.guyapooye.hubble.client.editor;

import foundry.veil.api.client.editor.SingleWindowInspector;
import imgui.ImGui;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class HubbleReentryInspector extends SingleWindowInspector {

    private Vector3f direction = new Vector3f(0.0f, 0.0f, -1.0f);
    private Vector4f color0 = new Vector4f(1.0f, 0.5f, 0.2f, 1.0f);
    private Vector4f color1 = new Vector4f(1.0f, 0.15f, 0.0f, 0.0f);
    private Vector4f color2 = new Vector4f(1.0f, 0.85f, 0.3f, 0.05f);
    private float length = 75.0f;
    private float strength = 1.0f;
    private float velocity = 1.0f;
    private float increment = 0.25f;
    private float taperSize = 0.5f;


    @Override
    protected void renderComponents() {

        Quaternionf dirQuat = new Vector3f(0.0f,0.0f,-1.0f).rotationTo(direction, new Quaternionf());
        Vector3f dirEuler = dirQuat.getEulerAnglesXYZ(new Vector3f());
        float[] newDir = new float[]{direction.x, direction.y, direction.z};
        float[] newLength = new float[]{length};
        float[] newVelocity = new float[]{velocity};
        float[] newIncrement = new float[]{increment};
        float[] newTaperSize = new float[]{taperSize};
        float[] newStrength = new float[]{strength};
        float[] newColor0 = new float[]{color0.x, color0.y, color0.z, color0.w};
        float[] newColor1 = new float[]{color1.x, color1.y, color1.z, color1.w};
        float[] newColor2 = new float[]{color2.x, color2.y, color2.z, color2.w};

        ImGui.pushItemWidth(300);
        ImGui.text("Direction");
        if (ImGui.dragFloat3("##direction", newDir, 0.005f)) {
//            Quaternionf newQuat = new Quaternionf().rotationXYZ(newDir[0], newDir[1], 0.0f);
//            direction = newQuat.transform(new Vector3f(0.0f, 0.0f,-1.0f));
            direction.set(newDir);
        }

        ImGui.text("Length");
        if (ImGui.dragFloat("##length", newLength, 0.05f)) {
            length = newLength[0];
        }

        ImGui.text("Strength");
        if (ImGui.dragFloat("##strength", newStrength, 0.05f)) {
            strength = newStrength[0];
        }

        ImGui.text("Velocity");
        if (ImGui.dragFloat("##velocity", newVelocity, 0.5f)) {
            velocity = newVelocity[0];
        }

        ImGui.text("Increment");
        if (ImGui.dragFloat("##increment", newIncrement, 0.05f)) {
            increment = newIncrement[0];
        }

        ImGui.text("Taper Size");
        if (ImGui.dragFloat("##taperSize", newTaperSize, 0.05f)) {
            taperSize = newTaperSize[0];
        }

        if (ImGui.treeNode("Color 0")) {
            if (ImGui.colorPicker4("##color0", newColor0)) {
                color0.set(newColor0);
            }

            ImGui.treePop();
        }

        if (ImGui.treeNode("Color 1")) {
            if (ImGui.colorPicker4("##color1", newColor1)) {
                color1.set(newColor1);
            }

            ImGui.treePop();
        }

        if (ImGui.treeNode("Color 2")) {
            if (ImGui.colorPicker4("##color2", newColor2)) {
                color2.set(newColor2);
            }

            ImGui.treePop();
        }


        ImGui.popItemWidth();

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

    public Vector4f getColor0() {
        return color0;
    }

    public Vector4f getColor1() {
        return color1;
    }

    public Vector4f getColor2() {
        return color2;
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
}
