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
import dev.jensderuiter.websk.skript.factory.ServerEvent;
import dev.jensderuiter.websk.utils.adapter.SkriptAdapter;

import org.bukkit.event.Event;



@Name("Request Header")
@Description({"When connection is made, client send data about it. ",
        "You can acces to them using this code!)"})
@Since("1.2.1")
@Examples({"return header \"User-Agent\" of the request",
        "if header \"Accept-Language\" of the request starts with \"en-US\":"})

public class ExprRequestHeader extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprRequestHeader.class, String.class, ExpressionType.COMBINED, "[the] header %string% of %request%");
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
        return "header " + this.param.toString(event, debug) + " of request";
    }

    @Override
    protected String[] get(Event event) {
        String paramObj = param.getSingle(event);
        String result = ((ServerEvent) event).getHttpExchange().getRequestHeaders().getFirst(paramObj);
        return new String[]{result};
    }
}
