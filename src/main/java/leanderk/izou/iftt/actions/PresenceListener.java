package leanderk.izou.iftt.actions;

import org.intellimate.izou.events.EventModel;

import java.util.function.Consumer;

/**
 * @author LeanderK
 * @version 1.0
 */
public interface PresenceListener {
    void presenceFired(EventModel eventModel, boolean strict, boolean knownUser, boolean firstEncounter,
                       boolean firstEncounterDay, long lastEncounter, Consumer<Boolean> callback);
}
