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

public class LoopValue extends SimpleExpression<Object> {

    public static Object lastEntity;

    static {
        Skript.registerExpression(
                LoopValue.class,
                Object.class,
                ExpressionType.SIMPLE,
                "[the] loop( |-)entity"
        );
    }

    @Override
    protected Object @NotNull [] get(@NotNull Event event) {
        return new Object[] {lastEntity};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "loop-value";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        return SkriptAdapter.getInstance().isCurrentEvents(ServerEvent.class);
    }
}
