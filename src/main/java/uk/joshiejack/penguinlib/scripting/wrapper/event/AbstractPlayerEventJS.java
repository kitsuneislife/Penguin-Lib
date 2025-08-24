package uk.joshiejack.penguinlib.scripting.wrapper.event;

import net.minecraftforge.event.entity.player.PlayerEvent;
import uk.joshiejack.penguinlib.scripting.wrapper.WrapperRegistry;
import uk.joshiejack.penguinlib.scripting.wrapper.PlayerJS;

public class AbstractPlayerEventJS<C extends PlayerEvent> extends AbstractEventJS<C> {
    public AbstractPlayerEventJS(C object) {
        super(object);
    }

    public PlayerJS player() {
        return WrapperRegistry.wrap(penguinScriptingObject.getEntity());
    }
}
