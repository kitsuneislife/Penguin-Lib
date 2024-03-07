package uk.joshiejack.penguinlib.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class PenguinTeamsClient {
    private static final Multimap<UUID, UUID> teamMembers = HashMultimap.create();
    private static PenguinTeam INSTANCE;

    public static PenguinTeam getInstance() {
        return INSTANCE;
    }

    public static void setInstance(CompoundTag data) {
        INSTANCE = new PenguinTeam(data);
        INSTANCE.setClient();//Yes bitch
    }

    public static void setMembers(Map<UUID, UUID> memberOf) {
        //We need to reverse this basically
        memberOf.forEach((key, value) -> teamMembers.get(value).add(key));
    }

    public static void changeTeam(UUID player, UUID oldTeam, UUID newTeam) {
        teamMembers.get(oldTeam).remove(player);
        teamMembers.get(newTeam).add(player);
    }

    public static Collection<UUID> members(UUID teamID) {
        return teamMembers.get(teamID);
    }

    public static void setTag(String tagName, CompoundTag tag) {
        INSTANCE.getData().put(tagName, tag);
    }
}
