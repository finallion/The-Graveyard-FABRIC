package com.lion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.mixin.TrunkPlacerTypeInvoker;
import com.finallion.graveyard.world.trunk_placer.BasicTreeTrunkPlacer;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class TGTrunkPlacer {

    public static final TrunkPlacerType<BasicTreeTrunkPlacer> BASIC_TREE_TRUNK_PLACER = TrunkPlacerTypeInvoker.callRegister("graveyard:basic_tree_trunk_placer", BasicTreeTrunkPlacer.CODEC);

    public static void init() {}
}
