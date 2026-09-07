package com.lion.graveyard.platform.fabric;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lion.graveyard.Graveyard;
import com.lion.graveyard.pose.SkeletalPoses;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

/// Poses are client resources: only the renderer needs them, so nothing has to travel to a client
/// over the network and no world file carries a copy.
public class SkeletalPoseResourceListener extends JsonDataLoader implements IdentifiableResourceReloadListener {

    public SkeletalPoseResourceListener() {
        super(new GsonBuilder().disableHtmlEscaping().setLenient().create(), "skeletal_poses");
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Graveyard.MOD_ID, "skeletal_poses_loader");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        SkeletalPoses.load(loader);
    }
}
