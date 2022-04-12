package dev.jensderuiter.websk.utils.parser;

import ch.njol.skript.variables.Variables;
import ch.njol.util.NonNullPair;
import dev.jensderuiter.websk.skript.type.statements.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sky
 */
public class ParserFactory {

    private final List<Class<? extends WebStatement>> registeredStatements;

    private final Pattern codePattern = Pattern.compile("\\{\\{([^}]+)}}", Pattern.DOTALL);
    private final Pattern echoPattern = Pattern.compile("show (.+)");
    private final Pattern forPattern = Pattern.compile("(for|loop) (.+) -> (.+)");
    private final Pattern ifPattern = Pattern.compile("if (.+) -> (.+)");
    private final Pattern endLoopPattern = Pattern.compile("\\{\\{/([^}]+)}}");
    private final Pattern endConditionPattern = Pattern.compile("\\{\\{/{2}([^}]+)}}");
    private static final ParserFactory instance = new ParserFactory();

    public ParserFactory() {
        registeredStatements = new ArrayList<>();
        registeredStatements.add(ShowStatement.class);
        registeredStatements.add(CommentStatement.class);
        registeredStatements.add(IfStatement.class);
        registeredStatements.add(ClosingStatement.class);
    }

    public static ParserFactory get() {
        return instance;
    }

    public String content;
    public List<String> errors;

    public NonNullPair<List<String>, String> parse(String raw, Event event) {
        content = raw;
        errors = new ArrayList<>();

        if (content.isEmpty())
            return new NonNullPair<>(new ArrayList<>(), "");

        final Matcher codeMatcher = codePattern.matcher(content);

        core: while (codeMatcher.find()) {
            final String data = codeMatcher.group();
            final String code = codeMatcher.group(1);
            for (Class<? extends WebStatement> cStatement : registeredStatements) {

                final WebStatement statement = construct(cStatement);
                if (statement == null)
                    throw new UnsupportedOperationException("One of the registered statement ("+cStatement.getName()+") does not have a valid constructor.");

                final WebStatement.ParseResult parseResult = new WebStatement.ParseResult(code, content, data);
                final WebStatement.LoadingResult result = statement.init(parseResult, event);

                if (!result.success() && result.hasErrors()) {
                    errors.addAll(Arrays.asList(result.getErrors()));
                    continue core;
                } else if (!result.success()) continue;

                final String representation = statement.convert(event);
                if (!result.disableReplace())
                    content = content.replace(data, representation == null ? "" : representation);
                continue core;
            }
            errors.add("Unknown WebSK Statement: " + code);
        }

        return new NonNullPair<>(errors, content);
    }

    private @Nullable WebStatement construct(Class<? extends WebStatement> c) {
        try {
            return c.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
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
