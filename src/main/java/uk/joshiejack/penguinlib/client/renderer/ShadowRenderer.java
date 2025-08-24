package uk.joshiejack.penguinlib.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.PenguinLib;

import javax.annotation.Nullable;
import java.io.IOException;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
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

    // RenderLevelStageEvent handler moved to inner Renderer class which is configured for FORGE bus

    @Mod.EventBusSubscriber(modid = PenguinLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public static class Renderer {
        @SubscribeEvent
        public static void postRender(RenderLevelStageEvent event) { //Clean the batch render
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                // For Forge 1.20.1, renderBuffers access might be different
                // This will need to be adapted to the proper rendering system
                // event.getLevelRenderer().renderBuffers.bufferSource().endBatch(SHADOW);
            }
        }
    }
}