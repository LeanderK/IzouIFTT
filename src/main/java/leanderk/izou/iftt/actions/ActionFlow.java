package leanderk.izou.iftt.actions;

import leanderk.izou.iftt.IFTT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final AtomicInteger atomicInteger;

    public ActionFlow(PresenceInfo presenceInfo, AtomicInteger atomicInteger) {
        this(null, () -> true, null, presenceInfo, atomicInteger);
    }

    public ActionFlow(SourceAction sourceAction, ConditionAction conditionAction, TargetAction targetAction, PresenceInfo presenceInfo, AtomicInteger atomicInteger) {
        this.sourceAction = sourceAction;
        this.conditionAction = conditionAction;
        this.targetAction = targetAction;
        this.presenceInfo = presenceInfo;
        this.atomicInteger = atomicInteger;
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

    public int getNextUniqueNumer() {
        return atomicInteger.incrementAndGet();
    }

    public void unregister() {
        unregisters.forEach(consumer -> consumer.accept(this));
    }

    /**
     * starts the action flow
     * @param callback
     */
    public void start(Consumer<Boolean> callback) {
        IFTT.write("ActionFlow started");
        if (targetAction == null) {
            IFTT.write("targetAction is null");
            callback.accept(true);
            return;
        }
        IFTT.write("targetAction is not null");
        if (conditionAction.evaluate()) {
            IFTT.write("conditionAction is true");
            targetAction.execute(callback);
        } else {
            IFTT.write("conditionAction is false");
            callback.accept(false);
        }
    }
}
