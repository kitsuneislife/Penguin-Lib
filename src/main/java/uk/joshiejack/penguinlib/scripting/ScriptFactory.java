package uk.joshiejack.penguinlib.scripting;

import com.google.common.collect.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.event.ScriptingEvents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")

public class ScriptFactory {
    private static final Map<ResourceLocation, Interpreter<?>> REGISTRY = Maps.newHashMap();
    private static final Multimap<String, Interpreter<?>> METHOD_TO_SCRIPTS = HashMultimap.create();
    private static final List<String> METHODS = Lists.newArrayList();
    private static ScriptFactory INSTANCE = null;
    public static Set<ResourceLocation> IGNORE = Sets.newHashSet();

    @Deprecated
    public static Interpreter<?> get(ResourceLocation resource) {
        return REGISTRY.get(resource);
    }

    public static Interpreter<?> getScript(ResourceLocation resource) {
        return get(resource);
    }

    public static boolean scriptExists(ResourceLocation resource) {
        return REGISTRY.containsKey(resource);
    }

    public static class Collect {
        @SubscribeEvent
        public static void onCollectScriptingMethods(ScriptingEvents.CollectMethod event) {
            event.add("onEntityInteract");
            event.add("onEntityKilled");
            event.add("onItemFished");
            event.add("onToolModification");
            event.add("onPlayerLogin");
            event.add("onRightClickBlock");
            event.add("onGetBreakSpeed");
            //event.add("onBlockSmashed");
            event.add("onItemExpire");
        }
    }


    @SubscribeEvent
    public void onTriggerFired(ScriptingEvents.TriggerFired event) {
        METHOD_TO_SCRIPTS.get(event.getMethod()).stream().filter(it -> !IGNORE.contains(it.scriptID)).forEach(script -> script.callFunction(event.getMethod(), event.getObjects()));
        IGNORE.clear(); //Clear out the ignore list
    }

    @SubscribeEvent
    public void onPlayerJoinedWorld(PlayerEvent.PlayerLoggedInEvent event) {
        NeoForge.EVENT_BUS.post(new ScriptingEvents.TriggerFired("onPlayerLogin", event.getEntity()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemExpire(ItemExpireEvent event) {
        try {
            if (!event.isCanceled() && METHOD_TO_SCRIPTS.containsKey("onItemExpire")) {
                double original = 0;
                int extra = 0;
                for (Interpreter<?> interpreter : METHOD_TO_SCRIPTS.get("onItemExpire")) {
                    Object ret = interpreter.getResultOfFunction("onItemExpire", event.getEntity(), extra);
                    if (ret instanceof Integer) {
                        extra = ((Integer)ret);
                    }
                }

                if (extra != 0) {
                    event.setCanceled(true);
                    event.setExtraLife(extra);
                }
            }
        } catch (NullPointerException ignored) {}
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        NeoForge.EVENT_BUS.post(new ScriptingEvents.TriggerFired("onRightClickBlock", event.getEntity(), event.getPos(), event.getItemStack(), event.getHand()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof LivingEntity)
            NeoForge.EVENT_BUS.post(new ScriptingEvents.TriggerFired("onEntityInteract", event.getEntity(), event.getTarget(), event.getHand()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityDeath(LivingDeathEvent event) {
        Entity source = event.getSource().getEntity();
        if (source instanceof Player player) {
            NeoForge.EVENT_BUS.post(new ScriptingEvents.TriggerFired("onEntityKilled", player, event.getEntity()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemFished(ItemFishedEvent event) {
        if (!event.getDrops().isEmpty())
            NeoForge.EVENT_BUS.post(new ScriptingEvents.TriggerFired("onItemFished", event.getEntity(), event.getDrops().get(0), event.getHookEntity()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (!event.isCanceled() && !event.isSimulated())
            NeoForge.EVENT_BUS.post(new ScriptingEvents.TriggerFired("onToolModification", event.getPlayer(), event.getPos(), event.getToolAction()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!event.isCanceled()) {
            float original = event.getOriginalSpeed();
            float newValue = original;
            for (Interpreter<?> interpreter: METHOD_TO_SCRIPTS.get("onGetBreakSpeed")) {
                Object ret = interpreter.getResultOfFunction("onGetBreakSpeed", event.getEntity(), event.getState(), event.getPosition(), newValue);
                if (ret != null && !ret.toString().equalsIgnoreCase("null")) {
                    newValue = Float.parseFloat(ret.toString());
                }
            }

            if (original != newValue) event.setNewSpeed(newValue);
        }
    }

    // This event is from "HarvestFestival TODO:
//    @SuppressWarnings("ConstantConditions")
//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public static void onBlockSmashed(BlockSmashedEvent event) {
//        boolean canceled = false;
//        for (Interpreter interpreter: METHOD_TO_SCRIPTS.get("onBlockSmashed")) {
//            if ((Boolean) interpreter.getResultOfFunction("onBlockSmashed", event.getEntityPlayer(), event.getHand(), event.getPos(), event.getState())) {
//                canceled = true;
//            }
//        }
//
//        if (canceled) event.setCanceled(true);
//    }

//    @SubscribeEvent(priority = EventPriority.LOWEST) //TODO: onHarvestDrop? maybe not cause of global loot mods?
//    public static void onHarvestDrop(BlockEvent.HarvestDropsEvent event) {
//        NeoForge.EVENT_BUS.post(new ScriptingTriggerFired("onHarvestDrop", event));
//    }

    public static void resetRegistry() {
        REGISTRY.clear();
        METHOD_TO_SCRIPTS.clear();
    }
    public static void register(ResourceLocation registryName, Interpreter<?> interpreter) {
        if (REGISTRY.containsKey(registryName))
            REGISTRY.get(registryName).destroy(); //Clear everything out
        getMethods().forEach(method -> {
            if (interpreter.hasMethod(method))
                METHOD_TO_SCRIPTS.get(method).add(interpreter);
        });

        if (INSTANCE == null) {
            INSTANCE = new ScriptFactory();
            NeoForge.EVENT_BUS.register(INSTANCE);
        }

        PenguinLib.LOGGER.info("Registered a Penguin-Script @ " + registryName);
        REGISTRY.put(registryName, interpreter);
    }

    public static List<String> getMethods() {
        if (METHODS.isEmpty()) {
            NeoForge.EVENT_BUS.post(new ScriptingEvents.CollectMethod(METHODS));
        }

        return METHODS;
    }

    public static void callFunction(@Nullable ResourceLocation script, String function, Object... data) {
        if (script != null) callFunction(REGISTRY.get(script), function, data);
    }

    public static void callFunction(@Nullable Interpreter<?> script, String function, Object... data) {
        if (script != null) script.callFunction(function, data);
    }

    @Nonnull
    public static <R> R getResult(@Nullable ResourceLocation script, String function, R default_, Object... data) {
        return script == null ? default_ : getResult(REGISTRY.get(script), function, default_, data);
    }

    @Nonnull
    public static <R> R getResult(@Nullable Interpreter<?> script, String function, R default_, Object... data) {
        return script == null ? default_ : script.getValue(function, default_, data);
    }

    public static boolean functionExists(ResourceLocation script, String function) {
        return functionExists(REGISTRY.get(script), function);
    }

    public static boolean functionExists(@Nullable Interpreter<?> script, String function) {
        return script != null && script.hasMethod(function);
    }
}
