package leanderk.izou.iftt.actions;

import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.util.AddOnModule;

/**
 * base implementation for all Actions
 * @author LeanderK
 * @version 1.0
 */
public class Action extends AddOnModule {
    private final ActionFlow actionFlow;

    public Action(ActionFlow actionFlow, Context context, String ID) throws IllegalArgumentException {
        super(context, ID);
        this.actionFlow = actionFlow;
    }

    public ActionFlow getActionFlow() {
        return actionFlow;
    }
}
