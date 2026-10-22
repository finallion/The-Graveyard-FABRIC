package com.lion.graveyard.config;

import java.util.List;

final class ConfigRules {
    private ConfigRules() {}

    static void apply(ConfigValues values, List<String> reset) {
        for (String mob : ConfigSchema.FULL_MOB_IDS) {
            String prefix = "mobs." + mob + ".spawn.";
            int weight = values.integer(prefix + "weight");
            int minGroup = raise(values, prefix + "minGroup", weight > 0 ? 1 : 0, reset);
            raise(values, prefix + "maxGroup", minGroup, reset);
        }

        int minDistance = values.integer("hordes.minDistance");
        raise(values, "hordes.maxDistance", minDistance, reset);

        int minOffers = values.integer("behaviour.namelessHangedOffers.min");
        raise(values, "behaviour.namelessHangedOffers.max", minOffers, reset);
    }

    private static int raise(ConfigValues values, String path, int floor, List<String> reset) {
        int current = values.integer(path);

        if (current >= floor) {
            return current;
        }

        values.set(path, floor);
        reset.add(path);
        return floor;
    }
}
