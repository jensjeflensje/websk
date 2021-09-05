package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.skript.type.Request;
import org.bukkit.event.Event;

public class ExprRequestParam extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprRequestParam.class, String.class, ExpressionType.COMBINED, "[the] parameter %string% of %-request%");
    }

    private Expression<String> param;
    private Expression<Request> request;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        param = (Expression<String>) exprs[0];
        request = (Expression<Request>) exprs[1];
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "Expression to get query parameters";
    }

    @Override
    protected String[] get(Event event) {
        Request requestObj = request.getSingle(event);
        String paramObj = param.getSingle(event);
        String result = requestObj.params.get(paramObj);
        return new String[]{result};
    }

}
