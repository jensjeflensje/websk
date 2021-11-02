package dev.jensderuiter.websk.utils.parser;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.ExprLoopValue;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.NonNullPair;
import ch.njol.util.StringUtils;
import dev.jensderuiter.websk.skript.expression.LoopValue;
import dev.jensderuiter.websk.utils.SkriptUtils;
import org.bukkit.event.Event;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sky
 */
public class ParserFactory {

    private final Pattern codePattern = Pattern.compile("\\{\\{([^}]+)}}", Pattern.DOTALL);
    private final Pattern echoPattern = Pattern.compile("show (.+)");
    private final Pattern forPattern = Pattern.compile("(for|loop) ([^>]+) > (.+)");
    private final Pattern endLoopPattern = Pattern.compile("\\{\\{\\/([^}]+)}}");
    private static final ParserFactory instance = new ParserFactory();

    public static ParserFactory get() {
        return instance;
    }

    private boolean inLoop = false;
    public NonNullPair<List<String>, String> parse(String content, Event event, boolean subGroup) {
        if (content.isEmpty())
            return new NonNullPair<>(new ArrayList<>(), "");

        final String originalContent = content;
        final List<String> errors = new ArrayList<>();

        final Matcher codeMatcher = codePattern.matcher(content);

        while (codeMatcher.find()) {
            final String data = codeMatcher.group();
            final String code = codeMatcher.group(1);
            final String formattedCode = code.replaceAll("\\t", "").replaceAll( " {4}", "");

            // Matchers
            final Matcher echoMatcher = echoPattern.matcher(formattedCode);
            final Matcher loopMatcher = forPattern.matcher(formattedCode);

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

                if (formattedCode.startsWith("/")) {
                    inLoop = false;
                    content = content.replace(data, "");

                } else {
                    final String value = parseVariable(formattedCode, event);
                    content = content.replace(data, value);
                }

            }
        }

        return new NonNullPair<>(errors, content);
    }

    private final Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
    public String escape(String str) {
        return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
    }

    public Object[] parseVariableList(String varName, Event event) {
        final Object value = Variables.getVariable(varName, event, true);
        if (!(value instanceof Map))
            return new Object[0];
        return ((Map<?, ?>) value).values().toArray();
    }

    public String parseVariable(String varName, Event event) {
        final Object value = Variables.getVariable(varName, event, true);
        Object str;
        try {
            str = ((Map<?, ?>) value)
                    .values()
                    .stream()
                    .map(Object::toString)
                    .toArray(String[]::new);
        } catch (ClassCastException ex) {
            str = value.toString();
        } catch (NullPointerException ex) {
            str = "<none>";
        }
        return str.toString();
    }
}
