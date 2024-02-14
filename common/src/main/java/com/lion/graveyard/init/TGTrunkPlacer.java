package com.lion.graveyard.init;


import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.world.trunk_placer.TGOakTreeTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.function.Supplier;

public class TGTrunkPlacer {

    public static final Supplier<TrunkPlacerType<?>> TG_OAK_TREE_TRUNK_PLACER = RegistryHelper.registerTrunkPlacerType("tg_oak_tree_trunk_placer", TGOakTreeTrunkPlacer.CODEC);

    public static void init() {}
}
