package dev.jensderuiter.websk.skript.type.statements;

import ch.njol.skript.lang.Expression;
import ch.njol.util.NonNullPair;
import dev.jensderuiter.websk.skript.expression.LoopValue;
import dev.jensderuiter.websk.utils.SkriptUtils;
import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoopStatement implements Statement {

    private final static Pattern loopPattern = Pattern.compile("loop (.[^->]+)( -> (.+))?");

    private Expression<?> expression;
    private String loopName;

    @Override
    public @Nullable ParsingResult init(String code, Event event, @NotNull ParserFactory parser, @Nullable String preCodeBetween) {
        final Matcher matcher = loopPattern.matcher(code);
        if (!matcher.matches())
            return null;
        final String rawLoop = matcher.group(1);
        final String loopName = matcher.group(3) == null ? "loop" : matcher.group(3);
        this.loopName = loopName;
        expression = SkriptUtils.parseExpression(rawLoop, null, event);
        if (expression == null)
            return new ParsingResult(null, "Can't understand this expression: " + rawLoop);
        if (expression.isSingle())
            return new ParsingResult(null, "This expression is not single, and therefore cannot be looped: " + rawLoop);

        return new ParsingResult(loopName);
    }

    @Override
    public @Nullable String parse(@NotNull Event event, @Nullable String codeBetween) {
        if (codeBetween == null)
            throw new UnsupportedOperationException("Code between is null in a section statement");
        final Object[] values = expression.getArray(event);
        if (values.length == 0)
            return null;
        final StringBuilder builder = new StringBuilder();

        for (Object value : values) {
            LoopValue.entities.put(loopName, value);
            LoopValue.lastLoop = loopName;
            final NonNullPair<List<String>, String> parsed = new ParserFactory().parse(codeBetween, event);
            if (parsed.getFirst().isEmpty())
                builder.append(parsed.getSecond());
        }

        return builder.toString();
    }
}
