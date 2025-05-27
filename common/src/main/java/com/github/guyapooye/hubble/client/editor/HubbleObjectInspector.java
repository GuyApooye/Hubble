package com.github.guyapooye.hubble.client.editor;

import com.github.guyapooye.hubble.Hubble;
import com.github.guyapooye.hubble.api.client.HubbleClientManager;
import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.api.object.CelestialBody;
import com.github.guyapooye.hubble.impl.object.PlanetBody;
import com.github.guyapooye.hubble.impl.object.SunBody;
import foundry.veil.api.client.editor.SingleWindowInspector;
import imgui.ImGui;
import imgui.type.ImBoolean;
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
    private static final Map<ResourceLocation, CelestialBody<?>> allObjects = HubbleClientManager.getInstance().allObjects();
    private final List<SunBody> sunObjects = new ArrayList<>();
    private final List<PlanetBody> planetObjects = new ArrayList<>();
    private final ImBoolean useRaytracedSuns = new ImBoolean(false);
    private final boolean devEnv;

    public HubbleObjectInspector(boolean devEnv) {
        this.devEnv = devEnv;
    }

    @Override
    protected void renderComponents() {

        ImGui.beginDisabled(!devEnv);

        if (ImGui.collapsingHeader("Sun")) {
            ImGui.pushID("suns");
            ImGui.indent();
            renderSunComponents();
            ImGui.unindent();
            ImGui.popID();
        }

        if (ImGui.collapsingHeader("Planet")) {
            ImGui.pushID("planets");
            ImGui.indent();
            renderPlanetComponents();
            ImGui.unindent();
            ImGui.popID();
        }

        ImGui.endDisabled();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("editor.hubble.inspector.object.title");
    }

    private void renderSunComponents() {
        if (ImGui.button("Add Sun")) {
            SunBody newSun = new SunBody(Hubble.path("sun" + sunObjects.size()), Minecraft.getInstance().level.dimension());
            newSun.setPosition(new Vector3f(0.0f));
            newSun.setDimensions(new Vector3f(1.0f));
            newSun.setRotation(new Quaterniond());
            newSun.setColor(new Vector3f(1.0f));
            newSun.setIntensity(100.0f);
            sunObjects.add(newSun);
        }

        ImGui.text("Use Raytraced Suns: ");
        ImGui.sameLine();
        ImGui.checkbox("##raytracedSuns", useRaytracedSuns);

        List<SunBody> copy = new ArrayList<>(sunObjects);

        ListIterator<SunBody> iterator = copy.listIterator();

        while (iterator.hasNext()) {

            SunBody sun = iterator.next();
            int index = iterator.nextIndex();

            ImGui.pushID("sun"+index);

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

            ImGui.popID();

        }
    }

    private void renderPlanetComponents() {
        if (ImGui.button("Add Planet")) {
            PlanetBody newPlanet = new PlanetBody(Hubble.path("planet" + planetObjects.size()), Minecraft.getInstance().level.dimension());
            newPlanet.setPosition(new Vector3f(0.0f));
            newPlanet.setDimensions(new Vector3f(1.0f));
            newPlanet.setRotation(new Quaterniond());
            newPlanet.setTexture(Hubble.path("textures/planet/earth.png"));
            planetObjects.add(newPlanet);
        }

//        ImGui.text("Use Raycasting:");
//        ImGui.sameLine();
//        ImGui.checkbox("##raycastSuns", useRaycastSuns);

        List<PlanetBody> copy = new ArrayList<>(planetObjects);

        ListIterator<PlanetBody> iterator = copy.listIterator();

        while (iterator.hasNext()) {
            PlanetBody planet = iterator.next();
            int index = iterator.nextIndex();

            ImGui.pushID("planet"+index);

            boolean wasRemoved = false;

            ResourceLocation oldId = ResourceLocation.parse(planet.getId().toString());

            if (ImGui.treeNode(index, planet.getId().toString())) {

                ResourceLocation texturePath = ResourceLocation.parse(planet.getTexture().toString());

                Vector3f position = planet.getPosition();
                Vector3f dimensions = planet.getDimensions();
                float[] newPos = new float[]{position.x, position.y, position.z};
                float[] newDims = new float[]{dimensions.x, dimensions.y, dimensions.z};
                Vector3d rotation = planet.getRotation().getEulerAnglesXYZ(new Vector3d());
                float[][] newRotation = new float[][]{{(float) rotation.x},{(float) rotation.y},{(float) rotation.z}};
                ImString newNamespace = new ImString(oldId.getNamespace(), 1000);
                ImString newPath = new ImString(oldId.getPath(), 1000);
                ImString newTextureNamespace = new ImString(texturePath.getNamespace(), 1000);
                ImString newTexturePath = new ImString(texturePath.getPath(), 1000);
                boolean idChanged = false;
                boolean textureChanged = false;

                ImGui.indent();
                ImGui.pushItemWidth(100.0f);

                ImGui.text("ID:");
                if (ImGui.inputText("##namespace", newNamespace)) {
                    idChanged = true;
                }

                ImGui.sameLine();
                ImGui.text(":");
                ImGui.sameLine();

                if (ImGui.inputText("##path", newPath)) {
                    idChanged = true;
                }

                ImGui.pushItemWidth(300.0f);
                ImGui.text("Position:");
                if (ImGui.dragFloat3("##pos", newPos)) {
                    setObjectPosition(planet, newPos);
                }

                ImGui.text("Dimensions:");
                if (ImGui.dragFloat3("##dims", newDims)) {
                    setPlanetDimensions(planet, newDims);
                }
                ImGui.popItemWidth();

                ImGui.text("Rotation:");
                ImGui.indent();
                ImGui.text("Yaw: ");
                ImGui.sameLine();
                if (ImGui.dragFloat("##yaw", newRotation[0], 0.05f, -10.0f, 10.0f)) {
                    setPlanetRotation(planet, newRotation);
                }

                ImGui.text("Pitch: ");
                ImGui.sameLine();
                if (ImGui.dragFloat("##pitch", newRotation[1], 0.05f, -10.0f, 10.0f)) {
                    setPlanetRotation(planet, newRotation);
                }

                ImGui.text("Roll: ");
                ImGui.sameLine();
                if (ImGui.dragFloat("##roll", newRotation[2], 0.05f, -10.0f, 10.0f)) {
                    setPlanetRotation(planet, newRotation);
                }
                ImGui.unindent();

                ImGui.text("Texture:");
                if (ImGui.inputText("##textureNamespace", newTextureNamespace)) {
                    textureChanged = true;
                }

                ImGui.sameLine();
                ImGui.text(":");
                ImGui.sameLine();

                if (ImGui.inputText("##texturePath", newTexturePath)) {
                    textureChanged = true;
                }

                ImGui.popItemWidth();

                if (ImGui.button("Delete")) {
                    wasRemoved = true;
                    planetObjects.remove(planet);
                }

                if (idChanged) {
                    if (isValidNamespace(newNamespace.get()) && isValidPath(newPath.get())) planet.setId(ResourceLocation.fromNamespaceAndPath(newNamespace.get(), newPath.get()));
                    wasRemoved = true;
                }

                if (textureChanged && isValidNamespace(newTextureNamespace.get()) && isValidPath(newTexturePath.get()))
                    planet.setTexture(ResourceLocation.fromNamespaceAndPath(newTextureNamespace.get(), newTexturePath.get()));

                ImGui.unindent();

                ImGui.treePop();
            }

            allObjects.put(planet.getId(), planet);
            if (wasRemoved) allObjects.remove(oldId);

            ImGui.popID();

        }
    }

    private void setObjectPosition(CelestialBody<?> object, float[] position) {
        object.setPosition(new Vector3f(position[0], position[1], position[2]));
    }

    private void setSunDimensions(SunBody sun, float[] dimensions) {
        sun.setDimensions(new Vector3f(dimensions[0], dimensions[1], dimensions[2]));
    }

    private void setSunRotation(SunBody sun, float[][] rotation) {
        sun.setRotation(new Quaterniond().rotationXYZ(rotation[0][0],rotation[1][0],rotation[2][0]));
    }

    private void setSunColor(SunBody sun, float[] color) {
        sun.setColor(new Vector3f(color[0], color[1], color[2]));
    }

    public boolean useRaycastSuns() {
        return useRaytracedSuns.get();
    }

    private void setPlanetDimensions(PlanetBody planet, float[] dimensions) {
        planet.setDimensions(new Vector3f(dimensions[0], dimensions[1], dimensions[2]));
    }

    private void setPlanetRotation(PlanetBody sun, float[][] rotation) {
        sun.setRotation(new Quaterniond().rotationXYZ(rotation[0][0],rotation[1][0],rotation[2][0]));
    }

}
