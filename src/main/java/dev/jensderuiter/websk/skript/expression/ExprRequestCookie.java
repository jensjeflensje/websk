package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.util.Kleenean;

import dev.jensderuiter.websk.skript.type.Request;
import dev.jensderuiter.websk.web.RequestData;
import dev.jensderuiter.websk.skript.factory.ServerEvent;
import dev.jensderuiter.websk.utils.adapter.SkriptAdapter;

import org.bukkit.event.Event;



@Name("Request Cookie")
@Description({"Access single cookies from cookie header"})
@Since("1.2.1")
@Examples({"return cookie \"User-Name\" of event-request",
        "if header \"Host-ID\" of the request is \"password\":"})

public class ExprRequestCookie extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprRequestCookie.class, String.class, ExpressionType.COMBINED, "[the] cookie %string% of %request%");
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
        return "cookie " + this.param.toString(event, debug) + " of request";
    }

    @Override
    protected String[] get(Event event) {
        Request requestObj = request.getSingle(event);
        String paramObj = param.getSingle(event);
        String result = requestObj.cookies.get(paramObj);
        return new String[]{result};
    }
}
