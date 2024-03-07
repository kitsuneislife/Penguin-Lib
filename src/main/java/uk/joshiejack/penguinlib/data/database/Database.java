package uk.joshiejack.penguinlib.data.database;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinConfig;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.penguinlib.event.DatabasePopulateEvent;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.SyncDatabasePacket;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID)
public class Database implements ResourceManagerReloadListener {
    public static final Database INSTANCE = new Database();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int pathSuffixLength = ".csv".length();
    private static final int dirLength = PenguinLib.DATABASE_FOLDER.length() + 1;
    public final Multimap<String, String> tableData = HashMultimap.create();

    @SubscribeEvent
    public static void onDataPack(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null)
            PenguinNetwork.sendToClient(event.getPlayer(), new SyncDatabasePacket(INSTANCE));
        else {
            event.getPlayerList().getPlayers().forEach(player ->
                    PenguinNetwork.sendToClient(player, new SyncDatabasePacket(INSTANCE)));
        }
    }

    @SubscribeEvent
    public static void registerData(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    public void set(Database database) {
        tableData.clear();
        tableData.putAll(database.tableData);
    }

    public static void print(Map<String, Table> tables) {
        LOGGER.info("############## DATABASE ##############");
        for (String table: tables.keySet()) {
            LOGGER.info("############## TABLE: " + table +   " ##############");
            LOGGER.info(tables.get(table).labelset());

            tables.get(table).rows().forEach(r -> {
                List<String> arr = new ArrayList<>();
                tables.get(table).labels().forEach(header -> arr.add(r.get(header).toString()));
                LOGGER.info(Arrays.toString(arr.toArray()));
            });
        }
    }

    @Nonnull
    public <T> T get(Map<String, Table> tables, String search) {
        String[] terms = search.split(",");
        String table = terms[0].trim();
        String row = terms[1].trim();
        String data = terms[2].trim();
        Preconditions.checkNotNull(table, "The table cannot be null: " + table);
        Preconditions.checkNotNull(row, "The id to search in the table cannot be null: " + row);
        Preconditions.checkNotNull(data, "The instance you are searching for cannot be null: " + data);
        return tables.getOrDefault(table, Table.EMPTY).find(row).get(data);
    }

    public static Table createTable(Map<String, Table> tables, String name, String... labelset) {
        if (tables.containsKey(name)) return tables.get(name);
        else {
            Table table = new Table(name, labelset);
            tables.put(name, table);
            return table;
        }
    }

    public static void parseCSV(Map<String, Table> tables, Multimap<String, String> tableData, String name, String csv) {
        //Ignore any directories registered for this csv and just go with the name
        String file_name = new File(name).getName();
        name = (file_name.startsWith("$") ? file_name.replace("$", "") : file_name.contains("$") ? file_name.split("\\$")[1] : file_name).toLowerCase(Locale.ROOT); //Ignore anything before the dollar symbol
        tableData.get(name).add(csv);
        String[] entries = csv.split("[\\r\\n]+");
        String[] labels = entries[0].split(",");
        Table table = createTable(tables, name, labels); //Get a table with this name if it already exists
        for (int i = 1; i < entries.length; i++) {
            if (!entries[i].startsWith("#") && !entries[i].isEmpty()) {
                List<String> list = CSVUtils.parse(entries[i]);
                if (!list.isEmpty()) {
                    try {
                        table.insert(list.toArray(new String[0]));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        LOGGER.log(Level.ERROR, "Failed to insert the csv: " + name + " as there was an issue on line: " + i + " " + entries[i]);
                    }
                }
            }
        }
    }

    private static void loadData(Map<String, Table> tables, ResourceLocation rl, Resource resource) {
        try {
            InputStream is = resource.open();
            Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            parseCSV(tables, INSTANCE.tableData, rl.getPath().substring(dirLength, rl.getPath().length() - pathSuffixLength), IOUtils.toString(reader));
        } catch (IllegalArgumentException | IOException ex){
            LOGGER.error("Couldn't parse data file from {}", rl, ex);
        }
    }

    public static Table loadTable(@Nonnull ResourceManager rm, String table) {
        Map<String, Table> tables = new HashMap<>();
        rm.listResources(PenguinLib.DATABASE_FOLDER, (fileName) -> fileName.toString().endsWith(".csv")).forEach((rl, rs) -> loadData(tables, rl, rs));
        return tables.getOrDefault(table, Table.EMPTY);
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager rm) {
        Map<String, Table> tables = new HashMap<>();
        tableData.clear(); //Remove the table data instance
        List<ModContainer> sorted = ModList.get().getSortedMods();
        rm.listResources(PenguinLib.DATABASE_FOLDER, (fileName) -> fileName.toString().endsWith(".csv"))
                .entrySet().stream().sorted((r1, r2) -> {
                    //Force the database to process resources in the order of the mods
                    String modid1 = r1.getKey().getNamespace();
                    String modid2 = r2.getKey().getNamespace();
                    int index1 = sorted.indexOf(ModList.get().getModContainerById(modid1).orElse(null));
                    int index2 = sorted.indexOf(ModList.get().getModContainerById(modid2).orElse(null));
                    return index1 - index2;
                })
                .forEach(r -> loadData(tables, r.getKey(), r.getValue()));
        if (PenguinConfig.enableDatabaseDebugger.get())
            print(tables);
        NeoForge.EVENT_BUS.post(new DatabasePopulateEvent(tables));
        NeoForge.EVENT_BUS.post(new DatabaseLoadedEvent(tables));
    }
}