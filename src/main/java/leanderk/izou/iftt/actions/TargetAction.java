package leanderk.izou.iftt.actions;

import java.util.function.Consumer;

/**
 * the Target actions are called in the end of an action flow
 * @author LeanderK
 * @version 1.0
 */
public interface TargetAction {
    /**
     * gets called.
     */
    void execute(Consumer<Boolean> callback);
}
