package dev.jensderuiter.websk.skript.type.statements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import dev.jensderuiter.websk.utils.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfStatement extends WebStatement {

    private final static Pattern conditionPattern = Pattern.compile("if (.+) -> (.+)");

    private Condition condition;
    private String codeBetween;
    private String data;
    private String conditionName;

    @Override
    public @NotNull LoadingResult init(ParseResult result, @NotNull Event event) {

        final Matcher matcher = conditionPattern.matcher(result.getRawContent());
        if (!matcher.matches())
            return new UnknownPattern();
        final String rawCondition = matcher.group(1);

        conditionName = matcher.group(2);
        data = result.getData();
        condition = SkriptUtils.parseExpression(
                rawCondition, Skript.getConditions().iterator(),
                null, event);

        if (condition == null)
            return new LoadingResult(false, "Can't understand this condition: " + rawCondition);

        if (!hasEndPattern(result.getContent(), conditionName))
            return new LoadingResult(false, "You are not closing the '"+conditionName+"' condition.");

        try {
            codeBetween = getParser().content
                    .split(Pattern.quote(result.getData()))[1]
                    .split(Pattern.quote("{{/" + conditionName + "}}"))[0];
        } catch (Exception ex) {
            return new ExceptionResult(ex);
        }

        return new LoadingResult(true, true);
    }

    @Override
    public @Nullable String convert(@NotNull Event event) {
        getParser().content = getParser().content.replace(data + codeBetween + "{{/"+conditionName+"}}",
                condition.check(event) ? codeBetween : "");
        return null;
    }

    /*
    final String formattedCode = code.replaceAll("\\t", "").replaceAll( " {4}", "");

            // Matchers
            final Matcher echoMatcher = echoPattern.matcher(formattedCode);
            final Matcher loopMatcher = forPattern.matcher(formattedCode);
            final Matcher ifMatcher = ifPattern.matcher(formattedCode);

            if (formattedCode.startsWith("#")) { // Commentary node
                content = content.replace(data, "");
            } else if (echoMatcher.find()) { // Echo pattern
                final String expr = echoMatcher.group(1);
                final Expression<?> expression = SkriptUtils.parseExpression(expr, null, event);
                if (expression == null) {
                    errors.add("Cannot understand this expression: '" + expr + "'");
                    continue;
                }
                String value;
                try {
                    try {
                        value = expression.isSingle() ? expression.getSingle(event).toString() :
                                StringUtils.join(expression.getArray(event), ", ");
                    } catch (Exception ex) {
                        value = "<none>";
                    }
                } catch (NullPointerException ex) {
                    value = "<none>";
                }
                content = content.replace(data, value);

            } else if (ifMatcher.find()) {

                final String condStr = ifMatcher.group(1);
                final String condName = ifMatcher.group(2);

                final Condition condition = SkriptUtils.parseExpression(
                        condStr, Skript.getConditions().iterator(),
                        null, event);
                if (condition == null) {
                    errors.add("Can't understand this condition '"+condStr+"'");
                    continue;
                }

                final Matcher endCondMatcher = endConditionPattern.matcher(originalContent);
                if (!endCondMatcher.find()) {
                    errors.add("Unable to find end of the '"+condName+"' condition.");
                    continue;
                }

                final String codeBetween;
                try {
                    codeBetween = originalContent
                            .split(Pattern.quote(data))[1]
                            .split(Pattern.quote("{{//" + condName + "}}"))[0];
                } catch (Exception ex) {
                    continue;
                }

                final NonNullPair<List<String>, String> parseResult = parse(codeBetween, event, true);
                errors.addAll(parseResult.getFirst());
                final String codeInside = parseResult.getSecond();

                if (condition.check(event)) {
                    content = content.replace(data + codeBetween, codeInside);
                } else {
                    content = content.replace(data + codeBetween, "");
                }

            } else if (loopMatcher.find()) { // Start loop

                final String expr = loopMatcher.group(2);
                final String loopName = loopMatcher.group(3);

                final Expression<?> expression = SkriptUtils.parseExpression(expr, null, event);
                Object[] values;
                if (expression == null) {
                    values = parseVariableList(expr, event);
                } else {
                    if (expression.isSingle()) {
                        errors.add("The '"+expr+"' return a single value and can therefore not be looped.");
                        continue;
                    }
                    values = expression.getArray(event);
                }
                inLoop = true;
                final Matcher endLoopMatcher = endLoopPattern.matcher(originalContent);
                if (!endLoopMatcher.find()) {
                    errors.add("Unable to find end of the '"+expr+"' loop.");
                    continue;
                }
                final String codeBetween;
                try {
                    codeBetween = originalContent
                            .split(Pattern.quote(data))[1]
                            .split(Pattern.quote("{{/" + loopName + "}}"))[0];
                } catch (Exception ex) {
                    continue;
                }

                String codeInside = "";
                for (Object value : values) {
                    LoopValue.lastEntity = value;
                    final NonNullPair<List<String>, String> parseResult = parse(codeBetween, event, true);
                    errors.addAll(parseResult.getFirst());
                    codeInside += parseResult.getSecond();
                    LoopValue.lastEntity = null;
                }
                content = content.replace(data + codeBetween, codeInside);

            } else {

                if (formattedCode.startsWith("//")) {
                    content = content.replace(data, "");
                } else if (formattedCode.startsWith("/")) {
                    inLoop = false;
                    content = content.replace(data, "");
                } else {
                    final String value = parseVariable(formattedCode, event);
                    content = content.replace(data, value);
                }

            }
     */

}
