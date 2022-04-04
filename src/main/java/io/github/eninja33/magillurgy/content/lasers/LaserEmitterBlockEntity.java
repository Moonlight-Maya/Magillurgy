package io.github.eninja33.magillurgy.content.lasers;

import io.github.eninja33.magillurgy.content.registrars.BlockEntityRegistrar;
import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LaserEmitterBlockEntity extends BlockEntity {

    private LaserBeam laserBeam; //Keeps track of all laser reflections and things

    private static final int STRENGTH = 256; //Default of 64 blocks the laser can travel
    private static final int UPDATE_TIME = 1; //Refresh lasers every x ticks

    public LaserEmitterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public LaserEmitterBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityRegistrar.LASER_EMITTER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, LaserEmitterBlockEntity be) {
        if (world.getTime() % UPDATE_TIME == 0) {
            be.updateLaser(world, pos);

            //Make it relative to the block itself
            for (int i = 0; i < be.laserBeam.numNodes; i++)
                be.laserBeam.beamNodes.get(i).pos.add(-pos.getX(), -pos.getY(), -pos.getZ());
        }
    }

    public LaserBeam getLaserBeam() {
        return laserBeam;
    }


    private static final Vec3f WHITE;
    static {
        float[] whiteComp = DyeColor.WHITE.getColorComponents();
        WHITE = new Vec3f(whiteComp[0], whiteComp[1], whiteComp[2]);
    }


    private void updateLaser(World world, BlockPos pos) {
        if (laserBeam == null)
            laserBeam = new LaserBeam();

        Vec3f curColor = WHITE.copy();
        laserBeam.reset();
        laserBeam.setNextOrAdd(pos.getX(), pos.getY(), pos.getZ(), curColor);

        Vec3i dir = getCachedState().get(LaserEmitterBlock.FACING).getVector();
        int strength = STRENGTH;
        pos = pos.add(dir);
        BlockState state = world.getBlockState(pos);

        while (!state.isOpaque() && strength > 0) { //TODO: replace with better detection for blockage
            if (state.getBlock() instanceof Reflector ref) {
                laserBeam.setNextOrAdd(pos.getX(), pos.getY(), pos.getZ(), curColor);
                dir = ref.reflect(state, dir);
            }
            else if (state.getBlock() instanceof Stainable stainable) {
                float[] comp = stainable.getColor().getColorComponents();
                curColor.set(comp[0], comp[1], comp[2]);

                laserBeam.setNextOrAdd(
                        pos.getX()-dir.getX()/2f,
                        pos.getY()-dir.getY()/2f,
                        pos.getZ()-dir.getZ()/2f,
                        curColor
                );
            }
            pos = pos.add(dir);
            strength--;
            state = world.getBlockState(pos);
        }
        laserBeam.setNextOrAdd(pos.getX(), pos.getY(), pos.getZ(), curColor);
    }

    public static class LaserBeam {
        public final ArrayList<BeamNode> beamNodes;
        public int numNodes;

        private LaserBeam() {
            this.beamNodes = new ArrayList<>();
            this.numNodes = 0;
        }

        public void reset() {
            numNodes = 0;
        }

        public void setNextOrAdd(float x, float y, float z, Vec3f rgb) {
            if (numNodes < beamNodes.size()) {
                beamNodes.get(numNodes).pos.set(x, y, z);
                beamNodes.get(numNodes).color.set(rgb);
            } else {
                beamNodes.add(new BeamNode(new Vec3f(x, y, z), rgb.copy()));
            }
            numNodes++;
        }
    }
    public record BeamNode(Vec3f pos, Vec3f color) {}
}
