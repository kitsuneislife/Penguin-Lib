package uk.joshiejack.penguinlib.event;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.penguinlib.scripting.Sandbox;
import uk.joshiejack.penguinlib.scripting.ScriptFunction;
import uk.joshiejack.penguinlib.scripting.wrapper.AbstractJS;
import uk.joshiejack.penguinlib.scripting.wrapper.WrapperRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScriptingEvents {
    public static class CollectGlobalVarsAndFunctions extends Event {
        private final Context context;
        private final Scriptable globalScope;
        private final List<String> list;
        private final List<Pair<String, ScriptFunction>> functions;

        public CollectGlobalVarsAndFunctions(Context context, Scriptable globalScope,
                                             List<String> list, List<Pair<String, ScriptFunction>> functions) {
            this.context = context;
            this.globalScope = globalScope;
            this.list = list;
            this.functions = functions;
        }

        public Scriptable getGlobalScope() {
            return globalScope;
        }

        public void add(String key, Object value) {
            context.addToScope(globalScope, key, value);
        }

        public <E extends Enum<E>> void registerEnum(Class<E> e) {
            Sandbox.allow(e.getName());
            for (E v: e.getEnumConstants()) {
                Object wrappedJavaClass = Context.javaToJS(context, v, globalScope);
                ScriptableObject.putProperty(globalScope, v.name().toLowerCase(Locale.ENGLISH), wrappedJavaClass, context);
                //context.addToScope(globalScope, v.name().toLowerCase(Locale.ENGLISH), v);
            }
        }

        public void registerVar(String var, Class<?> clazz) {
            Sandbox.allow(clazz.getName());
            Object wrappedJavaClass = Context.javaToJS(context, clazz, globalScope);
            ScriptableObject.putProperty(globalScope, var, wrappedJavaClass, context);
        }

        public void registerVar(String var, Object obj) {
            Sandbox.allow(obj.getClass().getName());
            Object wrappedJavaClass = Context.javaToJS(context, obj, globalScope);
            ScriptableObject.putProperty(globalScope, var, wrappedJavaClass, context);
        }

        public void registerFunction(String name, String code) {
            list.add("function " + name + " { " + code + " }");
        }

        public void registerJavaFunction(String name, ScriptFunction function) {
            functions.add(Pair.of(name, function));
        }
    }

    public static class CollectMethod extends Event {
        private final List<String> list;

        public CollectMethod(List<String> list) {
            this.list = list;
        }

        public void add(String string) {
            list.add(string);
        }
    }

    public static class CollectWrapper extends Event {
        private final List<Pair<Class<?>, WrapperRegistry.Builder<?, ?>>> extensibles;
        private final Map<Class<?>, WrapperRegistry.Builder<?, ?>> statics;

        public CollectWrapper(List<Pair<Class<?>, WrapperRegistry.Builder<?, ?>>> extensibles, Map<Class<?>, WrapperRegistry.Builder<?, ?>> statics) {
            this.extensibles = extensibles;
            this.statics = statics;
        }

        public <C, T extends AbstractJS<C>> void register (Class<T> wrapper) {
            Sandbox.allow(wrapper.getName());
        }

        public <C, T extends AbstractJS<C>> WrapperRegistry.Builder<C, T> registerExtensible(Class<T> wrapper, Class<C> clazz) {
            WrapperRegistry.Builder<C, T> builder = new WrapperRegistry.Builder<>(wrapper, clazz);
            Sandbox.allow(wrapper.getName());
            extensibles.add(Pair.of(clazz, builder));
            return builder;
        }

        public <C, T extends AbstractJS<C>> WrapperRegistry.Builder<C, T> register (Class<T> wrapper, Class<C> clazz) {
            WrapperRegistry.Builder<C, T> builder = new WrapperRegistry.Builder<>(wrapper, clazz);
            Sandbox.allow(wrapper.getName());
            statics.put(clazz, builder);
            return builder;
        }
    }

    public static class TriggerFired extends Event {
        private final String method;
        private final Object[] objects;

        public TriggerFired(String method, Object... objects) {
            this.method = method;
            this.objects = objects;
        }

        public String getMethod() {
            return method;
        }

        public Object[] getObjects() {
            return objects;
        }

        @Nullable
        public Player getPlayer() {
            return objects[0] instanceof Player ? (Player) objects[0] : null;
        }
    }
}
