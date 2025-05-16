package com.github.guyapooye.hubble.client.editor;

import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.impl.object.SunObject;
import foundry.veil.api.client.editor.SingleWindowInspector;
import imgui.ImGui;
import imgui.type.ImFloat;
import imgui.type.ImString;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class HubbleSunInspector extends SingleWindowInspector {
    private static final HubbleRenderer renderer = HubbleRenderer.getInstance();
    private final List<SunObject> sunObjects = new ArrayList<>();
    private int index;

    @Override
    protected void renderComponents() {

        if (ImGui.button("Add Sun")) {
            SunObject newSun = new SunObject(ResourceLocation.fromNamespaceAndPath("example","example" + sunObjects.size()), Minecraft.getInstance().level.dimension());
            newSun.setPosition(new Vector3f(0.0f));
            sunObjects.add(index++, newSun);
        }

        List<SunObject> copy = new ArrayList<>(sunObjects);

        ListIterator<SunObject> iterator = copy.listIterator();

        while (iterator.hasNext()) {
            SunObject sun = iterator.next();
            int index = iterator.nextIndex();

            if (ImGui.treeNode(index, sun.getId().toString())) {

//                SunObject newSun = new SunObject(sun.getId(), sun.getDimension());

                Vector3f sunPos = sun.getPosition();
                ResourceLocation sunId = sun.getId();
                float[] newPos = new float[]{sunPos.x, sunPos.y, sunPos.z};
                ImString newNamespace = new ImString(sunId.getNamespace(), 1000);
                ImString newPath = new ImString(sunId.getPath(), 1000);

                ImGui.indent();
                ImGui.pushItemWidth(100.0f);

                ImGui.text("Position:");
                if (ImGui.inputText("##namespace", newNamespace)) {
                    sun.setId(ResourceLocation.fromNamespaceAndPath(newNamespace.get(), newPath.get()));
                }

                ImGui.sameLine();
                ImGui.text(":");
                ImGui.sameLine();

                if (ImGui.inputText("##path", newPath)) {
                    sun.setId(ResourceLocation.fromNamespaceAndPath(newNamespace.get(), newPath.get()));
                }

                ImGui.pushItemWidth(300.0f);

                ImGui.text("Position:");
                if (ImGui.dragFloat3("##pos", newPos)) {
                    sun.setPosition(new Vector3f(newPos[0], newPos[1], newPos[2]));
                }

                ImGui.popItemWidth();
                ImGui.popItemWidth();

                if (ImGui.button("Delete")) {
                    sunObjects.remove(sun);
                }

                ImGui.unindent();
                ImGui.treePop();
            }
        }

    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("editor.hubble.inspector.sun.title");
    }
}
