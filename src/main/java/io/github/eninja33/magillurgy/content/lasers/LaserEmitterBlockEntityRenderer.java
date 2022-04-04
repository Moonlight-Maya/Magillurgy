package io.github.eninja33.magillurgy.content.lasers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;

public class LaserEmitterBlockEntityRenderer implements BlockEntityRenderer<LaserEmitterBlockEntity> {

    private static float OFFSET = 1.0f/32;

    public LaserEmitterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        //do nothing i guess? that's what the thing does anyway
    }

    @Override
    public void render(LaserEmitterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
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
            for (int i = 0; i < laserBeam.numNodes-1; i++) {
                LaserEmitterBlockEntity.BeamNode node = laserBeam.beamNodes.get(i);
                Vec3f start = node.pos();
                Vec3f end = laserBeam.beamNodes.get(i+1).pos();
                Vec3f color = node.color();
                drawLine(matrices, buffer, start, end, color);
            }
            matrices.pop();

            //Draw and reset rendering state
            tessellator.draw();
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
        }
    }

    private void drawLine(MatrixStack matrices, BufferBuilder buffer, Vec3f start, Vec3f end, Vec3f color) {
        //Create some handy vectors to help us draw the laser
        Vec3f diff = end.copy();
        diff.subtract(start);

        Vec3f normal;
        if (diff.getX() == 0 && diff.getZ() == 0)
            normal = new Vec3f(1,0,0);
        else
            normal = new Vec3f(-diff.getZ(), 0, diff.getX());

        Vec3f binormal = diff.copy();
        binormal.cross(normal);

        normal.normalize();
        normal.scale(OFFSET);
        binormal.normalize();
        binormal.scale(OFFSET);

        //Create the 8 vertices of the beam segment
        Vec3f p0 = start.copy();
        p0.subtract(normal);
        p0.subtract(binormal);
        Vec3f p1 = start.copy();
        p1.subtract(normal);
        p1.add(binormal);
        Vec3f p2 = start.copy();
        p2.add(normal);
        p2.subtract(binormal);
        Vec3f p3 = start.copy();
        p3.add(normal);
        p3.add(binormal);
        Vec3f p4 = end.copy();
        p4.subtract(normal);
        p4.subtract(binormal);
        Vec3f p5 = end.copy();
        p5.subtract(normal);
        p5.add(binormal);
        Vec3f p6 = end.copy();
        p6.add(normal);
        p6.subtract(binormal);
        Vec3f p7 = end.copy();
        p7.add(normal);
        p7.add(binormal);

        Matrix4f posMat = matrices.peek().getPositionMatrix();
        float r = color.getX();
        float g = color.getY();
        float b = color.getZ();

        //Chuck all those vertices into the buffer to make a nice colored beam
        buffer.vertex(posMat, p4.getX(), p4.getY(), p4.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p0.getX(), p0.getY(), p0.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p2.getX(), p2.getY(), p2.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p6.getX(), p6.getY(), p6.getZ()).color(r, g, b, 1).next();

        buffer.vertex(posMat, p6.getX(), p6.getY(), p6.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p2.getX(), p2.getY(), p2.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p3.getX(), p3.getY(), p3.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p7.getX(), p7.getY(), p7.getZ()).color(r, g, b, 1).next();

        buffer.vertex(posMat, p7.getX(), p7.getY(), p7.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p3.getX(), p3.getY(), p3.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p1.getX(), p1.getY(), p1.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p5.getX(), p5.getY(), p5.getZ()).color(r, g, b, 1).next();

        buffer.vertex(posMat, p5.getX(), p5.getY(), p5.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p1.getX(), p1.getY(), p1.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p0.getX(), p0.getY(), p0.getZ()).color(r, g, b, 1).next();
        buffer.vertex(posMat, p4.getX(), p4.getY(), p4.getZ()).color(r, g, b, 1).next();
    }
}
