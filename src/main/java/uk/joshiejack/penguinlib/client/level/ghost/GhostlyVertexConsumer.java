package uk.joshiejack.penguinlib.client.level.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.jetbrains.annotations.NotNull;

public class GhostlyVertexConsumer implements VertexConsumer {
    public static final GhostlyVertexConsumer INSTANCE = new GhostlyVertexConsumer();
    private VertexConsumer parent;
    private int offsetX;
    private int offsetY;
    private int offsetZ;

    public static GhostlyVertexConsumer of(VertexConsumer parent, int offsetX, int offsetY, int offsetZ) {
        INSTANCE.parent = parent;
        INSTANCE.offsetX = offsetX;
        INSTANCE.offsetY = offsetY;
        INSTANCE.offsetZ = offsetZ;
        return INSTANCE;
    }

    @Override
    public void putBulkData(PoseStack.@NotNull Pose pPoseEntry, @NotNull BakedQuad pQuad, float @NotNull [] pColorMuls, float pRed, float pGreen, float pBlue, int @NotNull [] pCombinedLights, int pCombinedOverlay, boolean pMulColor) {
        putBulkData(pPoseEntry, pQuad, pColorMuls, pRed, pGreen, pBlue, 0.85f, pCombinedLights, pCombinedOverlay, pMulColor);
    }

    @Override
    public @NotNull VertexConsumer vertex(double pX, double pY, double pZ) {
        return parent.vertex(pX + offsetX, pY + offsetY, pZ + offsetZ);
    }

    @Override
    public @NotNull VertexConsumer color(int pRed, int pGreen, int pBlue, int pAlpha) {
        return parent.color(pRed, pGreen, pBlue, pAlpha);
    }

    @Override
    public @NotNull VertexConsumer uv(float pU, float pV) {
        return parent.uv(pU, pV);
    }

    @Override
    public @NotNull VertexConsumer overlayCoords(int pU, int pV) {
        return parent.overlayCoords(pU, pV);
    }

    @Override
    public @NotNull VertexConsumer uv2(int pU, int pV) {
        return parent.uv2(pU, pV);
    }

    @Override
    public @NotNull VertexConsumer normal(float pX, float pY, float pZ) {
        return parent.normal(pX, pY, pZ);
    }

    @Override
    public void endVertex() {
        parent.endVertex();
    }

    @Override
    public void defaultColor(int pDefaultR, int pDefaultG, int pDefaultB, int pDefaultA) {
        parent.defaultColor(pDefaultR, pDefaultG, pDefaultB, pDefaultA);
    }

    @Override
    public void unsetDefaultColor() {
        parent.unsetDefaultColor();
    }
}
