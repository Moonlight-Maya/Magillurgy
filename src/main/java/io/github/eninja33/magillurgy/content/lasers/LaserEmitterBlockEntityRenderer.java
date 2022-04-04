package io.github.eninja33.magillurgy.content.lasers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;

public class LaserEmitterBlockEntityRenderer implements BlockEntityRenderer<LaserEmitterBlockEntity> {

    private static double OFFSET = 1.0/32;

    public LaserEmitterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        //do nothing i guess? that's what the thing does anyway
    }

    @Override
    public void render(LaserEmitterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        Vec3d prevPos = Vec3d.ZERO;
        if (entity.getLasers() != null) {
            for (Vec3d pos : entity.getLasers()) {
                matrices.push();
                matrices.translate(0.5, 0.5, 0.5);
                drawLine(matrices, buffer, prevPos, pos);
                matrices.pop();
                prevPos = pos;
            }
        }

        tessellator.draw();
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }

    private void drawLine(MatrixStack matrices, BufferBuilder buffer, Vec3d start, Vec3d end) {
        Vec3d diff = end.subtract(start);
        Vec3d normal = new Vec3d(-diff.getZ(), 0, diff.getX());
        if (normal.x == 0 && normal.y == 0 && normal.z == 0)
            normal = new Vec3d(1,0,0);
        Vec3d binormal = diff.crossProduct(normal);
        normal = normal.multiply(OFFSET / normal.length());
        binormal = binormal.multiply(OFFSET / binormal.length());

        Vec3d p0 = start.subtract(normal).subtract(binormal);
        Vec3d p1 = start.subtract(normal).add(binormal);
        Vec3d p2 = start.add(normal).subtract(binormal);
        Vec3d p3 = start.add(normal).add(binormal);
        Vec3d p4 = end.subtract(normal).subtract(binormal);
        Vec3d p5 = end.subtract(normal).add(binormal);
        Vec3d p6 = end.add(normal).subtract(binormal);
        Vec3d p7 = end.add(normal).add(binormal);

        Matrix4f posMat = matrices.peek().getPositionMatrix();

        buffer.vertex(posMat, (float) p4.x, (float) p4.y, (float) p4.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p0.x, (float) p0.y, (float) p0.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p2.x, (float) p2.y, (float) p2.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p6.x, (float) p6.y, (float) p6.z).color(255, 0, 0, 255).next();

        buffer.vertex(posMat, (float) p6.x, (float) p6.y, (float) p6.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p2.x, (float) p2.y, (float) p2.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p3.x, (float) p3.y, (float) p3.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p7.x, (float) p7.y, (float) p7.z).color(255, 0, 0, 255).next();

        buffer.vertex(posMat, (float) p7.x, (float) p7.y, (float) p7.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p3.x, (float) p3.y, (float) p3.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p1.x, (float) p1.y, (float) p1.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p5.x, (float) p5.y, (float) p5.z).color(255, 0, 0, 255).next();

        buffer.vertex(posMat, (float) p5.x, (float) p5.y, (float) p5.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p1.x, (float) p1.y, (float) p1.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p0.x, (float) p0.y, (float) p0.z).color(255, 0, 0, 255).next();
        buffer.vertex(posMat, (float) p4.x, (float) p4.y, (float) p4.z).color(255, 0, 0, 255).next();
    }
}
