package com.github.guyapooye.hubble.client.editor;

import com.github.guyapooye.hubble.api.client.HubbleClientManager;
import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.api.object.HubbleObject;
import com.github.guyapooye.hubble.impl.object.PlanetObject;
import com.github.guyapooye.hubble.impl.object.SunObject;
import foundry.veil.api.client.editor.SingleWindowInspector;
import imgui.ImGui;
import imgui.type.ImString;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static net.minecraft.resources.ResourceLocation.isValidNamespace;
import static net.minecraft.resources.ResourceLocation.isValidPath;

public class HubbleObjectInspector extends SingleWindowInspector {
    private static final HubbleRenderer renderer = HubbleRenderer.getInstance();
    private static final Map<ResourceLocation, HubbleObject<?>> allObjects = HubbleClientManager.getInstance().allObjects();
    private final List<SunObject> sunObjects = new ArrayList<>();
    private final List<PlanetObject> planetObjects = new ArrayList<>();
    private int index;

    @Override
    protected void renderComponents() {

        if (ImGui.collapsingHeader("Sun")) {
            ImGui.indent();

            renderSunComponents();

            ImGui.unindent();
        }

    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("editor.hubble.inspector.object.title");
    }

    private void renderSunComponents() {
        if (ImGui.button("Add Sun")) {
            SunObject newSun = new SunObject(ResourceLocation.fromNamespaceAndPath("hubble", "sun" + sunObjects.size()), Minecraft.getInstance().level.dimension());
            newSun.setPosition(new Vector3f(0.0f));
            newSun.setDimensions(new Vector3f(1.0f));
            newSun.setRotation(new Quaterniond());
            newSun.setColor(new Vector3f(1.0f));
            newSun.setIntensity(100.0f);
            sunObjects.add(newSun);
        }

        List<SunObject> copy = new ArrayList<>(sunObjects);

        ListIterator<SunObject> iterator = copy.listIterator();

        while (iterator.hasNext()) {
            SunObject sun = iterator.next();
            int index = iterator.nextIndex();

            boolean wasRemoved = false;

            ResourceLocation oldId = ResourceLocation.parse(sun.getId().toString());

            if (ImGui.treeNode(index, sun.getId().toString())) {

                Vector3f position = sun.getPosition();
                Vector3f dimensions = sun.getDimensions();
                Vector3f color = sun.getColor();
                float[] newPos = new float[]{position.x, position.y, position.z};
                float[] newDims = new float[]{dimensions.x, dimensions.y, dimensions.z};
                float[] newColor = new float[]{color.x, color.y, color.z};
                float[] newIntensity = new float[]{sun.getIntensity()};
                Vector3d rotation = sun.getRotation().getEulerAnglesXYZ(new Vector3d());
                float[][] newRotation = new float[][]{{(float) rotation.x},{(float) rotation.y},{(float) rotation.z}};
                ImString newNamespace = new ImString(oldId.getNamespace(), 1000);
                ImString newPath = new ImString(oldId.getPath(), 1000);

                ImGui.indent();
                ImGui.pushItemWidth(100.0f);

                ImGui.text("ID:");
                if (ImGui.inputText("##namespace", newNamespace)) {
                    if (isValidNamespace(newPath.get())) sun.setId(ResourceLocation.fromNamespaceAndPath(newNamespace.get(), newPath.get()));
                    wasRemoved = true;
                }

                ImGui.sameLine();
                ImGui.text(":");
                ImGui.sameLine();

                if (ImGui.inputText("##path", newPath)) {
                    if (isValidPath(newPath.get())) sun.setId(ResourceLocation.fromNamespaceAndPath(newNamespace.get(), newPath.get()));
                    wasRemoved = true;
                }

                ImGui.pushItemWidth(300.0f);
                ImGui.text("Position:");
                if (ImGui.dragFloat3("##pos", newPos)) {
                    setObjectPosition(sun, newPos);
                }

                ImGui.text("Dimensions:");
                if (ImGui.dragFloat3("##dims", newDims)) {
                    setSunDimensions(sun, newDims);
                }
                ImGui.popItemWidth();

                ImGui.text("Rotation:");
                ImGui.indent();
                ImGui.text("Yaw: ");
                ImGui.sameLine();
                if (ImGui.dragFloat("##yaw", newRotation[0], 0.05f, -10.0f, 10.0f)) {
                    setSunRotation(sun, newRotation);
                }

                ImGui.text("Pitch: ");
                ImGui.sameLine();
                if (ImGui.dragFloat("##pitch", newRotation[1], 0.05f, -10.0f, 10.0f)) {
                    setSunRotation(sun, newRotation);
                }

                ImGui.text("Roll: ");
                ImGui.sameLine();
                if (ImGui.dragFloat("##roll", newRotation[2], 0.05f, -10.0f, 10.0f)) {
                    setSunRotation(sun, newRotation);
                }
                ImGui.unindent();

                ImGui.pushItemWidth(300.0f);
                ImGui.text("Color:");
                if (ImGui.dragFloat3("##color", newColor, 0.005f, 0.0f, 1.0f)) {
                    setSunColor(sun, newColor);
                }
                ImGui.popItemWidth();

                ImGui.text("Intensity:");
                if (ImGui.dragFloat("##intensity", newIntensity, 100.0f, 0.0f)) {
                    sun.setIntensity(newIntensity[0]);
                }

                ImGui.popItemWidth();

                if (ImGui.button("Delete")) {
                    wasRemoved = true;
                    sunObjects.remove(sun);
                }

                ImGui.unindent();
                ImGui.treePop();
            }

            allObjects.put(sun.getId(), sun);
            if (wasRemoved) allObjects.remove(oldId);

        }
    }

    private void setObjectPosition(HubbleObject<?> object, float[] position) {
        object.setPosition(new Vector3f(position[0], position[1], position[2]));
    }

    private void setSunDimensions(SunObject sun, float[] dimensions) {
        sun.setDimensions(new Vector3f(dimensions[0], dimensions[1], dimensions[2]));
    }

    private void setSunRotation(SunObject sun, float[][] rotation) {
        sun.setRotation(new Quaterniond().rotationXYZ(rotation[0][0],rotation[1][0],rotation[1][0]));
    }

    private void setSunColor(SunObject sun, float[] color) {
        sun.setColor(new Vector3f(color[0], color[1], color[2]));
    }

}
