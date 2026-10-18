package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.init.TGTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FlyOntoPerchGoal extends WaterAvoidingRandomFlyingGoal {
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
    public boolean canUse() {
        if (this.corvid.isPecking()) {
            return false;
        }

        if (this.corvid.isPerched() && this.corvid.getRandom().nextInt(STAY_PERCHED_CHANCE) != 0) {
            return false;
        }

        return super.canUse();
    }

    /// Takes off first. Without this, a grounded bird paths along the floor.
    @Override
    public void start() {
        this.corvid.takeOff();
        super.start();
        Path path = this.corvid.getNavigation().getPath();

        if (path != null && !path.canReach() && path.getEndNode() != null
                && path.getEndNode().distanceToSqr(this.corvid.blockPosition()) < 9.0F) {
            this.corvid.getNavigation().stop();
        }
    }

    protected Vec3 getSearchCenter() {
        BlockPos home = this.corvid.getHome();

        if (this.corvid.isFarFromHome() && isUsableHome(home)) {
            return Vec3.atCenterOf(home);
        }

        return this.corvid.position();
    }

    private boolean isUsableHome(BlockPos home) {
        Level world = this.corvid.level();

        if (home.distToCenterSqr(this.corvid.position()) > MAX_HOME_SEARCH_DISTANCE * MAX_HOME_SEARCH_DISTANCE) {
            return false;
        }

        int reach = SEARCH_RADIUS;
        return world.hasChunkAt(home.offset(-reach, 0, -reach)) && world.hasChunkAt(home.offset(reach, 0, reach))
                && world.hasChunkAt(home.offset(-reach, 0, reach)) && world.hasChunkAt(home.offset(reach, 0, -reach));
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        if (this.mob.isInWater()) {
            Vec3 target = LandRandomPos.getPos(this.mob, 15, 15);

            if (target != null) {
                return target;
            }
        }

        Vec3 target = null;

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
    private Vec3 getWanderFallbackTarget() {
        Vec3 target = this.corvid.isFarFromHome()
                ? DefaultRandomPos.getPosTowards(this.mob, 12, 7, Vec3.atCenterOf(this.corvid.getHome()), Math.PI / 2)
                : null;

        if (target == null) {
            Vec3 direction = this.mob.getViewVector(0.0F);
            target = HoverRandomPos.getPos(this.mob, 8, 7, direction.x, direction.z, (float) (Math.PI / 2), 3, 1);

            if (target == null) {
                target = AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, direction.x, direction.z, Math.PI / 2);
            }
        }

        if (target == null) {
            return null;
        }

        BlockPos own = this.mob.blockPosition();
        double dx = own.getX() - target.x;
        double dz = own.getZ() - target.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        return new Vec3(target.x, Math.max(target.y, own.getY() - horizontalDistance), target.z);
    }

    @Nullable
    private Vec3 getSoarTarget() {
        Vec3 direction = this.mob.getViewVector(0.0F);
        return HoverRandomPos.getPos(this.mob, 24, 16, direction.x, direction.z, (float) (Math.PI / 2), 16, 10);
    }

    @Nullable
    private Vec3 getPerchTarget() {
        Level world = this.mob.level();
        Vec3 center = this.getSearchCenter();
        BlockPos own = this.mob.blockPosition();
        BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();

        BlockPos best = null;
        int bestScore = -1;
        int ties = 0;

        for (BlockPos pos : BlockPos.betweenClosed(
                Mth.floor(center.x - SEARCH_RADIUS),
                Mth.floor(center.y - SEARCH_DEPTH),
                Mth.floor(center.z - SEARCH_RADIUS),
                Mth.floor(center.x + SEARCH_RADIUS),
                Mth.floor(center.y + SEARCH_HEIGHT),
                Mth.floor(center.z + SEARCH_RADIUS)
        )) {
            if (own.equals(pos)) {
                continue;
            }

            // Steeper than 45 degrees gives a vertical path.
            double dx = own.getX() - pos.getX();
            double dz = own.getZ() - pos.getZ();

            if (own.getY() - pos.getY() > Math.sqrt(dx * dx + dz * dz) + 2) {
                continue;
            }

            BlockPos ground = pos.below();
            // Check bones first. A skeleton on a gravestone is a bone perch.
            boolean bonePerch = this.isBonePerch(world, pos);
            boolean solidGround = !bonePerch && this.canPerchOn(world, ground);

            if ((!solidGround && !bonePerch) || !this.isClear(world, pos)
                    || !this.isClear(world, scratch.setWithOffset(pos, Direction.UP))) {
                continue;
            }

            BlockState state = bonePerch ? world.getBlockState(pos) : world.getBlockState(ground);
            int score = this.scorePerch(state, ground);

            if (score > bestScore) {
                bestScore = score;
                best = pos.immutable();
                ties = 1;
            } else if (score == bestScore && this.mob.getRandom().nextInt(++ties) == 0) {
                best = pos.immutable();
            }
        }

        if (best == null || this.isRepelled(world, best)) {
            return null;
        }

        return Vec3.atBottomCenterOf(best);
    }

    private static boolean isField(BlockState state) {
        return state.is(Blocks.FARMLAND) || state.is(BlockTags.CROPS);
    }

    protected int scorePerch(BlockState ground, BlockPos pos) {
        if (ground.is(TGTags.CORVID_BONES)) {
            return 3;
        }

        if ((ground.is(Blocks.FARMLAND) || ground.is(BlockTags.CROPS)) && ground.is(this.corvid.getPerchTag())) {
            return 2;
        }

        if (ground.is(TGTags.CROW_LANDMARKS)) {
            return 1;
        }

        return 0;
    }

    /// Needs a collision shape too, because the tag contains wheat.
    protected boolean canPerchOn(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (this.corvid.getSpecies() == CorvidEntity.Species.CROW && isField(state) && !Graveyard.getConfig().flag("crowsEatCrops", true)) {
            return false;
        }

        return state.is(this.corvid.getPerchTag()) && !state.getCollisionShape(world, pos).isEmpty();
    }

    /// Skeleton blocks have no collision, so the bird stands inside the block.
    private boolean isBonePerch(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (!state.is(TGTags.CORVID_BONES) || !state.getCollisionShape(world, pos).isEmpty()) {
            return false;
        }

        // Any collision below counts. Farmland and gravestones are not full blocks.
        BlockPos ground = pos.below();
        return !world.getBlockState(ground).getCollisionShape(world, ground).isEmpty();
    }

    /// Call this last. The scarecrow scan is expensive.
    protected boolean isRepelled(Level world, BlockPos pos) {
        return false;
    }

    private boolean isClear(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.getCollisionShape(world, pos).isEmpty();
    }
}
