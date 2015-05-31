package leanderk.izou.iftt.actions.condition;

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
public class Before extends Action implements ConditionAction {
    public static final String ID = Before.class.getCanonicalName();
    private final LocalTime time;

    public Before(ActionFlow actionFlow, Context context, String parameter) throws IllegalArgumentException {
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
        return LocalTime.now().isBefore(time);
    }
}
