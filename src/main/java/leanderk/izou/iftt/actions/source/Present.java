package leanderk.izou.iftt.actions.source;

import leanderk.izou.iftt.actions.Action;
import leanderk.izou.iftt.actions.ActionFlow;
import leanderk.izou.iftt.actions.SourceAction;
import org.intellimate.izou.events.EventListenerModel;
import org.intellimate.izou.events.EventModel;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.presence.events.PresenceEvent;

import java.time.LocalDate;
import java.util.Collections;

/**
 * @author LeanderK
 * @version 1.0
 */
public class Present extends Action implements SourceAction, EventListenerModel {
    public static final String ID = Present.class.getCanonicalName();
    public static final String NON_STRICT = "nonStrict";
    public static final String EVERY_TIME = "everyTime";
    public static final String UNKNOWN = "unknown";
    public static final String FIRST_DAY = "firstEncounterDay";
    private LocalDate lastSeen;
    private final boolean nonStrict;
    private final boolean unknown;
    private final boolean everyTime;
    private final boolean firstEncounterDay;

    public Present(String parameter, ActionFlow actionFlow, Context context) throws IllegalArgumentException {
        super(actionFlow, context, ID);
        boolean nonStrict = false;
        boolean unknown = false;
        boolean everyTime = false;
        boolean firstEncounterDay = false;
        for (String command : parameter.split(",")) {
            command = command.trim();
            switch (command) {
                case NON_STRICT: nonStrict = true;
                    break;
                case EVERY_TIME: everyTime = true;
                    break;
                case UNKNOWN: unknown = true;
                    break;
                case FIRST_DAY: firstEncounterDay = true;
                    break;
            }
        }
        this.nonStrict = nonStrict;
        this.unknown = unknown;
        this.everyTime = everyTime;
        this.firstEncounterDay = firstEncounterDay;
        getContext().getEvents().registerEventListener(Collections.singletonList(PresenceEvent.ID), this);
        actionFlow.addToUnregisterCallback(actionFlow1 -> context.getEvents().unregisterEventListener(this));
    }

    /**
     * Invoked when an activator-event occurs.
     *
     * @param event an instance of Event
     */
    @Override
    public void eventFired(EventModel event) {
        if (firstEncounterDay && !lastSeen.plusDays(1).isEqual(LocalDate.now()))
            return;
        if (nonStrict && !event.containsDescriptor(PresenceEvent.STRICT_DESCRIPTOR))
            return;
        if (unknown && !event.containsDescriptor(PresenceEvent.KNOWN_DESCRIPTOR))
            return;
        if (everyTime && !event.containsDescriptor(PresenceEvent.FIRST_ENCOUNTER_DESCRIPTOR))
            return;
        lastSeen = LocalDate.now();
        getActionFlow().start();
    }
}