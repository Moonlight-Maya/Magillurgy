package io.github.eninja33.magillurgy.content.registrars;

import io.github.eninja33.magillurgy.MagillurgyMod;
import io.github.eninja33.magillurgy.content.lasers.LaserEmitterBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockEntityRegistrar {

    public static BlockEntityType<LaserEmitterBlockEntity> LASER_EMITTER;

    public static void register() {
        LASER_EMITTER = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MagillurgyMod.MODID, "laser_emitter"), FabricBlockEntityTypeBuilder.create(LaserEmitterBlockEntity::new, BlockRegistrar.LASER_EMITTER_BLOCK).build());
    }
}
