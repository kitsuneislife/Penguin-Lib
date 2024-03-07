package uk.joshiejack.penguinlib.util.random;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

import java.util.Map;
import java.util.Optional;

public class IdentifiableWeightedRandomList<E extends WeightedEntry> {
    private final Map<String, E> items;
    private final int totalWeight;

    IdentifiableWeightedRandomList(Map<String, E> map)  {
        this.items = ImmutableMap.copyOf(map);
        this.totalWeight = WeightedRandom.getTotalWeight(map.values().stream().toList());
    }

    public static <E extends WeightedEntry> IdentifiableWeightedRandomList<E> create() {
        return new IdentifiableWeightedRandomList<>(ImmutableMap.of());
    }

    @SafeVarargs
    public static <E extends WeightedEntry> IdentifiableWeightedRandomList<E> create(Map.Entry<String, E>... entries) {
        return new IdentifiableWeightedRandomList<>(ImmutableMap.ofEntries(entries));
    }

    public static <E extends WeightedEntry> IdentifiableWeightedRandomList<E> create(Map<String, E> map) {
        return new IdentifiableWeightedRandomList<>(map);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Optional<E> getRandom(RandomSource source) {
        if (totalWeight == 0) return Optional.empty();
        else {
            int i = source.nextInt(totalWeight);
            return WeightedRandom.getWeightedItem(items.values().stream().toList(), i);
        }
    }

    public Map<String, E> unwrap() {
        return items;
    }
}
