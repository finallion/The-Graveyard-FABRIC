package com.lion.graveyard.init;


import com.lion.graveyard.mixin.TrunkPlacerTypeInvoker;
import com.lion.graveyard.world.trunk_placer.BasicTreeTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class TGTrunkPlacer {

    public static final TrunkPlacerType<BasicTreeTrunkPlacer> BASIC_TREE_TRUNK_PLACER = TrunkPlacerTypeInvoker.callRegister("graveyard:basic_tree_trunk_placer", BasicTreeTrunkPlacer.CODEC);

    public static void init() {}
}
