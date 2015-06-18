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
    private SourceAction sourceAction;
    private ConditionAction conditionAction;
    private TargetAction targetAction;
    private final List<Consumer<ActionFlow>> unregisters = new ArrayList<>();
    private final PresenceInfo presenceInfo;

    public ActionFlow(PresenceInfo presenceInfo) {
        this(null, () -> true, null, presenceInfo);
    }

    public ActionFlow(SourceAction sourceAction, ConditionAction conditionAction, TargetAction targetAction, PresenceInfo presenceInfo) {
        this.sourceAction = sourceAction;
        this.conditionAction = conditionAction;
        this.targetAction = targetAction;
        this.presenceInfo = presenceInfo;
    }

    public ActionFlow setSourceAction(SourceAction sourceAction) {
        this.sourceAction = sourceAction;
        return this;
    }

    public ActionFlow setConditionAction(ConditionAction conditionAction) {
        this.conditionAction = conditionAction;
        return this;
    }

    public ActionFlow setTargetAction(TargetAction targetAction) {
        this.targetAction = targetAction;
        return this;
    }

    public void addToUnregisterCallback(Consumer<ActionFlow> callback) {
        if (callback != null)
            unregisters.add(callback);
    }

    public PresenceInfo getPresenceInfo() {
        return presenceInfo;
    }

    public void unregister() {
        unregisters.forEach(consumer -> consumer.accept(this));
    }

    /**
     * starts the action flow
     * @param callback
     */
    public void start(Consumer<Boolean> callback) {
        if (targetAction == null) {
            callback.accept(true);
            return;
        }
        if (conditionAction.evaluate()) {
            targetAction.execute(callback);
            return;
        }
        callback.accept(false);
    }
}
