package dev.jensderuiter.websk.skript.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.Main;
import org.bukkit.event.Event;

public class StopWebserver extends Effect {

    static {
        Skript.registerEffect(StopWebserver.class, "stop webserver");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "stop webserver";
    }

    @Override
    protected void execute(Event event) {
        if (Main.webserver == null) {
            Skript.error("Webserver has not started.");
            return;
        }

        Main.webserver.shutdown();
    }
}
