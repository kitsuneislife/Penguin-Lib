package uk.joshiejack.penguinlib.util.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class RegistryHelper {
    public static RegistryAccess registryAccess() {
        return FMLEnvironment.dist == Dist.CLIENT ? Client.getLevel().registryAccess() : ServerLifecycleHooks.getCurrentServer().registryAccess();
    }

    public static ResourceLocation id(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }

    public static ResourceLocation id(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourceLocation id(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation id (MobEffect effect) {
        return BuiltInRegistries.MOB_EFFECT.getKey(effect);
    }

    public static class Client {
        public static Level getLevel() {
            return Minecraft.getInstance().level;
        }
    }
}
