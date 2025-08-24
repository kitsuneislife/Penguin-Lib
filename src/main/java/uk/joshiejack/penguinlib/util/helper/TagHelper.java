package uk.joshiejack.penguinlib.util.helper;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import uk.joshiejack.penguinlib.PenguinLib;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TagHelper {
    public static CompoundTag getOrCreateTag(CompoundTag tag, String key) {
        if (!tag.contains(key)) {
            tag.put(key, new CompoundTag());
        }

        return tag.getCompound(key);
    }

    public static <K, V> ListTag writeMap(Map<K, V> map, BiConsumer<CompoundTag, K> keyWriter, BiConsumer<CompoundTag, V> valueWriter) {
        ListTag list = new ListTag();
        map.forEach((key, value) -> {
            CompoundTag tag = new CompoundTag();
            keyWriter.accept(tag, key);
            valueWriter.accept(tag, value);
            list.add(tag);
        });

        return list;

    }

    public static <K, V> void readMap(ListTag list, Map<K, V> map, Function<CompoundTag, K> keyReader, Function<CompoundTag, V> valueReader) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            K key = keyReader.apply(tag);
            V value = valueReader.apply(tag);
            map.put(key, value);
        }
    }

    public static <K, V> void readMap(ListTag list, Map<K, V> map, Function<CompoundTag, K> keyReader, BiFunction<CompoundTag, K, V> valueReader) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            K key = keyReader.apply(tag);
            V value = valueReader.apply(tag, key);
            map.put(key, value);
        }
    }

    private static final Component EMPTY = Component.empty();

    public static <T> void putCodec(Codec<T> codec, CompoundTag tag, String key, T value) {
        codec.encodeStart(NbtOps.INSTANCE, value)
                .resultOrPartial(PenguinLib.LOGGER::error)
                .ifPresent(data -> tag.put(key, data));
    }

    public static <T> T getFromCodec(Codec<T> codec, CompoundTag tag, String key, T default_) {
        return tag.contains(key) ? codec
                .parse(NbtOps.INSTANCE, tag.get(key))
                .resultOrPartial(PenguinLib.LOGGER::error)
                .orElse(default_) : default_;
    }

    public static void putComponent(CompoundTag tag, String key, Component component) {
        // ComponentSerialization.FLAT_CODEC não existe no Forge 1.20.1
        // Usando serialização simples
        tag.putString(key, Component.Serializer.toJson(component));
    }

    public static Component getComponent(CompoundTag tag, String key) {
        return tag.contains(key) ? Component.Serializer.fromJson(tag.getString(key))
                : EMPTY;
    }
}
