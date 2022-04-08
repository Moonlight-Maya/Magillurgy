package io.github.eninja33.magillurgy.content.lasers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class LaserEmitterBlockEntityRenderer implements BlockEntityRenderer<LaserEmitterBlockEntity> {

    private static float OFFSET = 1.0f/32;

    public LaserEmitterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        //do nothing i guess? that's what the thing does anyway
    }

    @Override
    public void render(LaserEmitterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        synchronized (entity) {
            LaserEmitterBlockEntity.LaserBeam laserBeam = entity.getLaserBeam();
            if (laserBeam != null) {
                //Setup rendering before pushing vertices
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                RenderSystem.enableDepthTest();
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                //Push all vertices
                matrices.push();
                matrices.translate(0.5, 0.5, 0.5);
                laserBeam.render(matrices, buffer);
                matrices.pop();

                //Draw and reset rendering state
                tessellator.draw();
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
            }
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(LaserEmitterBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    @Override
    public boolean isInRenderDistance(LaserEmitterBlockEntity blockEntity, Vec3d vec3d) {
        return Vec3d.ofCenter(blockEntity.getPos()).multiply(1.0, 0.0, 1.0).isInRange(vec3d.multiply(1.0, 0.0, 1.0), this.getRenderDistance());
    }
}
