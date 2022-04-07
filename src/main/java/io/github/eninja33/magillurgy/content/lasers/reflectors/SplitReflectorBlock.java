package io.github.eninja33.magillurgy.content.lasers.reflectors;

import io.github.eninja33.magillurgy.content.magic.Resonance;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class SplitReflectorBlock extends AbstractReflectorBlock {

    public static final float MIN_CONTAMINATION = 0.005f;
    public static final float MAX_CONTAMINATION = 0.01f;

    public SplitReflectorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Vec3i reflectMain(BlockState state, Vec3i input) {
        return input;
    }

    @Override
    public Vec3i reflectSide(BlockState state, Vec3i input) {
        Vec3d normal = getNormal(state);
        Vec3d in = new Vec3d(input.getX(), input.getY(), input.getZ());
        in = in.subtract(normal.multiply(2*in.dotProduct(normal)));
        return new Vec3i(Math.round(in.x), Math.round(in.y), Math.round(in.z));
    }

    @Override
    public int strengthMain(BlockState state, Vec3i input, int str) {
        return str / 2;
    }

    @Override
    public int strengthSide(BlockState state, Vec3i input, int str) {
        return str / 2;
    }

    @Override
    public void affectResonanceMain(Resonance resonance) {
        resonance.contaminate(MIN_CONTAMINATION, MAX_CONTAMINATION);
    }

    @Override
    public void affectResonanceSide(Resonance resonance) {
        resonance.contaminate(MIN_CONTAMINATION, MAX_CONTAMINATION);
    }
}
