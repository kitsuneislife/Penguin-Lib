package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

import java.util.List;

public abstract class PenguinRegistryListPacket<O extends ReloadableRegistry.PenguinRegistry<O>> implements PenguinPacket {
    private final ReloadableRegistry<O> registry;
    private final List<ResourceLocation> registryNames;

    public PenguinRegistryListPacket(ReloadableRegistry<O> registry, List<O> entry) {
        this.registry = registry;
        this.registryNames = entry.stream().map(registry::getID).toList();
    }

    public PenguinRegistryListPacket(ReloadableRegistry<O> registry, FriendlyByteBuf buffer) {
        this.registry = registry;
        this.registryNames = buffer.readList(FriendlyByteBuf::readResourceLocation);
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeCollection(registryNames, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public void handle(Player player) {
        handle(player, registryNames.stream().map(registry::get).toList());
    }

    protected abstract void handle(Player player, List<O> entries);
}
