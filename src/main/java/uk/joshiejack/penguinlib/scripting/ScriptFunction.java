package uk.joshiejack.penguinlib.scripting;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;

public interface ScriptFunction {
    Object call(Object... args);

    static Function of(ScriptFunction function) {
        return new BaseFunction() {
            @Override
            public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                return function.call(args);
            }
        };
    }
}
