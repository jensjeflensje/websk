package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.event.Event;

public class ExprLoadJson extends SimpleExpression<String> {


    static {
        Skript.registerExpression(ExprLoadJson.class, String.class, ExpressionType.COMBINED, "[the] object %-objects% as json");
    }

    private Expression<Object> subject;

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
        subject = (Expression<Object>) exprs[0];

        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "Expression to get query parameters";
    }

    @Override
    protected String[] get(Event event) {
        if (this.subject.isSingle()) {
            JsonObject jsonObject = new JsonObject();
            Object object = this.subject.getSingle(event);
            if (object != null) {
                jsonObject.addProperty("data", object.toString());
            }
            return new String[]{jsonObject.toString()};
        }
        JsonArray jsonObject = new JsonArray();
        Object[] actuallyParsedObject = this.subject.getAll(event);
        for (Object property : actuallyParsedObject) {
            try {
                jsonObject.add(property.toString());
            } catch (ClassCastException ignored) {
                Skript.error("This object is not able to convert to JSON");
                return new String[]{""};
            }
        }
        return new String[]{jsonObject.toString()};
    }

}
