package leanderk.izou.iftt;

import leanderk.izou.iftt.actions.*;
import leanderk.izou.iftt.actions.condition.*;
import leanderk.izou.iftt.actions.source.AfterListening;
import leanderk.izou.iftt.actions.source.Present;
import leanderk.izou.iftt.actions.source.WhileListening;
import leanderk.izou.iftt.actions.target.FireEvent;
import leanderk.izou.iftt.actions.target.PlayMusic;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.util.AddOnModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author LeanderK
 * @version 1.0
 */
public class Parser extends AddOnModule {
    public static final String ID = Parser.class.getCanonicalName();

    public Parser(Context context) {
        super(context, ID);
    }

    public List<ActionFlow> parseFile(File file, PresenceInfo presenceInfo, AtomicInteger atomicInteger) {
        Function<String, ActionFlow> parse = line -> {
            String[] parts = line.split("->");
            if (parts.length != 2 && parts.length != 3)
                return null;
            ActionFlow actionFlow = new ActionFlow(presenceInfo, atomicInteger);
            SourceAction sourceAction = getSourceAction(parts[0].trim(), actionFlow);
            if (sourceAction == null)
                return null;
            actionFlow = actionFlow.setSourceAction(sourceAction);
            if (parts.length == 2) {
                TargetAction targetAction = getTargetAction(parts[1].trim(), actionFlow);
                if (targetAction == null)
                    return null;
                actionFlow = actionFlow.setTargetAction(targetAction);
                return actionFlow;
            } else {
                ConditionAction conditionAction = getConditionAction(parts[1].trim(), actionFlow);
                if (conditionAction == null)
                    return null;
                TargetAction targetAction = getTargetAction(parts[2].trim(), actionFlow);
                if (targetAction == null)
                    return null;
                actionFlow = actionFlow.setConditionAction(conditionAction);
                actionFlow = actionFlow.setTargetAction(targetAction);
                return actionFlow;
            }
        };
        try {
            return Files.lines(file.toPath())
                    .filter(line -> !line.startsWith("#"))
                    .filter(line -> !line.isEmpty())
                    .map(parse)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private <V> V getAction (String raw, BiFunction<String, String, V> map) {
        raw = raw.trim();
        Pattern pattern = Pattern.compile("(?<name>\\w+)\\((?<parameter>.*)\\)");
        Matcher matcher = pattern.matcher(raw);
        if (!matcher.matches())
            return null;
        String name = matcher.group("name");
        String parameter = matcher.group("parameter");
        try {
            V apply = map.apply(name, parameter);
            if (apply == null)
                error("could not get action for input " + raw);
            return apply;
        } catch (IllegalArgumentException e) {
            error("error while trying to pares " + raw, e);
            return null;
        }
    }

    private ConditionAction getConditionAction(String raw, ActionFlow actionFlow) {
        Function<String, ConditionAction> getAction = input -> getAction(input, (name, parameter) -> {
           switch (name) {
               case "after": return new After(actionFlow, getContext(), parameter);
               case "before": return new Before(actionFlow, getContext(), parameter);
               case "present": return new PresentCondition(actionFlow, getContext(), parameter);
               default: return null;
           }
        });
        if (raw.startsWith("(") && raw.endsWith(")")) {
            String actions = raw.substring(1, raw.length() - 1);
            List<ConditionAction> actionList = getActionsList(actions, getAction);
            return new And(actionFlow, getContext(), actionList);
        } else if (raw.startsWith("|") && raw.endsWith("|")) {
            String actions = raw.substring(1, raw.length() - 1);
            List<ConditionAction> actionList = getActionsList(actions, getAction);
            return new Or(actionFlow, getContext(), actionList);
        } else {
            return getAction.apply(raw);
        }
    }

    private <V> List<V> getActionsList(String actions, Function<String, V> getAction) {
        int openingBrackets = 0;
        int start = 0;
        List<V> conditionActions = new ArrayList<>();
        for (int i = 0; i < actions.length(); i++) {
            char c = actions.charAt(i);
            if (c == '(') {
                openingBrackets++;
            } else if (c == ')') {
                openingBrackets--;
                if (openingBrackets == 0) {
                    String actionRaw = actions.substring(start, i + 1).trim();
                    V action = getAction.apply(actionRaw);
                    if (action != null)
                        conditionActions.add(action);
                    else
                        error("unable to parse " + actionRaw);
                    start = i + 1;
                }
            } else if ((i == start) && c == ',') {
                start++;
            }
        }
        return conditionActions;
    }

    private TargetAction getTargetAction(String raw, ActionFlow actionFlow) {
        return getAction(raw, (name, parameter) -> {
            switch (name) {
                case "fire": return new FireEvent(parameter, actionFlow, getContext());
                case "play": return new PlayMusic(parameter, actionFlow, getContext());
                default: return null;
            }
        });
    }

    private SourceAction getSourceAction(String raw, ActionFlow actionFlow) {
        return getAction(raw, (name, parameter) -> {
            switch (name) {
                case "present":
                    return new Present(parameter, actionFlow, getContext());
                case "after":
                    return new AfterListening(parameter, actionFlow, getContext());
                case "before":
                    return new WhileListening(parameter, actionFlow, getContext());
                default:
                    return null;
            }
        });
    }
}
