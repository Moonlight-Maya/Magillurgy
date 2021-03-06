package io.github.eninja33.magillurgy;

import io.github.eninja33.magillurgy.content.registrars.BlockEntityRendererRegistrar;
import io.github.eninja33.magillurgy.content.registrars.BlockRegistrar;
import net.fabricmc.api.ClientModInitializer;

public class MagillurgyModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistrar.register();
        BlockRegistrar.registerClient();
    }
}
