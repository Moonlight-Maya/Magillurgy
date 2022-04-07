package io.github.eninja33.magillurgy.content.lasers.reflectors;

import io.github.eninja33.magillurgy.content.magic.Resonance;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class SolidReflectorBlock extends AbstractReflectorBlock {

    public static final float MIN_CONTAMINATION = 0.01f;
    public static final float MAX_CONTAMINATION = 0.015f;

    public SolidReflectorBlock(Settings settings) {
        super(settings);
    }

    //LightAffector

    @Override
    public Vec3i reflectMain(BlockState state, Vec3i input) {
        return null;
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
        return 0;
    }
    @Override
    public int strengthSide(BlockState state, Vec3i input, int str) {
        return str;
    }

    @Override
    public void affectResonanceMain(Resonance resonance) {

    }

    @Override
    public void affectResonanceSide(Resonance resonance) {
        resonance.contaminate(MIN_CONTAMINATION, MAX_CONTAMINATION);
    }
}
