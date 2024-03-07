package uk.joshiejack.penguinlib.data.generator;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.data.TimeUnitRegistry;
import uk.joshiejack.penguinlib.data.database.CSVUtils;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractDatabaseProvider implements DataProvider {
    private final Multimap<String, String> data = LinkedHashMultimap.create();
    private final PackOutput.PathProvider modelPathProvider;
    private final Map<String, String> headings = new HashMap<>();
    private final String modid;

    public AbstractDatabaseProvider(PackOutput output, String modid) {
        this.modid = modid;
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, PenguinLib.DATABASE_FOLDER);
    }

    public void addEntry(String file, String headings, String line) {
        this.headings.put(file, headings);
        if (!this.data.get(file).contains(line))
            this.data.get(file).add(line);
    }

    protected void addFurnaceFuel(Item item, int burnTime) {
        addEntry("furnace_fuels", "Item,Burn Time", CSVUtils.join(BuiltInRegistries.ITEM.getKey(item).toString(), burnTime));
    }

    protected void addLootTableMerge(ResourceLocation target) {
        addEntry("merge_loot_table", "Target,Loot Table", CSVUtils.join(target, new ResourceLocation(modid, target.getPath())));
    }

    protected void addTimeUnitForMachine(BlockEntityType<?> type, TimeUnitRegistry.Defaults duration) {
        addTimeUnit(Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)).toString(), duration.getValue());
    }

    protected void addTimeUnitForMachine(BlockEntityType<?> type, long duration) {
        addTimeUnit(Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)).toString(), duration);
    }

    protected void addTimeUnit(String name, long duration) {
        addEntry("time_unit", "Name,Duration", CSVUtils.join(name, duration));
    }

    protected abstract void addDatabaseEntries();

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        addDatabaseEntries();
        if (data.isEmpty())
            throw new IllegalStateException("Database entries were not added to the database provider");
        return CompletableFuture.allOf(saveCollection(cache, data));
    }

    protected CompletableFuture<?> saveCollection(CachedOutput cache, Multimap<String, String> data) {
        return CompletableFuture.allOf(data.keySet().stream().map(key -> {
            Path path = modelPathProvider.file(new ResourceLocation(modid, key), "csv");
            return save(cache, headings.get(key), data.get(key), path);
        }).toArray(CompletableFuture[]::new));
    }

    @SuppressWarnings("UnstableApiUsage, deprecation")
    private CompletableFuture<?> save(CachedOutput cache, String headings, Collection<String> strings, Path target) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8)) ) {
                    Files.createDirectories(target.getParent());
                    writer.write(headings);
                    writer.write("\n");
                    writer.write(String.join("\n", strings));
                }

                cache.writeIfNeeded(target, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException e) {
                PenguinLib.LOGGER.error("Failed to save file to {}", target, e);
            }
        }, Util.backgroundExecutor());

    }

    @Nonnull
    @Override
    public String getName() {
        return "CSV Database";
    }
}
