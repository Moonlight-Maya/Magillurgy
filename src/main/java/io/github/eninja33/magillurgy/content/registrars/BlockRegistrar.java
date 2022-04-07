package io.github.eninja33.magillurgy.content.registrars;

import io.github.eninja33.magillurgy.MagillurgyMod;
import io.github.eninja33.magillurgy.content.lasers.LaserEmitterBlock;
import io.github.eninja33.magillurgy.content.lasers.reflectors.SolidReflectorBlock;
import io.github.eninja33.magillurgy.content.lasers.reflectors.SplitReflectorBlock;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public class BlockRegistrar {

    public static SolidReflectorBlock SOLID_REFLECTOR_BLOCK = new SolidReflectorBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque().solidBlock(BlockRegistrar::never));
    public static SplitReflectorBlock SPLIT_REFLECTOR_BLOCK = new SplitReflectorBlock(FabricBlockSettings.of(Material.GLASS).strength(2.0f).nonOpaque().solidBlock(BlockRegistrar::never));
    public static LaserEmitterBlock LASER_EMITTER_BLOCK = new LaserEmitterBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(MagillurgyMod.MODID, "solid_reflector_block"), SOLID_REFLECTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(MagillurgyMod.MODID, "split_reflector_block"), SPLIT_REFLECTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(MagillurgyMod.MODID, "laser_emitter_block"), LASER_EMITTER_BLOCK);
    }

    public static void registerClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(SPLIT_REFLECTOR_BLOCK, RenderLayer.getTranslucent());
    }

    private static boolean never(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }
}
