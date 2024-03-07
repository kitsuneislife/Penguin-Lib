package uk.joshiejack.penguinlib.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.penguinlib.client.renderer.ShadowRenderer;

@Mixin(ItemRenderer.class)
public class PenguinItemRenderer {
    @Inject(method = "getFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void getShadowBuffer(MultiBufferSource buffer, RenderType type, boolean isItem, boolean isShiny, CallbackInfoReturnable<VertexConsumer> cir) {
        if (ShadowRenderer.isEnabled())
            cir.setReturnValue(VertexMultiConsumer.create(buffer.getBuffer(ShadowRenderer.shadow()), buffer.getBuffer(type)));
    }

    @Inject(method = "getFoilBufferDirect", at = @At("HEAD"), cancellable = true)
    private static void getShadowBufferDirect(MultiBufferSource buffer, RenderType type, boolean isItem, boolean isShiny, CallbackInfoReturnable<VertexConsumer> cir) {
        if (ShadowRenderer.isEnabled())
            cir.setReturnValue(VertexMultiConsumer.create(buffer.getBuffer(ShadowRenderer.shadow()), buffer.getBuffer(type)));
    }
}