package dev.jensderuiter.websk.skript.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.Main;
import org.bukkit.event.Event;

public class AddWebserverPath extends Effect {

    static {
        Skript.registerEffect(AddWebserverPath.class, "add webserver path %string% to run <(.+)>\\([<.*?>]\\)");
    }

    private Expression<String> path;
    private String function;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        this.path = (Expression<String>) expressions[0];

        function = parser.regexes.get(0).group(0);
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "Add webserver path " + path.toString(event, debug) + "to run " + function;
    }

    @Override
    protected void execute(Event event) {
        if (Main.webserver == null) {
            Skript.error("Webserver has not started.");
            return;
        }
        Main.webserver.setStringContext(path.getSingle(event), function);
    }
}
