package uk.joshiejack.penguinlib.world.team;

import joptsimple.internal.Strings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.event.TeamChangedOwnerEvent;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.network.packet.SyncTeamDataPacket;
import uk.joshiejack.penguinlib.network.packet.SyncTeamSubTagPacket;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PenguinTeam implements INBTSerializable<CompoundTag> {
    private boolean isClient;
    private CompoundTag data;
    private Set<UUID> members;
    private Set<UUID> invited;
    private UUID teamUUID;
    private UUID owner;
    private String name = Strings.EMPTY;

    public PenguinTeam(UUID uuid) {
        this.teamUUID = uuid;
        this.members = new HashSet<>();
        this.invited = new HashSet<>();
        this.data = new CompoundTag();
    }

    public void setClient() {
        this.isClient = true;
    }

    public boolean isClient() {
        return isClient;
    }

    public void invite(ServerLevel level, UUID uuid) {
        invited.add(uuid);
    }


    public void clearInvite(UUID playerID) {
        invited.remove(playerID);
    }

    public boolean isInvited(UUID uuid) {
        return invited.contains(uuid);
    }

    public PenguinTeam onChanged(ServerLevel world) {
        if (!members.contains(owner)) {
            this.owner = members.stream().findFirst().orElse(null); //Grab a new one, it can be null
            MinecraftForge.EVENT_BUS.post(new TeamChangedOwnerEvent(teamUUID, owner));
        }

        syncToTeam(world);
        return this;
    }

    public PenguinTeam(CompoundTag data) {
        this.deserializeNBT(data);
    }

    public Set<UUID> members() {
        return members;
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    public UUID getID() {
        return teamUUID;
    }

    public CompoundTag getData() {
        return data;
    }

    public PenguinPacket getSyncPacket() {
        return new SyncTeamDataPacket(serializeNBT());
    }

    public void syncToPlayer(ServerPlayer player) {
        PenguinNetwork.sendToClient(player, new SyncTeamDataPacket(serializeNBT()));
    }

    public void syncToTeam(ServerLevel world) {
        PenguinNetwork.sendToTeam(world, teamUUID, new SyncTeamDataPacket(serializeNBT()));
    }

    public void syncSubTag(String subTag, ServerLevel world) {
        PenguinNetwork.sendToTeam(world, teamUUID, new SyncTeamSubTagPacket(subTag, data.getCompound(subTag)));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public @NotNull CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putString("UUID", teamUUID.toString());
        compound.putString("Name", name == null ? teamUUID.toString() : name);
        compound.put("Data", data);
        ListTag list = new ListTag();
        members.forEach(uuid -> list.add(StringTag.valueOf(uuid.toString())));
        compound.put("Members", list);

        ListTag invitedList = new ListTag();
        invited.forEach(uuid -> invitedList.add(StringTag.valueOf(uuid.toString())));
        compound.put("Invited", invitedList);
        if (owner != null) {
            compound.putString("Owner", owner.toString());
        }

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        teamUUID = UUID.fromString(compound.getString("UUID"));
        name = compound.contains("Name") ? compound.getString("Name") : teamUUID.toString();
        data = compound.getCompound("Data");
        members = new HashSet<>();
        invited = new HashSet<>();
        owner = compound.contains("Owner") ? UUID.fromString(compound.getString("Owner")) : null;
        ListTag list = compound.getList("Members", 8);
        for (int i = 0; i < list.size(); i++) {
            members.add(UUID.fromString(list.getString(i)));
        }

        compound.getList("Invited", 8)
                .forEach(nbt -> invited.add(UUID.fromString(nbt.getAsString())));

        if (owner == null) {
            owner = members.stream().findFirst().orElse(null);
            MinecraftForge.EVENT_BUS.post(new TeamChangedOwnerEvent(teamUUID, owner));
        }
    }
}
