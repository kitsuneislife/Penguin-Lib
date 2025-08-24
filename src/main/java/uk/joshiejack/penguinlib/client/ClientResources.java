package uk.joshiejack.penguinlib.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.PenguinLib;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ClientResources {
    public static final ResourceLocation SPEECH_BUBBLE = new ResourceLocation(PenguinLib.MODID, "extra/speech_bubble");

    @SubscribeEvent
    public static void loadModels(ModelEvent.RegisterAdditional event) {
        event.register(ClientResources.SPEECH_BUBBLE);
    }
}
