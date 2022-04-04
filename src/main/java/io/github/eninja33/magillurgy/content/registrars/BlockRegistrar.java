package io.github.eninja33.magillurgy.content.registrars;

import io.github.eninja33.magillurgy.MagillurgyMod;
import io.github.eninja33.magillurgy.content.lasers.LaserEmitterBlock;
import io.github.eninja33.magillurgy.content.lasers.ReflectorBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public class BlockRegistrar {

    public static ReflectorBlock REFLECTOR_BLOCK = new ReflectorBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque().solidBlock(BlockRegistrar::never));
    public static LaserEmitterBlock LASER_EMITTER_BLOCK = new LaserEmitterBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(MagillurgyMod.MODID, "reflector_block"), REFLECTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(MagillurgyMod.MODID, "laser_emitter_block"), LASER_EMITTER_BLOCK);
    }

    private static boolean never(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }
}
