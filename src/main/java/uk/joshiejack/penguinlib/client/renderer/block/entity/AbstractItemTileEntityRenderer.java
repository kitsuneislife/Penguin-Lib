package uk.joshiejack.penguinlib.client.renderer.block.entity;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import uk.joshiejack.penguinlib.client.ClientResources;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractItemTileEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private static final Supplier<ItemStack> STICK = () -> new ItemStack(Items.STICK);
    private static final Consumer<PoseStack> DEFAULT = (matrixStack -> matrixStack.scale(0.5F, 0.5F, 0.5F));
    private static ItemStack stack;

    public AbstractItemTileEntityRenderer(BlockEntityRendererProvider.Context context) {
        //TODO? super(dispatcher);
    }

    @Nonnull
    protected ItemStack getStick() {
        return stack == null ? stack = STICK.get() : stack;
    }

    protected void renderSpeechBubble(@Nonnull ItemStack stack, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        renderSpeechBubble(stack, matrix, buffer, combinedLightIn, combinedOverlayIn, null);
    }

    protected void renderSpeechBubble(@Nonnull ItemStack stack, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn, @Nullable Consumer<PoseStack> transforms) {
        matrix.pushPose();
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer renderer = mc.getItemRenderer();
        matrix.translate(0.5D, getYOffset(), 0.5D);
        matrix.scale(0.75F, 0.75F, 0.75F);
        matrix.pushPose();
        matrix.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        matrix.mulPose(Axis.YP.rotationDegrees(180.0F));
        ////TODO: TEST?Lighting.turnOff();
        Lighting.setupForFlatItems();
        matrix.pushPose();
        if (transforms != null) transforms.accept(matrix);
        matrix.scale(0.75F, 0.75F, 0.01F);
        BakedModel model = renderer.getModel(stack, mc.level, mc.player, combinedLightIn); //TODO: Uhh random not combinerd light in?
        renderer.render(stack, ItemDisplayContext.GUI, true, matrix, buffer, combinedLightIn, combinedOverlayIn, model);
        matrix.pushPose();
        matrix.scale(1.5F, 1.5F, 1.5F);
        matrix.translate(0F, -0.05F, -0.175F);
        BakedModel model2 = Minecraft.getInstance().getModelManager().getModel(ClientResources.SPEECH_BUBBLE);
        renderer.render(getStick(), ItemDisplayContext.GUI, true, matrix, buffer, combinedLightIn, combinedOverlayIn, model2);
        matrix.popPose();
        matrix.popPose();
        Lighting.setupFor3DItems();
        //Lighting.turnBackOn();
        matrix.popPose();
        matrix.popPose();
    }

    protected void renderItem(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int combinedLightIn) {
        renderItem(stack, matrix, buffer, combinedLightIn, DEFAULT);
    }

    protected void renderItem(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int combinedLightIn, Consumer<PoseStack> transforms) {
        if (!stack.isEmpty()) {
            matrix.pushPose();
            transforms.accept(matrix);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrix, buffer, Minecraft.getInstance().level, combinedLightIn); //TODO: Should be random not combined light in?
            matrix.popPose();
        }
    }

    protected double getYOffset() {
        return 1.5D;
    }
    /*
    protected void translateItem(boolean isBlock, float position, float rotation, float offset1, float offset2) {
        GlStateManager.translate(0.5F, 0.05F, 0.5F);
        GlStateManager.rotate(-15, 0F, 1F, 0F);
        GlStateManager.scale(0.25F, 0.25F, 0.25F);
        if (!isBlock) {
            GlStateManager.rotate(rotation, 0F, 1F, 0F);
            GlStateManager.rotate(-190, 0F, 1F, 0F);
            GlStateManager.translate(offset1 * 3F, offset2 * 3.5F, position * 0.75F);
        } else {
            GlStateManager.rotate(90, 1F, 0F, 0F);
            GlStateManager.translate(offset1 * 1.4F, 0.8F - offset2 * 2.5F, position - 1F);
        }
    }

    protected void renderItem(ItemStack stack, SpecialRenderData render, int i) {
        renderItem(stack, () -> translateItem(stack.getItem() instanceof ItemBlock, render.heightOffset[i], render.rotations[i], render.offset1[i], render.offset2[i]));
    }

    protected void renderItem(@Nonnull ItemStack stack, Runnable... r) {
        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (Minecraft.isAmbientOcclusionEnabled())  {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else  {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        for (Runnable r2: r) {
            r2.run(); //Execute the translations
        }

        GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        GL14.glBlendColor(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, FIXED);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    protected void renderGuiTexture(Direction facing, TextureAtlasSprite sprite, float x, float y, float z, float size, int texUMin, int texVMin, int uWidth, int vHeight) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        if (sprite != null) {
            bindTexture(LOCATION_BLOCKS_TEXTURE);
            double uMin = (double) sprite.getMinU();
            double uMax = (double) sprite.getMaxU();
            double vMin = (double) sprite.getMinV();
            double vMax = (double) sprite.getMaxV();

            if (facing == EnumFacing.NORTH) {
                GlStateManager.rotate(90F, 1F, 0F, 0F);
                GlStateManager.rotate(180F, 0F, 1F, 0F);
                vb.begin(7, POSITION_TEX);
                vb.pos(size / 2f, -size / 2f, -size / 2f).tex(uMax, vMax).endVertex();//Top Right
                vb.pos(size / 2f, -size / 2f, size / 2f).tex(uMax, vMin).endVertex(); //Top Left
                vb.pos(-size / 2f, -size / 2f, size / 2f).tex(uMin, vMin).endVertex(); //Bottom Left
                vb.pos(-size / 2f, -size / 2f, -size / 2f).tex(uMin, vMax).endVertex(); //Bottom Right
                tessellator.draw();
            } else if (facing == EnumFacing.SOUTH) {
                GlStateManager.rotate(90F, 1F, 0F, 0F);
                vb.begin(7, POSITION_TEX);
                vb.pos(size / 2f, 0, size / 2f).tex(uMax, vMax).endVertex();
                vb.pos(size / 2f, 0, -size / 2f).tex(uMax, vMin).endVertex();
                vb.pos(-size / 2f, 0, -size / 2f).tex(uMin, vMin).endVertex();
                vb.pos(-size / 2f, 0, size / 2f).tex(uMin, vMax).endVertex();
                tessellator.draw();
            } else if (facing == EnumFacing.WEST) {
                GlStateManager.rotate(90F, 1F, 0F, 0F);
                GlStateManager.rotate(90F, 0F, 0F, 1F);
                vb.begin(7, POSITION_TEX);
                vb.pos(size / 2f, 0, size / 2f).tex(uMax, vMax).endVertex();
                vb.pos(size / 2f, 0, -size / 2f).tex(uMax, vMin).endVertex();
                vb.pos(-size / 2f, 0, -size / 2f).tex(uMin, vMin).endVertex();
                vb.pos(-size / 2f, 0, size / 2f).tex(uMin, vMax).endVertex();
                tessellator.draw();
            } else {
                GlStateManager.rotate(180F, 0F, 0F, 1F);
                GlStateManager.rotate(90F, 1F, 0F, 0F);
                GlStateManager.rotate(270F, 0F, 0F, 1F);
                vb.begin(7, POSITION_TEX);
                vb.pos(size / 2f, -size / 2f, -size / 2f).tex(uMax, vMax).endVertex();//Top Right
                vb.pos(size / 2f, -size / 2f, size / 2f).tex(uMax, vMin).endVertex(); //Top Left
                vb.pos(-size / 2f, -size / 2f, size / 2f).tex(uMin, vMin).endVertex(); //Bottom Left
                vb.pos(-size / 2f, -size / 2f, -size / 2f).tex(uMin, vMax).endVertex(); //Bottom
                tessellator.draw();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    protected void renderFluidCube(ResourceLocation fluid, float x, float y, float z, float size) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.toString());
        if (sprite != null) {
            bindTexture(LOCATION_BLOCKS_TEXTURE);
            double uMin = (double) sprite.getMinU();
            double uMax = (double) sprite.getMaxU();
            double vMin = (double) sprite.getMinV();
            double vMax = (double) sprite.getMaxV();

            //Draw Top
            //
            vb.begin(7, POSITION_TEX);
            vb.pos(size / 2f, 0, size / 2f).tex(uMax, vMax).endVertex();
            vb.pos(size / 2f, 0, -size / 2f).tex(uMax, vMin).endVertex();
            vb.pos(-size / 2f, 0, -size / 2f).tex(uMin, vMin).endVertex();
            vb.pos(-size / 2f, 0, size / 2f).tex(uMin, vMax).endVertex();
            tessellator.draw();

            //Draw Bottom
            vb.begin(7, POSITION_TEX);
            vb.pos(size / 2f, -size / 2f, -size / 2f).tex(uMax, vMax).endVertex();//Top Right
            vb.pos(size / 2f, -size / 2f, size / 2f).tex(uMax, vMin).endVertex(); //Top Left
            vb.pos(-size / 2f, -size / 2f, size / 2f).tex(uMin, vMin).endVertex(); //Bottom Left
            vb.pos(-size / 2f, -size / 2f, -size / 2f).tex(uMin, vMax).endVertex(); //Bottom Right
            tessellator.draw();

            //Draw Side 1
            vb.begin(7, POSITION_TEX);
            vb.pos(-size / 2f, 0, size / 2f).tex(uMax, vMax).endVertex();
            vb.pos(-size / 2f, 0, -size / 2f).tex(uMax, vMin).endVertex();
            vb.pos(-size / 2f, -size / 2f, -size / 2f).tex(uMin, vMin).endVertex();
            vb.pos(-size / 2f, -size / 2f, size / 2f).tex(uMin, vMax).endVertex();
            tessellator.draw();

            //Draw Side 2
            vb.begin(7, POSITION_TEX);
            vb.pos(size / 2f, 0, -size / 2f).tex(uMax, vMax).endVertex();
            vb.pos(size / 2f, 0, size / 2f).tex(uMax, vMin).endVertex();
            vb.pos(size / 2f, -size / 2f, size / 2f).tex(uMin, vMin).endVertex();
            vb.pos(size / 2f, -size / 2f, -size / 2f).tex(uMin, vMax).endVertex();
            tessellator.draw();

            //Draw Side 3
            vb.begin(7, POSITION_TEX);
            vb.pos(size / 2f, 0, size / 2f).tex(uMax, vMax).endVertex(); // Top Right
            vb.pos(-size / 2f, 0, size / 2f).tex(uMax, vMin).endVertex(); //Top Left
            vb.pos(-size / 2f, -size / 2f, size / 2f).tex(uMin, vMin).endVertex(); //Bottom Left
            vb.pos(size / 2f, -size / 2f, size / 2f).tex(uMin, vMax).endVertex(); //Bottom Right
            tessellator.draw();

            //Draw Side 2
            vb.begin(7, POSITION_TEX);
            vb.pos(-size / 2f, 0, -size / 2f).tex(uMax, vMax).endVertex(); //Top Right
            vb.pos(size / 2f, 0, -size / 2f).tex(uMax, vMin).endVertex(); //Top Left
            vb.pos(size / 2f, -size / 2f, -size / 2f).tex(uMin, vMin).endVertex(); //Bottom Left
            vb.pos(-size / 2f, -size / 2f, -size / 2f).tex(uMin, vMax).endVertex(); //Bottom Right
            tessellator.draw();
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    @SuppressWarnings("ConstantConditions")
    protected void renderFluidPlane(ResourceLocation fluid, float x, float y, float z, float width, float length) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.toString());
        if (sprite != null) {
            bindTexture(LOCATION_BLOCKS_TEXTURE);
            double uMin = (double) sprite.getMinU();
            double uMax = (double) sprite.getMaxU();
            double vMin = (double) sprite.getMinV();
            double vMax = (double) sprite.getMaxV();

            vb.begin(7, POSITION_TEX);
            vb.pos(width / 2f, 0, length / 2f).tex(uMax, vMax).endVertex();
            vb.pos(width / 2f, 0, -length / 2f).tex(uMax, vMin).endVertex();
            vb.pos(-width / 2f, 0, -length / 2f).tex(uMin, vMin).endVertex();
            vb.pos(-width / 2f, 0, length / 2f).tex(uMin, vMax).endVertex();
            tessellator.draw();
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    } */
}
