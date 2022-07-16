package dev.jensderuiter.websk.skript.type.statements;

import dev.jensderuiter.websk.Main;
import dev.jensderuiter.websk.utils.SkriptUtils;
import dev.jensderuiter.websk.utils.parser.ParserFactory;

import ch.njol.skript.lang.Expression;
import ch.njol.util.StringUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import java.util.Arrays;

public class ShowStatement implements Statement{

    private Expression<?> expression;
    private Boolean escaped;

    @Override
    public @Nullable ParsingResult init(String code, Event event, @NotNull ParserFactory parser, @Nullable String preCodeBetween) {
        if (!code.startsWith("show "))
            return null;
        escaped = !code.startsWith("show unescaped");
        final String rawExpression = code.replace("show ", "").replace("unescaped ", "");
        expression = SkriptUtils.parseExpression(rawExpression, null, event);
        if (expression == null)
            return new ParsingResult(null, "Can't understand this expression: " + rawExpression);
        return new ParsingResult();
    }

    @Override
    public @Nullable String parse(@NotNull Event event, @Nullable String codeBetween) {
        String value;
        try {
            value = expression.isSingle() ? expression.getSingle(event).toString() :
                    StringUtils.join(expression.getArray(event), ", ");
        } catch (NullPointerException ex) {
            value = escapeHtml(Main.getInstance().getConfig().getString("default-value"));
        }
        
        if (escaped) value = escapeHtml(value);
        if (Main.getInstance().getConfig().getBoolean("show-newlines") == true) value = value.replaceAll("[\\t\\n\\r]+","<br>");
        else value = value.replaceAll("[\\t\\n\\r]+","%nl%");
        return value;
    }
}
