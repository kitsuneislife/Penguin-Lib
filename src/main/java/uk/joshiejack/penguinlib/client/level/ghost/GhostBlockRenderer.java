package uk.joshiejack.penguinlib.client.level.ghost;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder.RenderedBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A class that renders a map of blocks to the world
 * Mostly borrowed from the structureize mod
 */
public abstract class GhostBlockRenderer<D, T extends GhostBlockGetter<D>> implements AutoCloseable {
    private static final Supplier<Map<RenderType, VertexBuffer>> vertexBufferFactory = () -> RenderType.chunkBufferLayers()
            .stream()
            .collect(Collectors.toMap((type) -> type, (type) -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
    private static final RenderBuffers renderBuffers = new RenderBuffers(0);
    private static final float OFFSET = 0.00390625F;
    protected final T blockGetter;
    private final AABB aabb;
    private Map<RenderType, VertexBuffer> buffers;

    public GhostBlockRenderer(final T blockGetter) {
        this.blockGetter = blockGetter;
        this.aabb = blockGetter.getAABB();
    }

    protected abstract void setupRenderer(SectionBufferBuilderPack newBuffers, BlockRenderDispatcher blockRenderer, PoseStack poseStack, RandomSource random);

    private void setupGhostRenderer() {
        clearVertexBuffers();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        RandomSource random = RandomSource.create();
        PoseStack poseStack = new PoseStack();

        SectionBufferBuilderPack newBuffers = renderBuffers.fixedBufferPack();
        RenderType.chunkBufferLayers().forEach(type -> newBuffers.builder(type).begin(type.mode(), type.format()));
        RenderType[] renderTypes = RenderType.chunkBufferLayers().toArray(RenderType[]::new);
        setupRenderer(newBuffers, blockRenderer, poseStack, random);
        buffers = vertexBufferFactory.get();
        for (final RenderType renderType : renderTypes) {
            RenderedBuffer newBuffer = newBuffers.builder(renderType).endOrDiscardIfEmpty();
            if (newBuffer == null) {
                buffers.remove(renderType);
            } else {
                VertexBuffer vertexBuffer = buffers.get(renderType);
                vertexBuffer.bind();
                vertexBuffer.upload(newBuffer);
            }
        }

        newBuffers.clearAll();
        VertexBuffer.unbind();
    }

    public void draw(BlockPos pos, Frustum frustum, PoseStack poseStack) {
        try {
            if (!isViewable(pos, frustum))
                return;
            drawUnsafe(blockGetter, pos, poseStack);
        } catch (Exception ignored) {
        }
    }

    protected boolean isViewable(BlockPos pos, Frustum frustum) {
        return frustum.isVisible(aabb.move(pos));
    }

    private void drawUnsafe(final T data, final BlockPos pos, PoseStack poseStack) {
        //Init the buffers, if they haven't been already
        if (buffers == null)
            setupGhostRenderer();
        Minecraft mc = Minecraft.getInstance();
        Vec3 viewPosition = mc.gameRenderer.getMainCamera().getPosition();
        Vec3 actualPosition = Vec3.atLowerCornerOf(pos).subtract(viewPosition);
        Vector3f vector3f = actualPosition.toVector3f();
        poseStack.pushPose();
        poseStack.translate(OFFSET, OFFSET, OFFSET);
        poseStack.translate(viewPosition.x(), viewPosition.y(), viewPosition.z());
        Matrix4f matrix4f = poseStack.last().pose();
        Lighting.setupLevel(matrix4f);
        renderBlockLayer(RenderType.solid(), matrix4f, vector3f);
        mc.getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).setBlurMipmap(false, mc.options.mipmapLevels().get() > 0);
        renderBlockLayer(RenderType.cutoutMipped(), matrix4f, vector3f);
        mc.getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).restoreLastBlurMipmap();
        renderBlockLayer(RenderType.cutout(), matrix4f, vector3f);
        MultiBufferSource.BufferSource bufferSource = renderBuffers.bufferSource();
        bufferSource.endLastBatch();
        bufferSource.endBatch(RenderType.entitySolid(InventoryMenu.BLOCK_ATLAS));
        bufferSource.endBatch(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
        bufferSource.endBatch(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS));
        bufferSource.endBatch(RenderType.entitySmoothCutout(InventoryMenu.BLOCK_ATLAS));
        bufferSource.endBatch(RenderType.solid());
        bufferSource.endBatch(RenderType.endPortal());
        bufferSource.endBatch(RenderType.endGateway());
        bufferSource.endBatch(Sheets.solidBlockSheet());
        bufferSource.endBatch(Sheets.cutoutBlockSheet());
        bufferSource.endBatch(Sheets.bedSheet());
        bufferSource.endBatch(Sheets.shulkerBoxSheet());
        bufferSource.endBatch(Sheets.signSheet());
        bufferSource.endBatch(Sheets.hangingSignSheet());
        bufferSource.endBatch(Sheets.chestSheet());
        renderBuffers.outlineBufferSource().endOutlineBatch();
        bufferSource.endLastBatch();
        bufferSource.endBatch(Sheets.translucentCullBlockSheet());
        bufferSource.endBatch(Sheets.bannerSheet());
        bufferSource.endBatch(Sheets.shieldSheet());
        bufferSource.endBatch(RenderType.armorGlint());
        bufferSource.endBatch(RenderType.armorEntityGlint());
        bufferSource.endBatch(RenderType.glint());
        bufferSource.endBatch(RenderType.glintDirect());
        bufferSource.endBatch(RenderType.glintTranslucent());
        bufferSource.endBatch(RenderType.entityGlint());
        bufferSource.endBatch(RenderType.entityGlintDirect());
        bufferSource.endBatch(RenderType.waterMask());
        renderBuffers.crumblingBufferSource().endBatch();
        renderBlockLayer(RenderType.translucent(), matrix4f, vector3f);
        bufferSource.endBatch(RenderType.lines());
        bufferSource.endBatch();
        renderBlockLayer(RenderType.tripwire(), matrix4f, vector3f);
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupLevel(poseStack.last().pose());
    }

