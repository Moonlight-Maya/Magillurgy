package io.github.eninja33.magillurgy.content.registrars;

import io.github.eninja33.magillurgy.MagillurgyMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistrar {

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(MagillurgyMod.MODID, "solid_reflector_block"), new BlockItem(BlockRegistrar.SOLID_REFLECTOR_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE)));
        Registry.register(Registry.ITEM, new Identifier(MagillurgyMod.MODID, "split_reflector_block"), new BlockItem(BlockRegistrar.SPLIT_REFLECTOR_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE)));
        Registry.register(Registry.ITEM, new Identifier(MagillurgyMod.MODID, "laser_emitter_block"), new BlockItem(BlockRegistrar.LASER_EMITTER_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE)));
    }
}
