package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.skript.factory.ServerEvent;
import dev.jensderuiter.websk.utils.adapter.SkriptAdapter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EventPath extends SimpleExpression<String> {

    static {
        Skript.registerExpression(
                EventPath.class,
                String.class,
                ExpressionType.SIMPLE,
                "[the] [event( |-)](ur(l|i)|path)"
        );
    }

    @Override
    protected String @NotNull [] get(@NotNull Event event) {
        return new String[] {((ServerEvent) event).getHttpExchange().getRequestURI().toString()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "the event path";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        if (!SkriptAdapter.getInstance().isCurrentEvents(ServerEvent.class)) {
            Skript.error("The event path can only be used in a 'on String' scope of a webserver.");
            return false;
        }
        return true;
    }
}
