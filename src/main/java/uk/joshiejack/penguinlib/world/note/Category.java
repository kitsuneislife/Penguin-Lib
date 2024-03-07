package uk.joshiejack.penguinlib.world.note;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.data.PenguinRegistries;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

public class Category implements ReloadableRegistry.PenguinRegistry<Category> {
    public static final Codec<Category> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Icon.CODEC.optionalFieldOf("icon").forGetter(category -> java.util.Optional.ofNullable(category.icon))
    ).apply(instance, (icon) -> {
        Category category = new Category();
        icon.ifPresent(category::setIcon);
        return category;
    }));

    private Icon icon;

    public Category() {}

    public ResourceLocation id() {
        return PenguinRegistries.CATEGORIES.getID(this);
    }

    public Component getTitle() {
        return Component.translatable(Util.makeDescriptionId("note.category", id()));
    }

    public Category setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public Category fromNetwork(FriendlyByteBuf buf) {
        Category category = new Category();
        category.icon = Icon.fromNetwork(buf);
        return category;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        getIcon().toNetwork(buf);
    }
}