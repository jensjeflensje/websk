package dev.jensderuiter.websk.skript.type.statements.blocks;

import ch.njol.skript.lang.Expression;
import dev.jensderuiter.websk.skript.expression.ExprArgument;
import dev.jensderuiter.websk.utils.SkriptUtils;
import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;

import java.util.LinkedList;

public class Block {

    private final String name;
    private final String rawContent;

    public Block(String name, String rawContent) {
        this.name = name;
        this.rawContent = rawContent;
    }

    public String getName() {
        return name;
    }

    public String getRawContent() {
        return rawContent;
    }

    public String parse(Event event, String... rawArgs) {
        final LinkedList<Object> args = new LinkedList<>();
        for (String rawArg : rawArgs)
        {
            final Expression<?> parsed = SkriptUtils.parseExpression(
                    rawArg.startsWith("\"") && rawArg.endsWith("\"") ? rawArg.substring(1, rawArg.length() - 1) : rawArg,
                    null, event);
            args.add(parsed == null ? rawArg : parsed.getSingle(event));
        }
        ExprArgument.currentArguments = args;
        return new ParserFactory().parse(getRawContent(), event).getSecond();
    }

}
