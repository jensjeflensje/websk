package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import dev.jensderuiter.websk.skript.type.Request;

public class ExprRequestBody extends SimplePropertyExpression<Request, String> {

    static {
        register(ExprRequestBody.class, String.class, "[json ]body", "request");
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String convert(Request request) {
        String result = request.body.replaceAll("([^=]*)=([^&]*)&?", ", \"$1\": \"$2\"");
	    result = result.replaceFirst(", ", "");
	    result = "{" + result + "}";
        return result;
    }

    @Override
    protected String getPropertyName() {
        return "body";
    }

}