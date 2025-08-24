package uk.joshiejack.penguinlib.world.team;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.server.players.GameProfileCache;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.PenguinTeamsClient;
import uk.joshiejack.penguinlib.event.TeamChangedEvent;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.ChangeTeamPacket;
import uk.joshiejack.penguinlib.network.packet.SyncPlayerTagPacket;
import uk.joshiejack.penguinlib.network.packet.SyncTeamMembersPacket;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID)
public class PenguinTeams extends SavedData {
    private static final String DATA_NAME = "penguin_teams";
    private final Map<UUID, UUID> memberOf = new HashMap<>(); //Player ID > Team ID
    private final Map<UUID, PenguinTeam> teams = new HashMap<>(); // Team ID > Data
    private final Map<String, PenguinTeam> teamsByName = new HashMap<>(); //TeamName > Team (not saved)

    public static PenguinTeams get(ServerLevel world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(PenguinTeams::load, PenguinTeams::new, DATA_NAME);
    }

    public boolean nameExists(String name) {
        return teamsByName.containsKey(name);
    }

    public Collection<PenguinTeam> teams() {
        return teams.values();
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PenguinTeams teams = get((ServerLevel) player.level());
            PenguinNetwork.sendToClient(player,
                    getTeamForPlayer(player).getSyncPacket(),
                    new SyncPlayerTagPacket("PenguinStatuses", player),
                    new SyncTeamMembersPacket(teams.memberOf),
                    new SyncPlayerTagPacket("Notes", player) {
            });
        }
    }

    public int getMemberCount(UUID owner_id) {
        UUID team = memberOf.get(owner_id);
        return teams.get(team).members().size();
    }

    public void changeTeam(CommandContext<CommandSourceStack> ctx, UUID newTeam, Consumer<PenguinTeam> consumer) throws CommandSyntaxException {
        changeTeam(ctx.getSource().getLevel(), ctx.getSource().getPlayerOrException().getUUID(), newTeam, consumer);
    }

    public void changeTeam(ServerLevel world, UUID player, UUID newUUID) {
        changeTeam(world, player, newUUID, (pt) -> {});
    }

    public void changeTeam(ServerLevel world, UUID player, UUID newUUID, Consumer<PenguinTeam> function) {
        UUID oldUUID = memberOf.getOrDefault(player, player);
        memberOf.put(player, newUUID);
        PenguinTeam oldTeam = teams.get(oldUUID);
        if (oldTeam != null) {
            oldTeam.members().remove(player);
            oldTeam.onChanged(world);
        }

        if (!teams.containsKey(newUUID)) {
            teams.put(newUUID, new PenguinTeam(newUUID));
        }

        PenguinTeam newTeam = teams.get(newUUID);
        newTeam.members().add(player);
        teamsByName.remove(newTeam.getName(), newTeam); //Remove the old name
        if (player.equals(newUUID))
            newTeam.setName(world.getServer().getProfileCache().get(player).map(profile -> profile.getName()).orElse("Unknown"));
        function.accept(newTeam);
        teamsByName.put(newTeam.getName(), newTeam); //Add the new name
        newTeam.onChanged(world);
        MinecraftForge.EVENT_BUS.post(new TeamChangedEvent(world, player, oldUUID, newUUID));
        PenguinNetwork.sendToEveryone(new ChangeTeamPacket(player, oldUUID, newUUID));
        setDirty();
    }

    public PenguinTeam getTeam(UUID team) {
        return teams.get(team);
    }

    public CompoundTag getTeamData(UUID team) {
        return teams.get(team).getData();
    }

    public Collection<UUID> getTeamMembers(UUID team) {
        return teams.get(team).members();
    }

    public static PenguinTeam getTeamFromID(ServerLevel world, UUID team) {
        return get(world).teams.get(team);
    }

    public static PenguinTeam getTeamForPlayer(ServerLevel world, UUID uuid) {
        PenguinTeams data = get(world); //Load the serverdata
        if (!data.memberOf.containsKey(uuid)) {
            data.changeTeam(world, uuid, uuid);
            data.setDirty();
        }

        return data.teams.get(data.memberOf.get(uuid));
    }

    public static PenguinTeam getTeamForPlayer(Player player) {
        if (player.level().isClientSide) return PenguinTeamsClient.getInstance(); //Client data
        return getTeamForPlayer((ServerLevel) player.level(), player.getUUID());
    }

    public static UUID getTeamUUIDForPlayer(Player player) {
        return getTeamForPlayer(player).getID();
    }

    public static PenguinTeam getTeamFromContext(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        PenguinTeams teams = getTeamsFromContext(ctx);
        return teams.getTeam(teams.memberOf.get(ctx.getSource().getPlayerOrException().getUUID()));
    }

    public static PenguinTeams getTeamsFromContext(CommandContext<CommandSourceStack> ctx) {
        return get(ctx.getSource().getLevel());
    }

    //Used in Shopaholic
    @SuppressWarnings("unused")
    public static CompoundTag getPenguinStatuses(Player player) {
        CompoundTag data = getTeamForPlayer(player).getData();
        if (!data.contains("PenguinStatuses"))
            data.put("PenguinStatuses", new CompoundTag());
        return data.getCompound("PenguinStatuses");
    }

    public static PenguinTeams load(@Nonnull CompoundTag nbt) {
        PenguinTeams teamData = new PenguinTeams();
        ListTag data = nbt.getList("Teams", 10);
        for (int i = 0; i < data.size(); i++) {
            CompoundTag tag = data.getCompound(i);
            PenguinTeam team = new PenguinTeam(tag);
            teamData.teams.put(team.getID(), team);
            teamData.teamsByName.put(team.getName(), team);
            team.members().forEach(member -> teamData.memberOf.put(member, team.getID())); //Add the quick reference for members
        }
        return teamData;
    }

    @Nonnull
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compound) {
        ListTag data = new ListTag();
        for (Map.Entry<UUID, PenguinTeam> entry : teams.entrySet()) {
            data.add(entry.getValue().serializeNBT());
        }

        compound.put("Teams", data);

        return compound;
    }

    public PenguinTeam getTeamByName(String name) {
        return teamsByName.get(name);
    }
}