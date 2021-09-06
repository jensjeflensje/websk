package dev.jensderuiter.websk.skript.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.skript.type.Request;
import dev.jensderuiter.websk.web.RequestData;
import org.bukkit.event.Event;

import java.util.*;

public class SetSessionProperty extends Effect {

    static {
        Skript.registerEffect(SetSessionProperty.class, "set session on %request% to %string% is %string%");
    }

    private Expression<Request> request;
    private Expression<String> key;
    private Expression<Object> value;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        this.request = (Expression<Request>) expressions[0];
        this.key = (Expression<String>) expressions[1];
        this.value = (Expression<Object>) expressions[2];
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "set session on request to " + this.key.toString(event, debug) + " is " + this.value.toString(event, debug);
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
    protected void execute(Event event) {
        Request requestObj = request.getSingle(event);
        String keyObj = key.getSingle(event);
        Object valueObj;
        if (value.isSingle()) {
            valueObj = value.getSingle(event);
        } else {
            valueObj = value.getAll(event);
        }

        String sessionToken = requestObj.cookies.get("session");
        if (sessionToken == null || RequestData.sessions.get(sessionToken) == null) {
            sessionToken = generateSessionToken();
            List<String> futureCookies = new ArrayList<>();
            futureCookies.add("session=" + sessionToken);
            RequestData.futureCookies.put(requestObj.id, futureCookies);
        }
        Map<String, Object> sessionData = RequestData.sessions.computeIfAbsent(sessionToken, nothing -> new HashMap<>());
        sessionData.put(keyObj, valueObj);
        RequestData.sessions.put(sessionToken, sessionData);

    }
}
