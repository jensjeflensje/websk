package dev.jensderuiter.websk.skript.type.statements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import dev.jensderuiter.websk.utils.SkriptUtils;
import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExecuteStatement implements Statement {

    private Effect effect;

    @Override
    public @Nullable ParsingResult init(String code, Event event, @NotNull ParserFactory parser) {
        if (!code.startsWith("execute "))
            return null;
        final String rawEffect = code.substring(8);

        effect = SkriptUtils.parseExpression(rawEffect, Skript.getEffects().iterator(), null, event);
        if (effect == null)
            return new ParsingResult(null, "Could not parse effect: " + rawEffect);
        return new ParsingResult();
    }

    @Override
    public @Nullable String parse(@NotNull Event event, @Nullable String codeBetween) {
        effect.run(event);
        return "";
    }
}
