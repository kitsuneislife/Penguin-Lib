package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinConfig;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.data.database.Database;
import uk.joshiejack.penguinlib.data.database.Table;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.penguinlib.event.DatabasePopulateEvent;
import uk.joshiejack.penguinlib.util.registry.Packet;

import java.util.HashMap;
import java.util.Map;

@Packet(value = PacketFlow.CLIENTBOUND)
public class SyncDatabasePacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("database_sync");
    private final Database database;
    private final Map<String, Table> tables = new HashMap<>();

    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SyncDatabasePacket(Database database) {
        this.database = database;
    }

    public SyncDatabasePacket(FriendlyByteBuf buf) {
        database = new Database();
        int tableCount = buf.readShort();
        for (int i = 0; i < tableCount; i++) {
            String name = buf.readUtf();
            int parts = buf.readShort();
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < parts; j++)
                builder.append(buf.readUtf());
            Database.parseCSV(tables, database.tableData, name, builder.toString());
        }
    }

    public void write(FriendlyByteBuf buf) {
        int tableCount = database.tableData.size();
        buf.writeShort(tableCount);
        for (Map.Entry<String, String> entry : database.tableData.entries()) {
            buf.writeUtf(entry.getKey());
            int parts = (int) Math.ceil((double) entry.getValue().length() / (double) Short.MAX_VALUE);
            buf.writeShort(parts);
            for (int j = 0; j < parts; j++)
                buf.writeUtf(entry.getValue().substring(j * Short.MAX_VALUE, Math.min((j + 1) * Short.MAX_VALUE, entry.getValue().length())));
        }
    }

    
    public void handle(Player player) {
        Database.INSTANCE.set(database);
        if (PenguinConfig.enableDatabaseDebugger.get())
            Database.print(tables);
        MinecraftForge.EVENT_BUS.post(new DatabasePopulateEvent(tables));
        MinecraftForge.EVENT_BUS.post(new DatabaseLoadedEvent(tables));
    }
}

