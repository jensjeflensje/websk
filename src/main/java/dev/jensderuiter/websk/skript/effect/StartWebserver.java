package dev.jensderuiter.websk.skript.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.Main;
import dev.jensderuiter.websk.web.Webserver;
import org.bukkit.event.Event;

import java.io.IOException;

public class StartWebserver extends Effect {

    static {
        Skript.registerEffect(StartWebserver.class, "start webserver on port %-integer%");
    }

    private Expression<Integer> port;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        this.port = (Expression<Integer>) expressions[0];
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "Start webserver effect with expression port: " + port.toString(event, debug);
    }

    @Override
    protected void execute(Event event) {
        try {
            Main.webserver = new Webserver(port.getSingle(event));
            Main.webserver.start();
        } catch (IOException ignored) {
            Skript.error("There is already something listening on this port.");
        }


    }
}
