package com.lion.graveyard.entities.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.AnimatedCorruptedIllager;
import com.lion.graveyard.platform.ConfigDirectory;
import com.lion.graveyard.platform.DevEnvironment;
import com.lion.graveyard.init.TGSounds;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public final class AnimationPreviewClient {
    private static final String FILE = "graveyard_anim_preview.json";
    private static final Map<Entity, Playback> PLAYBACKS = new WeakHashMap<>();
    private static final Map<Entity, SoundInstance> TRACKED = new WeakHashMap<>();
    private static final Map<Entity, Dig> DIGS = new WeakHashMap<>();
    // The body leaves the ground at endTick. The arms dig at strokeTicks.
    private static final Map<String, DigSpec> DIG_SPECS = Map.of(
            "graveyard:corrupted_vindicator|animation.corrupted_illager.spawn", new DigSpec(85, new int[]{6, 15, 26, 37, 50}, true),
            "graveyard:corrupted_pillager|animation.corrupted_illager.spawn", new DigSpec(85, new int[]{6, 15, 26, 37, 50}, true),
            "graveyard:corrupted_evoker|animation.corrupted_illager.spawn", new DigSpec(85, new int[]{6, 15, 26, 37, 50}, true),
            "graveyard:ghoul|animation.ghoul.spawn", new DigSpec(70, new int[]{2, 15, 26, 42, 62}, false),
            "graveyard:ghouling|spawn", new DigSpec(54, new int[]{14, 17, 22, 27, 32, 47}, false));
    private static final AnimationClock.Listener LISTENER = new AnimationClock.Listener() {
        @Override
        public void keyframeZero(Entity entity, String animation, double startOffsetTicks) {
            startDig(entity, animation, startOffsetTicks);

            if (AnimationPreview.forcedAnimation(entity) != null) {
                startPreview(entity, animation);
            } else if (entity instanceof AnimatedCorruptedIllager illager) {
                illager.onAnimationKeyframeZero(animation, startOffsetTicks);
            }
        }

        @Override
        public void playDetached(Entity entity, @Nullable SoundEvent sound) {
            if (sound != null) {
                entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), sound, entity.getSoundSource(), 1.0F, 1.0F, false);
            }
        }

        @Override
        public void playTracked(Entity entity, @Nullable SoundEvent sound) {
            stop(TRACKED.remove(entity));

            if (sound != null) {
                TRACKED.put(entity, play(entity, sound, 1.0F, 1.0F));
            }
        }
    };
    private static Map<String, Map<String, List<Cue>>> cues;
    @Nullable
    private static net.minecraft.client.multiplayer.ClientLevel lastWorld;

    private AnimationPreviewClient() {}

    public static void clientTick(Minecraft client) {
        AnimationClock.setListener(LISTENER);
        // The sound instances keep their entity alive, so a world change must clear every map.
        if (client.level != lastWorld) {
            TRACKED.values().forEach(AnimationPreviewClient::stop);
            TRACKED.clear();
            PLAYBACKS.values().forEach(Playback::stopSounds);
            PLAYBACKS.clear();
            DIGS.clear();
            lastWorld = client.level;
        }

        TRACKED.keySet().removeIf(Entity::isRemoved);

        if (client.level != null) {
            long time = client.level.getGameTime();
            DIGS.entrySet().removeIf(entry -> entry.getKey().isRemoved() || !entry.getValue().tick(entry.getKey(), time));
        }

        if (!DevEnvironment.isDevelopment() || client.level == null) {
            return;
        }

        long now = client.level.getGameTime();
        Iterator<Map.Entry<Entity, Playback>> iterator = PLAYBACKS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Entity, Playback> entry = iterator.next();

            if (entry.getKey().isRemoved() || AnimationPreview.forcedAnimation(entry.getKey()) == null) {
                entry.getValue().stopSounds();
                iterator.remove();
            } else {
                entry.getValue().tick(entry.getKey(), now);
            }
        }
    }

    private static void startDig(Entity entity, String animation, double startOffsetTicks) {
        DigSpec spec = DIG_SPECS.get(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()) + "|" + animation);

        if (spec == null) {
            DIGS.remove(entity);
            return;
        }

        // Ghoul and ghouling spawn their own earth particles while the spawn timer runs.
        boolean particles = spec.particles() || AnimationPreview.forcedAnimation(entity) != null;
        DIGS.put(entity, new Dig(spec, entity.level().getGameTime() - (long) startOffsetTicks, particles));
    }

    private static void startPreview(Entity entity, String animation) {
        Minecraft client = Minecraft.getInstance();
        Playback old = PLAYBACKS.remove(entity);

        if (old != null) {
            old.stopSounds();
        }

        if (cues == null) {
            cues = load();
        }

        Map<String, List<Cue>> byAnimation = cues.get(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
        List<Cue> list = byAnimation == null ? null : byAnimation.get(animation);

        if (list == null || client.level == null) {
            return;
        }

        Playback playback = new Playback(list, client.level.getGameTime());
        PLAYBACKS.put(entity, playback);
        playback.tick(entity, client.level.getGameTime());
    }

    private static SoundInstance play(Entity entity, SoundEvent event, float volume, float pitch) {
        SoundInstance instance = new EntityBoundSoundInstance(event, entity.getSoundSource(), volume, pitch, entity, entity.level().getRandom().nextLong());
        Minecraft.getInstance().getSoundManager().play(instance);
        return instance;
    }

    private static void stop(@Nullable SoundInstance instance) {
        if (instance != null) {
            Minecraft.getInstance().getSoundManager().stop(instance);
        }
    }

    private static Map<String, Map<String, List<Cue>>> load() {
        Map<String, Map<String, List<Cue>>> result = new HashMap<>();
        Path file = ConfigDirectory.getConfigDirectory().resolve(FILE);

        if (!Files.isRegularFile(file)) {
            return result;
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> type : root.entrySet()) {
                Map<String, List<Cue>> byAnimation = new HashMap<>();
                for (Map.Entry<String, JsonElement> animation : type.getValue().getAsJsonObject().entrySet()) {
                    List<Cue> list = new ArrayList<>();
                    for (JsonElement element : animation.getValue().getAsJsonArray()) {
                        Cue cue = Cue.parse(element.getAsJsonObject());

                        if (cue != null) {
                            list.add(cue);
                        }
                    }
                    byAnimation.put(animation.getKey(), list);
                }
                result.put(type.getKey(), byAnimation);
            }
        } catch (IOException | RuntimeException exception) {
            Graveyard.LOGGER.warn("Cannot read {}", FILE, exception);
            result.clear();
        }
        return result;
    }

    private record Cue(SoundEvent event, int offsetTicks, int repeatTicks, float volume, float pitch) {
        static Cue parse(JsonObject json) {
            Identifier id = Identifier.tryParse(json.get("event").getAsString());
            SoundEvent event = id == null ? null : BuiltInRegistries.SOUND_EVENT.getValue(id);

            if (event == null) {
                return null;
            }

            return new Cue(
                    event,
                    json.has("offsetTicks") ? json.get("offsetTicks").getAsInt() : 0,
                    json.has("repeatTicks") ? json.get("repeatTicks").getAsInt() : 0,
                    json.has("volume") ? json.get("volume").getAsFloat() : 1.0F,
                    json.has("pitch") ? json.get("pitch").getAsFloat() : 1.0F
            );
        }
    }

    private record DigSpec(int endTick, int[] strokeTicks, boolean particles) {
    }

    private static final class Dig {
        private static final int MIN_GAP = 16;
        private static final int GAP_SPREAD = 8;
        final DigSpec spec;
        final long start;
        final boolean particles;
        int stroke;
        long nextScrape;
        long lastTick = Long.MIN_VALUE;
        boolean started;

        Dig(DigSpec spec, long start, boolean particles) {
            this.spec = spec;
            this.start = start;
            this.particles = particles;
            this.nextScrape = start;
        }

        boolean tick(Entity entity, long now) {
            // Client ticks continue while the game is paused. World time does not.
            if (now == this.lastTick) {
                return true;
            }

            this.lastTick = now;
            long elapsed = now - this.start;

            if (elapsed > this.spec.endTick()) {
                return false;
            }

            RandomSource random = entity.level().getRandom();
            BlockState ground = groundBelow(entity);

            if (!this.started) {
                this.started = true;
                while (this.stroke < this.spec.strokeTicks().length && this.spec.strokeTicks()[this.stroke] < elapsed) {
                    this.stroke++;
                }
            }

            while (this.stroke < this.spec.strokeTicks().length && elapsed >= this.spec.strokeTicks()[this.stroke]) {
                playDig(entity, 1.0F, 0.5F + random.nextFloat() * 0.08F);
                playEarth(entity, ground, 0.8F);
                this.stroke++;
            }

            if (now >= this.nextScrape) {
                playDig(entity, 0.6F, 0.5F + random.nextFloat() * 0.1F);
                playEarth(entity, ground, 0.35F);
                this.nextScrape = now + MIN_GAP + random.nextInt(GAP_SPREAD);
            }

            if (this.particles) {
                if (ground.getRenderShape() != RenderShape.INVISIBLE) {
                    for (int i = 0; i < 10; i++) {
                        entity.level().addParticle(
                                new BlockParticleOption(ParticleTypes.BLOCK, ground),
                                entity.getX() + random.nextFloat() * 1.4F - 0.7F,
                                entity.getY(),
                                entity.getZ() + random.nextFloat() * 1.4F - 0.7F,
                                0.0D,
                                0.0D,
                                0.0D
                        );
                    }
                }
            }

            return true;
        }

        private static BlockState groundBelow(Entity entity) {
            return entity.level().getBlockState(BlockPos.containing(entity.getX(), entity.getY() - 0.2D, entity.getZ()));
        }

        private static void playEarth(Entity entity, BlockState ground, float volume) {
            BlockState sound = ground.isAir() || ground.getRenderShape() == RenderShape.INVISIBLE ? Blocks.DIRT.defaultBlockState() : ground;
            entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), sound.getSoundType().getBreakSound(), SoundSource.HOSTILE, volume, 0.8F, false);
        }

        private static void playDig(Entity entity, float volume, float pitch) {
            entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), TGSounds.DIG.get(), SoundSource.HOSTILE, volume, pitch, false);
        }
    }

    private static final class Playback {
        final List<Cue> list;
        final double[] next;
        final List<SoundInstance> instances = new ArrayList<>();

        Playback(List<Cue> list, long start) {
            this.list = list;
            this.next = new double[list.size()];
            for (int i = 0; i < this.next.length; i++) {
                this.next[i] = start + list.get(i).offsetTicks();
            }
        }

        void tick(Entity entity, long now) {
            for (int i = 0; i < this.next.length; i++) {
                Cue cue = this.list.get(i);

                if (now >= this.next[i]) {
                    this.instances.add(play(entity, cue.event(), cue.volume(), cue.pitch()));
                    this.next[i] = cue.repeatTicks() > 0 ? this.next[i] + cue.repeatTicks() : Double.MAX_VALUE;
                }
            }
        }

        void stopSounds() {
            this.instances.forEach(AnimationPreviewClient::stop);
            this.instances.clear();
        }
    }
}
