package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.skript.type.Request;
import dev.jensderuiter.websk.web.RequestData;
import org.bukkit.event.Event;

import java.util.Map;

public class ExprRequestSessionProperty extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprRequestSessionProperty.class, String.class, ExpressionType.COMBINED, "[the] session property %string% of %request%");
    }

    private Expression<String> property;
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
        property = (Expression<String>) exprs[0];
        request = (Expression<Request>) exprs[1];
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "session property " + this.property.toString(event, debug) + " of request";
    }

    @Override
    protected String[] get(Event event) {
        Request requestObj = request.getSingle(event);
        String propertyObj = property.getSingle(event);
        String session = requestObj.cookies.get("session");
        Map<String, Object> sessionData = RequestData.sessions.get(session);
        if (sessionData == null) return new String[]{""};
        Object object = sessionData.get(propertyObj);
        if (object == null) return new String[]{""};
        return new String[]{object.toString()};
    }

}
