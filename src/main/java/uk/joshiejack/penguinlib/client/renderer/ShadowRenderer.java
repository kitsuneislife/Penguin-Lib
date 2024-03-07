package uk.joshiejack.penguinlib.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import uk.joshiejack.penguinlib.PenguinLib;

import javax.annotation.Nullable;
import java.io.IOException;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ShadowRenderer extends RenderStateShard {
    @Nullable
    private static ShaderInstance shadowShader;

    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_SHADOW_SHADER = new RenderStateShard.ShaderStateShard(() -> shadowShader);
    private static final ResourceLocation SHADOW_TEXTURE_LOCATION = new ResourceLocation(PenguinLib.MODID, "textures/misc/shadow.png");
    private static final RenderType SHADOW = RenderType.create("shadow", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS,
            256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_SHADOW_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(SHADOW_TEXTURE_LOCATION, true, false))
                    .setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST)
                    .createCompositeState(false));

    private static boolean enabled;

    public ShadowRenderer(String name, Runnable r1, Runnable r2) {
        super(name, r1, r2);
    }

    public static RenderType shadow() {
        return SHADOW;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void registerShaderEvent(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(PenguinLib.MODID, "rendertype_shadow"), DefaultVertexFormat.POSITION_TEX),
                shaderInstance -> shadowShader = shaderInstance);
    }

    @SubscribeEvent
    public static void registerRenderType(RegisterRenderBuffersEvent event) {
        event.registerRenderBuffer(SHADOW); //Register my shadow buffer
    }

    @Mod.EventBusSubscriber(modid = PenguinLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class Renderer {
        @SubscribeEvent
        public static void postRender(RenderLevelStageEvent event) { //Clean the batch render
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES)
                event.getLevelRenderer().renderBuffers.bufferSource().endBatch(SHADOW);
        }
    }
}