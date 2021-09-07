package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import dev.jensderuiter.websk.skript.type.Request;

public class ExprRequestMethod extends SimplePropertyExpression<Request, String> {

    static {
        register(ExprRequestMethod.class, String.class, "method", "request");
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String convert(Request request) {
        return request.method;
    }

    @Override
    protected String getPropertyName() {
        return "method";
    }

}