    private void clearVertexBuffers() {
        if (buffers != null) {
            buffers.values().forEach(VertexBuffer::close);
            buffers = null;
        }
    }

    @Override
    public void close() {
        clearVertexBuffers();
    }

    protected void addRender(SectionBufferBuilderPack newBuffers, BlockRenderDispatcher blockRenderer, PoseStack poseStack, BlockState state, BlockPos pos, RandomSource random) {
        final FluidState fluidState = state.getFluidState();
        try {
            if (!fluidState.isEmpty()) {
                RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluidState);
                int chunkOffsetX = pos.getX() - (pos.getX() & 15);
                int chunkOffsetY = pos.getY() - (pos.getY() & 15);
                int chunkOffsetZ = pos.getZ() - (pos.getZ() & 15);
                VertexConsumer buffer = GhostlyVertexConsumer.of(newBuffers.builder(renderType), chunkOffsetX, chunkOffsetY, chunkOffsetZ);
                blockRenderer.renderLiquid(pos, blockGetter, buffer, state, fluidState);
            }

            //SectionRenderDispatcher
            if (state.getRenderShape() != RenderShape.INVISIBLE) {
                BakedModel model = getBlockModel(blockRenderer, state);
                ModelData modelData = model.getModelData(blockGetter, pos, state, ModelData.EMPTY);
                //Render the blocks in each render type?
                poseStack.pushPose();
                poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
                for (RenderType renderType : model.getRenderTypes(state, random, modelData)) {
                    renderBatched(blockRenderer, state, pos, blockGetter, poseStack, newBuffers.builder(renderType), random, modelData, renderType);
                    renderType.clearRenderState();
                }

                poseStack.popPose();
            }
        } catch (final Exception ignored) {
        }
    }

    private void renderBlockLayer(RenderType renderType, Matrix4f projectionMatrix, Vector3f realRenderRootPos) {
        VertexBuffer vertexBuffer = buffers.get(renderType);
        if (vertexBuffer == null)
            return;

        renderType.setupRenderState();
        ShaderInstance shaderinstance = RenderSystem.getShader();

        for (int i = 0; i < 12; ++i)
            shaderinstance.setSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
        if (shaderinstance.MODEL_VIEW_MATRIX != null)
            shaderinstance.MODEL_VIEW_MATRIX.set(projectionMatrix);
        if (shaderinstance.PROJECTION_MATRIX != null)
            shaderinstance.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
        if (shaderinstance.COLOR_MODULATOR != null)
            shaderinstance.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        if (shaderinstance.GLINT_ALPHA != null)
            shaderinstance.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
        if (shaderinstance.FOG_START != null)
            shaderinstance.FOG_START.set(RenderSystem.getShaderFogStart());
        if (shaderinstance.FOG_END != null)
            shaderinstance.FOG_END.set(RenderSystem.getShaderFogEnd());
        if (shaderinstance.FOG_COLOR != null)
            shaderinstance.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        if (shaderinstance.FOG_SHAPE != null)
            shaderinstance.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        if (shaderinstance.TEXTURE_MATRIX != null)
            shaderinstance.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        if (shaderinstance.GAME_TIME != null)
            shaderinstance.GAME_TIME.set(RenderSystem.getShaderGameTime());

        RenderSystem.setupShaderLights(shaderinstance);
        shaderinstance.apply();

        Uniform uniform = shaderinstance.CHUNK_OFFSET;
        if (uniform != null) {
            uniform.set(realRenderRootPos);
            uniform.upload();
        }

        vertexBuffer.bind();
        vertexBuffer.draw();

        if (uniform != null)
            uniform.set(new Vector3f(0, 0, 0));
        shaderinstance.clear();
        VertexBuffer.unbind();
        renderType.clearRenderState();
    }

    protected BakedModel getBlockModel(BlockRenderDispatcher blockRenderer, BlockState state) {
        return blockRenderer.getBlockModel(state);
    }

    //Rework the renderBatched Method to allow for a custom baked model
    private void renderBatched(BlockRenderDispatcher blockRenderer, BlockState state, BlockPos pos, BlockAndTintGetter getter, PoseStack poseStack,
                               VertexConsumer vertexConsumer, RandomSource random, ModelData data, RenderType renderType) {
        try {
            RenderShape rendershape = state.getRenderShape();
            if (rendershape == RenderShape.MODEL) {
                blockRenderer.getModelRenderer()
                        .tesselateBlock(getter, getBlockModel(blockRenderer, state), state, pos, poseStack, vertexConsumer, true, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY, data, renderType);
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, getter, pos, state);
            throw new ReportedException(crashreport);
        }
    }
}
