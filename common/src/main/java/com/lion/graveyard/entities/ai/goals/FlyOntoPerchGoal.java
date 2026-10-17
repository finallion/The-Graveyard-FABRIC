package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.init.TGTags;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FlyOntoPerchGoal extends FlyGoal {
    private static final int SEARCH_RADIUS = 8;
    private static final double MAX_HOME_SEARCH_DISTANCE = 48.0D;
    private static final int SEARCH_HEIGHT = 6;
    private static final int SEARCH_DEPTH = 12;
    private static final int STAY_PERCHED_CHANCE = 6;
    private static final float SOAR_CHANCE = 0.25F;

    protected final CorvidEntity corvid;

    public FlyOntoPerchGoal(CorvidEntity corvid, double speed) {
        super(corvid, speed);
        this.corvid = corvid;
    }

    @Override
    public boolean canStart() {
        if (this.corvid.isPecking()) {
            return false;
        }
        if (this.corvid.isPerched() && this.corvid.getRandom().nextInt(STAY_PERCHED_CHANCE) != 0) {
            return false;
        }
        return super.canStart();
    }

    /// Takes off first. Without this, a grounded bird paths along the floor.
    @Override
    public void start() {
        this.corvid.takeOff();
        super.start();
        Path path = this.corvid.getNavigation().getCurrentPath();
        if (path != null && !path.reachesTarget() && path.getEnd() != null
                && path.getEnd().getSquaredDistance(this.corvid.getBlockPos()) < 9.0F) {
            this.corvid.getNavigation().stop();
        }
    }

    protected Vec3d getSearchCenter() {
        BlockPos home = this.corvid.getHome();
        if (this.corvid.isFarFromHome() && isUsableHome(home)) {
            return Vec3d.ofCenter(home);
        }
        return this.corvid.getPos();
    }

    private boolean isUsableHome(BlockPos home) {
        World world = this.corvid.getWorld();
        if (home.getSquaredDistance(this.corvid.getPos()) > MAX_HOME_SEARCH_DISTANCE * MAX_HOME_SEARCH_DISTANCE) {
            return false;
        }
        int reach = SEARCH_RADIUS;
        return world.isChunkLoaded(home.add(-reach, 0, -reach)) && world.isChunkLoaded(home.add(reach, 0, reach))
                && world.isChunkLoaded(home.add(-reach, 0, reach)) && world.isChunkLoaded(home.add(reach, 0, -reach));
    }

    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        if (this.mob.isTouchingWater()) {
            Vec3d target = FuzzyTargeting.find(this.mob, 15, 15);
            if (target != null) {
                return target;
            }
        }
        Vec3d target = null;
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            target = !this.corvid.isFarFromHome() && this.mob.getRandom().nextFloat() < SOAR_CHANCE ? this.getSoarTarget() : null;
            if (target == null) {
                target = this.getPerchTarget();
            }
        }
        return target == null ? this.getWanderFallbackTarget() : target;
    }

    /// Raises a target below the bird to 45 degrees. A steeper target gives a vertical path.
    @Nullable
    private Vec3d getWanderFallbackTarget() {
        Vec3d target = this.corvid.isFarFromHome()
                ? NoPenaltyTargeting.findTo(this.mob, 12, 7, Vec3d.ofCenter(this.corvid.getHome()), Math.PI / 2)
                : null;
        if (target == null) {
            Vec3d direction = this.mob.getRotationVec(0.0F);
            target = AboveGroundTargeting.find(this.mob, 8, 7, direction.x, direction.z, (float) (Math.PI / 2), 3, 1);
            if (target == null) {
                target = NoPenaltySolidTargeting.find(this.mob, 8, 4, -2, direction.x, direction.z, Math.PI / 2);
            }
        }
        if (target == null) {
            return null;
        }
        BlockPos own = this.mob.getBlockPos();
        double dx = own.getX() - target.x;
        double dz = own.getZ() - target.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        return new Vec3d(target.x, Math.max(target.y, own.getY() - horizontalDistance), target.z);
    }

    @Nullable
    private Vec3d getSoarTarget() {
        Vec3d direction = this.mob.getRotationVec(0.0F);
        return AboveGroundTargeting.find(this.mob, 24, 16, direction.x, direction.z, (float) (Math.PI / 2), 16, 10);
    }

    @Nullable
    private Vec3d getPerchTarget() {
        World world = this.mob.getWorld();
        Vec3d center = this.getSearchCenter();
        BlockPos own = this.mob.getBlockPos();
        BlockPos.Mutable scratch = new BlockPos.Mutable();

        BlockPos best = null;
        int bestScore = -1;
        int ties = 0;

        for (BlockPos pos : BlockPos.iterate(
                MathHelper.floor(center.x - SEARCH_RADIUS), MathHelper.floor(center.y - SEARCH_DEPTH), MathHelper.floor(center.z - SEARCH_RADIUS),
                MathHelper.floor(center.x + SEARCH_RADIUS), MathHelper.floor(center.y + SEARCH_HEIGHT), MathHelper.floor(center.z + SEARCH_RADIUS))) {
            if (own.equals(pos)) {
                continue;
            }
            // Steeper than 45 degrees gives a vertical path.
            double dx = own.getX() - pos.getX();
            double dz = own.getZ() - pos.getZ();
            if (own.getY() - pos.getY() > Math.sqrt(dx * dx + dz * dz) + 2) {
                continue;
            }
            BlockPos ground = pos.down();
            // Check bones first. A skeleton on a gravestone is a bone perch.
            boolean bonePerch = this.isBonePerch(world, pos);
            boolean solidGround = !bonePerch && this.canPerchOn(world, ground);
            if ((!solidGround && !bonePerch) || !this.isClear(world, pos)
                    || !this.isClear(world, scratch.set(pos, Direction.UP))) {
                continue;
            }
            BlockState state = bonePerch ? world.getBlockState(pos) : world.getBlockState(ground);
            int score = this.scorePerch(state, ground);
            if (score > bestScore) {
                bestScore = score;
                best = pos.toImmutable();
                ties = 1;
            } else if (score == bestScore && this.mob.getRandom().nextInt(++ties) == 0) {
                best = pos.toImmutable();
            }
        }
        if (best == null || this.isRepelled(world, best)) {
            return null;
        }
        return Vec3d.ofBottomCenter(best);
    }

    private static boolean isField(BlockState state) {
        return state.isOf(Blocks.FARMLAND) || state.isIn(BlockTags.CROPS);
    }

    protected int scorePerch(BlockState ground, BlockPos pos) {
        if (ground.isIn(TGTags.CORVID_BONES)) {
            return 3;
        }
        if ((ground.isOf(Blocks.FARMLAND) || ground.isIn(BlockTags.CROPS)) && ground.isIn(this.corvid.getPerchTag())) {
            return 2;
        }
        if (ground.isIn(TGTags.CROW_LANDMARKS)) {
            return 1;
        }
        return 0;
    }

    /// Needs a collision shape too, because the tag contains wheat.
    protected boolean canPerchOn(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (this.corvid.getSpecies() == CorvidEntity.Species.CROW && isField(state) && !Graveyard.getConfig().flag("crowsEatCrops", true)) {
            return false;
        }
        return state.isIn(this.corvid.getPerchTag()) && !state.getCollisionShape(world, pos).isEmpty();
    }

    /// Skeleton blocks have no collision, so the bird stands inside the block.
    private boolean isBonePerch(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.isIn(TGTags.CORVID_BONES) || !state.getCollisionShape(world, pos).isEmpty()) {
            return false;
        }
        // Any collision below counts. Farmland and gravestones are not full blocks.
        BlockPos ground = pos.down();
        return !world.getBlockState(ground).getCollisionShape(world, ground).isEmpty();
    }

    /// Call this last. The scarecrow scan is expensive.
    protected boolean isRepelled(World world, BlockPos pos) {
        return false;
    }

    private boolean isClear(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.getCollisionShape(world, pos).isEmpty();
    }
}
