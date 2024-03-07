package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;
import java.util.UUID;

public class ServerLevelJS extends LevelJS<ServerLevel> {
    public ServerLevelJS(ServerLevel world) {
        super(world);
    }

    public void displayParticle(SimpleParticleType type, double x, double y, double z, double speed) {
        penguinScriptingObject.addParticle(type, x, y, z, 0D, 0D, speed);
    }

    public EntityJS<?> getEntityByUUID(String uuid) {
        return WrapperRegistry.wrap(Objects.requireNonNull(penguinScriptingObject.getEntity(UUID.fromString(uuid))));
    }
}
