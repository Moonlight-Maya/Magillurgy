package io.github.eninja33.magillurgy.content.registrars;

import io.github.eninja33.magillurgy.content.lasers.LaserEmitterBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class BlockEntityRendererRegistrar {

    public static void register() {
        BlockEntityRendererRegistry.register(BlockEntityRegistrar.LASER_EMITTER, LaserEmitterBlockEntityRenderer::new);
    }

}
