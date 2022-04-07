package io.github.eninja33.magillurgy.content.lasers;

import io.github.eninja33.magillurgy.content.registrars.BlockEntityRegistrar;
import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LaserEmitterBlockEntity extends BlockEntity {

    private LaserBeam laserBeam = LaserBeam.get(new Vec3f(pos.getX(), pos.getY(), pos.getZ()), WHITE);; //Keeps track of all laser reflections and things

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
            be.laserBeam.update(world, pos);
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

    public static class LaserBeam {

        private final Vec3f startPos, endPos, color;
        private final List<LaserBeam> children = new ArrayList<>();

        private LaserBeam(Vec3f startPos, Vec3f color) {
            this.startPos = startPos.copy();
            this.endPos = startPos.copy();
            this.color = color.copy();
        }

        public void update(World world, BlockPos pos) {
            clear();
            startPos.set(pos.getX(), pos.getY(), pos.getZ());
            endPos.set(startPos);
            recurse(world, world.getBlockState(pos).get(LaserEmitterBlock.FACING).getVector(), STRENGTH);
            shift(-pos.getX(), -pos.getY(), -pos.getZ());
        }

        /**
         * Moves along a ray, updating endPos. If we hit a
         * block that splits or changes our direction, create new LaserBeam
         * objects and recurse those as well.
         */
        private void recurse(World world, Vec3i dir, int strength) {
            BlockPos.Mutable curPos = new BlockPos.Mutable(
                    startPos.getX()+dir.getX(),
                    startPos.getY()+dir.getY(),
                    startPos.getZ()+dir.getZ()
            );
            BlockState state = world.getBlockState(curPos);
            while (!state.isOpaque() && strength > 0) { //TODO: replace with better detection for blockage
                if (state.getBlock() instanceof LightAffector ref) {
                    int sideStr = ref.strengthSide(state, dir, strength);
                    int mainStr = ref.strengthMain(state, dir, strength);
                    if (sideStr > 0) {
                        Vec3i newDir = ref.reflectSide(state, dir);
                        LaserBeam newBeam = get(curPos.getX(), curPos.getY(), curPos.getZ(), color.getX(), color.getY(), color.getZ());
                        newBeam.recurse(world, newDir, sideStr);
                        children.add(newBeam);
                    }
                    if (mainStr > 0) {
                        Vec3i newDir = ref.reflectMain(state, dir);
                        LaserBeam newBeam = get(curPos.getX(), curPos.getY(), curPos.getZ(), color.getX(), color.getY(), color.getZ());
                        newBeam.recurse(world, newDir, mainStr);
                        children.add(newBeam);
                    }
                    break;
                }
                else if (state.getBlock() instanceof Stainable stainable) {
                    float[] comp = stainable.getColor().getColorComponents();
                    LaserBeam newBeam = get(new Vec3f(
                            curPos.getX()-dir.getX()/2f,
                            curPos.getY()-dir.getY()/2f,
                            curPos.getZ()-dir.getZ()/2f
                    ), new Vec3f(
                            comp[0], comp[1], comp[2]
                    ));
                    newBeam.recurse(world, dir, strength-1);
                    children.add(newBeam);
                    break;
                }
                curPos.move(dir);
                strength--;
                state = world.getBlockState(curPos);
            }
            endPos.set(curPos.getX(), curPos.getY(), curPos.getZ());
        }

        public void shift(float x, float y, float z) {
            startPos.add(x, y, z);
            endPos.add(x, y, z);
            for (LaserBeam child : children)
                child.shift(x, y, z);
        }

        public Vec3f getColor() {
            return color;
        }

        private static float OFFSET = 1.0f / 32;
        public void render(MatrixStack matrices, BufferBuilder buffer) {
            //Create some handy vectors to help us draw the laser
            Vec3f diff = endPos.copy();
            diff.subtract(startPos);

            Vec3f normal;
            if (diff.getX() == 0 && diff.getZ() == 0)
                normal = new Vec3f(1,0,0);
            else
                normal = new Vec3f(-diff.getZ(), 0, diff.getX());

            Vec3f binormal = diff.copy();
            binormal.cross(normal);

            normal.normalize();
            normal.scale(OFFSET);
            binormal.normalize();
            binormal.scale(OFFSET);

            //Create the 8 vertices of the beam segment
            Vec3f p0 = startPos.copy();
            p0.subtract(normal);
            p0.subtract(binormal);
            Vec3f p1 = startPos.copy();
            p1.subtract(normal);
            p1.add(binormal);
            Vec3f p2 = startPos.copy();
            p2.add(normal);
            p2.subtract(binormal);
            Vec3f p3 = startPos.copy();
            p3.add(normal);
            p3.add(binormal);
            Vec3f p4 = endPos.copy();
            p4.subtract(normal);
            p4.subtract(binormal);
            Vec3f p5 = endPos.copy();
            p5.subtract(normal);
            p5.add(binormal);
            Vec3f p6 = endPos.copy();
            p6.add(normal);
            p6.subtract(binormal);
            Vec3f p7 = endPos.copy();
            p7.add(normal);
            p7.add(binormal);

            Matrix4f posMat = matrices.peek().getPositionMatrix();
            Vec3f color = getColor();
            float r = color.getX();
            float g = color.getY();
            float b = color.getZ();

            //Chuck all those vertices into the buffer to make a nice colored beam
            buffer.vertex(posMat, p4.getX(), p4.getY(), p4.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p0.getX(), p0.getY(), p0.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p2.getX(), p2.getY(), p2.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p6.getX(), p6.getY(), p6.getZ()).color(r, g, b, 1).next();

            buffer.vertex(posMat, p6.getX(), p6.getY(), p6.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p2.getX(), p2.getY(), p2.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p3.getX(), p3.getY(), p3.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p7.getX(), p7.getY(), p7.getZ()).color(r, g, b, 1).next();

            buffer.vertex(posMat, p7.getX(), p7.getY(), p7.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p3.getX(), p3.getY(), p3.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p1.getX(), p1.getY(), p1.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p5.getX(), p5.getY(), p5.getZ()).color(r, g, b, 1).next();

            buffer.vertex(posMat, p5.getX(), p5.getY(), p5.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p1.getX(), p1.getY(), p1.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p0.getX(), p0.getY(), p0.getZ()).color(r, g, b, 1).next();
            buffer.vertex(posMat, p4.getX(), p4.getY(), p4.getZ()).color(r, g, b, 1).next();

            for (LaserBeam child : children)
                child.render(matrices, buffer);
        }

        //Caching to reduce number of allocations
        private static final Queue<LaserBeam> cache = new LinkedList<>();
        public static LaserBeam get(Vec3f pos, Vec3f color) {
            return get(pos.getX(), pos.getY(), pos.getZ(), color.getX(), color.getY(), color.getZ());
        }
        public static LaserBeam get(float x, float y, float z, float r, float g, float b) {
            LaserBeam result = cache.poll();
            if (result == null)
                return new LaserBeam(new Vec3f(x, y, z), new Vec3f(r, g, b));
            result.startPos.set(x, y, z);
            result.endPos.set(x, y, z);
            result.color.set(r, g, b);
            return result;
        }
        public void clear() {
            for (LaserBeam child : children)
                child.free();
            children.clear();
        }
        private void free() {
            clear();
            cache.offer(this);
        }
    }
}
