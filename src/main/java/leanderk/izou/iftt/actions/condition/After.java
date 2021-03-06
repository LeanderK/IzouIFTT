package leanderk.izou.iftt.actions.condition;

import leanderk.izou.iftt.IFTT;
import leanderk.izou.iftt.actions.Action;
import leanderk.izou.iftt.actions.ActionFlow;
import leanderk.izou.iftt.actions.ConditionAction;
import org.intellimate.izou.sdk.Context;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author LeanderK
 * @version 1.0
 */
public class After extends Action implements ConditionAction {
    public static final String ID = After.class.getCanonicalName();
    private final LocalTime time;

    public After(ActionFlow actionFlow, Context context, String parameter) throws IllegalArgumentException {
        super(actionFlow, context, ID);
        try {
            time = LocalTime.parse(parameter, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("exception while parsing: " + parameter, e);
        }
    }

    /**
     * true if the Action-Flow should continue
     *
     * @return true if continue, false if abort
     */
    @Override
    public boolean evaluate() {
        IFTT.write(LocalTime.now().toString() + " is after " + time);
        return LocalTime.now().isAfter(time);
    }
}
