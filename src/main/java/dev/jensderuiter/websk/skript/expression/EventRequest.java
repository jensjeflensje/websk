package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.skript.factory.ServerEvent;
import dev.jensderuiter.websk.skript.type.Request;
import dev.jensderuiter.websk.utils.adapter.SkriptAdapter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EventRequest extends SimpleExpression<Request> {

    static {
        Skript.registerExpression(
                EventRequest.class,
                Request.class,
                ExpressionType.SIMPLE,
                "[the] [event( |-)]request"
        );
    }

    @Override
    protected Request @NotNull [] get(@NotNull Event event) {
        return new Request[] {((ServerEvent) event).getRequest()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Request> getReturnType() {
        return Request.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "the event request";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        if (!SkriptAdapter.getInstance().isCurrentEvents(ServerEvent.class)) {
            Skript.error("The event request can only be used in a 'on request' scope of a webserver.");
            return false;
        }
        return true;
    }
}
