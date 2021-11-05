package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.skript.type.Header;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NewHeader extends SimpleExpression<Header> {

    static {
        Skript.registerExpression(
                NewHeader.class,
                Header.class,
                ExpressionType.SIMPLE,
                "[a] new header with [the] (key|name) %string% [and] with [the] (value|data) %string%"
        );
    }

    private Expression<String> exprKey;
    private Expression<String> exprValue;

    @Override
    protected Header @NotNull [] get(@NotNull Event e) {
        final String key = exprKey.getSingle(e);
        final String value = exprValue.getSingle(e);
        if (key == null || value == null)
            return new Header[0];
        return new Header[] {new Header(key, value)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Header> getReturnType() {
        return Header.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "new header with key " + exprKey.toString(event, b) + " with value " + exprValue.toString(event, b);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        exprKey = (Expression<String>) expressions[0];
        exprValue = (Expression<String>) expressions[1];
        return true;
    }
}
