package com.github.guyapooye.hubble.ext;

import org.joml.Quaternionf;

public interface EntityExtension {
    Quaternionf hubble$getRotation();
    Quaternionf hubble$getViewRotation(float t);
    void hubble$setRotation(Quaternionf rotation);
    void hubble$setRotation0(Quaternionf rotation);
    void hubble$roll(float angle);
    boolean hubble$canSpaceWalk();
}
