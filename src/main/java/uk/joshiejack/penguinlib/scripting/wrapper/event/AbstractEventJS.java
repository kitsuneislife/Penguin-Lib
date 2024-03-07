package uk.joshiejack.penguinlib.scripting.wrapper.event;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
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
        if (event instanceof ICancellableEvent cancellableEvent)
            cancellableEvent.setCanceled(true);
    }
}
