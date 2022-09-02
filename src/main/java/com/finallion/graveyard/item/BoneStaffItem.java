package com.finallion.graveyard.item;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulingEntity;
import com.finallion.graveyard.entities.LichEntity;
import com.finallion.graveyard.init.TGEntities;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.checkerframework.checker.units.qual.A;

import java.util.function.Predicate;

public class BoneStaffItem extends Item {
    private GhoulingEntity ghouling;

    public BoneStaffItem() {
        super(new FabricItemSettings().maxCount(1).group(TheGraveyard.GROUP));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (player != null && (stack.getNbt() == null || !stack.getNbt().getBoolean("Minion"))) {
            GhoulingEntity ghouling = (GhoulingEntity) TGEntities.GHOULING.create(world);
            ghouling.refreshPositionAndAngles(blockPos.up(), 0.0F, 0.0F);
            ghouling.setOwner(player);
            ghouling.onSummoned(stack);
            this.ghouling = ghouling;
            stack.getOrCreateNbt().putBoolean("Minion", true);
            world.spawnEntity(ghouling);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && user.getMainHandStack().getNbt() != null && user.getMainHandStack().getNbt().getBoolean("Minion") && ghouling != null) {
            Predicate<Entity> predicate = entity -> entity instanceof LivingEntity;
            int distance = 100;
            Vec3d start = user.getEyePos();
            Vec3d rot = user.getRotationVec(1.0F).multiply(distance);
            Vec3d end = start.add(rot);
            Box box = user.getBoundingBox().stretch(rot).expand(1.0D);
            HitResult result = ProjectileUtil.raycast(user, start, end, box, predicate, distance * distance);
            if (result != null && result.getType() == HitResult.Type.ENTITY) {
                EntityHitResult hitResult = (EntityHitResult) result;
                this.ghouling.setTarget((LivingEntity) hitResult.getEntity());
                this.ghouling.setAttacking(true);
            }
        }
        return super.use(world, user, hand);
    }


}
