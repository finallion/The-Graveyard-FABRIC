package com.lion.graveyard.client;

import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public final class GraveyardDataTickets {
    public static final DataTicket<Byte> GHOUL_VARIANT = DataTicket.create("graveyard_ghoul_variant", Byte.class);
    public static final DataTicket<Byte> GHOULING_VARIANT = DataTicket.create("graveyard_ghouling_variant", Byte.class);
    public static final DataTicket<Boolean> GHOULING_HAS_COFFIN = DataTicket.create("graveyard_ghouling_has_coffin", Boolean.class);
    public static final DataTicket<ItemStack> GHOULING_OFFHAND = DataTicket.create("graveyard_ghouling_offhand", ItemStack.class);
    public static final DataTicket<ResourceLocation> ILLAGER_TEXTURE = DataTicket.create("graveyard_illager_texture", ResourceLocation.class);
    public static final DataTicket<Boolean> ILLAGER_LEFT_CLAW = DataTicket.create("graveyard_illager_left_claw", Boolean.class);
    public static final DataTicket<Boolean> ILLAGER_RIGHT_CLAW = DataTicket.create("graveyard_illager_right_claw", Boolean.class);
    public static final DataTicket<CorvidEntity.Species> CORVID_SPECIES = DataTicket.create("graveyard_corvid_species", CorvidEntity.Species.class);
    public static final DataTicket<Float> CORVID_LOOK_WEIGHT = DataTicket.create("graveyard_corvid_look_weight", Float.class);
    public static final DataTicket<ItemStack> CORVID_HELD_ITEM = DataTicket.create("graveyard_corvid_held_item", ItemStack.class);
    public static final DataTicket<Integer> LICH_PHASE = DataTicket.create("graveyard_lich_phase", Integer.class);

    private GraveyardDataTickets() {}
}
