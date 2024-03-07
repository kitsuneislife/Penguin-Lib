package uk.joshiejack.penguinlib.scripting.wrapper;

import dev.latvian.mods.rhino.util.HideFromJS;

public class AbstractJS<C>  {
    @HideFromJS
    protected final C penguinScriptingObject;

    @HideFromJS
    public AbstractJS(C object) {
        this.penguinScriptingObject = object;
    }

    @HideFromJS
    public C get() {
        return penguinScriptingObject;
    }
}
