package io.github.eninja33.magillurgy.content.lasers;

import io.github.eninja33.magillurgy.content.magic.Resonance;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;

public interface LightAffector {
    Vec3i reflectMain(BlockState state, Vec3i input);
    Vec3i reflectSide(BlockState state, Vec3i input);
    int strengthMain(BlockState state, Vec3i input, int str);
    int strengthSide(BlockState state, Vec3i input, int str);
    void affectResonanceMain(Resonance resonance);
    void affectResonanceSide(Resonance resonance);
}
