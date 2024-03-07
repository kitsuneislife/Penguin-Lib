package uk.joshiejack.penguinlib.util.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;

public class TagIcon extends AbstractCyclicIcon.ItemStack {
    public static final Codec<TagIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(obj -> obj.tag),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(obj -> obj.originalCount)
    ).apply(instance, TagIcon::new));

    public final TagKey<Item> tag;

    public TagIcon(TagKey<Item> tag, int count) {
        super(Type.TAG, BuiltInRegistries.ITEM.getTag(tag).stream()
                .flatMap(HolderSet.ListBacked::stream)
                .map(h -> new ItemIcon(h.value().getDefaultInstance()))
                .toList(), count);
        this.tag = tag;
    }

    public TagIcon(FriendlyByteBuf buf) {
        this(ItemTags.create(buf.readResourceLocation()), buf.readInt());
    }

    @Override
    public Codec<? extends Icon> codec() {
        return CODEC;
    }

    @Override
    public void toNetwork(FriendlyByteBuf pb) {
        pb.writeResourceLocation(tag.location());
        pb.writeInt(originalCount);
    }
}