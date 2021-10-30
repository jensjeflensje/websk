package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import dev.jensderuiter.websk.skript.type.Request;
import dev.jensderuiter.websk.web.RequestData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Name("Session Property")
@Description({"This property represent a data stored through a session, that you can set , clear or get.",
        "You can only set it as text, and it therefore return string (you have to parse it to the desired type yourself)"})
@Since("1.1.2")
@Examples({"set session property \"is_logged\" of {_req} to \"true\"",
        "if session property \"is_logged\" of {_req} parsed as boolean is true",
        "set {_name} to session property \"username\" of {_req}"})
public class ExprRequestSessionProperty extends SimplePropertyExpression<Request, String> {

    static {
        register(ExprRequestSessionProperty.class, String.class, "session property %string%", "request");
    }

    private Expression<String> exprKey;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprKey = (Expression<String>) exprs[0];
        setExpr((Expression<? extends Request>) exprs[1]);
        return true;
    }

    @Override
    protected String @NotNull [] get(@NotNull Event e, Request @NotNull [] source) {
        final String key = exprKey.getSingle(e);
        assert key != null;
        System.out.println(key + ":");
        System.out.println(getProperty(key, source[0]));
        return new String[] {getProperty(key, source[0])};
    }

    @Override
    public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        if (delta == null || delta.length == 0 || delta[0] == null) return;
        final Object value = delta[0];
        final String key = exprKey.getSingle(e);
        final Request request = getExpr().getSingle(e);
        assert key != null && request != null;

        String sessionToken = request.cookies.get("session");
        if (sessionToken == null || RequestData.sessions.get(sessionToken) == null) {
            sessionToken = generateSessionToken();
            List<String> futureCookies = new ArrayList<>();
            futureCookies.add("session=" + sessionToken);
            RequestData.futureCookies.put(request.id, futureCookies);
        }
        Map<String, Object> sessionData = RequestData.sessions.computeIfAbsent(sessionToken, nothing -> new HashMap<>());
        sessionData.put(key, value);
        RequestData.sessions.put(sessionToken, sessionData);
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode.equals(Changer.ChangeMode.SET) || mode.equals(Changer.ChangeMode.DELETE))
            return CollectionUtils.array(String.class);
        return CollectionUtils.array();
    }

    protected String getProperty(String key, Request request) {
        String session = request.cookies.get("session");
        Map<String, Object> sessionData = RequestData.sessions.get(session);
        if (sessionData == null) return null;
        Object object = sessionData.get(key);
        if (object == null) return null;
        return object.toString();
    }

    private String generateSessionToken() {
        int leftLimit = 'a'; // letter 'a'
        int rightLimit = 'z'; // letter 'z'
        int targetStringLength = 32;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "session property " + exprKey.toString(e, debug) + " of request " + getExpr().toString(e, debug);
    }

    // Passing the property name method since we use the toString one instead
    @Override
    protected @NotNull String getPropertyName() {
        return null;
    }

    // Passing the convert method, since we use the get one instead
    @Override
    public @Nullable String convert(@NotNull Request request) {
        return null;
    }
}
