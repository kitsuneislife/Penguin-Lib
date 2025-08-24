package uk.joshiejack.penguinlib.scripting;

import com.google.common.collect.Maps;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.joshiejack.penguinlib.event.ScriptingEvents;
import uk.joshiejack.penguinlib.scripting.wrapper.*;
import uk.joshiejack.penguinlib.util.helper.FakePlayerHelper;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;

import java.util.Map;
import java.util.stream.Stream;

public class DefaultScripting {
    @HideFromJS
    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCollectWrappers(ScriptingEvents.CollectWrapper event) {
        //Extendable Objects vs Non Extendable
        event.register(BiomeJS.class, Biome.class);
        event.register(DataJS.class, CompoundTag.class);
        event.register(ItemEntityJS.class, ItemEntity.class).setDynamic().setSided();
        event.registerExtensible(LivingEntityJS.class, LivingEntity.class).setDynamic().setPriority(EventPriority.LOW).setSided();
        event.registerExtensible(EntityJS.class, Entity.class).setDynamic().setPriority(EventPriority.LOWEST).setSided();
        event.register(ItemStackJS.class, ItemStack.class);
        event.register(PlayerStatusJS.class, PlayerJS.class).setSided().disable();
        event.registerExtensible(PlayerJS.class, Player.class).setDynamic().setSided();
        event.registerExtensible(PositionJS.class, BlockPos.class);
        event.registerExtensible(BlockStateJS.class, BlockState.class);
        event.register(TeamStatusJS.class, TeamJS.class).setSided().disable();
        event.register(TeamJS.class, PenguinTeam.class).setSided();
        event.registerExtensible(ServerLevelJS.class, ServerLevel.class);
        event.registerExtensible(LevelJS.class, Level.class).setPriority(EventPriority.LOW);
        //event.register(HarvestDropsEventJS.class, BlockEvent.HarvestDropsEvent.class);
    }

    @HideFromJS
    @SubscribeEvent
    public static void onCollectGlobalVarsAndFunctions(ScriptingEvents.CollectGlobalVarsAndFunctions event) {
        Sandbox.deny("java.lang");
        Sandbox.allow("java.lang.Class");
        Sandbox.allow("java.lang.Object");
        Sandbox.allow("java.lang.Integer");
        Sandbox.allow("java.lang.Long");
        Sandbox.allow("java.lang.Short");
        Sandbox.allow("java.lang.Byte");
        Sandbox.allow("java.lang.Float");
        Sandbox.allow("java.lang.Double");
        Sandbox.allow("java.lang.Boolean");
        Sandbox.allow("java.lang.Character");
        Sandbox.allow("java.lang.String");
        Sandbox.allow("java.lang.Math");
        event.registerJavaFunction("print", (args) -> {
            Stream.of(args).forEach(System.out::println);
            return Context.getUndefinedValue();
        });

        event.registerEnum(InteractionHand.class);
        //TODO: No longer VALID event.registerEnum(SimpleParticleType.class);
        event.registerEnum(SoundSource.class);

        event.registerVar("scripting", Helper.INSTANCE);
        event.registerFunction("createStack(name)", "return scripting.createItemStack(name);");
        event.registerFunction("createStacks(name)", "return scripting.createItemStacks(name);");
        event.registerFunction("random(min, max)", "return Math.floor(Math.random() * (max - min + 1) + min);");
        event.registerFunction("getState(name)", "return scripting.getState(name);");
        event.registerFunction("fakePlayer(world, pos)", "return scripting.getFakePlayer(name);");
        event.registerFunction("includes(string, substring)", "return string.indexOf(substring) !== -1;");
        event.registerFunction("include(string)", """
                if (includes(string, ':')) {
                \tvar split = string.split(':');
                \tvar suffix = (includes(split[1], '.js')) ? (split[1]) : (split[1] + ".js");
                    load('classpath:data/' + split[0] + '/' + suffix);
                }""");
        event.registerFunction("hasFunction(x)", "return eval(typeof(x) == typeof(Function));");
    }

    //TODO: No longer VALID
//    public static NonNullList<ItemStackJS> createItemStacks(String[] stacks) {
//        NonNullList<ItemStackJS> list = NonNullList.withSize(stacks.length, ItemStackJS.EMPTY);
//        for (int i = 0; i < stacks.length; i++) {
//            list.set(i, WrapperRegistry.wrap(StackHelper.getStackFromString(stacks[i])));
//        }
//
//        return list;
//    }

    public static class Helper {
        public static final Helper INSTANCE = new Helper();
        public Map createMap() {
            return Maps.newHashMap();
        }

        public ItemStackJS createUnbreakableItemStack(String name) {
            ItemStack stack = createItemStack(name).get();
            stack.getOrCreateTag().putBoolean("Unbreakable", true);
            return WrapperRegistry.wrap(stack);
        }

        public ItemStackJS createItemStack(String name) {
            return createItemStack(name, 1);
        }

        public ItemStackJS createItemStack(String name, int amount) {
            return WrapperRegistry.wrap(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(name)), amount));
        }

        //TODO???
//    public static BlockStateJS getState(@Nonnull String name) {
//        return WrapperRegistry.wrap(Objects.requireNonNull(StateAdapter.fromString(name)));
//    }

        public boolean isTrue(String script, String function, Object... vars) {
            return ScriptFactory.getResult(new ResourceLocation(script), function, false, vars);
        }

        public PlayerJS getFakePlayer(ServerLevelJS world, PositionJS pos) {
            return WrapperRegistry.wrap(FakePlayerHelper.getFakePlayerWithPosition(world.get(), pos.get()));
        }

        public void call(String script, String function, Object... vars) {
            ScriptFactory.callFunction(new ResourceLocation(function), function, vars);
        }
    }
}
