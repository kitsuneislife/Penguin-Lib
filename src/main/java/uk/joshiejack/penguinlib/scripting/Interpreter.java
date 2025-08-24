package uk.joshiejack.penguinlib.scripting;

import com.google.common.collect.Lists;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.EcmaError;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.event.ScriptingEvents;
import uk.joshiejack.penguinlib.scripting.wrapper.WrapperRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter<O> {
    public static final Context context = Sandbox.enter();
    protected static final Scriptable globalScope = context.initStandardObjects();
    protected static final Pattern functions = Pattern.compile("function\\s+([\\w$]+)\\s*\\(");
    protected static boolean isInit = false;
    protected final Set<String> methods = new HashSet<>();
    protected Scriptable localScope;
    protected ScriptLoader.ScriptLocation location;
    public ResourceLocation scriptID;
    @Nullable public O data;

    protected Interpreter(ResourceLocation registryName, @Nonnull String javascript, ScriptLoader.ScriptLocation location, O data) {
        Interpreter.initGlobal();
        this.localScope = context.newObject(globalScope);
        this.localScope.setPrototype(globalScope);
        this.localScope.setParentScope(null);
        this.location = location;
        this.data = data;
        this.scriptID = registryName;
        Matcher matcher = functions.matcher(javascript);
        while (matcher.find())
            methods.add(matcher.group(1));
        try {
            addGlobals(this.data);
            context.evaluateString(localScope, String.join("\n", javascript), scriptID.toString(), 1, null);
        } catch (StackOverflowError e) {
            PenguinLib.LOGGER.error("Issue with loading in the script @ " + registryName);
        }
    }

    protected void addGlobals(O data) {
        context.addToScope(localScope, "this_id", Context.javaToJS(context, scriptID.toString(), localScope));
    }

    protected static void initGlobal() {
        if (!isInit) {
            List<String> strings = Lists.newArrayList();
            List<Pair<String, ScriptFunction>> functions = Lists.newArrayList();
            MinecraftForge.EVENT_BUS.post(new ScriptingEvents.CollectGlobalVarsAndFunctions(context, globalScope, strings, functions));
            strings.forEach(s ->
                    context.evaluateString(globalScope, s, "scripting", 1, null));
            functions.forEach(pair ->
                    globalScope.put(context, pair.getLeft(), globalScope, ScriptFunction.of(pair.getRight())));
            isInit = true;
        }
    }

    public void destroy() {
        localScope = null;
    }

    public boolean hasMethod(String string) {
        return methods.contains(string);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <R> R getValue(String function, R default_, Object... parameters) {
        Object o = getResultOfFunction(function, parameters);
        return o == null ? default_ : (R) o;
    }

    @Nullable
    public Object getResultOfFunction(String function, Object... parameters) {
        if (!function.equals("typeof") && !hasMethod(function))
            return null; //Don't bother continuing if we don't have the function
        try {
            localScope.get(context, function, localScope); //Check if the function exists
            Object r = localScope.get(context, function, localScope);
            if (r instanceof Function f)
                return f.call(context, globalScope, localScope, WrapperRegistry.wrapAll(localScope, parameters));
        } catch (IllegalStateException ex ) {
            ex.printStackTrace();
        } catch (EcmaError error) {
            PenguinLib.LOGGER.error("Error in " + scriptID + " with " + function + " " + Arrays.toString(parameters));
            error.printStackTrace();
        }

        return null;
    }

    public boolean callFunction(String function, Object... parameters) {
        return getResultOfFunction(function, parameters) != null;
    }

    public boolean isTrue(String function, Object... parameters) {
        Object result = getResultOfFunction(function, parameters);
        return result instanceof Boolean && ((boolean) result);
    }

    public static class Script extends Interpreter<Void> {
        public Script(ResourceLocation registryName, @Nonnull String javascript, ScriptLoader.ScriptLocation location) {
            super(registryName, javascript, location, null);
        }
    }
}

