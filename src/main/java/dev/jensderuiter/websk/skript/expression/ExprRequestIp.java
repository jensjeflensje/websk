package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import dev.jensderuiter.websk.skript.type.Request;

public class ExprRequestIp extends SimplePropertyExpression<Request, String> {

    static {
        register(ExprRequestIp.class, String.class, "ip", "request");
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String convert(Request request) {
        return request.ip;
    }

    @Override
    protected String getPropertyName() {
        return "ip";
    }

}
