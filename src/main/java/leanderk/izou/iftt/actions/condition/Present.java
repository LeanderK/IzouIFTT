package leanderk.izou.iftt.actions.condition;

import leanderk.izou.iftt.actions.Action;
import leanderk.izou.iftt.actions.ActionFlow;
import leanderk.izou.iftt.actions.ConditionAction;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.presence.consumer.PresenceResourceUser;

/**
 * @author LeanderK
 * @version 1.0
 */
public class Present extends Action implements ConditionAction, PresenceResourceUser {
    public static final String ID = Present.class.getCanonicalName();
    public static final String STRICT = "strict";
    private final boolean strict;

    public Present(ActionFlow actionFlow, Context context, String parameter) throws IllegalArgumentException {
        super(actionFlow, context, ID);
        strict = parameter.equals(STRICT);
    }

    /**
     * true if the Action-Flow should continue
     *
     * @return true if continue, false if abort
     */
    @Override
    public boolean evaluate() {
        return isPresent(strict);
    }
}
