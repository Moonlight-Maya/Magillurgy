package io.github.eninja33.magillurgy.content.lasers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ReflectorBlock extends Block implements Reflector {

    public static final DirectionProperty MOUNTED = DirectionProperty.of("mounted"); //The direction of the block this is mounted on
    public static final IntProperty ROTATION = IntProperty.of("rotation", 0, 7);
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public static final VoxelShape DOWN = Block.createCuboidShape(1, 0, 1, 15, 1, 15);
    public static final VoxelShape UP = Block.createCuboidShape(1, 15, 1, 15, 16, 15);
    public static final VoxelShape NORTH = Block.createCuboidShape(1, 1, 0, 15, 15, 1);
    public static final VoxelShape SOUTH = Block.createCuboidShape(1, 1, 15, 15, 15, 16);
    public static final VoxelShape EAST = Block.createCuboidShape(15, 1, 1, 16, 15, 15);
    public static final VoxelShape WEST = Block.createCuboidShape(0, 1, 1, 1, 15, 15);
    public static final VoxelShape OUTLINE = Block.createCuboidShape(1, 1, 1, 15, 15, 15);

    public ReflectorBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(MOUNTED, Direction.DOWN).with(ROTATION, 0).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(MOUNTED);
        stateManager.add(ROTATION);
        stateManager.add(POWERED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        //Figures out what direction to face the mirror when you place the block.
        //Tries to place it so the mirror faces you.
        BlockState state = getDefaultState().with(MOUNTED, ctx.getSide().getOpposite());
        Vec3d playerLooking = ctx.getHitPos().subtract(ctx.getPlayer().getEyePos()).normalize();
        if (state.get(MOUNTED).getUnitVector().dot(new Vec3f(playerLooking)) < 0.9) {
            double max = -1000;
            for (int i = 0; i < 8; i++) {
                Vec3d normal = getNormal(state.with(ROTATION, i));
                double val = Math.abs(playerLooking.dotProduct(normal));
                if (val > max) {
                    max = val;
                    state = state.with(ROTATION, i);
                }
            }
        }
        return state;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult result) {
        //Direction we came in, cross product with mounted direction.
        //Dot this with (the hit location minus the center of the block)
        //If positive, then counterclockwise.
        Vec3f vec1 = result.getSide().getUnitVector();
        vec1.cross(state.get(MOUNTED).getUnitVector());
        Vec3d vec2 = result.getPos().subtract(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
        rotate(state, world, pos, vec2.dotProduct(new Vec3d(vec1)) > 0);
        return ActionResult.SUCCESS;
    }

    private static void rotate(BlockState state, World world, BlockPos pos, boolean counterclockwise) {
        int rot = state.get(ROTATION);
        int newRot = counterclockwise ? (rot+1)%8 : (rot+7)%8;
        world.setBlockState(pos, state.with(ROTATION, newRot));
    }

    private static Vec3d getNormal(BlockState state) {
        double angle = state.get(ROTATION)*0.3926991; //22.5 deg in rad
        Direction dir = state.get(MOUNTED);
        if (dir.getAxis() == Direction.Axis.Y) {
            if (dir == Direction.UP) angle = -angle;
            return new Vec3d(Math.sin(angle), 0, Math.cos(angle));
        } else if (dir.getAxis() == Direction.Axis.X) {
            if (dir == Direction.EAST) angle = -angle;
            return new Vec3d(0, Math.cos(angle), Math.sin(angle));
        }
        if (dir == Direction.NORTH) angle = -angle;
        return new Vec3d(Math.sin(angle), Math.cos(angle), 0);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(MOUNTED)) {
            case DOWN:
                return DOWN;
            case UP:
                return UP;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case EAST:
                return EAST;
            default:
                return WEST;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(OUTLINE, getCollisionShape(state, world, pos, null));
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public Vec3i reflect(BlockState state, Vec3i input) {
        Vec3d normal = getNormal(state);
        Vec3d in = new Vec3d(input.getX(), input.getY(), input.getZ());
        in = in.subtract(normal.multiply(2*in.dotProduct(normal)));
        return new Vec3i(Math.round(in.x), Math.round(in.y), Math.round(in.z));
    }
}
