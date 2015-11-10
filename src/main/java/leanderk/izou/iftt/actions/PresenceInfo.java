package leanderk.izou.iftt.actions;

import leanderk.izou.iftt.IFTT;
import org.intellimate.izou.events.EventListenerModel;
import org.intellimate.izou.events.EventModel;
import org.intellimate.izou.identification.IdentificationManager;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.events.Event;
import org.intellimate.izou.sdk.frameworks.presence.events.PresenceEvent;
import org.intellimate.izou.sdk.frameworks.presence.resources.LastEncountered;
import org.intellimate.izou.sdk.util.AddOnModule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LeanderK
 * @version 1.0
 */
public class PresenceInfo extends AddOnModule implements EventListenerModel {
    public static final String ID = PresenceInfo.class.getCanonicalName();
    private final List<PresenceListener> presenceListeners = new ArrayList<>();
    private LocalDateTime lastSeen = LocalDateTime.now().minusDays(1);
    private LocalDate lastDaySeen = LocalDate.now().minusDays(1);
    /**
     * initializes the Module
     *
     * @param context the current Context
     */
    public PresenceInfo(Context context) {
        super(context, ID);
        getContext().getEvents().registerEventListener(Collections.singletonList(PresenceEvent.ID), this);
    }

    BooleanConsumerHolder isFirstEncounterDay(Consumer<Boolean> callback) {
        IFTT.write("checking if it is first Encounter");
        IFTT.write("lastDaySeen = " + lastDaySeen);
        IFTT.write("now = " + LocalDate.now());
        if (lastDaySeen.equals(LocalDate.now()))
            return new BooleanConsumerHolder(false, callback);
        IFTT.write("lastDaySeen != now");
        IFTT.write("lastSeen = " + lastSeen);
        IFTT.write("lastSeen.plus(4, ChronoUnit.HOURS) = " + lastSeen.plus(4, ChronoUnit.HOURS));
        if (lastSeen.plus(4, ChronoUnit.HOURS).isBefore(LocalDateTime.now())) {
            IFTT.write("lastSeen.plus(4, ChronoUnit.HOURS) is before now");
            return new BooleanConsumerHolder(true, callback.andThen(success -> {
                IFTT.write("callback");
                IFTT.write("success = " + success);
                StackTraceElement[] stackTrace = new Exception().getStackTrace();
                IFTT.write("trace");
                for (int i = 0; i < stackTrace.length; i++) {
                    IFTT.write(stackTrace[i].getFileName() + ":" + stackTrace[i].getClassName() + ":"
                            + stackTrace[i].getMethodName() + ":" + stackTrace[i].getLineNumber());
                }
                if (success) {
                    lastDaySeen = LocalDate.now();
                }
            }));
        }
        return new BooleanConsumerHolder(false, callback);
    }

    public void addPresenceListener(PresenceListener presenceListener) {
        presenceListeners.add(presenceListener);
    }

    public void removePresenceListener(PresenceListener presenceListener) {
        presenceListeners.remove(presenceListener);
    }

    public void test() {
        Consumer<Event> doThisForEvent = event -> {
            Consumer<Boolean> callback = success -> {};
            boolean strict = event.containsDescriptor(PresenceEvent.STRICT_DESCRIPTOR);
            boolean firstEncounterDay = false;
            if (strict) {
                BooleanConsumerHolder booleanConsumerHolder = isFirstEncounterDay(callback);
                firstEncounterDay = booleanConsumerHolder.aBoolean;
                callback = booleanConsumerHolder.callback
                        .andThen(success -> lastSeen = LocalDateTime.now());
            }
            boolean known = event.containsDescriptor(PresenceEvent.KNOWN_DESCRIPTOR);
            boolean firstEncounter = event.containsDescriptor(PresenceEvent.FIRST_ENCOUNTER_DESCRIPTOR);
            long lastSeen = LastEncountered.getTimePassed(event).orElse(1000000000L);
            final Consumer<Boolean> finalCallback = callback;
            final boolean finalFirstEncounterDay = firstEncounterDay;
            presenceListeners.forEach(presenceListener -> presenceListener.presenceFired(event, strict, known, firstEncounter, finalFirstEncounterDay, lastSeen, finalCallback));
        };
        Supplier<Event> getStrictFirstEncounterPresenceEvent = () -> IdentificationManager.getInstance()
                .getIdentification(this)
                .flatMap(id -> PresenceEvent.createPresenceEvent(id, true, true, true, new ArrayList<>(), 10000L))
                .get();

        lastDaySeen = LocalDate.now().minusDays(1);
        lastSeen = LocalDateTime.now().minusDays(1);
        doThisForEvent.accept(getStrictFirstEncounterPresenceEvent.get());
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastDaySeen = LocalDate.now().minusDays(1);
        lastSeen = LocalDateTime.now().minusDays(1);
        doThisForEvent.accept(getStrictFirstEncounterPresenceEvent.get());
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        doThisForEvent.accept(getStrictFirstEncounterPresenceEvent.get());
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoked when an activator-event occurs.
     *
     * @param event an instance of Event
     */
    @Override
    public void eventFired(EventModel event) {
        Consumer<Boolean> callback = success -> {};
        boolean strict = event.containsDescriptor(PresenceEvent.STRICT_DESCRIPTOR);
        boolean firstEncounterDay = false;
        if (strict) {
            BooleanConsumerHolder booleanConsumerHolder = isFirstEncounterDay(callback);
            firstEncounterDay = booleanConsumerHolder.aBoolean;
            callback = booleanConsumerHolder.callback
                    .andThen(success -> lastSeen = LocalDateTime.now());
        }
        boolean known = event.containsDescriptor(PresenceEvent.KNOWN_DESCRIPTOR);
        boolean firstEncounter = event.containsDescriptor(PresenceEvent.FIRST_ENCOUNTER_DESCRIPTOR);
        long lastSeen = LastEncountered.getTimePassed(event).orElse(1000000000L);
        final Consumer<Boolean> finalCallback = callback;
        final boolean finalFirstEncounterDay = firstEncounterDay;
        presenceListeners.forEach(presenceListener -> presenceListener.presenceFired(event, strict, known, firstEncounter, finalFirstEncounterDay, lastSeen, finalCallback));
    }

    class BooleanConsumerHolder {
        final boolean aBoolean;
        final Consumer<Boolean> callback;

        public BooleanConsumerHolder(boolean aBoolean, Consumer<Boolean> callback) {
            this.aBoolean = aBoolean;
            this.callback = callback;
        }
    }
}
