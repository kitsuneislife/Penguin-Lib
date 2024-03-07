package uk.joshiejack.penguinlib.scripting;

import com.google.common.collect.Sets;
import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;

import java.util.Set;

public class Sandbox implements ClassShutter {
    private static final Set<String> ALLOWED = Sets.newHashSet();
    private static final Set<String> DENIED = Sets.newHashSet();

    public static void allow(String clazz) {
        ALLOWED.add(clazz);
    }
    public static void deny (String clazz) {
        DENIED.add(clazz);
    }

    public static Context enter() {
        Context context = Context.enter();
        context.setClassShutter(new Sandbox());
        return context;
    }

    @Override
    public boolean visibleToScripts(String s, int i) {
        //Deny everything that starts with whatever is in the denied list
        //Unless the full name is in the allowed list
        if (DENIED.stream().anyMatch(s::startsWith)) {
            return ALLOWED.contains(s);
        } else return ALLOWED.stream().anyMatch(s::startsWith);
    }
}
