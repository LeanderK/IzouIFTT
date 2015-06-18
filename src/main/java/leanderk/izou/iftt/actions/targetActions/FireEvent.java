package leanderk.izou.iftt.actions.targetActions;

import leanderk.izou.iftt.actions.Action;
import leanderk.izou.iftt.actions.ActionFlow;
import leanderk.izou.iftt.actions.TargetAction;
import org.intellimate.izou.events.EventLifeCycle;
import org.intellimate.izou.identification.IdentificationManager;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.events.CommonEvents;
import org.intellimate.izou.sdk.events.Event;

import java.util.Optional;
import java.util.function.Consumer;


/**
 * this target fires an Event
 * @author LeanderK
 * @version 1.0
 */
public class FireEvent extends Action implements TargetAction, org.intellimate.izou.sdk.util.FireEvent {
    public static final String ID = FireEvent.class.getCanonicalName();
    private String descriptor;

    public FireEvent(String parameter, ActionFlow actionFlow, Context context) {
        super(actionFlow, context, ID);
        parameter = parameter.trim();
        String eventID = context.getPropertiesAssistant().getEventPropertiesAssistant().getEventID(parameter);
        if (eventID != null) {
            descriptor = eventID;
        } else {
            descriptor = parameter;
        }
    }

    /**
     * gets called.
     */
    @Override
    public void execute(Consumer<Boolean> callback) {
        Optional<Boolean> optional = IdentificationManager.getInstance().getIdentification(this)
                .flatMap(id -> Event.createEvent(CommonEvents.Type.RESPONSE_TYPE, id))
                .map(event -> event.addDescriptor(descriptor))
                .map(event -> {
                    event.addEventLifeCycleListener(EventLifeCycle.APPROVED, eventLifeCycle -> callback.accept(true));
                    return event;
                })
                .map(event -> {
                    event.addEventLifeCycleListener(EventLifeCycle.CANCELED, eventLifeCycle -> callback.accept(false));
                    return event;
                })
                .map(this::fire);
        if (!optional.isPresent() || !optional.get()) {
            callback.accept(false);
        }
    }
}
