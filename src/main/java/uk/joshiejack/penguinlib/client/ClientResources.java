package uk.joshiejack.penguinlib.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import uk.joshiejack.penguinlib.PenguinLib;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientResources {
    public static final ResourceLocation SPEECH_BUBBLE = new ResourceLocation(PenguinLib.MODID, "extra/speech_bubble");

    @SubscribeEvent
    public static void loadModels(ModelEvent.RegisterAdditional event) {
        event.register(ClientResources.SPEECH_BUBBLE);
    }
}
