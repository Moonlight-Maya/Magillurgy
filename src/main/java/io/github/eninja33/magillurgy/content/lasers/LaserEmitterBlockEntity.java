package io.github.eninja33.magillurgy.content.lasers;

import io.github.eninja33.magillurgy.content.registrars.BlockEntityRegistrar;
import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.block.StainedGlassPaneBlock;
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

    private LaserBeam laserBeam = LaserBeam.get(new Vec3f(pos.getX(), pos.getY(), pos.getZ()), WHITE); //Keeps track of all laser reflections and things

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
            synchronized (be) {
                be.laserBeam.free();
                be.laserBeam = LaserBeam.get(pos.getX(), pos.getY(), pos.getZ(), WHITE.getX(), WHITE.getY(), WHITE.getZ());
                be.laserBeam.update(world, pos);
            }
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
        private boolean freed = false;

        private LaserBeam(Vec3f startPos, Vec3f color) {
            this.startPos = startPos.copy();
            this.endPos = startPos.copy();
            this.color = color.copy();
        }

        public void update(World world, BlockPos pos) {
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
                    startPos.getX()+0.5+dir.getX(),
                    startPos.getY()+0.5+dir.getY(),
                    startPos.getZ()+0.5+dir.getZ()
            );
            BlockState state = world.getBlockState(curPos);
            boolean shouldSetPosAtEnd = true;
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
                    float f = stainable instanceof StainedGlassPaneBlock ? 16.001f : 2.001f;
                    float[] comp = stainable.getColor().getColorComponents();

                    float nx = curPos.getX()-dir.getX()/f;
                    float ny = curPos.getY()-dir.getY()/f;
                    float nz = curPos.getZ()-dir.getZ()/f;
                    LaserBeam newBeam = get(nx, ny, nz, comp[0], comp[1], comp[2]);
                    newBeam.recurse(world, dir, strength-1);
                    children.add(newBeam);
                    endPos.set(nx, ny, nz);
                    shouldSetPosAtEnd = false;
                    break;
                }
                curPos.move(dir);
                strength--;
                state = world.getBlockState(curPos);
            }
            if (shouldSetPosAtEnd)
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

        //Static, reusable instances for rendering to avoid allocations every frame
        private static final Vec3f
                diff = new Vec3f(),
                normal = new Vec3f(),
                binormal = new Vec3f(),
                p0 = new Vec3f(),
                p1 = new Vec3f(),
                p2 = new Vec3f(),
                p3 = new Vec3f(),
                p4 = new Vec3f(),
                p5 = new Vec3f(),
                p6 = new Vec3f(),
                p7 = new Vec3f();
        public void render(MatrixStack matrices, BufferBuilder buffer) {
            //Initialize our handy vectors to help draw the laser
            diff.set(endPos);
            diff.subtract(startPos);

            if (diff.getX() == 0 && diff.getZ() == 0)
                normal.set(1, 0, 0);
            else
                normal.set(-diff.getZ(), 0, diff.getX());

            binormal.set(diff);
            binormal.cross(normal);

            normal.normalize();
            normal.scale(OFFSET);
            binormal.normalize();
            binormal.scale(OFFSET);

            //Create the 8 vertices of the beam segment
            p0.set(startPos);
            p0.subtract(normal);
            p0.subtract(binormal);
            p1.set(startPos);
            p1.subtract(normal);
            p1.add(binormal);
            p2.set(startPos);
            p2.add(normal);
            p2.subtract(binormal);
            p3.set(startPos);
            p3.add(normal);
            p3.add(binormal);
            p4.set(endPos);
            p4.subtract(normal);
            p4.subtract(binormal);
            p5.set(endPos);
            p5.subtract(normal);
            p5.add(binormal);
            p6.set(endPos);
            p6.add(normal);
            p6.subtract(binormal);
            p7.set(endPos);
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
            LaserBeam result;
            synchronized (cache) {
                result = cache.poll();
            }
            if (result == null)
                return new LaserBeam(new Vec3f(x, y, z), new Vec3f(r, g, b));
            if (!result.freed) {
                System.out.println("This result wasn't freed, but we still got it from cache!");
            }
            result.startPos.set(x, y, z);
            result.endPos.set(x, y, z);
            result.color.set(r, g, b);
            result.freed = false;
            return result;
        }
        private void free() {
            for (LaserBeam child : children)
                child.free();
            children.clear();
            freed = true;
            synchronized (cache) {
                cache.offer(this);
            }
        }
    }
}
