package leanderk.izou.iftt.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * an Action flow starts with an SourceAction, may be canceled by an ConditionAction and in the end trigger an
 * TargetAction
 * @author LeanderK
 * @version 1.0
 */
public class ActionFlow {
    private final SourceAction sourceAction;
    private final ConditionAction conditionAction;
    private final TargetAction targetAction;
    private final List<Consumer<ActionFlow>> unregisters = new ArrayList<>();

    public ActionFlow() {
        this(null, () -> true, null);
    }

    public ActionFlow(SourceAction sourceAction, ConditionAction conditionAction, TargetAction targetAction) {
        this.sourceAction = sourceAction;
        this.conditionAction = conditionAction;
        this.targetAction = targetAction;
    }

    public ActionFlow setSourceAction(SourceAction sourceAction) {
        return new ActionFlow(sourceAction, conditionAction, targetAction);
    }

    public ActionFlow setConditionAction(ConditionAction conditionAction) {
        return new ActionFlow(sourceAction, conditionAction, targetAction);
    }

    public ActionFlow setTargetAction(TargetAction targetAction) {
        return new ActionFlow(sourceAction, conditionAction, targetAction);
    }

    public void addToUnregisterCallback(Consumer<ActionFlow> callback) {
        if (callback != null)
            unregisters.add(callback);
    }

    public void unregister() {
        unregisters.forEach(consumer -> consumer.accept(this));
    }

    /**
     * starts the action flow
     */
    public void start() {
        if (targetAction == null)
            return;
        if (conditionAction.evaluate())
            targetAction.execute();
    }
}
