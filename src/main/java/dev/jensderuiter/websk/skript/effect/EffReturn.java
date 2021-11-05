package dev.jensderuiter.websk.skript.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.skript.factory.ServerEvent;
import dev.jensderuiter.websk.skript.type.Header;
import dev.jensderuiter.websk.utils.adapter.SkriptAdapter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffReturn extends Effect {

    static {
        Skript.registerEffect(
                EffReturn.class,
                "return %string% [with [the] code %-number%] [with [the] [header] %-webheaders%]"
        );
    }

    private Expression<String> input;
    private Expression<Header> exprHeaders;
    private Expression<Number> exprCode;
    public static String value = null;
    public static Number code = null;
    public static Header[] headers = null;

    @Override
    protected void execute(@NotNull Event e) { }

    @Override
    protected @Nullable TriggerItem walk(@NotNull Event e) {
        value = input.getSingle(e);
        headers = exprHeaders.getArray(e);
        code = exprCode.getSingle(e);
        return null;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean b) {
        return "return " + input.toString(e, b) + (exprHeaders != null ? " with headers " + exprHeaders.toString(e, b) : "");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        if (!SkriptAdapter.getInstance().isCurrentEvents(ServerEvent.class))
            return false;
        input = (Expression<String>) expressions[0];
        exprCode = (Expression<Number>) expressions[1];
        exprHeaders = (Expression<Header>) expressions[2];
        return true;
    }
}
