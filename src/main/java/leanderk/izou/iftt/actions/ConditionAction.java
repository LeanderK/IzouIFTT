package leanderk.izou.iftt.actions;

/**
 * the condition action are used to maipulate an action flow
 * @author LeanderK
 * @version 1.0
 */
public interface ConditionAction {
    /**
     * true if the Action-Flow should continue
     * @return true if continue, false if abort
     */
    boolean evaluate();
}
