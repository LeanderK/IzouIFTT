package leanderk.izou.iftt.actions.source;

import leanderk.izou.iftt.actions.Action;
import leanderk.izou.iftt.actions.ActionFlow;
import leanderk.izou.iftt.actions.PresenceListener;
import leanderk.izou.iftt.actions.SourceAction;
import org.intellimate.izou.events.EventModel;
import org.intellimate.izou.sdk.Context;

import java.util.function.Consumer;

/**
 * @author LeanderK
 * @version 1.0
 */
public class Present extends Action implements SourceAction, PresenceListener {
    public static final String ID = Present.class.getCanonicalName();
    public static final String NON_STRICT = "nonStrict";
    public static final String EVERY_TIME = "everyTime";
    public static final String UNKNOWN = "unknown";
    public static final String FIRST_DAY = "firstEncounterDay";
    public static final String NOT_FIRST_DAY = "notFirstEncounterDay";
    public static final String LAST_SEEN = "lastSeen";
    private final boolean nonStrict;
    private final boolean unknown;
    private final boolean everyTime;
    private final boolean firstEncounterDay;
    private final boolean notFirstEncounterDay;
    private final int lastSeenThreshold;

    public Present(String parameter, ActionFlow actionFlow, Context context) throws IllegalArgumentException {
        super(actionFlow, context, ID);
        boolean nonStrict = false;
        boolean unknown = false;
        boolean everyTime = false;
        boolean firstEncounterDay = false;
        boolean notFirstEncounterDay = false;
        int lastSeenThreshold = -1;
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
                case NOT_FIRST_DAY: notFirstEncounterDay = true;
                    break;
            }
            if (command.startsWith(LAST_SEEN)) {
                String[] split = command.split("=");
                if (split.length < 2)
                    continue;
                try {
                    lastSeenThreshold = Integer.parseInt(split[1]) * 60;
                } catch (NumberFormatException e) {
                    debug("unable to parse: " + command, e);
                }
            }
        }
        this.nonStrict = nonStrict;
        this.unknown = unknown;
        this.everyTime = everyTime;
        this.firstEncounterDay = firstEncounterDay;
        this.lastSeenThreshold = lastSeenThreshold;
        this.notFirstEncounterDay = notFirstEncounterDay;
        actionFlow.getPresenceInfo().addPresenceListener(this);
        actionFlow.addToUnregisterCallback(actionFlow1 -> actionFlow.getPresenceInfo().removePresenceListener(this));
    }

    @Override
    public void presenceFired(EventModel eventModel, boolean strict, boolean knownUser, boolean firstEncounter,
                              boolean firstEncounterDay, long lastEncounter, Consumer<Boolean> callback) {
        if (notFirstEncounterDay && firstEncounterDay) {
            callback.accept(false);
            return;
        }
        if (this.firstEncounterDay && !firstEncounterDay) {
            callback.accept(false);
            return;
        }
        if (!nonStrict && !strict) {
            callback.accept(false);
            return;
        }
        if (!unknown && !knownUser) {
            callback.accept(false);
            return;
        }
        if (!everyTime) {
            if (!firstEncounter) {
                callback.accept(false);
                return;
            }
        }
        if (lastSeenThreshold != -1 && lastEncounter < lastSeenThreshold) {
            callback.accept(false);
            return;
        }
        getActionFlow().start(callback);
    }
}