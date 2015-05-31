package leanderk.izou.iftt.actions.condition;

import leanderk.izou.iftt.actions.Action;
import leanderk.izou.iftt.actions.ActionFlow;
import leanderk.izou.iftt.actions.ConditionAction;
import org.intellimate.izou.sdk.Context;

import java.util.List;

/**
 * @author LeanderK
 * @version 1.0
 */
public class Or extends Action implements ConditionAction {
    public static final String ID = Or.class.getCanonicalName();
    private final List<ConditionAction> conditionActions;

    public Or(ActionFlow actionFlow, Context context, List<ConditionAction> conditionActions) throws IllegalArgumentException {
        super(actionFlow, context, ID);
        this.conditionActions = conditionActions;
    }

    /**
     * true if the Action-Flow should continue
     *
     * @return true if continue, false if abort
     */
    @Override
    public boolean evaluate() {
        return conditionActions.stream()
                .anyMatch(ConditionAction::evaluate);
    }
}