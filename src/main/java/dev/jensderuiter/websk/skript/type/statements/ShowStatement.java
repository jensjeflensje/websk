package dev.jensderuiter.websk.skript.type.statements;

import ch.njol.skript.lang.Expression;
import ch.njol.util.StringUtils;
import dev.jensderuiter.websk.utils.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShowStatement extends WebStatement {

    private Expression<?> expression;

    @Override
    public @NotNull LoadingResult init(ParseResult result, @NotNull Event event) {
        if (!result.getRawContent().startsWith("show "))
            return new UnknownPattern();
        final String rawExpression = result.getRawContent().replaceFirst("show ", "");
        expression = SkriptUtils.parseExpression(rawExpression, null, event);
        if (expression == null)
            return new LoadingResult(false, "Can't understand this expression: " + rawExpression);
        return new LoadingResult(true);
    }

    @Override
    public @Nullable String convert(@NotNull Event event) {
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
        return value;
    }

}
