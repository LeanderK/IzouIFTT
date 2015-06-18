package leanderk.izou.iftt.actions.source;

import leanderk.izou.iftt.actions.Action;
import leanderk.izou.iftt.actions.ActionFlow;
import leanderk.izou.iftt.actions.SourceAction;
import org.intellimate.izou.events.EventListenerModel;
import org.intellimate.izou.events.EventModel;
import org.intellimate.izou.sdk.Context;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LeanderK
 * @version 1.0
 */
public class AfterListening extends Action implements SourceAction, EventListenerModel {
    public static final String ID = AfterListening.class.getCanonicalName();

    public AfterListening(String parameter, ActionFlow actionFlow, Context context) throws IllegalArgumentException {
        super(actionFlow, context, ID);
        List<String> descriptors = Arrays.stream(parameter.split(","))
                .map(String::trim)
                .map(string -> {
                    String eventID = context.getPropertiesAssistant().getEventPropertiesAssistant().getEventID(parameter);
                    if (eventID != null) {
                        return eventID;
                    } else {
                        return parameter;
                    }
                }).collect(Collectors.toList());
        context.getEvents().registerEventFinishedListener(descriptors, this);
        actionFlow.addToUnregisterCallback(actionFlow1 -> context.getEvents().unregisterEventListener(this));
    }

    /**
     * Invoked when an activator-event occurs.
     *
     * @param event an instance of Event
     */
    @Override
    public void eventFired(EventModel event) {
        getActionFlow().start(callback);
    }
}
