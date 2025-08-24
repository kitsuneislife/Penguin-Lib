package uk.joshiejack.penguinlib.scripting.wrapper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.penguinlib.event.ScriptingEvents;
import uk.joshiejack.penguinlib.scripting.Interpreter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WrapperRegistry {
    private static final Cache<Object[], Object[]> WRAPPED = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
    private static final List<Pair<Class<?>, Builder<?, ?>>> EXTENSIBLES = Lists.newArrayList();
    private static final Map<Class<?>, Builder<?, ?>> STATICS = Maps.newHashMap();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCollectForRegistration(ScriptingEvents.CollectGlobalVarsAndFunctions event) {
    MinecraftForge.EVENT_BUS.post(new ScriptingEvents.CollectWrapper(EXTENSIBLES, STATICS));
        EXTENSIBLES.sort(Comparator.comparing(p -> p.getValue().getPriority()));
    }

    @SuppressWarnings("unchecked")
    public static <O> O unwrap(Object o) {
        return o instanceof AbstractJS<?> ? (O) ((AbstractJS<?>) o).penguinScriptingObject : (O) o;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <R extends AbstractJS<?>> R wrap(Object object) {
        if (STATICS.containsKey(object.getClass())) {
            return (R) Objects.requireNonNull(STATICS.get(object.getClass()).wrap(object));
        } else {
            Optional<? extends Builder<?, ?>> b = EXTENSIBLES.stream().filter(w -> w.getLeft().isAssignableFrom(object.getClass())).map(Pair::getRight).findFirst();
            return Objects.requireNonNull(b.map(builder -> (R) Objects.requireNonNull(builder.wrap(object))).orElse(null));
        }
    }

    private static Object javaToJS(Context context, Object object, Scriptable scope) {
        return Context.javaToJS(context, object, scope);
    }

    private static Object[] doWrap(Scriptable scope, Object... objects) {
        Object[] ret = new Object[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (objects[0] instanceof AbstractJS) {
                ret[i] = javaToJS(Interpreter.context, objects[i], scope);
            } else {
                Class<?> clazz = objects[i].getClass();
                if (STATICS.containsKey(clazz) && STATICS.get(objects[i].getClass()).isEnabled()) {
                    ret[i] = javaToJS(Interpreter.context, STATICS.get(objects[i].getClass()).wrap(objects[i]), scope);

                } else {
                    Object o = objects[i];
                    Optional<? extends Builder<?, ?>> b = EXTENSIBLES.stream().filter(w ->
                                    w.getLeft().isAssignableFrom(o.getClass()) && w.getRight().isEnabled())
                            .map(Pair::getRight).findFirst();
                    if (b.isPresent()) {
                        ret[i] = javaToJS(Interpreter.context, b.get().wrap(o), scope);
                    } else {
                        ret[i] = javaToJS(Interpreter.context, objects[i], scope);
                    }
                }
            }
        }

        return ret;
    }

    public static Object[] wrapAll(Scriptable scope, Object... objects) {
        try {
            return WRAPPED.get(objects, () -> doWrap(scope, objects));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return doWrap(scope, objects);
        }
    }

    public static class Builder<C, R extends AbstractJS<C>> {
        private final Cache<C, R> serverCache = CacheBuilder.newBuilder().build();
        private final Cache<C, R> clientCache = CacheBuilder.newBuilder().build();
        private final Class<C> parameter;
        private final Class<R> wrapper;
        private EventPriority priority = EventPriority.NORMAL;
        private boolean dynamic;
        private boolean disabled;
        private boolean sided;

        public Builder(Class<R> wrapper, Class<C> parameter) {
            this.wrapper = wrapper;
            this.parameter = parameter;
        }

        public EventPriority getPriority() {
            return priority;
        }

        public Builder<C, R> setPriority(EventPriority priority) {
            this.priority = priority;
            return this;
        }

        public boolean isEnabled() {
            return !disabled;
        }

        public Builder<C, R> disable() {
            this.disabled = true;
            return this;
        }

        public Builder<C, R> setDynamic() {
            this.dynamic = true;
            return this;
        }

        public Builder<C, R> setSided() {
            this.sided = true;
            return this;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        public R wrap(Object object) {
            try {
                if (dynamic) return wrapper.getConstructor(parameter).newInstance(object);
                Cache<C, R> cache = sided && FMLEnvironment.dist == Dist.CLIENT ? clientCache : serverCache;
                return cache.get((C) object, () -> wrapper.getConstructor(parameter).newInstance(object));
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
