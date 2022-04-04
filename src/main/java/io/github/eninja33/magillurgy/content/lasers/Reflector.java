package io.github.eninja33.magillurgy.content.lasers;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;

public interface Reflector {
    Vec3i reflect(BlockState state, Vec3i input);
}
