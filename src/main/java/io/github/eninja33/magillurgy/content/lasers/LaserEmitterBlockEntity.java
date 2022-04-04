package io.github.eninja33.magillurgy.content.lasers;

import io.github.eninja33.magillurgy.content.registrars.BlockEntityRegistrar;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LaserEmitterBlockEntity extends BlockEntity {

    private List<Vec3d> laserChain; //Keeps track of all laser reflections and things

    private static final int STRENGTH = 64; //Default of 64 blocks the laser can travel
    private static final int UPDATE_TIME = 1; //Refresh lasers every 10 ticks

    public LaserEmitterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public LaserEmitterBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityRegistrar.LASER_EMITTER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, LaserEmitterBlockEntity be) {
        if (world.getTime() % UPDATE_TIME == 0) {
            be.updateLaser(world, pos, state);

            //Make it relative to the block itself
            for (int i = 0; i < be.laserChain.size(); i++)
                be.laserChain.set(i, be.laserChain.get(i).subtract(pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    public Iterable<Vec3d> getLasers() {
        return laserChain;
    }

    private void updateLaser(World world, BlockPos pos, BlockState state) {
        if (laserChain == null)
            laserChain = new ArrayList<>();

        laserChain.clear();
        Vec3i dir = getCachedState().get(LaserEmitterBlock.FACING).getVector();
        int strength = STRENGTH;
        pos = pos.add(dir);
        state = world.getBlockState(pos);

        while (!state.isOpaque() && strength > 0) { //TODO: replace with better detection for blockage
            if (state.getBlock() instanceof Reflector ref) {
                laserChain.add(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
                dir = ref.reflect(state, dir);
            }
            pos = pos.add(dir);
            strength--;
            state = world.getBlockState(pos);
        }
        laserChain.add(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));

    }
}
