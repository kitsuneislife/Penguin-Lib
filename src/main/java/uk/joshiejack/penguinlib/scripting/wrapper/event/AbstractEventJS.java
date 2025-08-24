package uk.joshiejack.penguinlib.scripting.wrapper.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Cancelable;
import uk.joshiejack.penguinlib.scripting.wrapper.AbstractJS;

public abstract class AbstractEventJS<C extends Event> extends AbstractJS<C> {
    protected C object;

    public AbstractEventJS(C object) {
        super(object);
        this.object = object;
    }

    protected C event() {
        return penguinScriptingObject;
    }

    public void allow() {
        C event = penguinScriptingObject;
        if (event.hasResult()) event.setResult(Event.Result.ALLOW);
    }

    public void deny() {
        C event = penguinScriptingObject;
        if (event.hasResult()) event.setResult(Event.Result.DENY);
    }

    public void cancel() {
        C event = penguinScriptingObject;
        // For Forge 1.20.1, ICancellableEvent was removed
        // Cancel functionality needs to be handled differently
        if (event.isCancelable()) {
            event.setCanceled(true);
        }
    }
}
