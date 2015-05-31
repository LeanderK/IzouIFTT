package leanderk.izou.iftt.actions;

/**
 * an Action flow starts with an SourceAction, my be abortet by an ConditionAction and in the end trigger an
 * TargetAction
 * @author LeanderK
 * @version 1.0
 */
public class ActionFlow {
    private SourceAction sourceAction;
    private ConditionAction conditionAction;
    private TargetAction targetAction;

    public ActionFlow(SourceAction sourceAction, ConditionAction conditionAction, TargetAction targetAction) {
        this.sourceAction = sourceAction;
        this.conditionAction = conditionAction;
        this.targetAction = targetAction;
    }

    /**
     * starts the action flow
     */
    public void start() {
        if (conditionAction.evaluate())
            targetAction.execute();
    }
}
