package uk.joshiejack.penguinlib.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.world.block.entity.machine.MachineBlockEntity;

public class AbstractMachineBlockEntityRenderer <T extends MachineBlockEntity> extends AbstractItemTileEntityRenderer<T> {
    public AbstractMachineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull T machine, float pPartialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay) {
        ItemStack inSlot = machine.getItem(0);
        if (machine.shouldRender(inSlot))
            renderSpeechBubble(inSlot, pose, buffer, pPackedLight, pPackedOverlay);
    }
}
